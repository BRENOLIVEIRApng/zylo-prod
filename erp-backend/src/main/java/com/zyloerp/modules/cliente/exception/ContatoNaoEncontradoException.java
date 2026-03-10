package com.zyloerp.modules.cliente.exception;

public class ContatoNaoEncontradoException extends RuntimeException {

    public ContatoNaoEncontradoException(Long id) {
        super("Contato não encontrado com código: " + id);
    }
}