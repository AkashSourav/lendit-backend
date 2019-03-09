package com.codimen.lendit.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PaginationDTO implements Serializable {

	private static final long serialVersionUID = -1L;

	private Integer pageNo;
	private Integer pageSize;
	private String sortField;
	private String sortOrder;
	
	public PaginationDTO() {
	}

	public PaginationDTO(Integer pageNo, Integer pageSize, String sortField, String sortOrder) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.sortField = sortField;
		this.sortOrder = sortOrder;
	}
}