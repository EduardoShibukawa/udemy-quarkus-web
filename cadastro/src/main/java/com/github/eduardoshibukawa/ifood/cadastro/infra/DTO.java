package com.github.eduardoshibukawa.ifood.cadastro.infra;

import javax.validation.ConstraintValidatorContext;

public interface DTO {

    default boolean isValid(ConstraintValidatorContext context) {
        return true;
    }

}