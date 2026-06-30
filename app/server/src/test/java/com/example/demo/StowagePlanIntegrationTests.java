package com.example.demo;

import com.example.demo.controller.api.dto.entity.stowage.StowagePlanDto;
import com.example.demo.controller.api.response.stowage.CreateStowagePlanResponse;
import com.example.demo.controller.api.response.stowage.GetStowagePlanResponse;
import com.example.demo.controller.api.response.stowage.SyncVesselStructureResponse;
import com.example.demo.entity.stowage.StowagePlan;
import com.example.demo.entity.stowage.slot.StowagePlanSlot;
import com.example.demo.repository.MongoDBCollection;
import com.example.demo.service.StowagePlanService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
class StowagePlanIntegrationTests {

    private static final String CONTAINER_PATH = "/tmp/test-data";

    private static final String STOWAGE_PLAN_V1_API_BASE_URL = "/api/v1/stowage-plan";
    private static final String STOWAGE_PLAN_NEW_URL = STOWAGE_PLAN_V1_API_BASE_URL + "/new";
    private static final String STOWAGE_PLAN_SYNC_VESSEL_STRUCTURE = STOWAGE_PLAN_V1_API_BASE_URL + "/sync-vessel-structure";

    @ServiceConnection
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withCopyFileToContainer(
            MountableFile.forClasspathResource("/test-data"), CONTAINER_PATH
    );

    private static final Logger log = LoggerFactory.getLogger(StowagePlanIntegrationTests.class);

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
    private StowagePlanService stowagePlanService;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

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
    void getStowagePlan_meta_only() throws IOException, InterruptedException {
        loadTestData(MongoDBCollection.STOWAGE_PLAN, CONTAINER_PATH + "/stowage-plan/stowage_plan.json");
        loadTestData(MongoDBCollection.STOWAGE_PLAN_SLOT, CONTAINER_PATH + "/stowage-plan-slot/stowage_plan_slot.json");
        GetStowagePlanResponse response = restTestClient.get()
                .uri(STOWAGE_PLAN_V1_API_BASE_URL + "/184ee948-4534-4a08-b394-79d5508b48bb")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(GetStowagePlanResponse.class)
                .returnResult().getResponseBody();

        Assertions.assertNotNull(response);
        Assertions.assertNull(response.getStowagePlanSlot());
        Assertions.assertEquals(21,
                response.getStowagePlan().getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getBayCount()
        );
        Assertions.assertEquals(16,
                response.getStowagePlan().getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getRowCount()
        );
        Assertions.assertEquals(18,
                response.getStowagePlan().getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getTierCount()
        );
        Assertions.assertEquals(0.1,
                response.getStowagePlan().getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getTcgTolerance()
        );
        Assertions.assertEquals(21, response.getStowagePlan().getBaySnapshotDtoList().size());
        Assertions.assertEquals("184ee948-4534-4a08-b394-79d5508b48bb", response.getStowagePlan().getPlanId());
        StowagePlanDto dto = response.getStowagePlan();

        StowagePlan stowagePlan = stowagePlanService.getStowagePlanById(dto.getPlanId());
        Assertions.assertEquals(stowagePlan.getName(), dto.getName());
        Assertions.assertEquals(
                stowagePlan.getVesselProfileSnapshot().getVesselId(),
                dto.getVesselProfileSnapshotDto().getVesselId()
        );
        Assertions.assertEquals(
                stowagePlan.getVesselProfileSnapshot().getTankSnapshots().size(),
                dto.getVesselProfileSnapshotDto().getTankSnapshotDtoList().size()
        );
        Assertions.assertEquals(
                stowagePlan.getBaySnapshots().size(),
                dto.getBaySnapshotDtoList().size()
        );

    }

