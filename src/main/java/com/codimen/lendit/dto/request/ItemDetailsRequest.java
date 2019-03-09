package com.codimen.lendit.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ItemDetailsRequest implements Serializable {

    private String address;
    private Date lendStartDate;
    private Date lendEndDate;
    private boolean beedingType;
    private int minPrice;
    private int maxPrice;
    private int flatPrice;

}
