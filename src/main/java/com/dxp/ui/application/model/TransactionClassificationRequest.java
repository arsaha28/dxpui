package com.dxp.ui.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionClassificationRequest {

    private String amount;
    private String merchantname;
    private String location;
    private String mcccode;
}
