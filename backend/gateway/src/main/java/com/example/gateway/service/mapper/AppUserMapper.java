package com.example.gateway.service.mapper;

import com.example.gateway.domain.AppUser;
import com.example.gateway.service.dto.AppUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppUser} and its DTO {@link AppUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppUserMapper extends EntityMapper<AppUserDTO, AppUser> {
    @Override
    @Mapping(target = "passwordHash", ignore = true)
    AppUser toEntity(AppUserDTO dto);

    @Override
    @Mapping(target = "password", ignore = true)
    AppUserDTO toDto(AppUser entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "passwordHash", ignore = true)
    void partialUpdate(@MappingTarget AppUser entity, AppUserDTO dto);
}
