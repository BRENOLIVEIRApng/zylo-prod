package com.zyloerp.modules.cliente.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {

    // ─── Dados cadastrais ─────────────────────────────────────────────────────
    @NotBlank(message = "Razão social é obrigatória")
    @Size(min = 3, max = 200, message = "Razão social deve ter entre 3 e 200 caracteres")
    private String razaoSocial;

    @Size(max = 200, message = "Nome fantasia deve ter no máximo 200 caracteres")
    private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(
            regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}",
            message = "CNPJ inválido. Formato esperado: 00.000.000/0000-00"
    )
    private String cnpj;

    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    private String inscricaoEstadual;

    // ─── Endereço ─────────────────────────────────────────────────────────────
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP inválido. Formato esperado: 00000-000")
    private String cep;

    @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
    private String logradouro;

    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    private String numeroEndereco;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    private String complemento;

    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    private String bairro;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @Size(min = 2, max = 2, message = "Estado deve ter exatamente 2 caracteres (sigla UF)")
    private String estado;
}