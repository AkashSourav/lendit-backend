package com.codimen.lendit.service;

import com.codimen.lendit.dto.request.CreateItemRequest;

import com.codimen.lendit.dto.request.ItemRelendDetailsRequest;
import com.codimen.lendit.exception.DuplicateDataException;
import com.codimen.lendit.dto.request.ItemsFilterRequest;
import com.codimen.lendit.dto.response.ItemsResponse;
import com.codimen.lendit.model.Item;
import com.codimen.lendit.model.ItemDetails;
import com.codimen.lendit.repository.ItemDetailsRepository;
import com.codimen.lendit.repository.ItemRepository;
import com.codimen.lendit.security.UserInfo;
import com.codimen.lendit.utils.ResponseJsonUtil;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Map;
import java.util.*;

@Service
@Slf4j
public class ItemServices {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    EntityManager em;

    @Transactional(rollbackOn = Throwable.class)
    public Map createItem(CreateItemRequest createItemRequest)
    {
        log.info("<=== Started item creation for owner ===>");
        Item newItem = new Item();
        newItem.setItemCategoryId(createItemRequest.getCategoryId());
        newItem.setItemName(createItemRequest.getItemName());
        newItem.setLandStatus(false);
        newItem.setOwnerId(1l);
        itemRepository.save(newItem);
        ItemRelendDetailsRequest itemRelendDetailsRequest=new ItemRelendDetailsRequest();
        itemRelendDetailsRequest.setId(newItem.getId());
        itemRelendDetailsRequest.setAddress(createItemRequest.getAddress());
        itemRelendDetailsRequest.setBeedingType(createItemRequest.isBeedingType());
        itemRelendDetailsRequest.setFlatPrice(createItemRequest.getFlatPrice());
        itemRelendDetailsRequest.setLendEndDate(createItemRequest.getLendEndDate());
        itemRelendDetailsRequest.setLendStartDate(createItemRequest.getLendStartDate());
        itemRelendDetailsRequest.setMaxPrice(createItemRequest.getMaxPrice());
        itemRelendDetailsRequest.setMinPrice(createItemRequest.getMinPrice());
        this.createItemDetails(itemRelendDetailsRequest);
        log.info("<=== Completed item creation for owner ===>");
        return ResponseJsonUtil.getSuccessResponseJson();
    }


    private void createItemDetails(ItemRelendDetailsRequest itemDetailsRequest){
        log.info("<=== Started creating item details ===>");
        ItemDetails itemDetails=new ItemDetails();
        itemDetails.setAddress(itemDetailsRequest.getAddress());
        itemDetails.setBeedingType(itemDetailsRequest.isBeedingType());
        if(itemDetailsRequest.isBeedingType()){
            itemDetails.setMaxPrice(itemDetailsRequest.getMaxPrice());
            itemDetails.setMinPrice(itemDetailsRequest.getMinPrice());
        }
        else{
            itemDetails.setFlatPrice(itemDetailsRequest.getFlatPrice());
        }

        itemDetails.setItem(itemRepository.getOne(itemDetailsRequest.getId()));
        itemDetails.setLendStartDate(itemDetailsRequest.getLendStartDate());
        itemDetails.setLendEndDate(itemDetailsRequest.getLendEndDate());
        itemDetails.setSoldStatus(false);
        itemDetailsRepository.save(itemDetails);
        log.info("<=== Completed creating item details ===>");
    }

    public Map createRelendItemDetails(ItemRelendDetailsRequest itemRelendDetailsRequest) throws DuplicateDataException {
        log.info("<=== Started creating relend item details ===>");

        ItemDetails itemDetails=itemDetailsRepository.findOneByItemIdAndLendEndDateGreaterThan(itemRelendDetailsRequest.getId(),new Date());
        if(itemDetails != null){
                log.error("<=== Item {} all ready present for lend ===>", itemRelendDetailsRequest.getId());
                throw new DuplicateDataException("Item already present for lend");
        }
        this.createItemDetails(itemRelendDetailsRequest);
        log.info("<=== Completed creating relend item details ===>");
        return ResponseJsonUtil.getSuccessResponseJson();
    }

    public HashMap findAllItems(ItemsFilterRequest itemsFilterRequest) {
        HashMap response = new HashMap();

        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ItemDetails.class, "itemDetails");
        criteria.createAlias("itemDetails.item", "item");
        criteria.add(Restrictions.or(
                Restrictions.like("itemDetails.soldStatus", false)));
        if(itemsFilterRequest != null && itemsFilterRequest.getFilters() != null){

            if(itemsFilterRequest.getFilters().containsKey("itemName")){
                String itemName = (String)itemsFilterRequest.getFilters().get("itemName");

                log.info(" itemName " + itemName);

                criteria.add(Restrictions.or(
                        Restrictions.like("item.itemName", "%" + itemName + "%").ignoreCase()));
            }
        }

