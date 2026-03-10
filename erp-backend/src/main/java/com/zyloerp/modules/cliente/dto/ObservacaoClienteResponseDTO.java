package com.zyloerp.modules.cliente.dto;

import com.zyloerp.modules.cliente.model.ObservacaoCliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObservacaoClienteResponseDTO {

    private Long codigoObservacao;
    private String observacao;
    private String nomeUsuario;   // quem registrou
    private LocalDateTime dataHoraObservacao;

    public static ObservacaoClienteResponseDTO fromEntity(ObservacaoCliente obs) {
        return ObservacaoClienteResponseDTO.builder()
                .codigoObservacao(obs.getCodigoObservacao())
                .observacao(obs.getObservacao())
                .nomeUsuario(obs.getUsuario() != null ? obs.getUsuario().getNomeCompleto() : null)
                .dataHoraObservacao(obs.getDataHoraObservacao())
                .build();
    }
}