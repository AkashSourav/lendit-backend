package com.codimen.lendit.controller;

import com.codimen.lendit.dto.request.*;
import com.codimen.lendit.exception.DuplicateDataException;
import com.codimen.lendit.exception.InvalidDetailsException;
import com.codimen.lendit.service.ItemServices;
import com.codimen.lendit.utils.ResponseJsonUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemServices itemServices;

    @PostMapping(value = "/new",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-XSRF-TOKEN", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")
    })
    private ResponseEntity createItem(@RequestParam("file") MultipartFile files,
                                      @NotNull @NotEmpty @NotBlank @RequestParam String submitData) throws Exception{
        Map response=itemServices.createItem(files, submitData);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping(value = "/re-lend",produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity createRelendItem(@RequestBody ItemRelendDetailsRequest itemRelendDetailsRequest) throws DuplicateDataException {
        Map response=itemServices.createRelendItemDetails(itemRelendDetailsRequest);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping(value = "/get-all-items",produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> getAllItems(@RequestBody ItemsFilterRequest itemsFilterRequest){
       return new ResponseEntity<>(ResponseJsonUtil.getSuccessResponseJson(
                itemServices.findAllItems(itemsFilterRequest)),HttpStatus.OK);
    }

    @PostMapping(value = "/place-order",produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> placeOrder(@RequestBody OrderDetailsRequest orderDetailsRequest) throws DuplicateDataException, InvalidDetailsException {
        Map response=itemServices.placeOrder(orderDetailsRequest);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PutMapping(value = "/approve-order",produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> approveOrder(@RequestBody ApproveOrderRequest approveOrderRequest){
        Map response=itemServices.approveRequest(approveOrderRequest);
        return new ResponseEntity(response, HttpStatus.OK);
    }

}
