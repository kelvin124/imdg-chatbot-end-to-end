package com.example.demo;

import com.example.demo.controller.api.response.stowage.GetStowagePlanSlotResponse;
import com.example.demo.entity.stowage.slot.StowagePlanSlot;
import com.example.demo.repository.MongoDBCollection;
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
class StowagePlanSlotIntegrationTests {

    private static final String CONTAINER_PATH = "/tmp/test-data";

    private static final String STOWAGE_PLAN_SLOT_V1_API_BASE_URL = "/api/v1/stowage-plan-slot";
    private static final String STOWAGE_PLAN_SLOT_ADD_URL = STOWAGE_PLAN_SLOT_V1_API_BASE_URL + "/add";
    private static final String STOWAGE_PLAN_SLOT_DELETE_URL = STOWAGE_PLAN_SLOT_V1_API_BASE_URL + "/delete";
    private static final String STOWAGE_PLAN_SLOT_UPDATE_URL = STOWAGE_PLAN_SLOT_V1_API_BASE_URL + "/update";

    @ServiceConnection
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withCopyFileToContainer(
            MountableFile.forClasspathResource("/test-data"), CONTAINER_PATH
    );

    private static final Logger log = LoggerFactory.getLogger(StowagePlanSlotIntegrationTests.class);

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
    }

    private static void loadTestData(String collectionName, String filePath) throws IOException, InterruptedException {
        String command = String.format("mongoimport --db stowage_plan --collection %s --file %s --jsonArray", collectionName, filePath);
        Container.ExecResult result = mongoDBContainer.execInContainer("sh", "-c", command);
        log.info(result.getStdout());
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
        Assertions.assertEquals(MongoDBIntegrationTest.CONTAINER_COUNT, containerCount);
        Assertions.assertEquals(MongoDBIntegrationTest.PROFILE_COUNT, profileCount);
        Assertions.assertEquals(MongoDBIntegrationTest.BAY_COUNT, bayCount);
        Assertions.assertEquals(MongoDBIntegrationTest.STOWAGE_PLAN_COUNT, stowagePlanCount);
    }

    @BeforeEach
    void deleteAllSlots_before() {
        mongoTemplate.findAndRemove(new Query(), StowagePlanSlot.class);
    }

    @AfterEach
    void deleteAllSlots_after() {
        mongoTemplate.findAndRemove(new Query(), StowagePlanSlot.class);
    }

    @Test
    void addStowagePlanSlot() {
        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        requestPayload.put("bayIndex", 1);
        requestPayload.put("type", "D");
        requestPayload.put("rowIndex", 4);
        requestPayload.put("tierIndex", 14);
        requestPayload.put("containerNo", "MSKU9012345");
        requestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(requestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNotNull(stowagePlanSlot);
        Assertions.assertEquals("MSKU9012345", stowagePlanSlot.getContainerSnapshot().getContainerNo());
    }

    @Test
    void addStowagePlanSlot_same_container_add_twice_with_different_operation_type_should_ok() {

        ObjectNode dischargeRequestPayload = objectMapper.createObjectNode();
        dischargeRequestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        dischargeRequestPayload.put("bayIndex", 1);
        dischargeRequestPayload.put("type", "D");
        dischargeRequestPayload.put("rowIndex", 4);
        dischargeRequestPayload.put("tierIndex", 14);
        dischargeRequestPayload.put("containerNo", "MSKU9012345");
        dischargeRequestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(dischargeRequestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON);

        ObjectNode loadingRequestPayload = objectMapper.createObjectNode();
        loadingRequestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        loadingRequestPayload.put("bayIndex", 1);
        loadingRequestPayload.put("type", "D");
        loadingRequestPayload.put("rowIndex", 4);
        loadingRequestPayload.put("tierIndex", 14);
        loadingRequestPayload.put("containerNo", "MSKU9012345");
        loadingRequestPayload.put("operationType", OperationType.LOADING);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(loadingRequestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON);

        StowagePlanSlot dischargeSlot = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        StowagePlanSlot loadingSlot = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'LD',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNotNull(dischargeSlot);
        Assertions.assertNotNull(loadingSlot);
        Assertions.assertEquals("MSKU9012345", dischargeSlot.getContainerSnapshot().getContainerNo());
        Assertions.assertEquals("DS", dischargeSlot.getOperationType());
        Assertions.assertEquals("MSKU9012345", loadingSlot.getContainerSnapshot().getContainerNo());
        Assertions.assertEquals("LD", loadingSlot.getOperationType());
    }

    @Test
    void addStowagePlanSlot_non_existence_container_should_throw_exception() {
        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        requestPayload.put("bayIndex", 1);
        requestPayload.put("type", "D");
        requestPayload.put("rowIndex", 4);
        requestPayload.put("tierIndex", 14);
        requestPayload.put("containerNo", "XXXXX");
        requestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(requestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNotFound();
        Assertions.assertEquals(0, countDocument());
    }

    @Test
    void addStowagePlanSlot_reefer_container_to_reefer_cell_should_ok() {
        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        requestPayload.put("bayIndex", 6);
        requestPayload.put("type", "D");
        requestPayload.put("rowIndex", 1);
        requestPayload.put("tierIndex", 11);
        requestPayload.put("containerNo", "MSCU7788990");
        requestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(requestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 6,
                                    'position.rowIndex': 1,
                                    'position.tierIndex': 11,
                                    'containerSnapshot.containerNo': 'MSCU7788990',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNotNull(stowagePlanSlot);
        Assertions.assertEquals("MSCU7788990", stowagePlanSlot.getContainerSnapshot().getContainerNo());
        Assertions.assertEquals(true, stowagePlanSlot.getAllowReefer());
        Assertions.assertEquals(true, stowagePlanSlot.getContainerSnapshot().getIsReefer());
    }

    @Test
    void addStowagePlanSlot_reefer_container_to_non_reefer_cell_should_throw_exception() {
        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        requestPayload.put("bayIndex", 6);
        requestPayload.put("type", "D");
        requestPayload.put("rowIndex", 1);
        requestPayload.put("tierIndex", 15);
        requestPayload.put("containerNo", "MSCU7788990");
        requestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(requestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 6,
                                    'position.rowIndex': 1,
                                    'position.tierIndex': 15,
                                    'containerSnapshot.containerNo': 'MSCU7788990',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertEquals(0, countDocument());
        Assertions.assertNull(stowagePlanSlot);
    }

    @Test
    void addStowagePlanSlot_two_containers_to_same_slot_should_throw_exception() {
        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        requestPayload.put("bayIndex", 1);
        requestPayload.put("type", "D");
        requestPayload.put("rowIndex", 4);
        requestPayload.put("tierIndex", 14);
        requestPayload.put("containerNo", "MSKU9012345");
        requestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(requestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON);

        StowagePlanSlot stowagePlanSlot1st = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);

        StowagePlanSlot stowagePlanSlot2nd = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'OOLU3456789',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNull(stowagePlanSlot2nd);
        Assertions.assertNotNull(stowagePlanSlot1st);
        Assertions.assertEquals("MSKU9012345", stowagePlanSlot1st.getContainerSnapshot().getContainerNo());
        Assertions.assertEquals(1, stowagePlanSlot1st.getStowagePlanSlotPosition().getBayIndex());
        Assertions.assertEquals(4, stowagePlanSlot1st.getStowagePlanSlotPosition().getRowIndex());
        Assertions.assertEquals(14, stowagePlanSlot1st.getStowagePlanSlotPosition().getTierIndex());

        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(requestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isBadRequest().expectHeader().contentType(MediaType.APPLICATION_JSON);
        Assertions.assertEquals(1, countDocument());
    }

    @Test
    void addStowagePlanSlot_same_container_to_two_slots_should_throw_exception() {
        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        requestPayload.put("bayIndex", 1);
        requestPayload.put("type", "D");
        requestPayload.put("rowIndex", 4);
        requestPayload.put("tierIndex", 14);
        requestPayload.put("containerNo", "MSKU9012345");
        requestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(requestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNotNull(stowagePlanSlot);
        Assertions.assertEquals("MSKU9012345", stowagePlanSlot.getContainerSnapshot().getContainerNo());

        requestPayload.put("tierIndex", 13);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(requestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isBadRequest().expectHeader().contentType(MediaType.APPLICATION_JSON);
        Assertions.assertEquals(1, countDocument());
    }

    @Test
    void addStowagePlanSlot_non_existence_cell_should_throw_exception() {
        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        requestPayload.put("bayIndex", 99);
        requestPayload.put("type", "D");
        requestPayload.put("rowIndex", 99);
        requestPayload.put("tierIndex", 99);
        requestPayload.put("containerNo", "MSKU9012345");
        requestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(requestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isBadRequest().expectHeader().contentType(MediaType.APPLICATION_JSON);
        Assertions.assertEquals(0, countDocument());
    }

    @Test
    void updateStowagePlanSlot_update_slot_with_new_and_unplanned_container_should_ok() {
        ObjectNode addSlotRequestPayload = objectMapper.createObjectNode();
        addSlotRequestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        addSlotRequestPayload.put("bayIndex", 1);
        addSlotRequestPayload.put("type", "D");
        addSlotRequestPayload.put("rowIndex", 4);
        addSlotRequestPayload.put("tierIndex", 14);
        addSlotRequestPayload.put("containerNo", "MSKU9012345");
        addSlotRequestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot1st = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNotNull(stowagePlanSlot1st);
        Assertions.assertEquals("MSKU9012345", stowagePlanSlot1st.getContainerSnapshot().getContainerNo());

        ObjectNode updateRequestPayload = objectMapper.createObjectNode();
        updateRequestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        updateRequestPayload.put("bayIndex", 1);
        updateRequestPayload.put("rowIndex", 4);
        updateRequestPayload.put("tierIndex", 14);
        updateRequestPayload.put("containerNo", "OOLU3456789");
        updateRequestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_UPDATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(updateRequestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot2nd = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'OOLU3456789',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNotNull(stowagePlanSlot2nd);
        Assertions.assertEquals("OOLU3456789", stowagePlanSlot2nd.getContainerSnapshot().getContainerNo());
    }

    @Test
    void updateStowagePlanSlot_update_slot_with_planned_container_should_fail() {
        ObjectNode addSlotRequestPayload1 = objectMapper.createObjectNode();
        addSlotRequestPayload1.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        addSlotRequestPayload1.put("bayIndex", 1);
        addSlotRequestPayload1.put("type", "D");
        addSlotRequestPayload1.put("rowIndex", 4);
        addSlotRequestPayload1.put("tierIndex", 14);
        addSlotRequestPayload1.put("containerNo", "MSKU9012345");
        addSlotRequestPayload1.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequestPayload1))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot1st = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNotNull(stowagePlanSlot1st);
        Assertions.assertEquals("MSKU9012345", stowagePlanSlot1st.getContainerSnapshot().getContainerNo());

        ObjectNode addSlotRequestPayload2 = objectMapper.createObjectNode();
        addSlotRequestPayload2.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        addSlotRequestPayload2.put("bayIndex", 1);
        addSlotRequestPayload2.put("type", "D");
        addSlotRequestPayload2.put("rowIndex", 4);
        addSlotRequestPayload2.put("tierIndex", 13);
        addSlotRequestPayload2.put("containerNo", "TGHU4321098");
        addSlotRequestPayload2.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequestPayload2))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot2nd = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 13,
                                    'containerSnapshot.containerNo': 'TGHU4321098',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNotNull(stowagePlanSlot2nd);
        Assertions.assertEquals("TGHU4321098", stowagePlanSlot2nd.getContainerSnapshot().getContainerNo());

        ObjectNode updateRequestPayload = objectMapper.createObjectNode();
        updateRequestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        updateRequestPayload.put("bayIndex", 1);
        updateRequestPayload.put("rowIndex", 4);
        updateRequestPayload.put("tierIndex", 14);
        updateRequestPayload.put("containerNo", "TGHU4321098");
        updateRequestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_UPDATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(updateRequestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot3rd = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'TGHU4321098',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNull(stowagePlanSlot3rd);
        Assertions.assertEquals(2, countDocument());
    }

    @Test
    void updateStowagePlanSlot_update_slot_with_non_existence_container_should_fail() {
        ObjectNode addSlotRequestPayload1 = objectMapper.createObjectNode();
        addSlotRequestPayload1.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        addSlotRequestPayload1.put("bayIndex", 1);
        addSlotRequestPayload1.put("type", "D");
        addSlotRequestPayload1.put("rowIndex", 4);
        addSlotRequestPayload1.put("tierIndex", 14);
        addSlotRequestPayload1.put("containerNo", "MSKU9012345");
        addSlotRequestPayload1.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequestPayload1))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot1st = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNotNull(stowagePlanSlot1st);
        Assertions.assertEquals("MSKU9012345", stowagePlanSlot1st.getContainerSnapshot().getContainerNo());

        ObjectNode addSlotRequestPayload2 = objectMapper.createObjectNode();
        addSlotRequestPayload2.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        addSlotRequestPayload2.put("bayIndex", 1);
        addSlotRequestPayload2.put("rowIndex", 4);
        addSlotRequestPayload2.put("tierIndex", 13);
        addSlotRequestPayload2.put("containerNo", "ABCD1234567");
        addSlotRequestPayload2.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_UPDATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequestPayload2))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot2nd = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 13,
                                    'containerSnapshot.containerNo': 'ABCD1234567',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        StowagePlanSlot stowagePlanSlot1stAgain = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNull(stowagePlanSlot2nd);
        Assertions.assertEquals(1, countDocument());
        Assertions.assertEquals("MSKU9012345", stowagePlanSlot1stAgain.getContainerSnapshot().getContainerNo());

    }

    @Test
    void deleteStowagePlanSlot() {
        ObjectNode addSlotRequestPayload = objectMapper.createObjectNode();
        addSlotRequestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        addSlotRequestPayload.put("bayIndex", 1);
        addSlotRequestPayload.put("type", "D");
        addSlotRequestPayload.put("rowIndex", 4);
        addSlotRequestPayload.put("tierIndex", 14);
        addSlotRequestPayload.put("containerNo", "MSKU9012345");
        addSlotRequestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNotNull(stowagePlanSlot);
        Assertions.assertEquals(1, countDocument());
        Assertions.assertEquals("MSKU9012345", stowagePlanSlot.getContainerSnapshot().getContainerNo());

        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_DELETE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        StowagePlanSlot stowagePlanSlotAgain = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNull(stowagePlanSlotAgain);
        Assertions.assertEquals(0, countDocument());

    }

    @Test
    void getStowagePlanSlot() {
        ObjectNode addSlotRequestPayload = objectMapper.createObjectNode();
        addSlotRequestPayload.put("stowagePlanId", "ecc44ca4-1193-4093-92fe-53a2d0ef8729");
        addSlotRequestPayload.put("bayIndex", 1);
        addSlotRequestPayload.put("type", "D");
        addSlotRequestPayload.put("rowIndex", 4);
        addSlotRequestPayload.put("tierIndex", 14);
        addSlotRequestPayload.put("containerNo", "MSKU9012345");
        addSlotRequestPayload.put("operationType", OperationType.DISCHARGE);
        restTestClient.post()
                .uri(STOWAGE_PLAN_SLOT_ADD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(addSlotRequestPayload))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
        StowagePlanSlot stowagePlanSlot = mongoTemplate.findOne(
                new BasicQuery("""
                                {
                                    'planId': 'ecc44ca4-1193-4093-92fe-53a2d0ef8729',
                                    'position.bayIndex': 1,
                                    'position.rowIndex': 4,
                                    'position.tierIndex': 14,
                                    'containerSnapshot.containerNo': 'MSKU9012345',
                                    'operationType': 'DS',
                                }
                        """), StowagePlanSlot.class);
        Assertions.assertNotNull(stowagePlanSlot);
        Assertions.assertEquals(1, countDocument());
        Assertions.assertEquals("MSKU9012345", stowagePlanSlot.getContainerSnapshot().getContainerNo());

        GetStowagePlanSlotResponse response = restTestClient.get()
                .uri(STOWAGE_PLAN_SLOT_V1_API_BASE_URL + "/ecc44ca4-1193-4093-92fe-53a2d0ef8729" +
                        "?bayIndex=1&rowIndex=4&tierIndex=14&&opType=DS")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetStowagePlanSlotResponse.class).returnResult().getResponseBody();

        Assertions.assertEquals(stowagePlanSlot.getPlanId(), response.getStowagePlanSlotDto().getPlanId());

        Assertions.assertEquals(stowagePlanSlot.getStowagePlanSlotPosition().getBayIndex(),
                response.getStowagePlanSlotDto().getStowagePlanSlotPositionDto().getBayIndex());

        Assertions.assertEquals(stowagePlanSlot.getStowagePlanSlotPosition().getRowIndex(),
                response.getStowagePlanSlotDto().getStowagePlanSlotPositionDto().getRowIndex());

        Assertions.assertEquals(stowagePlanSlot.getStowagePlanSlotPosition().getTierIndex(),
                response.getStowagePlanSlotDto().getStowagePlanSlotPositionDto().getTierIndex());

        Assertions.assertEquals(stowagePlanSlot.getContainerSnapshot().getContainerNo(),
                response.getStowagePlanSlotDto().getContainerSnapshotDto().getContainerNo());

        Assertions.assertEquals(stowagePlanSlot.getOperationType(),
                response.getStowagePlanSlotDto().getOperationType());

    }

    private Long countDocument() {
        return mongoTemplate.count(new Query(), StowagePlanSlot.class);
    }

}
