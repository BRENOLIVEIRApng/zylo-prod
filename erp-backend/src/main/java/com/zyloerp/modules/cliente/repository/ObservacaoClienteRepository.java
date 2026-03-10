package com.zyloerp.modules.cliente.repository;

import com.zyloerp.modules.cliente.model.ObservacaoCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservacaoClienteRepository extends JpaRepository<ObservacaoCliente, Long> {

    // Retorna observações do cliente ordenadas pela mais recente (via Pageable)
    Page<ObservacaoCliente> findByCliente_CodigoClienteOrderByDataHoraObservacaoDesc(
            Long codigoCliente, Pageable pageable
    );
}