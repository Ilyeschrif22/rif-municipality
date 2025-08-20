package com.example.gateway.service.mapper;

import com.example.gateway.domain.Municipality;
import com.example.gateway.service.dto.MunicipalityDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Municipality} and its DTO {@link MunicipalityDTO}.
 */
@Mapper(componentModel = "spring")
public interface MunicipalityMapper extends EntityMapper<MunicipalityDTO, Municipality> {}
