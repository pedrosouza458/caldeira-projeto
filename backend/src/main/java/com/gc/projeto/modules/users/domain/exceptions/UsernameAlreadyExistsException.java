package com.gc.projeto.modules.users.domain.exceptions;

import com.gc.projeto.shared.domain.exceptions.BusinessException;

public class UsernameAlreadyExistsException extends BusinessException {
    public UsernameAlreadyExistsException(){
        super("O nome de usuário informado já está sendo usado.");
    }
}
