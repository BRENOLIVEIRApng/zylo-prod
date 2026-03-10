package com.zyloerp.modules.cliente.model;

import com.zyloerp.modules.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "observacoes_cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacaoCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_observacao")
    private Long codigoObservacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "observacao", nullable = false, columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "data_hora_observacao", nullable = false, updatable = false)
    private LocalDateTime dataHoraObservacao;

    @PrePersist
    protected void onCreate() {
        this.dataHoraObservacao = LocalDateTime.now();
    }
}