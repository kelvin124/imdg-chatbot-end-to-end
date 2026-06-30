package com.example.demo.event.listener;

import com.example.demo.entity.container.Container;
import com.example.demo.entity.imdg.*;
import com.example.demo.entity.vessel.profile.VesselProfile;
import com.example.demo.entity.vessel.structure.Bay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.stereotype.Component;

@Component
public class MongoDBInitializer {

    @Autowired
    private MongoTemplate mongoTemplate;

    @EventListener(ContextRefreshedEvent.class)
    public void initIndicesAfterStartup() {
        this.createIndexesForClass(VesselProfile.class);
        this.createIndexesForClass(Bay.class);
        this.createIndexesForClass(Container.class);
        this.createIndexesForClass(DangerousGoods.class);
        this.createIndexesForClass(CompatibilityGroup.class);
        this.createIndexesForClass(SegregationRule.class);
        this.createIndexesForClass(SegregationRuleCode.class);
        this.createIndexesForClass(SegregationCode.class);
        this.createIndexesForClass(SegregationGroupCode.class);
        this.createIndexesForClass(SegregationGroupCode.class);
        this.createIndexesForClass(HazardClassDefinition.class);
        this.createIndexesForClass(HazardDivisionDefinition.class);
    }

    private void createIndexesForClass(Class<?> clazz) {

        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate
                .getConverter().getMappingContext();
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
        IndexOperations indexOps = mongoTemplate.indexOps(clazz);
        resolver.resolveIndexFor(clazz).forEach(indexOps::createIndex);
    }


}
