package com.gabriel.promocoes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "categorias")
@Getter @Setter @NoArgsConstructor
public class Categoria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Por favor insira um nome para a Categoria.")
    @Column(name = "nome_categoria", nullable = false, unique = true)
    private String nome;

    @JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<Promocao> promocoes;

    @Column(name = "num_promocoes")
    private int numeroPromocoes;
}
