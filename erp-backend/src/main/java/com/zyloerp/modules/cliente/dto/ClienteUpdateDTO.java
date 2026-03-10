package com.zyloerp.modules.cliente.dto;


import com.zyloerp.shared.enums.StatusCliente;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteUpdateDTO {

    @Size(min = 3, max = 200, message = "Razão social deve ter entre 3 e 200 caracteres")
    private String razaoSocial;

    @Size(max = 200)
    private String nomeFantasia;

    @Pattern(
            regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}",
            message = "CNPJ inválido. Formato esperado: 00.000.000/0000-00"
    )
    private String cnpj;

    @Size(max = 20)
    private String inscricaoEstadual;

    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP inválido. Formato esperado: 00000-000")
    private String cep;

    @Size(max = 200)
    private String logradouro;

    @Size(max = 20)
    private String numeroEndereco;

    @Size(max = 100)
    private String complemento;

    @Size(max = 100)
    private String bairro;

    @Size(max = 100)
    private String cidade;

    @Size(min = 2, max = 2, message = "Estado deve ter exatamente 2 caracteres (sigla UF)")
    private String estado;

    // Status também pode ser atualizado via PATCH /status, mas aceita aqui também
    private StatusCliente statusCliente;
}