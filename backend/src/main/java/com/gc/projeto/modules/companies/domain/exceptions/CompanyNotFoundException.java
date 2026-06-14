package com.gc.projeto.modules.companies.domain.exceptions;

import com.gc.projeto.shared.domain.exceptions.BusinessException;

public class CompanyNotFoundException extends BusinessException {
    public CompanyNotFoundException() {
        super("Empresa não encontrada.");
    }
}
