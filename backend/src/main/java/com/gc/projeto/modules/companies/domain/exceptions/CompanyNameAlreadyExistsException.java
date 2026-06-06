package com.gc.projeto.modules.companies.domain.exceptions;

import com.gc.projeto.shared.domain.exceptions.BusinessException;

public class CompanyNameAlreadyExistsException extends BusinessException {
    public CompanyNameAlreadyExistsException() {
        super("O nome da empresa informada já está sendo usado.");
    }
}
