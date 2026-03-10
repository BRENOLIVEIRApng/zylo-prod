package com.zyloerp.modules.cliente.service;

import com.zyloerp.modules.cliente.dto.*;
import com.zyloerp.shared.enums.StatusCliente;
import org.springframework.data.domain.Page;

public interface ClienteService {

    // ─── Cliente ──────────────────────────────────────────────────────────────
    ClienteResponseDTO criar(ClienteRequestDTO dto, Long codigoUsuarioLogado);

    ClienteResponseDTO buscarPorId(Long id);

    Page<ClienteResponseDTO> listar(ClienteFiltroDTO filtro);

    ClienteResponseDTO atualizar(Long id, ClienteUpdateDTO dto, Long codigoUsuarioLogado);

    void remover(Long id, Long codigoUsuarioLogado);

    ClienteResponseDTO alterarStatus(Long id, StatusCliente novoStatus, Long codigoUsuarioLogado);

    // ─── Contatos ─────────────────────────────────────────────────────────────
    ContatoClienteResponseDTO adicionarContato(Long codigoCliente, ContatoClienteRequestDTO dto);

    ContatoClienteResponseDTO atualizarContato(Long codigoCliente, Long codigoContato, ContatoClienteRequestDTO dto);

    void removerContato(Long codigoCliente, Long codigoContato);

    void definirContatoPrincipal(Long codigoCliente, Long codigoContato);

    // ─── Observações ──────────────────────────────────────────────────────────
    ObservacaoClienteResponseDTO adicionarObservacao(Long codigoCliente, ObservacaoClienteRequestDTO dto, Long codigoUsuarioLogado);

    Page<ObservacaoClienteResponseDTO> listarObservacoes(Long codigoCliente, int page, int size);
}