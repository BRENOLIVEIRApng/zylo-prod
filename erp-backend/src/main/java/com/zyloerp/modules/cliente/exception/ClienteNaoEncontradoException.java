package com.zyloerp.modules.cliente.exception;

public class ClienteNaoEncontradoException extends RuntimeException {

    public ClienteNaoEncontradoException(Long id) {
        super("Cliente não encontrado com código: " + id);
    }

    public ClienteNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}