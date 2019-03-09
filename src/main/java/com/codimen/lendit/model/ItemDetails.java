package com.codimen.lendit.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "item_details")
public class ItemDetails extends Traceable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "sold_status")
    private boolean soldStatus=false;

    @Column(name = "sold_price")
    private int soldPrice;

    @Column(name = "address")
    private String address;

    @Column(name = "lend_start_date")
    private Date lendStartDate;

    @Column(name = "lend_end_date")
    private Date lendEndDate;

}
