package com.codimen.lendit.service;

import com.codimen.lendit.dto.request.*;
import com.codimen.lendit.exception.DataFoundNullException;
import com.codimen.lendit.exception.DuplicateDataException;
import com.codimen.lendit.dto.response.ItemsResponse;
import com.codimen.lendit.exception.InvalidDetailsException;
import com.codimen.lendit.model.Item;
import com.codimen.lendit.model.ItemDetails;
import com.codimen.lendit.model.ItemPriceDetails;
import com.codimen.lendit.model.projection.ItemDetailsProjection;
import com.codimen.lendit.repository.ItemCategoryRepository;
import com.codimen.lendit.repository.ItemDetailsRepository;
import com.codimen.lendit.repository.ItemPriceDetailsRepository;
import com.codimen.lendit.repository.ItemRepository;
import com.codimen.lendit.utils.FileUploadUtil;
import com.codimen.lendit.utils.ResponseJsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
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
    private ItemPriceDetailsRepository itemPriceDetailsRepository;


    @Autowired
    private ItemCategoryRepository itemCategoryRepository;

    @Autowired
    EntityManager em;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Transactional(rollbackOn = Throwable.class)
    public Map createItem(MultipartFile file, String submitData) throws Exception
    {
        log.info("<=== Started item creation for owner ===>");

        CreateItemRequest createItemRequest =  new ObjectMapper().readValue(submitData, CreateItemRequest.class);
        String fileurl = savePic(file);
        Item newItem = new Item();
        newItem.setItemCategory(itemCategoryRepository.getOne(createItemRequest.getCategoryId()));
        newItem.setItemName(createItemRequest.getItemName());
        newItem.setLandStatus(false);
        newItem.setOwnerId(1l);
        newItem.setPictures(fileurl);
        newItem.setManufacturer(createItemRequest.getManufacturer());
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

    private String savePic(MultipartFile file) throws DataFoundNullException, IOException {
        // Get the filename and build the local file path
        if(file == null || file.isEmpty()){
            log.error("file found null/empty file");
            throw new DataFoundNullException("file");
        }
        String receivedFilename = file.getOriginalFilename();
        String extension = fileUploadUtil.getExtension(receivedFilename);
        if(extension == null){
            log.error("File extension not supported");
            throw new MultipartException("File extension not supported");
        }
        boolean doesExtMatched = fileUploadUtil.matchProfilePicExtension(extension);
        if(!doesExtMatched){
            log.error("File extension not supported");
            throw new MultipartException("File extension not supported");
        }

        if (!fileUploadUtil.doesProfileFileSizeLessThenMaxSize(file)) {
            log.info("Size of the file - " + String.valueOf(file.getSize()) +
                    " and maxFileSize allowed - " + fileUploadUtil.getProfilePicMaxFileSize());
            throw new MultipartException("File larger than maximum size limit!");
        }

        String finalItemPicName = fileUploadUtil.getItemPicName(receivedFilename, "items",-1L);

        String profilePicUploadPath = fileUploadUtil.getProfilePicUploadPath(finalItemPicName);

        // Save the file locally
        fileUploadUtil.saveUserProfilePic(file,profilePicUploadPath);

        return fileUploadUtil.getProfilePicUrl(finalItemPicName);
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

        ItemDetailsProjection itemDetails=itemDetailsRepository.findIdOneByItemIdAndLendEndDateGreaterThan(itemRelendDetailsRequest.getId(),new Date());
        if(itemDetails.getId() != null){
                log.error("<=== Item {} all ready present for lend ===>", itemRelendDetailsRequest.getId());
                throw new DuplicateDataException("Item already present for lend");
        }
        this.createItemDetails(itemRelendDetailsRequest);
        log.info("<=== Completed creating relend item details ===>");
        return ResponseJsonUtil.getSuccessResponseJson();
    }

    public Map placeOrder(OrderDetailsRequest orderDetailsRequest) throws DuplicateDataException, InvalidDetailsException {
        log.info("<=== Started placing order for the item {} ===>",orderDetailsRequest.getItemDetailsId());

        ItemDetails itemDetails=itemDetailsRepository.findByIdAndSoldStatusAndLendEndDateGreaterThan(orderDetailsRequest.getItemDetailsId(),false,new Date());
        if(itemDetails==null){
            log.error("<=== Item {} already lend or date is expire  ===>", orderDetailsRequest.getItemDetailsId());
            throw new DuplicateDataException("Item already lend or lend date expire");
        }

        ItemDetailsProjection itemPriceDetailsObj=itemPriceDetailsRepository.findIdByItemDetailsIdAndUserId(orderDetailsRequest.getItemDetailsId(),2l);
        if(itemPriceDetailsObj != null){
            log.error("<=== Item {} already lend by the user {}  ===>", orderDetailsRequest.getItemDetailsId(),2l);
            throw new DuplicateDataException("Item already requested for lend");
        }

        if(itemDetails.isBeedingType()){
            if(orderDetailsRequest.getPrice() > itemDetails.getMaxPrice() || orderDetailsRequest.getPrice()<itemDetails.getMinPrice()){
                log.error("<=== Invalid Price for the item {} ===>",orderDetailsRequest.getItemDetailsId());
                throw new InvalidDetailsException("Please provide bid between min and max price");
            }
        }
        else{
            if(orderDetailsRequest.getPrice()>itemDetails.getFlatPrice() || orderDetailsRequest.getPrice()<itemDetails.getFlatPrice()){
                log.error("<=== Invalid Price for the item {} ===>",orderDetailsRequest.getItemDetailsId());
                throw new InvalidDetailsException("Please provide the actual price");
            }
        }
        ItemPriceDetails itemPriceDetails=new ItemPriceDetails();
        itemPriceDetails.setItemDetailsId(orderDetailsRequest.getItemDetailsId());
        itemPriceDetails.setPrice(orderDetailsRequest.getPrice());
        itemPriceDetails.setOwnerApproval(false);
        itemPriceDetails.setUserId(1l);
        itemPriceDetails.setViewedStatus(false);
        itemPriceDetailsRepository.save(itemPriceDetails);
        log.info("<=== Completed placing order for the item {} ===>",orderDetailsRequest.getItemDetailsId());
        return ResponseJsonUtil.getSuccessResponseJson();
    }

    public Map approveRequest(ApproveOrderRequest approveOrderRequest){
        log.info("<=== Started approving requested for the item_details {}===>",approveOrderRequest.getItemDetailsId());

        log.info("<=== Completed approving requested for the item_details {}===>",approveOrderRequest.getItemDetailsId());
        return ResponseJsonUtil.getSuccessResponseJson("Item approved successfully");
    }

    public HashMap findAllItems(ItemsFilterRequest itemsFilterRequest) {
        HashMap response = new HashMap();

        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ItemDetails.class, "itemDetails");
        criteria.createAlias("itemDetails.item", "item");

        criteria.createAlias("item.itemCategory", "itemCategory");

        criteria.add(Restrictions.or(
                Restrictions.like("itemDetails.soldStatus", false)));
        if(itemsFilterRequest != null && itemsFilterRequest.getFilters() != null){

            if(itemsFilterRequest.getFilters().containsKey("itemName")){
                String itemName = (String)itemsFilterRequest.getFilters().get("itemName");

                log.info(" itemName " + itemName);

                criteria.add(Restrictions.or(
                        Restrictions.like("item.itemName", "%" + itemName + "%").ignoreCase()));
            }

            if(itemsFilterRequest.getFilters().containsKey("category")){
                String categoryName = (String)itemsFilterRequest.getFilters().get("category");

                log.info(" categoryName " + categoryName);

                criteria.add(Restrictions.or(
                        Restrictions.like("itemCategory.categoryName", "%" + categoryName + "%").ignoreCase()));
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
        projectionList.add(Projections.property("itemCategory.id"));
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
        projectionList.add(Projections.property("item.manufacturer"));

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
                if( ob[8] != null){
                    responseEntity.setSoldPrice((Integer) ob[8]);
                }
                responseEntity.setAddress((String) ob[9]);
                responseEntity.setLendStartDate((Date) ob[10]);
                responseEntity.setLendEndDate((Date) ob[11]);
                responseEntity.setBeedingType((Boolean) ob[12]);
                if( ob[13] != null){
                    responseEntity.setMinPrice((Integer) ob[13]);
                }

                if( ob[14] != null){
                    responseEntity.setMaxPrice((Integer) ob[14]);
                }
                if(ob[15] != null){
                    responseEntity.setFlatPrice((Integer) ob[15]);
                }
                responseEntity.setItemName(((String) ob[16]));
                if(ob[17] != null){
                    responseEntity.setManufacturer(((String) ob[17]));
                }

                itemDetailsArrayList.add(responseEntity);

            }
        }
        response.put("totalRecord",totalResult);
        response.put("data", itemDetailsArrayList);

        return response;
    }

    public HashMap getAllItemHistory(Long userId) {

        HashMap response  =  new HashMap();

        List<Item> itemList = itemRepository.findAllByOwnerId(userId);

        response.put("data", itemList);
        return response;

    }
}
