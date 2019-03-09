package com.codimen.lendit.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CreateItemRequest extends ItemDetailsRequest implements Serializable {

   private String itemName;
   private Long categoryId;

}
