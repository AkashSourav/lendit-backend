package com.codimen.lendit.controller;

import com.codimen.lendit.dto.request.ItemsFilterRequest;
import com.codimen.lendit.service.ItemCategoryService;
import com.codimen.lendit.utils.ResponseJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController()
@RequestMapping(value = "/api/category/")
public class ItemCategoryController {


    @Autowired
    private ItemCategoryService itemCategoryService;

    @GetMapping(value = "/get-all-category",produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> getAllItems(){

        return new ResponseEntity<>(ResponseJsonUtil.getSuccessResponseJson(
                itemCategoryService.findAllCategory()), HttpStatus.OK);
    }
}
