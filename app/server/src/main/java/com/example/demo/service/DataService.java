package com.example.demo.service;

import com.example.demo.entity.imdg.*;
import com.example.demo.entity.vessel.profile.*;
import com.example.demo.entity.vessel.structure.*;
import com.example.demo.repository.MongoDBCollection;
import com.example.demo.repository.imdg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DataService {

    private static final Logger log = LoggerFactory.getLogger(com.example.demo.service.DataService.class);

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    private static final String SEED_LOCATION = "classpath:mongodb/seed/vessel*/";
    private static final String DG_SEED_LOCATION = "classpath:mongodb/seed/imdg/dgl-full.csv";
    private static final String DG_SEED_PATH = "mongodb/seed/imdg/";

    @Autowired
    private CompatibilityGroupRepository compatibilityGroupRepository;
    @Autowired
    private SegregationRuleRepository segregationRuleRepository;
    @Autowired
    private SegregationRuleCodeRepository segregationRuleCodeRepository;
    @Autowired
    private SegregationCodeRepository segregationCodeRepository;
    @Autowired
    private SegregationGroupCodeRepository segregationGroupCodeRepository;

    public DataService(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    public ImportResult importAll() throws IOException {
        mongoTemplate.dropCollection(MongoDBCollection.VESSEL_PROFILE);
        mongoTemplate.dropCollection(MongoDBCollection.VESSEL_STRUCTURE_BAY);
        ImportResult result = new ImportResult();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(SEED_LOCATION + "*.json");

        int vesselProfileCount = 0;
        int bayCount = 0;

        for (Resource resource : resources) {
            try (InputStream is = resource.getInputStream()) {
                JsonNode root = objectMapper.readTree(is);

                // Determine document type based on content
                if (!root.has("shipInfo")) {
                    Bay bay = parseBay(root);
                    mongoTemplate.save(bay, MongoDBCollection.VESSEL_STRUCTURE_BAY);
                    bayCount++;
                } else {
                    VesselProfile profile = parseVesselProfile(root);
                    mongoTemplate.save(profile, MongoDBCollection.VESSEL_PROFILE);
                    vesselProfileCount++;
                }
            }
        }

        result.vesselProfiles = vesselProfileCount;
        result.bays = bayCount;
        result.total = resources.length;
        return result;
    }

    private VesselProfile parseVesselProfile(JsonNode root) {
        VesselProfile profile = new VesselProfile();
        profile.setVesselName(root.get("vesselName").asText());
        profile.setVesselId(root.get("vesselName").asText());

        // Parse shipInfo
        JsonNode shipInfoNode = root.get("shipInfo");
        if (shipInfoNode != null) {
            ShipInfoSnapshot shipInfoSnapshot = new ShipInfoSnapshot();
            shipInfoSnapshot.setBayCount(shipInfoNode.get("bays").asInt());
            shipInfoSnapshot.setRowCount(shipInfoNode.get("rows").asInt());
            shipInfoSnapshot.setTierCount(shipInfoNode.get("tiers").asInt());
            if (shipInfoNode.has("tcgTolerance")) {
                shipInfoSnapshot.setTcgTolerance(shipInfoNode.get("tcgTolerance").asDouble());
            }
            profile.setShipInfo(shipInfoSnapshot);
        }

        // Parse hydroPoints
        JsonNode hydroPointsNode = root.get("hydroPoints");
        if (hydroPointsNode != null && hydroPointsNode.isArray()) {
            List<HydroPoint> hydroPoints = new ArrayList<>();
            for (JsonNode hpNode : hydroPointsNode) {
                HydroPoint hp = new HydroPoint();
                hp.setDisplacement(hpNode.get("displacement").asInt());
                hp.setMinLcg(hpNode.get("minLcg").asDouble());
                hp.setMaxLcg(hpNode.get("maxLcg").asDouble());
                hp.setMetacenter(hpNode.get("metacenter").asDouble());
                hydroPoints.add(hp);
            }
            profile.setHydroPoints(hydroPoints);
        }

        // Parse tanks
        JsonNode tanksNode = root.get("tanks");
        if (tanksNode != null && tanksNode.isArray()) {
            List<Tank> tanks = new ArrayList<>();
            for (JsonNode tankNode : tanksNode) {
                Tank tank = new Tank();
                tank.setCapTon(tankNode.get("capTon").asInt());
                tank.setLcg(tankNode.get("lcg").asInt());
                tank.setTcg(tankNode.get("tcg").asInt());
                tank.setVcgEmpty(tankNode.get("vcgEmpty").asInt());
                tank.setVcgFull(tankNode.get("vcgFull").asInt());

                JsonNode bayCoveragesNode = tankNode.get("bayCoverages");
                if (bayCoveragesNode != null && bayCoveragesNode.isArray()) {
                    BayCoverage[] bayCoverages = new BayCoverage[bayCoveragesNode.size()];
                    for (Integer i = 0; i < bayCoveragesNode.size(); i++) {
                        JsonNode bcNode = bayCoveragesNode.get(i);
                        BayCoverage bc = new BayCoverage();
                        bc.setBayIndex(bcNode.get("bayIdxZeroBased").asInt());
                        bc.setCoverageRatio(bcNode.get("coverageRatio").asDouble());
                        bayCoverages[i] = bc;
                    }
                    tank.setBayCoverages(Arrays.stream(bayCoverages).toList());
                }
                tanks.add(tank);
            }
            profile.setTanks(tanks);
        }

        return profile;
    }

    private Bay parseBay(JsonNode root) {
        Bay bay = new Bay();
        bay.setVesselName(root.get("vesselName").asText());

        // vesselId may be missing in bay JSON files; derive from vesselName if not present
        // e.g., "vessel-s" -> "default-vessel-s"
        JsonNode vesselIdNode = root.get("vesselId");
        if (vesselIdNode != null) {
            bay.setVesselId(vesselIdNode.asText());
        } else {
            String vesselName = root.get("vesselName").asText();
            bay.setVesselId("default-" + vesselName);
        }

        bay.setBayIndex(root.get("index").asInt());
        bay.setLcg(root.get("lcg").asDouble());
        bay.setMinShear(root.get("minShear").asDouble());
        bay.setMaxShear(root.get("maxShear").asDouble());
        bay.setMaxBending(root.get("maxBending").asDouble());
        bay.setConstWeight(root.get("constWeight").asDouble());

        // JSON field uses "constWeighVcg" (typo in seed data) rather than "constWeightVcg"
        JsonNode constWeightVcgNode = root.get("constWeighVcg");
        if (constWeightVcgNode == null) {
            constWeightVcgNode = root.get("constWeightVcg");
        }
        if (constWeightVcgNode != null) {
            bay.setConstWeightVcg(constWeightVcgNode.asDouble());
        }

        // Parse buoyancyPoints
        JsonNode buoyancyPointsNode = root.get("buoyancyPoints");
        if (buoyancyPointsNode != null && buoyancyPointsNode.isArray()) {
            double[] buoyancyPoints = new double[buoyancyPointsNode.size()];
            for (Integer i = 0; i < buoyancyPointsNode.size(); i++) {
                buoyancyPoints[i] = buoyancyPointsNode.get(i).asDouble();
            }
            bay.setBuoyancyPoints(Arrays.stream(buoyancyPoints).boxed().toList());
        }

        // Parse rows
        JsonNode rowsNode = root.get("rows");
        if (rowsNode != null && rowsNode.isArray()) {
            List<Row> rows = new ArrayList<>();
            for (JsonNode rowNode : rowsNode) {
                Row row = new Row();
                row.setRowIndex(rowNode.get("index").asInt());

                // tcg
                if (rowNode.has("tcg")) {
                    row.setTcg(rowNode.get("tcg").asDouble());
                }

                // lcg (optional, may not be present in all row nodes)
                if (rowNode.has("lcg")) {
                    row.setLcg(rowNode.get("lcg").asDouble());
                }

                // identifier
                if (rowNode.has("identifier")) {
                    row.setIdentifier(rowNode.get("identifier").asInt());
                }

                // maxHeight
                if (rowNode.has("maxHeight")) {
                    row.setMaxHeight(rowNode.get("maxHeight").asDouble());
                }

                // maxWeight20
                if (rowNode.has("maxWeight20")) {
                    row.setMaxWeight20(rowNode.get("maxWeight20").asDouble());
                }

                // maxWeight40
                if (rowNode.has("maxWeight40")) {
                    row.setMaxWeight40(rowNode.get("maxWeight40").asDouble());
                }

                // vcg
                if (rowNode.has("vcg")) {
                    row.setVcg(rowNode.get("vcg").asDouble());
                }

                // cells
                JsonNode cellsNode = rowNode.get("cells");
//                if (cellsNode != null && cellsNode.isArray()) {
//                    row.setCells(parseCells(cellsNode));
//                }

                // Parse hold (optional, may be nested)
                JsonNode holdNode = rowNode.get("hold");
                if (holdNode != null && !holdNode.isNull()) {
                    row.setHold(parseHold(holdNode));
                }

                // Parse deck (optional, may be nested)
                JsonNode deckNode = rowNode.get("deck");
                if (deckNode != null && !deckNode.isNull()) {
                    row.setDeck(parseDeck(deckNode));
                }

                rows.add(row);
            }
            bay.setRows(rows);
        }

        return bay;
    }

    private Hold parseHold(JsonNode node) {
        Hold hold = new Hold();
        hold.setIdentifier(node.get("identifier").asInt());
        hold.setMaxHeight(node.get("maxHeight").asDouble());
        hold.setMaxWeight20(node.get("maxWeight20").asDouble());
        hold.setMaxWeight40(node.get("maxWeight40").asDouble());
        hold.setVcg(node.get("vcg").asDouble());

        JsonNode cellsNode = node.get("cells");
        if (cellsNode != null && cellsNode.isArray()) {
            hold.setCells(parseCells(cellsNode));
        }
        return hold;
    }

    private Deck parseDeck(JsonNode node) {
        Deck deck = new Deck();
        deck.setIdentifier(node.get("identifier").asInt());
        deck.setMaxHeight(node.get("maxHeight").asDouble());
        deck.setMaxWeight20(node.get("maxWeight20").asDouble());
        deck.setMaxWeight40(node.get("maxWeight40").asDouble());
        deck.setVcg(node.get("vcg").asDouble());

        JsonNode cellsNode = node.get("cells");
        if (cellsNode != null && cellsNode.isArray()) {
            deck.setCells(parseCells(cellsNode));
        }
        return deck;
    }

    private List<Cell> parseCells(JsonNode cellsNode) {
        List<Cell> cells = new ArrayList<>();
        for (JsonNode cellNode : cellsNode) {
            Cell cell = new Cell();
            cell.setTierIndex(cellNode.get("tier").asInt());
            if (cellNode.has("isReefer")) {
                cell.setAllowReefer(cellNode.get("isReefer").asBoolean());
            }
            cells.add(cell);
        }
        return cells;
    }

    public static class ImportResult {
        private Integer vesselProfiles;
        private Integer bays;
        private Integer total;

        public Integer getVesselProfiles() {
            return vesselProfiles;
        }

        public Integer getBays() {
            return bays;
        }

        public Integer getTotal() {
            return total;
        }

        @Override
        public String toString() {
            return "ImportResult{" +
                    "vesselProfiles=" + vesselProfiles +
                    ", bays=" + bays +
                    ", total=" + total +
                    '}';
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public int importDangerousGoods() throws IOException {
        mongoTemplate.remove(new Query(), MongoDBCollection.DANGEROUS_GOODS);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(DG_SEED_LOCATION);

        List<DangerousGoods> records = new ArrayList<>();

        try (InputStream is = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            // Skip header line
            String header = reader.readLine();
            if (header == null) {
                return 0;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                DangerousGoods dg = parseLine(line);
                String regex = "^(1\\.[1-6])([A-S])$";
                Pattern pattern = Pattern.compile(regex);
                if (dg.getDivision() != null) {
                    Matcher matcher = pattern.matcher(dg.getDivision());
                    if (matcher.matches()) {
                        String division = matcher.group(1);
                        String compatabilityGroup = matcher.group(2);
                        dg.setDivision(division);
                        dg.setCompatibilityGroup(compatabilityGroup);
                        records.add(dg);
                    }
                }
                records.add(dg);
            }
        }
        mongoTemplate.insertAll(records.stream().filter(distinctByKey(DangerousGoods::getUnNo)).toList());
        return records.size();
    }

    private DangerousGoods parseLine(String line) {
        // CSV format: "unNo","psn","classOrDivision","subsidiaryHazard","segregation"
        // Fields may contain commas inside quotes, so we parse carefully
        List<String> fields = parseCsvLine(line);

        DangerousGoods dg = new DangerousGoods();

        if (fields.size() > 0) {
            dg.setUnNo(fields.get(0));
        }
        if (fields.size() > 1) {
            dg.setPsn(fields.get(1));
        }
        if (fields.size() > 2 && !fields.get(2).isEmpty()) {
            if (fields.get(2).contains(".")) {
                dg.setDivision(fields.get(2));
                dg.setDgClass(fields.get(2).substring(0, fields.get(2).indexOf('.')));
            } else {
                dg.setDgClass(fields.get(2));
            }
        }
        if (fields.size() > 3 && !fields.get(3).isEmpty()) {
            dg.setSubsidiaryHazard(fields.get(3));
        }
        if (fields.size() > 4) {
            String segValue = fields.get(4);
            List<String> segregation = parseSegregation(segValue);
            dg.setSegregation(segregation);
        }

        return dg;
    }

    private List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());

        return fields;
    }

    private List<String> parseSegregation(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // Segregation values can be space-separated ("SGG18 SG35") or comma-separated ("SGG1, SG36, SG49")
        // Split by both commas and spaces
        return Arrays.stream(value.split("[,\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public List<CompatibilityGroup> importCompatibilityGroups() throws IOException {
        List<CompatibilityGroup> groups = readJsonFile(DG_SEED_PATH + "compatibility-groups.json",
                new TypeReference<List<CompatibilityGroup>>() {
                });
        mongoTemplate.remove(new Query(), MongoDBCollection.COMPATIBILITY_GROUP);
        mongoTemplate.insertAll(groups);
        log.info("Imported {} compatibility groups", groups.size());
        return groups;
    }

    public List<SegregationRule> importIMDGSegregation() throws IOException {
        List<SegregationRule> segregation = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(DG_SEED_PATH + "segregation-rules.json");
        try (InputStream is = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    SegregationRule rule = new SegregationRule();
                    rule.setDivision(firstAvailableText(node, "division", "hazardClass"));
                    JsonNode rulesNode = firstAvailableNode(node, "rules", "requirement");
                    Map<String, String> rules = objectMapper.convertValue(
                            rulesNode,
                            new TypeReference<Map<String, String>>() {
                            }
                    );
                    rule.setRules(rules);
                    segregation.add(rule);
                }
            }
        }
        mongoTemplate.remove(new Query(), MongoDBCollection.SEGREGATION_RULE);
        mongoTemplate.insertAll(segregation);
        log.info("Imported {} IMDG segregation", segregation.size());
        return segregation;
    }

    public List<SegregationRuleCode> importIMDGSegregationRequirementCodes() throws IOException {
        List<SegregationRuleCode> codes = readJsonFile(DG_SEED_PATH + "segregation-rule-codes.json",
                new TypeReference<List<SegregationRuleCode>>() {
                });
        mongoTemplate.remove(new Query(), MongoDBCollection.SEGREGATION_RULE_CODE);
        mongoTemplate.insertAll(codes);
        log.info("Imported {} IMDG segregation requirement codes", codes.size());
        return codes;
    }

    public List<SegregationCode> importSegregationCodes() throws IOException {
        List<SegregationCode> codes = readJsonFile(DG_SEED_PATH + "segregation-codes.json",
                new TypeReference<List<SegregationCode>>() {
                });
        mongoTemplate.remove(new Query(), MongoDBCollection.SEGREGATION_CODE);
        mongoTemplate.insertAll(codes);
        log.info("Imported {} segregation codes", codes.size());
        return codes;
    }

    public List<SegregationGroupCode> importSegregationGroupCodes() throws IOException {
        List<SegregationGroupCode> codes = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(DG_SEED_PATH + "segregation-group-codes.json");
        try (InputStream is = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    SegregationGroupCode code = new SegregationGroupCode();
                    code.setCode(firstAvailableText(node, "code", "segregationGroupCode"));
                    code.setGroup(firstAvailableText(node, "group", "groupNumber"));
                    code.setDescription(firstAvailableText(node, "description"));
                    codes.add(code);
                }
            }
        }
        mongoTemplate.remove(new Query(), MongoDBCollection.SEGREGATION_GROUP_CODE);
        mongoTemplate.insertAll(codes);
        log.info("Imported {} segregation group codes", codes.size());
        return codes;
    }

    public List<HazardDivisionDefinition> importDivisionDefinition() throws IOException {
        List<HazardDivisionDefinition> definitions = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(DG_SEED_PATH + "hazard-division-definition.json");
        try (InputStream is = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    HazardDivisionDefinition definition = new HazardDivisionDefinition();
                    definition.setHazardClass(firstAvailableText(node, "hazardClass"));
                    definition.setDivision(firstAvailableText(node, "division"));
                    definition.setSubstance(firstAvailableText(node, "substance"));
                    definitions.add(definition);
                }
            }
        }
        mongoTemplate.remove(new Query(), MongoDBCollection.HAZARD_DIVISION_DEFINITION);
        mongoTemplate.insertAll(definitions);
        log.info("Imported {} hazard class definitions", definitions.size());
        return definitions;
    }

    public List<HazardClassDefinition> importHazardClassDefinitions() throws IOException {
        List<HazardClassDefinition> definitions = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(DG_SEED_PATH + "hazard-class-definiton.json");
        try (InputStream is = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    HazardClassDefinition definition = new HazardClassDefinition();
                    definition.setHazardClass(firstAvailableText(node, "hazardClass"));
                    definition.setHazardClass(firstAvailableText(node, "hazardClass"));
                    definition.setSubstance(firstAvailableText(node, "substance"));
                    definitions.add(definition);
                }
            }
        }
        mongoTemplate.remove(new Query(), MongoDBCollection.HAZARD_CLASS_DEFINITION);
        mongoTemplate.insertAll(definitions);
        log.info("Imported {} hazard class definitions", definitions.size());
        return definitions;
    }

    private <T> List<T> readJsonFile(String classpath, TypeReference<List<T>> typeReference) throws IOException {
        ClassPathResource resource = new ClassPathResource(classpath);
        try (InputStream is = resource.getInputStream()) {
            return objectMapper.readValue(is, typeReference);
        }
    }

    private String firstAvailableText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode field = node.path(fieldName);
            if (!field.isMissingNode() && !field.isNull()) {
                return field.asText();
            }
        }
        return null;
    }

    private JsonNode firstAvailableNode(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode field = node.path(fieldName);
            if (!field.isMissingNode() && !field.isNull()) {
                return field;
            }
        }
        return objectMapper.createObjectNode();
    }
}