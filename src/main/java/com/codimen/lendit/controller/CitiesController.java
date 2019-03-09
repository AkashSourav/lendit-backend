package com.codimen.lendit.controller;


import com.codimen.lendit.repository.CitiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController()
@RequestMapping(value = "/api/cities/")
public class CitiesController {

    @Autowired
    private CitiesRepository citiesRepository;

    @RequestMapping(value = "get-all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getALlCities() {

        Map response = new HashMap();
        response.put("cities", citiesRepository.findAll());
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}

