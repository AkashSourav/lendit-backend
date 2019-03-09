package com.codimen.lendit.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "item_price_details")
public class ItemPriceDetails extends Traceable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_details_id")
    private Long itemDetailsId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "price")
    private int price;

    @Column(name = "owner_approval")
    private boolean ownerApproval;

    @Column(name = "viewed_status")
    private boolean viewedStatus;

    @Column(name = "approval_date")
    private Date approvalDate;

}
