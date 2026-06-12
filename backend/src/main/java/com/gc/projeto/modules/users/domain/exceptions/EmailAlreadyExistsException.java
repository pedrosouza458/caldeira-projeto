package com.gc.projeto.modules.users.domain.exceptions;

import com.gc.projeto.shared.domain.exceptions.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(){
        super("O e-mail informado já está sendo usado.");
    }
}
