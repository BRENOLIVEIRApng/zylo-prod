package com.zyloerp.modules.contrato.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.zyloerp.core.entity.BaseEntity;
import com.zyloerp.modules.cliente.model.Cliente;
import com.zyloerp.shared.enums.StatusContrato;
import com.zyloerp.shared.enums.TipoContrato;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contratos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contrato extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODIGO_CONTRATO")
    private Long codigoContrato;

    @Column(name = "NUMERO_CONTRATO", unique = true, length = 20)
    private String numeroContrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODIGO_CLIENTE", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CONTRATO", nullable = false, length = 30)
    private TipoContrato tipoContrato;

    @Column(name = "VALOR_TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "DATA_INICIO", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "DATA_FIM", nullable = false)
    private LocalDate dataFim;

    @Column(name = "DURACAO_MESES")
    private Integer duracaoMeses;

    @Column(name = "SLA_HORAS")
    private Integer slaHoras;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_CONTRATO", nullable = false, length = 20)
    @Builder.Default
    private StatusContrato statusContrato = StatusContrato.ATIVO;

    @Column(name = "OBSERVACOES", columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ContratoServico> servicos = new HashSet<>();

    public void adicionarServico(ContratoServico contratoServico) {
        servicos.add(contratoServico);
        contratoServico.setContrato(this);
    }

    public void removerServico(ContratoServico contratoServico) {
        servicos.remove(contratoServico);
        contratoServico.setContrato(null);
    }

    public boolean isAtivo() {
        return this.statusContrato == StatusContrato.ATIVO;
    }

    public void suspender() {
        this.statusContrato = StatusContrato.SUSPENSO;
    }

    public void reativar() {
        this.statusContrato = StatusContrato.ATIVO;
    }

    public void encerrar() {
        this.statusContrato = StatusContrato.ENCERRADO;
    }

    public void cancelar() {
        this.statusContrato = StatusContrato.CANCELADO;
    }
}