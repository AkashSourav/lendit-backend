package com.codimen.lendit.repository;

import com.codimen.lendit.model.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCategoryRepository extends JpaRepository<ItemCategory,Long> {
}
