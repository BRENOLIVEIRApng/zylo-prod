package com.zyloerp.modules.cliente.exception;

public class ClienteJaExisteException extends RuntimeException {

    public ClienteJaExisteException(String cnpj) {
        super("CNPJ já cadastrado: " + cnpj);
    }
}