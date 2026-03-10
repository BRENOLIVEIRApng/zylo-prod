package com.zyloerp.modules.cliente.controller;

import com.zyloerp.modules.cliente.dto.*;
import com.zyloerp.shared.enums.StatusCliente;
import com.zyloerp.modules.cliente.service.ClienteService;
import com.zyloerp.modules.usuario.model.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // ─── CRUD Cliente ─────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTES:CRIAR')")
    public ResponseEntity<ClienteResponseDTO> criar(
            @Valid @RequestBody ClienteRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.criar(dto, usuarioLogado.getCodigoUsuario()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTES:VER')")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENTES:VER')")
    public ResponseEntity<Page<ClienteResponseDTO>> listar(
            @RequestParam(required = false) String razaoSocial,
            @RequestParam(required = false) StatusCliente statusCliente,
            @RequestParam(required = false) String cidade,
            @RequestParam(defaultValue = "0")            int page,
            @RequestParam(defaultValue = "20")           int size,
            @RequestParam(defaultValue = "razaoSocial")  String ordenarPor
    ) {
        ClienteFiltroDTO filtro = ClienteFiltroDTO.builder()
                .razaoSocial(razaoSocial)
                .statusCliente(statusCliente)
                .cidade(cidade)
                .page(page)
                .size(size)
                .ordenarPor(ordenarPor)
                .build();

        return ResponseEntity.ok(clienteService.listar(filtro));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTES:EDITAR')")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteUpdateDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(clienteService.atualizar(id, dto, usuarioLogado.getCodigoUsuario()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTES:EXCLUIR')")
    public ResponseEntity<Void> remover(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        clienteService.remover(id, usuarioLogado.getCodigoUsuario());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('CLIENTES:EDITAR')")
    public ResponseEntity<ClienteResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam StatusCliente novoStatus,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(clienteService.alterarStatus(id, novoStatus, usuarioLogado.getCodigoUsuario()));
    }

    // ─── Contatos ─────────────────────────────────────────────────────────────

    @PostMapping("/{codigoCliente}/contatos")
    @PreAuthorize("hasAuthority('CLIENTES:EDITAR')")
    public ResponseEntity<ContatoClienteResponseDTO> adicionarContato(
            @PathVariable Long codigoCliente,
            @Valid @RequestBody ContatoClienteRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.adicionarContato(codigoCliente, dto));
    }

    @PutMapping("/{codigoCliente}/contatos/{codigoContato}")
    @PreAuthorize("hasAuthority('CLIENTES:EDITAR')")
    public ResponseEntity<ContatoClienteResponseDTO> atualizarContato(
            @PathVariable Long codigoCliente,
            @PathVariable Long codigoContato,
            @Valid @RequestBody ContatoClienteRequestDTO dto
    ) {
        return ResponseEntity.ok(clienteService.atualizarContato(codigoCliente, codigoContato, dto));
    }

    @DeleteMapping("/{codigoCliente}/contatos/{codigoContato}")
    @PreAuthorize("hasAuthority('CLIENTES:EDITAR')")
    public ResponseEntity<Void> removerContato(
            @PathVariable Long codigoCliente,
            @PathVariable Long codigoContato
    ) {
        clienteService.removerContato(codigoCliente, codigoContato);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codigoCliente}/contatos/{codigoContato}/principal")
    @PreAuthorize("hasAuthority('CLIENTES:EDITAR')")
    public ResponseEntity<Void> definirContatoPrincipal(
            @PathVariable Long codigoCliente,
            @PathVariable Long codigoContato
    ) {
        clienteService.definirContatoPrincipal(codigoCliente, codigoContato);
        return ResponseEntity.noContent().build();
    }

    // ─── Observações ──────────────────────────────────────────────────────────

    @PostMapping("/{codigoCliente}/observacoes")
    @PreAuthorize("hasAuthority('CLIENTES:VER')")
    public ResponseEntity<ObservacaoClienteResponseDTO> adicionarObservacao(
            @PathVariable Long codigoCliente,
            @Valid @RequestBody ObservacaoClienteRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.adicionarObservacao(codigoCliente, dto, usuarioLogado.getCodigoUsuario()));
    }

    @GetMapping("/{codigoCliente}/observacoes")
    @PreAuthorize("hasAuthority('CLIENTES:VER')")
    public ResponseEntity<Page<ObservacaoClienteResponseDTO>> listarObservacoes(
            @PathVariable Long codigoCliente,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(clienteService.listarObservacoes(codigoCliente, page, size));
    }
}