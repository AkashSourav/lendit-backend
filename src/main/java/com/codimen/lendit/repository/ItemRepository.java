package com.codimen.lendit.repository;

import com.codimen.lendit.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long> {



    Item findOneByIdAndLandStatus(Long Id, boolean status);
    List<Item> findAllByOwnerId(Long userId);
}