        criteria.setProjection(Projections.rowCount());
        Integer totalResult = ((Long) criteria.uniqueResult()).intValue();
        criteria.setProjection(null);

        // For Pagination
        if (itemsFilterRequest != null && itemsFilterRequest.getPaginationDTO() != null) {
            Integer pageNo = itemsFilterRequest.getPaginationDTO().getPageNo();
            Integer pageSize = itemsFilterRequest.getPaginationDTO().getPageSize();

            if (pageNo != null && pageSize != null) {
                criteria.setFirstResult(pageNo * pageSize);
                criteria.setMaxResults(pageSize);
                log.info("PageNo : [" + pageNo + "] pageSize[" + pageSize + "]");
            }

            String sortField = itemsFilterRequest.getPaginationDTO().getSortField();
            String sortOrder = itemsFilterRequest.getPaginationDTO().getSortOrder();
            if (sortField != null && sortOrder != null) {
                log.info("sortField : [" + sortField + "] sortOrder[" + sortOrder + "]");

                if (sortField.equals("price")) {

                    sortField = "itemDetails.flatPrice";

                }

                if (sortOrder.equals("DESCENDING")) {
                    criteria.addOrder(Order.desc(sortField));
                } else {
                    criteria.addOrder(Order.asc(sortField));
                }
            } else {
                criteria.addOrder(Order.desc("itemDetails.createdDate"));
            }
        }

        log.info("Criteria Query  : " + criteria);

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("itemDetails.id"));
        projectionList.add(Projections.property("item.id"));
        projectionList.add(Projections.property("item.itemCategoryId"));
        projectionList.add(Projections.property("item.ownerId"));
        projectionList.add(Projections.property("item.lastLendDate"));
        projectionList.add(Projections.property("item.landStatus"));
        projectionList.add(Projections.property("item.pictures"));

        projectionList.add(Projections.property("itemDetails.soldStatus"));
        projectionList.add(Projections.property("itemDetails.soldPrice"));
        projectionList.add(Projections.property("itemDetails.address"));
        projectionList.add(Projections.property("itemDetails.lendStartDate"));
        projectionList.add(Projections.property("itemDetails.lendEndDate"));
        projectionList.add(Projections.property("itemDetails.beedingType"));
        projectionList.add(Projections.property("itemDetails.minPrice"));
        projectionList.add(Projections.property("itemDetails.maxPrice"));
        projectionList.add(Projections.property("itemDetails.flatPrice"));
        projectionList.add(Projections.property("item.itemName"));

        criteria.setProjection(projectionList);
        List<ItemsResponse> itemDetailsArrayList = new ArrayList<>();
        List<?> resultList = criteria.list();
        if (resultList != null) {
            Iterator<?> it = resultList.iterator();
            while (it.hasNext()) {
                Object ob[] = (Object[]) it.next();
                ItemsResponse responseEntity = new ItemsResponse();
                responseEntity.setItemDetailsId((Long) ob[0]);

                responseEntity.setItemId((Long) ob[1]);
                responseEntity.setItemCategoryId((Long) ob[2]);
                responseEntity.setOwnerId((Long) ob[3]);
                responseEntity.setLastLendDate((Date) ob[4]);
                responseEntity.setLandStatus((Boolean) ob[5]);
                responseEntity.setPictures((String) ob[6]);

                responseEntity.setSoldStatus((Boolean) ob[7]);
                responseEntity.setSoldPrice((Integer) ob[8]);
                responseEntity.setAddress((String) ob[9]);
                responseEntity.setLendStartDate((Date) ob[10]);
                responseEntity.setLendEndDate((Date) ob[11]);
                responseEntity.setBeedingType((Boolean) ob[12]);
                responseEntity.setMinPrice((Integer) ob[13]);
                if( ob[14] != null){
                    responseEntity.setMaxPrice((Integer) ob[14]);
                }
                responseEntity.setFlatPrice((Integer) ob[15]);
                responseEntity.setItemName(((String) ob[16]));

                itemDetailsArrayList.add(responseEntity);

            }
        }
        response.put("totalRecord",totalResult);
        response.put("data", itemDetailsArrayList);

        return response;
    }

}
