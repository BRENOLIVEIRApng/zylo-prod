package com.zyloerp.modules.usuario.repository;

import com.zyloerp.modules.usuario.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByNomePerfil(String nomePerfil);

    boolean existsByNomePerfil(String nomePerfil);

    List<Perfil> findBySistemaTrue();

    List<Perfil> findBySistemaFalse();

    @Query("SELECT DISTINCT p FROM Perfil p LEFT JOIN FETCH p.permissoes")
    List<Perfil> findAllComPermissoes();
}