package com.gabriel.promocoes.dto;

import com.gabriel.promocoes.model.Categoria;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter @Setter
public class PromocaoDTO {

    @NotNull
    private Long id;

    @NotBlank(message = "Por favor insira um título.")
    private String titulo;

    private String descricao;

    @NotBlank(message = "Por favor insira um link de Imagem.")
    private String linkImagem;

    @NotNull(message = "Por favor insira um valor.")
    @NumberFormat(style = NumberFormat.Style.CURRENCY, pattern = "#,##0.00")
    private BigDecimal preco;

    @NotNull(message = "Por favor selecione uma Categoria.")
    private Categoria categoria;


}
