package com.gabriel.promocoes.repository;

import com.gabriel.promocoes.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("SELECT c FROM Categoria c WHERE c.nome LIKE %:search%")
    Page<Categoria> findBySearch(@Param("search") String search, Pageable pageable);

    @Transactional(readOnly = false)
    @Modifying
    @Query("UPDATE Categoria c SET c.numeroPromocoes = :num WHERE c.id = :id")
    void updatePromocoes(@Param("id") Long id, @Param("num") int num);

    @Query("SELECT COUNT(p.id) FROM Promocao p WHERE p.categoria.id = :id")
    int getNumPromocoes(@Param("id") Long id);

    @Query("SELECT c FROM Categoria c WHERE c.nome = :nome")
    Categoria verificarNomeIgual(@Param("nome") String nome);

}
