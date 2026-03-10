package com.zyloerp.modules.cliente.dto;

import com.zyloerp.modules.cliente.model.Cliente;
import com.zyloerp.shared.enums.StatusCliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    private Long codigoCliente;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadual;
    private String cep;
    private String logradouro;
    private String numeroEndereco;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private StatusCliente statusCliente;
    private LocalDateTime criadoEm;

    // Populados apenas no buscarPorId (tela de detalhes)
    private List<ContatoClienteResponseDTO> contatos;
    private List<ObservacaoClienteResponseDTO> observacoes;

    // ─── fromEntity: listagem (sem contatos/observações — evita N+1) ──────────
    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .codigoCliente(cliente.getCodigoCliente())
                .razaoSocial(cliente.getRazaoSocial())
                .nomeFantasia(cliente.getNomeFantasia())
                .cnpj(cliente.getCnpj())
                .inscricaoEstadual(cliente.getInscricaoEstadual())
                .cep(cliente.getCep())
                .logradouro(cliente.getLogradouro())
                .numeroEndereco(cliente.getNumeroEndereco())
                .complemento(cliente.getComplemento())
                .bairro(cliente.getBairro())
                .cidade(cliente.getCidade())
                .estado(cliente.getEstado())
                .statusCliente(cliente.getStatusCliente())
                .criadoEm(cliente.getCriadoEm())
                .build();
    }

    // ─── fromEntityComDetalhes: tela de detalhes (com contatos e observações) ─
    public static ClienteResponseDTO fromEntityComDetalhes(Cliente cliente) {
        ClienteResponseDTO dto = fromEntity(cliente);

        dto.setContatos(
                cliente.getContatos().stream()
                        .filter(c -> Boolean.TRUE.equals(c.getAtivo()))
                        .map(ContatoClienteResponseDTO::fromEntity)
                        .toList()
        );

        dto.setObservacoes(
                cliente.getObservacoes().stream()
                        .map(ObservacaoClienteResponseDTO::fromEntity)
                        .toList()
        );

        return dto;
    }
}