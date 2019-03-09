package com.codimen.lendit.repository;

import com.codimen.lendit.model.ItemDetails;
import com.codimen.lendit.model.ItemPriceDetails;
import com.codimen.lendit.model.projection.ItemDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface ItemDetailsRepository extends JpaRepository<ItemDetails,Long> {

    ItemDetailsProjection findIdOneByItemIdAndLendEndDateGreaterThan(Long itemId, Date date);

    ItemDetails findByIdAndSoldStatusAndLendEndDateGreaterThan(Long itemId, boolean status, Date date);

}
