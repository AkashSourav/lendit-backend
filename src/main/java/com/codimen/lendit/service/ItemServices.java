package com.codimen.lendit.service;

import com.codimen.lendit.dto.request.CreateItemRequest;
import com.codimen.lendit.model.Item;
import com.codimen.lendit.model.ItemDetails;
import com.codimen.lendit.repository.ItemDetailsRepository;
import com.codimen.lendit.repository.ItemRepository;
import com.codimen.lendit.utils.ResponseJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
        log.info("Started item creation for owner");
        Item newItem = new Item();
        newItem.setItemCategoryId(createItemRequest.getCategoryId());
        newItem.setItemName(createItemRequest.getItemName());
        newItem.setLandStatus(false);
        newItem.setOwnerId(1l);
        itemRepository.save(newItem);

        ItemDetails itemDetails=new ItemDetails();
        itemDetails.setAddress(createItemRequest.getAddress());
        itemDetails.setBeedingType(createItemRequest.isBeedingType());
        if(createItemRequest.isBeedingType()){
            itemDetails.setMaxPrice(createItemRequest.getMaxPrice());
            itemDetails.setMinPrice(createItemRequest.getMinPrice());
        }
        else{
            itemDetails.setFlatPrice(createItemRequest.getFlatPrice());
        }
        itemDetails.setItemId(newItem.getId());
        itemDetails.setLendStartDate(createItemRequest.getLendStartDate());
        itemDetails.setLendEndDate(createItemRequest.getLendEndDate());
        itemDetails.setSoldStatus(false);
        itemDetailsRepository.save(itemDetails);
        log.info("Completed item creation for owner");
        return ResponseJsonUtil.getSuccessResponseJson();
    }

}
