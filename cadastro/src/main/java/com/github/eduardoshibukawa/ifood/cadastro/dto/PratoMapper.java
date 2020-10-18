package com.github.eduardoshibukawa.ifood.cadastro.dto;

import com.github.eduardoshibukawa.ifood.cadastro.domain.Prato;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface PratoMapper {

    public Prato toPrato(AdicionarPratoDTO dto);

    public void toPrato(AtualizarPratoDTO dto, @MappingTarget Prato p);

    public PratoDTO toDTO(Prato p);
    
}
