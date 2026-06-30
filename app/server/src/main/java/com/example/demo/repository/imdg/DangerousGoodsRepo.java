package com.example.demo.repository.imdg;

import com.example.demo.entity.imdg.DangerousGoods;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DangerousGoodsRepo extends MongoRepository<DangerousGoods, String> {

    @Query("{ 'unNo' : '?0' }")
    Optional<DangerousGoods> findByUnNo(String unNo);

    @Query("{ 'unNo' : { $in: ?0 } }")
    List<DangerousGoods> findByUnNoIn(List<String> unNos);

    @Query("{ 'dgClass' : { $in: ?0 } }")
    List<DangerousGoods> findByClassesIn(List<String> dgClasses);

    @Query("{ 'division' : { $in: ?0 } }")
    List<DangerousGoods> findByDivisionIn(List<String> divisions);

}
