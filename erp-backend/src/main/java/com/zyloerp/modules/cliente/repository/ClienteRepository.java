package com.zyloerp.modules.cliente.repository;

import com.zyloerp.modules.cliente.model.Cliente;
import com.zyloerp.shared.enums.StatusCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCnpj(String cnpj);

    // Para edição: verifica duplicidade ignorando o próprio registro
    boolean existsByCnpjAndCodigoClienteNot(String cnpj, Long codigoCliente);

    // Busca por ID ignorando soft-deleted
    @Query("SELECT c FROM Cliente c WHERE c.codigoCliente = :id AND c.excluidoEm IS NULL")
    Optional<Cliente> findAtivoById(@Param("id") Long id);

    // Listagem paginada com filtros opcionais
    @Query("""
            SELECT c FROM Cliente c
            WHERE c.excluidoEm IS NULL
            AND (:razaoSocial IS NULL OR LOWER(c.razaoSocial) LIKE LOWER(CONCAT('%', :razaoSocial, '%')))
            AND (:status     IS NULL OR c.statusCliente = :status)
            AND (:cidade     IS NULL OR LOWER(c.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))
            """)
    Page<Cliente> findAllFiltrado(
            @Param("razaoSocial") String razaoSocial,
            @Param("status") StatusCliente status,
            @Param("cidade") String cidade,
            Pageable pageable
    );
}