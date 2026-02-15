package com.zyloerp.modules.usuario.dto;

import com.zyloerp.modules.usuario.model.Permissao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissaoResponseDTO {

    private Long codigoPermissao;
    private String modulo;
    private String acao;
    private String descricaoPermissao;
    private String authority;

    public static PermissaoResponseDTO fromEntity(Permissao permissao) {
        return PermissaoResponseDTO.builder()
                .codigoPermissao(permissao.getCodigoPermissao())
                .modulo(permissao.getModulo())
                .acao(permissao.getAcao())
                .descricaoPermissao(permissao.getDescricaoPermissao())
                .authority(permissao.getAuthority())
                .build();
    }
}