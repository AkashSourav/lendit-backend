package com.codimen.lendit.repository;

import com.codimen.lendit.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item,Long> {
}
