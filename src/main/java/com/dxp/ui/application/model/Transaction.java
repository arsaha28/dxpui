package com.dxp.ui.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private Double amount;
    private String merchantName;
    private String date;
    private String trxId;
    private String location;
    private String category;
    private String isRecurring ="Unknown";

    public String getIsRecurring() {
        return isRecurring.equalsIgnoreCase("true")||isRecurring.equalsIgnoreCase("Yes")?"CPA":"NA";
    }
}
