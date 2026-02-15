package com.zyloerp.modules.usuario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SincronizarPermissoesDTO {

    @NotNull(message = "Lista de permissões não pode ser nula")
    private Set<Long> permissoesIds;
}