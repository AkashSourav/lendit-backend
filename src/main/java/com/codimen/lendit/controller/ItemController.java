package com.codimen.lendit.controller;

import com.codimen.lendit.dto.request.CreateItemRequest;
import com.codimen.lendit.dto.request.ItemRelendDetailsRequest;
import com.codimen.lendit.exception.DuplicateDataException;
import com.codimen.lendit.dto.request.ItemsFilterRequest;
import com.codimen.lendit.service.ItemServices;
import com.codimen.lendit.utils.ResponseJsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemServices itemServices;

    @PostMapping(value = "/new",produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity createItem(@RequestBody CreateItemRequest createItemRequest){
        Map response=itemServices.createItem(createItemRequest);
        return new ResponseEntity(response, HttpStatus.OK);
    }


    @PostMapping(value = "/relend",produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity createRelendItem(@RequestBody ItemRelendDetailsRequest itemRelendDetailsRequest) throws DuplicateDataException {
        Map response=itemServices.createRelendItemDetails(itemRelendDetailsRequest);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping(value = "/get-all-items",produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> getAllItems(@RequestBody ItemsFilterRequest itemsFilterRequest){

       return new ResponseEntity<>(ResponseJsonUtil.getSuccessResponseJson(
                itemServices.findAllItems(itemsFilterRequest)),HttpStatus.OK);
    }


}
