package com.gabriel.promocoes.model;

import lombok.*;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "promocoes")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class Promocao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Por favor insira um título.")
    @Column(name = "titulo", nullable = false)
    private String titulo;

    @NotBlank(message = "Por favor insira um link válido.")
    @Column(name = "link_promocao", nullable = false)
    private String linkPromocao;

    @Column(name = "site_promocao", nullable = false)
    private String site;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "link_imagem")
    private String linkImagem;

    @NotNull(message = "Por favor insira um valor.")
    @NumberFormat(style = NumberFormat.Style.CURRENCY, pattern = "#,##0.00")
    @Column(name = "preco", nullable = false)
    private BigDecimal preco;

    @Column(name = "total_likes")
    private int likes;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dtCadastro;

    @NotNull(message = "Selecione a categoria.")
    @ManyToOne
    @JoinColumn(name = "categoria_fk")
    private Categoria categoria;
}
