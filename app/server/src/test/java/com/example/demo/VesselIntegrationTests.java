package com.example.demo;

import com.example.demo.controller.api.dto.entity.vessel.structure.BayDto;
import com.example.demo.controller.api.dto.entity.vessel.structure.CellDto;
import com.example.demo.controller.api.dto.entity.vessel.structure.RowDto;
import com.example.demo.entity.stowage.slot.StowagePlanSlot;
import com.example.demo.repository.MongoDBCollection;
import com.example.demo.service.VesselStabilityService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
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

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Testcontainers
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VesselIntegrationTests {

    private static final String CONTAINER_PATH = "/tmp/test-data";

    private static final String VESSEL_V1_API_BASE_URL = "/api/v1/vessel";
    private static final String VESSEL_V1_API_GET_BAY = VESSEL_V1_API_BASE_URL + "/bay";
    private static final String VESSEL_V1_API_GET_ROW = VESSEL_V1_API_BASE_URL + "/row";
    private static final String VESSEL_V1_API_GET_CELL = VESSEL_V1_API_BASE_URL + "/cell";
    private static final String VESSEL_V1_API_GET_REEFER_FLAG = VESSEL_V1_API_BASE_URL + "/cell/reefer";

    @ServiceConnection
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withCopyFileToContainer(
            MountableFile.forClasspathResource("/test-data"), CONTAINER_PATH
    );

    private static final Logger log = LoggerFactory.getLogger(VesselIntegrationTests.class);

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
    void getBay() {
        BayDto bayDto = restTestClient.get()
                .uri(VESSEL_V1_API_GET_BAY + "?vesselId=default-vessel-s&bayIndex=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BayDto.class)
                .returnResult().getResponseBody();
        Assertions.assertNotNull(bayDto);
        Assertions.assertNotNull(bayDto.getRowDtoList());
        Assertions.assertNotNull(bayDto.getRowDtoList().get(9).getDeckDto());
        Assertions.assertNotNull(bayDto.getRowDtoList().get(9).getHoldDto());
        Assertions.assertEquals(129.8, bayDto.getLcg());
        Assertions.assertEquals(16, bayDto.getRowDtoList().size());
        Assertions.assertEquals(5, bayDto.getRowDtoList().get(9).getDeckDto().getCellDtoList().size());
        Assertions.assertEquals(3, bayDto.getRowDtoList().get(9).getHoldDto().getCellDtoList().size());
        Assertions.assertEquals(14, bayDto.getRowDtoList().get(9).getDeckDto().getCellDtoList().get(0).getTierIndex());
        Assertions.assertEquals(8, bayDto.getRowDtoList().get(9).getHoldDto().getCellDtoList().get(0).getTierIndex());
    }

    @Test
    void getRow() {
        RowDto rowDto = restTestClient.get()
                .uri(VESSEL_V1_API_GET_ROW + "?vesselId=default-vessel-s&bayIndex=1&rowIndex=9")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(RowDto.class)
                .returnResult().getResponseBody();
        Assertions.assertEquals(5, rowDto.getDeckDto().getCellDtoList().size());
        Assertions.assertEquals(3, rowDto.getHoldDto().getCellDtoList().size());
        Assertions.assertEquals(14, rowDto.getDeckDto().getCellDtoList().get(0).getTierIndex());
        Assertions.assertEquals(8, rowDto.getHoldDto().getCellDtoList().get(0).getTierIndex());
    }

    @Test
    void getCellOnDeck() {
        CellDto cellDto = restTestClient.get()
                .uri(VESSEL_V1_API_GET_CELL + "?vesselId=default-vessel-s&bayIndex=1&rowIndex=9&tierIndex=13&bayType=D")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CellDto.class)
                .returnResult().getResponseBody();
        Assertions.assertEquals(13, cellDto.getTierIndex());
    }

    @Test
    void getCellInHold() {
        CellDto cellDto = restTestClient.get()
                .uri(VESSEL_V1_API_GET_CELL + "?vesselId=default-vessel-s&bayIndex=1&rowIndex=9&tierIndex=6&bayType=H")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CellDto.class)
                .returnResult().getResponseBody();
        Assertions.assertEquals(6, cellDto.getTierIndex());
    }

    @Test
    void getAllowReeferFlag_false() {
        Boolean allowReefer = restTestClient.get()
                .uri(VESSEL_V1_API_GET_REEFER_FLAG + "?vesselId=default-vessel-s&bayIndex=1&rowIndex=9&tierIndex=6&bayType=H")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Boolean.class)
                .returnResult().getResponseBody();
        Assertions.assertFalse(allowReefer);
    }

    @Test
    void getAllowReeferFlag_true() {
        Boolean allowReefer = restTestClient.get()
                .uri(VESSEL_V1_API_GET_REEFER_FLAG + "?vesselId=default-vessel-s&bayIndex=8&rowIndex=2&tierIndex=11&bayType=D")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Boolean.class)
                .returnResult().getResponseBody();
        Assertions.assertTrue(allowReefer);
    }

}
