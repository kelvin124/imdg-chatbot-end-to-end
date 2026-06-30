package com.example.demo.repository.bay;

import com.example.demo.entity.vessel.structure.Cell;
import com.example.demo.entity.vessel.structure.Row;

import java.util.Optional;

public interface BayCustomQuery {

    Optional<Row> findRow(String vesselId, int bayIndex, int rowIndex);

    Optional<Cell> findCell(String vesselId, int bayIndex, int rowIndex, int tierIndex, String bayType);

}
