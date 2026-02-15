package com.zyloerp.modules.usuario.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUpdateDTO {

    @Size(min = 3, max = 50, message = "Nome deve ter entre 3 e 50 caracteres")
    private String nomePerfil;

    private String descricaoPerfil;
}