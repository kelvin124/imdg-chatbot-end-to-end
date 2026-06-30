package com.example.demo;

import com.example.demo.controller.api.response.stability.VesselStabilityCheckResponse;
import com.example.demo.entity.stowage.StowagePlan;
import com.example.demo.entity.stowage.slot.StowagePlanSlot;
import com.example.demo.repository.MongoDBCollection;
import com.example.demo.service.VesselStabilityService;
import com.example.demo.service.domain.OperationType;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Testcontainers
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StabilityIntegrationTests {

    private static final String CONTAINER_PATH = "/tmp/test-data";

    private static final String STABILITY_V1_API_BASE_URL = "/api/v1/stability";
    private static final String STABILITY_V1_CG_API = STABILITY_V1_API_BASE_URL + "/cg/compute";
    private static final String STABILITY_V1_KG_API = STABILITY_V1_API_BASE_URL + "/kg/compute";
    private static final String STABILITY_V1_CG_VALIDATE_API = STABILITY_V1_API_BASE_URL + "/cg/validate";
    private static final String STABILITY_V1_KG_VALIDATE_API = STABILITY_V1_API_BASE_URL + "/kg/validate";
    private static final String STABILITY_V1_VESSEL_VALIDATE_API = STABILITY_V1_API_BASE_URL + "/kg/validate";

    private static final String STOWAGE_PLAN_V1_API_BASE_URL = "/api/v1/stowage-plan";
    private static final String STOWAGE_PLAN_SLOT_V1_API_BASE_URL = "/api/v1/stowage-plan-slot";
    private static final String STOWAGE_PLAN_SLOT_ADD_URL = STOWAGE_PLAN_SLOT_V1_API_BASE_URL + "/add";

    @ServiceConnection
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withCopyFileToContainer(
            MountableFile.forClasspathResource("/test-data"), CONTAINER_PATH
    );

    private static final Logger log = LoggerFactory.getLogger(StabilityIntegrationTests.class);

    @BeforeAll
    public static void beforeAll() throws IOException, InterruptedException {
        log.info("Starting MongoDB container with image: {}", mongoDBContainer.getDockerImageName());
        mongoDBContainer.start();
        Instant start = Instant.now();
        while (!mongoDBContainer.isRunning()) {
            if (Duration.between(start, Instant.now()).toMillis() <= 5000) {
                log.error("MongoDB container failed to start within 5 seconds.");
                throw new RuntimeException("MongoDB container failed to start within 5 seconds.");
            }
        }
        loadTestData(MongoDBCollection.CONTAINER, CONTAINER_PATH + "/container/container.json");
        loadTestData(MongoDBCollection.VESSEL_PROFILE, CONTAINER_PATH + "/vessel-profile/vessel_profile.json");
        loadTestData(MongoDBCollection.VESSEL_STRUCTURE_BAY, CONTAINER_PATH + "/vessel-structure-bay/vessel_structure_bay.json");
        loadTestData(MongoDBCollection.STOWAGE_PLAN, CONTAINER_PATH + "/stowage-plan/stowage_plan.json");
        loadTestData(MongoDBCollection.STOWAGE_PLAN_SLOT, CONTAINER_PATH + "/stowage-plan-slot/stowage_plan_slot.json");
    }

    private static void loadTestData(String collectionName, String filePath) throws IOException, InterruptedException {
        String command = String.format("mongoimport --db stowage_plan --collection %s --file %s --jsonArray", collectionName, filePath);
        Container.ExecResult result = mongoDBContainer.execInContainer("sh", "-c", command);
        if (result.getExitCode() != 0) {
            throw new RuntimeException("Failed to import JSON: " + result.getStderr());
        }
    }

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private VesselStabilityService vesselStabilityService;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        mongoTemplate.findAndRemove(new Query(), StowagePlanSlot.class);
        loadTestData(MongoDBCollection.STOWAGE_PLAN_SLOT, CONTAINER_PATH + "/stowage-plan-slot/stowage_plan_slot.json");
        var stowagePlanSlotCount = mongoTemplate.count(new Query(), MongoDBCollection.STOWAGE_PLAN_SLOT);
        Assertions.assertEquals(MongoDBIntegrationTest.STOWAGE_PLAN_SLOT_COUNT, stowagePlanSlotCount);
    }

    @AfterEach
    public void afterEach() {
        mongoTemplate.findAndRemove(new Query(), StowagePlanSlot.class);
    }

    @Test
    @Order(1)
    void verifyDocumentCount() {
        var containerCount = mongoTemplate.count(new Query(), MongoDBCollection.CONTAINER);
        var profileCount = mongoTemplate.count(new Query(), MongoDBCollection.VESSEL_PROFILE);
        var bayCount = mongoTemplate.count(new Query(), MongoDBCollection.VESSEL_STRUCTURE_BAY);
        var stowagePlanCount = mongoTemplate.count(new Query(), MongoDBCollection.STOWAGE_PLAN);
        var stowagePlanSlotCount = mongoTemplate.count(new Query(), MongoDBCollection.STOWAGE_PLAN_SLOT);
        Assertions.assertEquals(MongoDBIntegrationTest.CONTAINER_COUNT, containerCount);
        Assertions.assertEquals(MongoDBIntegrationTest.PROFILE_COUNT, profileCount);
        Assertions.assertEquals(MongoDBIntegrationTest.BAY_COUNT, bayCount);
        Assertions.assertEquals(MongoDBIntegrationTest.STOWAGE_PLAN_COUNT, stowagePlanCount);
        Assertions.assertEquals(MongoDBIntegrationTest.STOWAGE_PLAN_SLOT_COUNT, stowagePlanSlotCount);
    }

    @Test
    void test_cg_add_heavy_container_to_the_first_bay_cg_should_increase() {

        ObjectNode computeRequest = objectMapper.createObjectNode();
        computeRequest.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        VesselStabilityCheckResponse responseBefore = restTestClient.post()
                .uri(STABILITY_V1_CG_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(computeRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody(VesselStabilityCheckResponse.class).returnResult().getResponseBody();

        Double cgBefore = responseBefore.getCg();
        Assertions.assertNotNull(cgBefore);

        ObjectNode addSlotRequest = objectMapper.createObjectNode();
        addSlotRequest.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        addSlotRequest.put("bayIndex", 1);
        addSlotRequest.put("type", "D");
        addSlotRequest.put("rowIndex", 4);
        addSlotRequest.put("tierIndex", 13);
        addSlotRequest.put("containerNo", "GYUY7139044");
        addSlotRequest.put("operationType", OperationType.DISCHARGE);

        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON);

        VesselStabilityCheckResponse responseAfter = restTestClient.post()
                .uri(STABILITY_V1_CG_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(computeRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody(VesselStabilityCheckResponse.class).returnResult().getResponseBody();

        Double cgAfter = responseAfter.getCg();
        Assertions.assertNotNull(cgAfter);

        Assertions.assertNotEquals(cgBefore, cgAfter);
        Assertions.assertTrue(cgAfter > cgBefore);

    }

    @Test
    void test_cg_add_heavy_container_to_the_last_bay_cg_should_decrease() {

        ObjectNode computeRequest = objectMapper.createObjectNode();
        computeRequest.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        VesselStabilityCheckResponse responseBefore = restTestClient.post()
                .uri(STABILITY_V1_CG_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(computeRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody(VesselStabilityCheckResponse.class).returnResult().getResponseBody();

        Double cgBefore = responseBefore.getCg();
        Assertions.assertNotNull(cgBefore);

        ObjectNode addSlotRequest = objectMapper.createObjectNode();
        addSlotRequest.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        addSlotRequest.put("bayIndex", 20);
        addSlotRequest.put("type", "D");
        addSlotRequest.put("rowIndex", 0);
        addSlotRequest.put("tierIndex", 17);
        addSlotRequest.put("containerNo", "GYUY7139044");
        addSlotRequest.put("operationType", OperationType.DISCHARGE);

        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON);

        VesselStabilityCheckResponse responseAfter = restTestClient.post()
                .uri(STABILITY_V1_CG_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(computeRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody(VesselStabilityCheckResponse.class).returnResult().getResponseBody();

        Double cgAfter = responseAfter.getCg();
        Assertions.assertNotNull(cgAfter);

        Assertions.assertNotEquals(cgBefore, cgAfter);
        Assertions.assertTrue(cgAfter < cgBefore);

    }

    @Test
    void test_kg_add_heavy_container_to_higher_tier_kg_should_increase() {

        ObjectNode computeRequest = objectMapper.createObjectNode();
        computeRequest.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        VesselStabilityCheckResponse responseBefore = restTestClient.post()
                .uri(STABILITY_V1_KG_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(computeRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody(VesselStabilityCheckResponse.class).returnResult().getResponseBody();

        Double kgBefore = responseBefore.getKg();
        Assertions.assertNotNull(kgBefore);

        ObjectNode addSlotRequest = objectMapper.createObjectNode();
        addSlotRequest.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        addSlotRequest.put("bayIndex", 1);
        addSlotRequest.put("type", "D");
        addSlotRequest.put("rowIndex", 9);
        addSlotRequest.put("tierIndex", 14);
        addSlotRequest.put("containerNo", "GYUY7139044");
        addSlotRequest.put("operationType", OperationType.DISCHARGE);

        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON);

        VesselStabilityCheckResponse responseAfter = restTestClient.post()
                .uri(STABILITY_V1_KG_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(computeRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody(VesselStabilityCheckResponse.class).returnResult().getResponseBody();

        Double kgAfter = responseAfter.getKg();
        Assertions.assertNotNull(kgAfter);

        Assertions.assertNotEquals(kgBefore, kgAfter);
        Assertions.assertTrue(kgAfter > kgBefore);

    }

    @Test
    void test_kg_add_heavy_container_to_lower_tier_kg_should_decrease() {

        ObjectNode computeRequest = objectMapper.createObjectNode();
        computeRequest.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        VesselStabilityCheckResponse responseBefore = restTestClient.post()
                .uri(STABILITY_V1_KG_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(computeRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody(VesselStabilityCheckResponse.class).returnResult().getResponseBody();

        Double kgBefore = responseBefore.getKg();
        Assertions.assertNotNull(kgBefore);

        ObjectNode addSlotRequest = objectMapper.createObjectNode();
        addSlotRequest.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        addSlotRequest.put("bayIndex", 1);
        addSlotRequest.put("type", "H");
        addSlotRequest.put("rowIndex", 9);
        addSlotRequest.put("tierIndex", 6);
        addSlotRequest.put("containerNo", "GYUY7139044");
        addSlotRequest.put("operationType", OperationType.DISCHARGE);

        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON);

        VesselStabilityCheckResponse responseAfter = restTestClient.post()
                .uri(STABILITY_V1_KG_API)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(computeRequest))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody(VesselStabilityCheckResponse.class).returnResult().getResponseBody();

        Double kgAfter = responseAfter.getKg();
        Assertions.assertNotNull(kgAfter);

        Assertions.assertNotEquals(kgBefore, kgAfter);
        Assertions.assertTrue(kgAfter < kgBefore);

    }

    @Test
    void test_vessel_length() {
        StowagePlan stowagePlan = mongoTemplate.findOne(new BasicQuery("""
                {
                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729'
                }
                """), StowagePlan.class);
        Assertions.assertNotNull(stowagePlan);
        Double vesselLength = vesselStabilityService.getVesselLength(stowagePlan.getBaySnapshots());
        Assertions.assertEquals(325.6, vesselLength);
    }

}
