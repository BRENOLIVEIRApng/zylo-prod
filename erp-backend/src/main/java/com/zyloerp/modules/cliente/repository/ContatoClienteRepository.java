package com.zyloerp.modules.cliente.repository;

import com.zyloerp.modules.cliente.model.ContatoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContatoClienteRepository extends JpaRepository<ContatoCliente, Long> {

    List<ContatoCliente> findByCliente_CodigoClienteAndAtivoTrue(Long codigoCliente);

    Optional<ContatoCliente> findByCodigoContatoAndCliente_CodigoCliente(Long codigoContato, Long codigoCliente);

    // Garante apenas 1 principal por cliente — batch UPDATE evita carregar coleção
    @Modifying
    @Query("UPDATE ContatoCliente c SET c.principal = false WHERE c.cliente.codigoCliente = :codigoCliente")
    void desmarcarTodosPrincipais(@Param("codigoCliente") Long codigoCliente);
}