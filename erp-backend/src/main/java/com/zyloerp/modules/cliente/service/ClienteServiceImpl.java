package com.zyloerp.modules.cliente.service;

import com.zyloerp.modules.cliente.dto.*;
import com.zyloerp.modules.cliente.exception.ClienteJaExisteException;
import com.zyloerp.modules.cliente.exception.ClienteNaoEncontradoException;
import com.zyloerp.modules.cliente.exception.ContatoNaoEncontradoException;
import com.zyloerp.modules.cliente.model.Cliente;
import com.zyloerp.modules.cliente.model.ContatoCliente;
import com.zyloerp.modules.cliente.model.ObservacaoCliente;
import com.zyloerp.shared.enums.StatusCliente;
import com.zyloerp.modules.cliente.repository.ClienteRepository;
import com.zyloerp.modules.cliente.repository.ContatoClienteRepository;
import com.zyloerp.modules.cliente.repository.ObservacaoClienteRepository;
import com.zyloerp.modules.usuario.exception.ValidacaoException;
import com.zyloerp.modules.usuario.model.Usuario;
import com.zyloerp.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository            clienteRepository;
    private final ContatoClienteRepository     contatoClienteRepository;
    private final ObservacaoClienteRepository  observacaoClienteRepository;
    private final UsuarioRepository            usuarioRepository;

    // ─── Criar ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO dto, Long codigoUsuarioLogado) {
        validarCnpjUnico(dto.getCnpj(), null);

        Cliente cliente = Cliente.builder()
                .razaoSocial(dto.getRazaoSocial().trim())
                .nomeFantasia(dto.getNomeFantasia() != null ? dto.getNomeFantasia().trim() : null)
                .cnpj(dto.getCnpj())
                .inscricaoEstadual(dto.getInscricaoEstadual())
                .cep(dto.getCep())
                .logradouro(dto.getLogradouro())
                .numeroEndereco(dto.getNumeroEndereco())
                .complemento(dto.getComplemento())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .estado(dto.getEstado() != null ? dto.getEstado().toUpperCase() : null)
                .statusCliente(StatusCliente.ATIVO)
                .build();

        cliente.setCriadoEm(LocalDateTime.now());
        cliente.setCriadoPor(codigoUsuarioLogado);
        cliente.setAtualizadoEm(LocalDateTime.now());
        cliente.setAtualizadoPor(codigoUsuarioLogado);

        return ClienteResponseDTO.fromEntity(clienteRepository.save(cliente));
    }

    // ─── Buscar por ID ────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = buscarClienteAtivo(id);

        // Force lazy load dentro da transação (tela de detalhes)
        cliente.getContatos().size();
        cliente.getObservacoes().size();

        return ClienteResponseDTO.fromEntityComDetalhes(cliente);
    }

    // ─── Listar paginado com filtros ──────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listar(ClienteFiltroDTO filtro) {
        Pageable pageable = PageRequest.of(
                filtro.getPage(),
                filtro.getSize(),
                Sort.by(Sort.Direction.ASC, filtro.getOrdenarPor())
        );

        return clienteRepository
                .findAllFiltrado(filtro.getRazaoSocial(), filtro.getStatusCliente(), filtro.getCidade(), pageable)
                .map(ClienteResponseDTO::fromEntity);
    }

    // ─── Atualizar ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteUpdateDTO dto, Long codigoUsuarioLogado) {
        Cliente cliente = buscarClienteAtivo(id);

        if (dto.getCnpj() != null) {
            validarCnpjUnico(dto.getCnpj(), id);
            cliente.setCnpj(dto.getCnpj());
        }

        if (dto.getRazaoSocial() != null)      cliente.setRazaoSocial(dto.getRazaoSocial().trim());
        if (dto.getNomeFantasia() != null)     cliente.setNomeFantasia(dto.getNomeFantasia().trim());
        if (dto.getInscricaoEstadual() != null) cliente.setInscricaoEstadual(dto.getInscricaoEstadual());
        if (dto.getCep() != null)              cliente.setCep(dto.getCep());
        if (dto.getLogradouro() != null)       cliente.setLogradouro(dto.getLogradouro());
        if (dto.getNumeroEndereco() != null)   cliente.setNumeroEndereco(dto.getNumeroEndereco());
        if (dto.getComplemento() != null)      cliente.setComplemento(dto.getComplemento());
        if (dto.getBairro() != null)           cliente.setBairro(dto.getBairro());
        if (dto.getCidade() != null)           cliente.setCidade(dto.getCidade());
        if (dto.getEstado() != null)           cliente.setEstado(dto.getEstado().toUpperCase());
        if (dto.getStatusCliente() != null)    cliente.setStatusCliente(dto.getStatusCliente());

        cliente.setAtualizadoEm(LocalDateTime.now());
        cliente.setAtualizadoPor(codigoUsuarioLogado);

        return ClienteResponseDTO.fromEntity(clienteRepository.save(cliente));
    }

    // ─── Remover (soft delete) ────────────────────────────────────────────────

    @Override
    @Transactional
    public void remover(Long id, Long codigoUsuarioLogado) {
        Cliente cliente = buscarClienteAtivo(id);
        cliente.setExcluidoEm(LocalDateTime.now());
        cliente.setExcluidoPor(codigoUsuarioLogado);
        clienteRepository.save(cliente);
    }

    // ─── Alterar status ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public ClienteResponseDTO alterarStatus(Long id, StatusCliente novoStatus, Long codigoUsuarioLogado) {
        Cliente cliente = buscarClienteAtivo(id);

        // RN-CLI-002: cliente suspenso/encerrado não pode voltar direto para contratos
        // O reativar() apenas muda o status — a regra de contratos fica no módulo de contratos
        switch (novoStatus) {
            case SUSPENSO  -> cliente.suspender();
            case ENCERRADO -> cliente.encerrar();
            case ATIVO     -> cliente.reativar();
        }

        cliente.setAtualizadoEm(LocalDateTime.now());
        cliente.setAtualizadoPor(codigoUsuarioLogado);

        return ClienteResponseDTO.fromEntity(clienteRepository.save(cliente));
    }

    // ─── Contatos ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ContatoClienteResponseDTO adicionarContato(Long codigoCliente, ContatoClienteRequestDTO dto) {
        Cliente cliente = buscarClienteAtivo(codigoCliente);

        // RN da migration: ao menos um meio de contato
        if (!dto.temMeioDeContato()) {
            throw new ValidacaoException("Informe ao menos um meio de contato: email, telefone ou celular");
        }

        // RN: apenas 1 principal por cliente — batch UPDATE antes de inserir
        if (Boolean.TRUE.equals(dto.getPrincipal())) {
            contatoClienteRepository.desmarcarTodosPrincipais(codigoCliente);
        }

        ContatoCliente contato = ContatoCliente.builder()
                .nomeContato(dto.getNomeContato().trim())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .celular(dto.getCelular())
                .cargo(dto.getCargo())
                .principal(Boolean.TRUE.equals(dto.getPrincipal()))
                .ativo(true)
                .build();

        cliente.adicionarContato(contato);
        return ContatoClienteResponseDTO.fromEntity(contatoClienteRepository.save(contato));
    }

    @Override
    @Transactional
    public ContatoClienteResponseDTO atualizarContato(Long codigoCliente, Long codigoContato, ContatoClienteRequestDTO dto) {
        buscarClienteAtivo(codigoCliente);

        ContatoCliente contato = buscarContato(codigoContato, codigoCliente);

        if (dto.getNomeContato() != null) contato.setNomeContato(dto.getNomeContato().trim());
        if (dto.getEmail() != null)       contato.setEmail(dto.getEmail());
        if (dto.getTelefone() != null)    contato.setTelefone(dto.getTelefone());
        if (dto.getCelular() != null)     contato.setCelular(dto.getCelular());
        if (dto.getCargo() != null)       contato.setCargo(dto.getCargo());

        // Valida meio de contato após atualização
        ContatoClienteRequestDTO estadoAtual = ContatoClienteRequestDTO.builder()
                .email(contato.getEmail())
                .telefone(contato.getTelefone())
                .celular(contato.getCelular())
                .build();
        if (!estadoAtual.temMeioDeContato()) {
            throw new ValidacaoException("O contato deve ter ao menos um meio de contato: email, telefone ou celular");
        }

        if (Boolean.TRUE.equals(dto.getPrincipal())) {
            contatoClienteRepository.desmarcarTodosPrincipais(codigoCliente);
            contato.setPrincipal(true);
        }

        return ContatoClienteResponseDTO.fromEntity(contatoClienteRepository.save(contato));
    }

    @Override
    @Transactional
    public void removerContato(Long codigoCliente, Long codigoContato) {
        buscarClienteAtivo(codigoCliente);
        ContatoCliente contato = buscarContato(codigoContato, codigoCliente);

        // Soft delete — preserva histórico
        contato.setAtivo(false);
        contatoClienteRepository.save(contato);
    }

    @Override
    @Transactional
    public void definirContatoPrincipal(Long codigoCliente, Long codigoContato) {
        buscarClienteAtivo(codigoCliente);
        ContatoCliente contato = buscarContato(codigoContato, codigoCliente);

        if (!Boolean.TRUE.equals(contato.getAtivo())) {
            throw new ValidacaoException("Contato inativo não pode ser definido como principal");
        }

        contatoClienteRepository.desmarcarTodosPrincipais(codigoCliente);
        contato.setPrincipal(true);
        contatoClienteRepository.save(contato);
    }

    // ─── Observações ──────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ObservacaoClienteResponseDTO adicionarObservacao(Long codigoCliente, ObservacaoClienteRequestDTO dto, Long codigoUsuarioLogado) {
        Cliente cliente = buscarClienteAtivo(codigoCliente);

        Usuario usuario = usuarioRepository.findById(codigoUsuarioLogado)
                .orElseThrow(() -> new ValidacaoException("Usuário não encontrado"));

        ObservacaoCliente observacao = ObservacaoCliente.builder()
                .observacao(dto.getObservacao().trim())
                .usuario(usuario)
                .build();

        cliente.adicionarObservacao(observacao);
        return ObservacaoClienteResponseDTO.fromEntity(observacaoClienteRepository.save(observacao));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObservacaoClienteResponseDTO> listarObservacoes(Long codigoCliente, int page, int size) {
        buscarClienteAtivo(codigoCliente); // valida existência

        Pageable pageable = PageRequest.of(page, size);
        return observacaoClienteRepository
                .findByCliente_CodigoClienteOrderByDataHoraObservacaoDesc(codigoCliente, pageable)
                .map(ObservacaoClienteResponseDTO::fromEntity);
    }

    // ─── Helpers privados ─────────────────────────────────────────────────────

    private Cliente buscarClienteAtivo(Long id) {
        return clienteRepository.findAtivoById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));
    }

    private ContatoCliente buscarContato(Long codigoContato, Long codigoCliente) {
        return contatoClienteRepository
                .findByCodigoContatoAndCliente_CodigoCliente(codigoContato, codigoCliente)
                .orElseThrow(() -> new ContatoNaoEncontradoException(codigoContato));
    }

    private void validarCnpjUnico(String cnpj, Long idAtual) {
        boolean existe = idAtual == null
                ? clienteRepository.existsByCnpj(cnpj)
                : clienteRepository.existsByCnpjAndCodigoClienteNot(cnpj, idAtual);

        if (existe) throw new ClienteJaExisteException(cnpj);
    }
}