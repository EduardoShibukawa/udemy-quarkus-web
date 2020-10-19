package com.github.eduardoshibukawa.ifood.cadastro.dto;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.github.eduardoshibukawa.ifood.cadastro.domain.Restaurante;
import com.github.eduardoshibukawa.ifood.cadastro.infra.DTO;
import com.github.eduardoshibukawa.ifood.cadastro.infra.ValidDTO;

import io.smallrye.common.constraint.NotNull;

@ValidDTO
public class AdicionarRestauranteDTO implements DTO {
    
    @NotEmpty
    @NotNull
    public String proprietario;
    
    @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}$")
    @NotNull
    public String cnpj;

    @Size(min = 3, max = 30)
    public String nomeFantasia;

    public LocalizacaoDTO localizacao;

    @Override
    public boolean isValid(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (Restaurante.find("cnpj", cnpj).count() > 0) {
            context.buildConstraintViolationWithTemplate("CNPJ já cadastrado")
                .addPropertyNode("cnpj")
                .addConstraintViolation();
                
            return false;
        }

        return true;
    }
}
