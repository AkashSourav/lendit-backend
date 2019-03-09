package com.codimen.lendit.dto.request;

import lombok.Data;

@Data
public class ApproveOrderRequest {

    private Long itemDetailsId;
    private Long itemPriceDetailsId;

}
