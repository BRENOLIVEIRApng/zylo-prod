package com.zyloerp.modules.cliente.dto;

import com.zyloerp.shared.enums.StatusCliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteFiltroDTO {

    private String razaoSocial;
    private StatusCliente statusCliente;
    private String cidade;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;

    @Builder.Default
    private String ordenarPor = "razaoSocial";
}