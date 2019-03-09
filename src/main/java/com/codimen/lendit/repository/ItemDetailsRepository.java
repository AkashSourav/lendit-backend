package com.codimen.lendit.repository;

import com.codimen.lendit.model.ItemDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface ItemDetailsRepository extends JpaRepository<ItemDetails,Long> {

    ItemDetails findOneByItemIdAndLendEndDateGreaterThan(Long itemId, Date date);

}
