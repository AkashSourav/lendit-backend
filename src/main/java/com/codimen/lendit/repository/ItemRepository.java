package com.codimen.lendit.repository;

import com.codimen.lendit.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item,Long> {

    @Query(value = "SELECT * from item where id = (select distinct(task_id) from item_details where id = :taskId)",nativeQuery = true)
    Item findOneByTaskId(@Param("taskId") Long taskId);

}
