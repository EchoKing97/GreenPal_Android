package com.example.firsttest.plantcare.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OperationRecord {
    private int id;
    private int userId;
    private int plantId;
    private String plantName;
    private String operation;
    private Date operationTime;

    public OperationRecord(String operation, Date operationTime, String plantName, int plantId, int userId) {
        this.operation = operation;
        this.operationTime = operationTime;
        this.plantName = plantName;
        this.plantId = plantId;
        this.userId = userId;

    }
}
