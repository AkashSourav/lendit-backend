package com.codimen.lendit.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "item")
public class Item extends Traceable{

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name="id")
private Long id;

@Column(name = "item_name")
private String itemName;

@Column(name = "item_category_id")
private int itemCategoryId;

@Column(name = "owner_id")
private Long ownerId;

@Column(name = "last_lend_date")
private Date lastLendDate;

@Column(name = "land_status")
private boolean landStatus;

@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
@JoinColumn(name = "item_id")
private List<ItemDetails> itemsDetailsList = new ArrayList<>();

}
