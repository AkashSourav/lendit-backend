package com.codimen.lendit.model;

import lombok.Data;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Data
public abstract class Traceable implements Serializable {

    private static final long serialVersionUID = -5294602114181769989L;

    private Date createdDate = new Date();
    private Date updatedDate = new Date();
}