    @Test
    void getStowagePlan_with_slots() {
        GetStowagePlanResponse response = restTestClient.get()
                .uri(STOWAGE_PLAN_V1_API_BASE_URL + "/ecc44ca4-1193-4093-92fe-53a2d0ef8729?slots=true")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(GetStowagePlanResponse.class)
                .returnResult().getResponseBody();
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getStowagePlanSlot());
        Assertions.assertEquals(21,
                response.getStowagePlan().getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getBayCount()
        );
        Assertions.assertEquals(16,
                response.getStowagePlan().getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getRowCount()
        );
        Assertions.assertEquals(18,
                response.getStowagePlan().getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getTierCount()
        );
        Assertions.assertEquals(0.1,
                response.getStowagePlan().getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getTcgTolerance()
        );
        Assertions.assertEquals(21, response.getStowagePlan().getBaySnapshotDtoList().size());
        Assertions.assertEquals("ecc44ca4-1193-4093-92fe-53a2d0ef8729", response.getStowagePlan().getPlanId());
        StowagePlanDto dto = response.getStowagePlan();

        StowagePlan stowagePlan = stowagePlanService.getStowagePlanById(dto.getPlanId());
        Assertions.assertEquals(stowagePlan.getName(), dto.getName());
        Assertions.assertEquals(
                stowagePlan.getVesselProfileSnapshot().getVesselId(),
                dto.getVesselProfileSnapshotDto().getVesselId()
        );
        Assertions.assertEquals(
                stowagePlan.getVesselProfileSnapshot().getTankSnapshots().size(),
                dto.getVesselProfileSnapshotDto().getTankSnapshotDtoList().size()
        );
        Assertions.assertEquals(
                stowagePlan.getBaySnapshots().size(),
                dto.getBaySnapshotDtoList().size()
        );
        Assertions.assertEquals(1,
                response.getStowagePlanSlot().size()
        );
        Assertions.assertEquals("DS",
                response.getStowagePlanSlot().get(0).getOperationType()
        );
        Assertions.assertEquals(1,
                response.getStowagePlanSlot().get(0).getStowagePlanSlotPositionDto().getBayIndex()
        );
        Assertions.assertEquals(4,
                response.getStowagePlanSlot().get(0).getStowagePlanSlotPositionDto().getRowIndex()
        );
        Assertions.assertEquals(14,
                response.getStowagePlanSlot().get(0).getStowagePlanSlotPositionDto().getTierIndex()
        );

    }

    @Test
    void createStowagePlan() {
        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.put("vesselId", "default-vessel-s");
        requestPayload.put("name", "test-plan");
        String json = objectMapper.writeValueAsString(requestPayload);
        RestTestClient.ResponseSpec responseSpec = restTestClient.post()
                .uri(STOWAGE_PLAN_NEW_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        responseSpec.expectStatus().isOk();
        responseSpec.expectHeader().contentType(MediaType.APPLICATION_JSON);

        CreateStowagePlanResponse response = responseSpec.expectBody(CreateStowagePlanResponse.class).returnResult().getResponseBody();
        Assertions.assertNotNull(response);
        StowagePlanDto dto = response.getStowagePlan();

        StowagePlan stowagePlan = stowagePlanService.getStowagePlanById(dto.getPlanId());
        Assertions.assertEquals(stowagePlan.getName(), dto.getName());
        Assertions.assertEquals(
                stowagePlan.getVesselProfileSnapshot().getVesselId(),
                dto.getVesselProfileSnapshotDto().getVesselId()
        );
        Assertions.assertEquals(
                stowagePlan.getVesselProfileSnapshot().getTankSnapshots().size(),
                dto.getVesselProfileSnapshotDto().getTankSnapshotDtoList().size()
        );
        Assertions.assertEquals(
                stowagePlan.getBaySnapshots().size(),
                dto.getBaySnapshotDtoList().size()
        );


    }

    @Test
    void deleteStowagePlan() {
        StowagePlan stowagePlan = mongoTemplate.findOne(
                new BasicQuery("{ 'planId': '184ee948-4534-4a08-b394-79d5508b48bb'}"),
                StowagePlan.class
        );
        Assertions.assertNotNull(stowagePlan);
        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.put("stowagePlanId", "184ee948-4534-4a08-b394-79d5508b48bb");
        String json = objectMapper.writeValueAsString(requestPayload);
        restTestClient.post()
                .uri(STOWAGE_PLAN_V1_API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk();
        StowagePlan stowagePlanAgain = mongoTemplate.findOne(
                new BasicQuery("{ 'planId': 'plan-id-to-delete'}"),
                StowagePlan.class
        );
        Assertions.assertNull(stowagePlanAgain);
        Long slotCount = mongoTemplate.count(
                new BasicQuery("{ 'planId': 'plan-id-to-delete'}"),
                StowagePlanSlot.class
        );
        Assertions.assertEquals(0, slotCount);
    }

    @Test
    void syncVesselStructure() {
        StowagePlan stowagePlan = mongoTemplate.findOne(
                new BasicQuery("{ 'planId': '2823e00f-e20d-4ad3-8032-c9bbea3a92d2'}"),
                StowagePlan.class
        );
        Assertions.assertNull(stowagePlan.getBaySnapshots());
        Assertions.assertEquals(999, stowagePlan.getVesselProfileSnapshot().getShipInfoSnapshot().getBayCount());
        Assertions.assertEquals(999, stowagePlan.getVesselProfileSnapshot().getShipInfoSnapshot().getRowCount());
        Assertions.assertEquals(999, stowagePlan.getVesselProfileSnapshot().getShipInfoSnapshot().getTierCount());
        Assertions.assertEquals(999, stowagePlan.getVesselProfileSnapshot().getShipInfoSnapshot().getTcgTolerance());
        Assertions.assertEquals("dummy-vessel-s", stowagePlan.getVesselProfileSnapshot().getVesselName());

        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.put("stowagePlanId", "2823e00f-e20d-4ad3-8032-c9bbea3a92d2");
        String json = objectMapper.writeValueAsString(requestPayload);
        RestTestClient.ResponseSpec responseSpec = restTestClient.post()
                .uri(STOWAGE_PLAN_SYNC_VESSEL_STRUCTURE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        responseSpec.expectStatus().isOk();
        responseSpec.expectHeader().contentType(MediaType.APPLICATION_JSON);

        SyncVesselStructureResponse response = responseSpec.expectBody(SyncVesselStructureResponse.class).returnResult().getResponseBody();
        Assertions.assertNotNull(response);
        StowagePlanDto dto = response.getStowagePlanDto();
        Assertions.assertNotNull(dto.getBaySnapshotDtoList());
        Assertions.assertEquals(21, dto.getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getBayCount());
        Assertions.assertEquals(16, dto.getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getRowCount());
        Assertions.assertEquals(18, dto.getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getTierCount());
        Assertions.assertEquals(0.1, dto.getVesselProfileSnapshotDto().getShipInfoSnapshotDto().getTcgTolerance());
        Assertions.assertEquals("vessel-s", dto.getVesselProfileSnapshotDto().getVesselName());
        StowagePlan stowagePlanAfterSync = mongoTemplate.findOne(
                new BasicQuery("{ 'planId': '2823e00f-e20d-4ad3-8032-c9bbea3a92d2'}"),
                StowagePlan.class
        );
        Assertions.assertNotNull(stowagePlanAfterSync.getBaySnapshots());
        Assertions.assertEquals(21, stowagePlanAfterSync.getBaySnapshots().size());
        Assertions.assertEquals(21, stowagePlanAfterSync.getVesselProfileSnapshot().getShipInfoSnapshot().getBayCount());
        Assertions.assertEquals(16, stowagePlanAfterSync.getVesselProfileSnapshot().getShipInfoSnapshot().getRowCount());
        Assertions.assertEquals(18, stowagePlanAfterSync.getVesselProfileSnapshot().getShipInfoSnapshot().getTierCount());
        Assertions.assertEquals(0.1, stowagePlanAfterSync.getVesselProfileSnapshot().getShipInfoSnapshot().getTcgTolerance());
        Assertions.assertEquals("vessel-s", stowagePlanAfterSync.getVesselProfileSnapshot().getVesselName());
    }

}
