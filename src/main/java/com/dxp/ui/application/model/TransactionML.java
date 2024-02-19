package com.dxp.ui.application.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionML implements  Cloneable{

    private String amount;
    private String merchantName;
    private String date;
    private String trxId;
    private String location;
    private String category;
    private String mccCode;

    @Override
    public String toString() {
        return "" +
                "" + amount +
                ", " + merchantName  +
                ", " + location  +
                ", " + category  +
                ", " + mccCode  ;
    }

    private String isRecurring ="Unknown";

    public TransactionML(String amount, String merchantName,String mccCode ,String location,String category) {
        this.amount = amount;
        this.merchantName = merchantName;
        this.mccCode = mccCode;
        this.location = location;
        this.category = category;
    }


    public String getIsRecurring() {
        return isRecurring.equalsIgnoreCase("true")||isRecurring.equalsIgnoreCase("Yes")?"CPA":"NA";
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
