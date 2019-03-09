package com.codimen.lendit.service;

import com.codimen.lendit.dto.request.CreateItemRequest;
import com.codimen.lendit.dto.request.ItemDetailsRequest;
import com.codimen.lendit.dto.request.ItemRelendDetailsRequest;
import com.codimen.lendit.exception.DuplicateDataException;
import com.codimen.lendit.model.Item;
import com.codimen.lendit.model.ItemDetails;
import com.codimen.lendit.repository.ItemDetailsRepository;
import com.codimen.lendit.repository.ItemRepository;
import com.codimen.lendit.utils.ResponseJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class ItemServices {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Transactional(rollbackOn = Throwable.class)
    public Map createItem(CreateItemRequest createItemRequest)
    {
        log.info("<=== Started item creation for owner ===>");
        Item newItem = new Item();
        newItem.setItemCategoryId(createItemRequest.getCategoryId());
        newItem.setItemName(createItemRequest.getItemName());
        newItem.setLandStatus(false);
        newItem.setOwnerId(1l);
        itemRepository.save(newItem);
        ItemRelendDetailsRequest itemRelendDetailsRequest=new ItemRelendDetailsRequest();
        itemRelendDetailsRequest.setId(newItem.getId());
        itemRelendDetailsRequest.setAddress(createItemRequest.getAddress());
        itemRelendDetailsRequest.setBeedingType(createItemRequest.isBeedingType());
        itemRelendDetailsRequest.setFlatPrice(createItemRequest.getFlatPrice());
        itemRelendDetailsRequest.setLendEndDate(createItemRequest.getLendEndDate());
        itemRelendDetailsRequest.setLendStartDate(createItemRequest.getLendStartDate());
        itemRelendDetailsRequest.setMaxPrice(createItemRequest.getMaxPrice());
        itemRelendDetailsRequest.setMinPrice(createItemRequest.getMinPrice());
        this.createItemDetails(itemRelendDetailsRequest);
        log.info("<=== Completed item creation for owner ===>");
        return ResponseJsonUtil.getSuccessResponseJson();
    }


    private void createItemDetails(ItemRelendDetailsRequest itemDetailsRequest){
        log.info("<=== Started creating item details ===>");
        ItemDetails itemDetails=new ItemDetails();
        itemDetails.setAddress(itemDetailsRequest.getAddress());
        itemDetails.setBeedingType(itemDetailsRequest.isBeedingType());
        if(itemDetailsRequest.isBeedingType()){
            itemDetails.setMaxPrice(itemDetailsRequest.getMaxPrice());
            itemDetails.setMinPrice(itemDetailsRequest.getMinPrice());
        }
        else{
            itemDetails.setFlatPrice(itemDetailsRequest.getFlatPrice());
        }
        itemDetails.setItemId(itemDetailsRequest.getId());
        itemDetails.setLendStartDate(itemDetailsRequest.getLendStartDate());
        itemDetails.setLendEndDate(itemDetailsRequest.getLendEndDate());
        itemDetails.setSoldStatus(false);
        itemDetailsRepository.save(itemDetails);
        log.info("<=== Completed creating item details ===>");
    }

    public Map createRelendItemDetails(ItemRelendDetailsRequest itemRelendDetailsRequest) throws DuplicateDataException {
        log.info("<=== Started creating relend item details ===>");

        ItemDetails itemDetails=itemDetailsRepository.findOneByItemIdAndLendEndDateGreaterThan(itemRelendDetailsRequest.getId(),new Date());
        if(itemDetails != null){
                log.error("<=== Item {} all ready present for lend ===>", itemRelendDetailsRequest.getId());
                throw new DuplicateDataException("Item already present for lend");
        }
        this.createItemDetails(itemRelendDetailsRequest);
        log.info("<=== Completed creating relend item details ===>");
        return ResponseJsonUtil.getSuccessResponseJson();
    }

}
