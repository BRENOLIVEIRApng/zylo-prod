package com.zyloerp.modules.cliente.dto;

import com.zyloerp.modules.cliente.model.ContatoCliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContatoClienteResponseDTO {

    private Long codigoContato;
    private String nomeContato;
    private String email;
    private String telefone;
    private String celular;
    private String cargo;
    private Boolean principal;
    private Boolean ativo;

    public static ContatoClienteResponseDTO fromEntity(ContatoCliente contato) {
        return ContatoClienteResponseDTO.builder()
                .codigoContato(contato.getCodigoContato())
                .nomeContato(contato.getNomeContato())
                .email(contato.getEmail())
                .telefone(contato.getTelefone())
                .celular(contato.getCelular())
                .cargo(contato.getCargo())
                .principal(contato.getPrincipal())
                .ativo(contato.getAtivo())
                .build();
    }
}