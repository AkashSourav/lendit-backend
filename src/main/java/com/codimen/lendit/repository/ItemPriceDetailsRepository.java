package com.codimen.lendit.repository;

import com.codimen.lendit.model.ItemPriceDetails;
import com.codimen.lendit.model.projection.ItemDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPriceDetailsRepository extends JpaRepository<ItemPriceDetails,Long> {

    ItemDetailsProjection findIdByItemDetailsIdAndUserId(Long itemId, Long userId);

    ItemPriceDetails findOneByIdAndOwnerApproval(Long id,boolean status);

}
