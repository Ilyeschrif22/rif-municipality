package com.example.municipality.service.mapper;

import com.example.municipality.domain.Municipality;
import com.example.municipality.service.dto.MunicipalityDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Municipality} and its DTO {@link MunicipalityDTO}.
 */
@Mapper(componentModel = "spring")
public interface MunicipalityMapper extends EntityMapper<MunicipalityDTO, Municipality> {}
