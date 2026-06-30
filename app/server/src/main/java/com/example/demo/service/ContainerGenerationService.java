package com.example.demo.service;

import com.example.demo.entity.container.Container;
import com.example.demo.entity.imdg.DangerousGoods;
import com.example.demo.repository.MongoDBCollection;
import com.example.demo.repository.container.ContainerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ContainerGenerationService {

    private final ContainerRepo repository;
    private final MongoTemplate mongoTemplate;
    private final Random random = new Random();

    // Sample data for generation
    private static final String[] PORTS = {"HKG", "SHA", "NGB", "SIN", "ROT", "LGB", "HAM", "ANT", "JFK", "LHR"};
    private static final String[] CARGO_TYPES = {"Electronics", "Clothing", "Machinery", "Food", "Furniture", "Chemicals", "Toys", "Books", null};

    public ContainerGenerationService(@Autowired ContainerRepo repository, @Autowired MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public void generateAndSave(Integer count) {
        repository.deleteAll();

        // Fetch Dangerous Goods data from MongoDB
        List<DangerousGoods> allDg = mongoTemplate.findAll(DangerousGoods.class, MongoDBCollection.DANGEROUS_GOODS);

        // At least 30% DG (but limited by available DG records)
        int dgCount = Math.min((int) Math.ceil(count * 0.3), allDg.size());
        // At least 30% reefer
        int reeferCount = (int) Math.ceil(count * 0.3);

        // Shuffle indices for random distribution of DG and reefer
        List<Integer> dgIndices = new ArrayList<>();
        List<Integer> reeferIndices = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            dgIndices.add(i);
            reeferIndices.add(i);
        }
        Collections.shuffle(dgIndices, random);
        Collections.shuffle(reeferIndices, random);

        // Track which indices get DG
        boolean[] isDgContainer = new boolean[count];
        for (int i = 0; i < dgCount; i++) {
            isDgContainer[dgIndices.get(i)] = true;
        }

        // Track which indices get reefer
        boolean[] isReeferContainer = new boolean[count];
        for (int i = 0; i < reeferCount; i++) {
            isReeferContainer[reeferIndices.get(i)] = true;
        }

        List<Container> containers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            DangerousGoods dg = isDgContainer[i] ? allDg.get(random.nextInt(allDg.size())) : null;
            containers.add(generateRandomContainer(dg, isReeferContainer[i]));
        }
        repository.saveAll(containers);
        System.out.println("Generated and saved " + count + " containers. (DG: " + dgCount + ", Reefer: " + reeferCount + ")");
    }

    private Container generateRandomContainer(DangerousGoods dg, boolean isReefer) {
        Container c = new Container();
        c.setContainerNo(generateContainerNumber());
        c.setWeight(round(2000 + random.nextDouble() * 25000, 1)); // 2t to 27t
        c.setHeight(random.nextBoolean() ? 2.59 : 2.89); // standard (8'6") vs high cube (9'6")
        c.setLength(random.nextDouble() < 0.7 ? 20 : 40); // 70% 20ft, 30% 40ft
        c.setPortOfLoading(randomElement(PORTS));
        c.setPortOfDischarge(randomElement(PORTS));

        if (dg != null) {
            c.setIsDg(true);
            c.setImdgClass(dg.getDgClass());
            c.setUndgCode(dg.getUnNo());
        } else {
            c.setIsDg(false);
        }

        c.setIsOutOfGauge(random.nextDouble() < 0.02); // 2% out of gauge
        c.setIsHighCube(c.getHeight() > 2.7); // if height > 2.7m -> high cube
        if (c.getIsDg()) {
            mongoTemplate.find(query(where("unNo").is(c.getUndgCode())), DangerousGoods.class, MongoDBCollection.DANGEROUS_GOODS)
                    .stream().findFirst().ifPresent(dgRecord -> c.setCargo(dgRecord.getPsn()));
        } else {
            c.setCargo(randomElement(CARGO_TYPES));
        }

        // Always set isReefer explicitly: true for reefer, false otherwise (never null)
        c.setIsReefer(isReefer);

        return c;
    }

    private String generateContainerNumber() {
        // Format: 4 letters + 7 digits (e.g., MSCU1234567)
        String letters = "";
        for (Integer i = 0; i < 4; i++) {
            letters += (char) ('A' + random.nextInt(26));
        }
        int digits = 1_000_000 + random.nextInt(9_000_000);
        return letters + digits;
    }

    private String randomElement(String[] array) {
        return array[random.nextInt(array.length)];
    }

    private Double round(Double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}