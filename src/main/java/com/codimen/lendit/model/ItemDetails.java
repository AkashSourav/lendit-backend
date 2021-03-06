package com.codimen.lendit.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class ItemDetails extends Traceable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "sold_status")
    private boolean soldStatus=false;

    @Column(name = "sold_price")
    private Integer soldPrice;

    @Column(name = "address")
    private String address;

    @Column(name = "lend_start_date")
    private Date lendStartDate;

    @Column(name = "lend_end_date")
    private Date lendEndDate;

    @Column(name = "beeding_type")
    private boolean beedingType;

    @Column(name = "min_price")
    private Integer minPrice;

    @Column(name = "max_price")
    private Integer maxPrice;

    @Column(name = "flat_price")
    private Integer flatPrice;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @JoinColumn(name = "item_details_id")
    private List<ItemPriceDetails> itemsPriceDetailsList = new ArrayList<>();

}
