package com.zyloerp.modules.cliente.model;

import com.zyloerp.core.entity.BaseEntity;
import com.zyloerp.shared.enums.StatusCliente;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_cliente")
    private Long codigoCliente;

    // ─── Dados cadastrais ─────────────────────────────────────────────────────
    @Column(name = "razao_social", nullable = false, length = 200)
    private String razaoSocial;

    @Column(name = "nome_fantasia", length = 200)
    private String nomeFantasia;

    @Column(name = "cnpj", nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    // ─── Endereço ─────────────────────────────────────────────────────────────
    @Column(name = "cep", length = 10)
    private String cep;

    @Column(name = "logradouro", length = 200)
    private String logradouro;

    @Column(name = "numero_endereco", length = 20)
    private String numeroEndereco;

    @Column(name = "complemento", length = 100)
    private String complemento;

    @Column(name = "bairro", length = 100)
    private String bairro;

    @Column(name = "cidade", length = 100)
    private String cidade;

    @Column(name = "estado", length = 2)
    private String estado;

    // ─── Status ───────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "status_cliente", nullable = false, length = 20)
    @Builder.Default
    private StatusCliente statusCliente = StatusCliente.ATIVO;

    // ─── Relacionamentos ──────────────────────────────────────────────────────
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ContatoCliente> contatos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("dataHoraObservacao DESC")
    @Builder.Default
    private List<ObservacaoCliente> observacoes = new ArrayList<>();

    // ─── Business methods ─────────────────────────────────────────────────────

    public void adicionarContato(ContatoCliente contato) {
        contatos.add(contato);
        contato.setCliente(this);
    }

    public void adicionarObservacao(ObservacaoCliente observacao) {
        observacoes.add(observacao);
        observacao.setCliente(this);
    }

    public boolean isAtivo() {
        return StatusCliente.ATIVO.equals(this.statusCliente) && getExcluidoEm() == null;
    }

    public void suspender() {
        this.statusCliente = StatusCliente.SUSPENSO;
    }

    public void encerrar() {
        this.statusCliente = StatusCliente.ENCERRADO;
    }

    public void reativar() {
        this.statusCliente = StatusCliente.ATIVO;
    }
}