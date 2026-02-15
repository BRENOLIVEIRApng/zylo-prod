package com.zyloerp.modules.usuario.dto;

import com.zyloerp.modules.usuario.model.Perfil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilResponseDTO {

    private Long codigoPerfil;
    private String nomePerfil;
    private String descricaoPerfil;
    private Boolean sistema;
    private LocalDateTime criadoEm;
    private List<PermissaoResponseDTO> permissoes;

    public static PerfilResponseDTO fromEntity(Perfil perfil) {
        return PerfilResponseDTO.builder()
                .codigoPerfil(perfil.getCodigoPerfil())
                .nomePerfil(perfil.getNomePerfil())
                .descricaoPerfil(perfil.getDescricaoPerfil())
                .sistema(perfil.getSistema())
                .criadoEm(perfil.getCriadoEm())
                .permissoes(
                        perfil.getPermissoes() != null
                                ? perfil.getPermissoes().stream()
                                .map(PermissaoResponseDTO::fromEntity)
                                .collect(Collectors.toList())
                                : null
                )
                .build();
    }
}