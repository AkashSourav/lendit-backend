package com.codimen.lendit.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemsResponse implements Serializable {

    private static final long serialVersionUID = 4219113406398434445L;

    private Long itemDetailsId;
    private Long itemId;

    private String itemName;
    private Long itemCategoryId;
    private Long ownerId;
    private Date lastLendDate;
    private boolean landStatus;
    private String pictures;

    private boolean soldStatus=false;
    private int soldPrice;
    private String address;
    private Date lendStartDate;
    private Date lendEndDate;
    private boolean beedingType;
    private int minPrice;
    private int maxPrice;
    private int flatPrice;
}
