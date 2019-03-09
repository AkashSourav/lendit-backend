package com.codimen.lendit.service;

import com.codimen.lendit.model.ItemCategory;
import com.codimen.lendit.repository.ItemCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class ItemCategoryService {

    @Autowired
    private ItemCategoryRepository itemCategoryRepository;

    public HashMap findAllCategory() {

        HashMap response = new HashMap();
        List<ItemCategory> itemCategories = itemCategoryRepository.findAll();
        if(itemCategories != null){
            itemCategories.forEach( itemCategory -> itemCategory.setItemsList(null));
        }
        response.put("categories", itemCategories);
        return response;
    }
}
