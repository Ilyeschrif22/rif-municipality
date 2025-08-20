package com.example.request.service.mapper;

import com.example.request.domain.Request;
import com.example.request.service.dto.RequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Request} and its DTO {@link RequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface RequestMapper extends EntityMapper<RequestDTO, Request> {}
