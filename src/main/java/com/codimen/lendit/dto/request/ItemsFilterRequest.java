package com.codimen.lendit.dto.request;

import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;

@Data
public class ItemsFilterRequest {

    private HashMap filters;
    private PaginationDTO paginationDTO;
}
