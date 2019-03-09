package com.codimen.lendit.controller;

import com.codimen.lendit.dto.request.CreateItemRequest;
import com.codimen.lendit.service.ItemServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
