package com.example.gateway.service.mapper;

import com.example.gateway.domain.Request;
import com.example.gateway.service.dto.RequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Request} and its DTO {@link RequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface RequestMapper extends EntityMapper<RequestDTO, Request> {}
