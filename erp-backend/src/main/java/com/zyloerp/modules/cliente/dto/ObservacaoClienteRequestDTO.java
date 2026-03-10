package com.zyloerp.modules.cliente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObservacaoClienteRequestDTO {

    @NotBlank(message = "Observação é obrigatória")
    @Size(min = 5, message = "Observação deve ter no mínimo 5 caracteres")
    private String observacao;
}