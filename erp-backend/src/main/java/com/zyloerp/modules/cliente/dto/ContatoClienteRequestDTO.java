package com.zyloerp.modules.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContatoClienteRequestDTO {

    @NotBlank(message = "Nome do contato é obrigatório")
    @Size(min = 3, max = 100, message = "Nome do contato deve ter entre 3 e 100 caracteres")
    private String nomeContato;

    @Email(message = "Email inválido")
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String telefone;

    @Size(max = 20)
    private String celular;

    @Size(max = 100)
    private String cargo;

    private Boolean principal;

    // Validação de negócio: ao menos um meio de contato (RN da migration)
    public boolean temMeioDeContato() {
        return (email != null && !email.isBlank())
                || (telefone != null && !telefone.isBlank())
                || (celular != null && !celular.isBlank());
    }
}