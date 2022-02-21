package com.gabriel.promocoes.controller;

import com.gabriel.promocoes.model.Categoria;
import com.gabriel.promocoes.repository.CategoriaRepository;
import com.gabriel.promocoes.repository.PromocaoRepository;
import com.gabriel.promocoes.service.CategoriaDataTablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;


    @GetMapping("/list")
    public String listarCategoria(ModelMap model) {
        //repository.findAllCategorias
        model.addAttribute("categorias", null);
        return "/categories/categoria-datatable";
    }

    @GetMapping("/datatables/server")
    public ResponseEntity<?> dataTables(HttpServletRequest request) {
        Map<String, Object> data = new CategoriaDataTablesService().execute(categoriaRepository, request);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> preEditarCategoria(@PathVariable("id") Long id) {
        Categoria categoria = categoriaRepository.findById(id).get();
        return ResponseEntity.ok(categoria);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editarCategoria(@Valid Categoria categoria, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.unprocessableEntity().body(errors);
        }

        categoriaRepository.save(categoria);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> removerCategoria(@PathVariable("id") Long id) {
        if (categoriaRepository.getNumPromocoes(id) > 0) {
            return ResponseEntity.unprocessableEntity().build();
        }

        categoriaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/add")
    public String addCategoria() {
        return "/categories/categoria-add";
    }

    @PostMapping("/save")
    public ResponseEntity<?> salvarCategoria(@Valid Categoria categoria, BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.unprocessableEntity().body(errors);
        }

        if (categoriaRepository.verificarNomeIgual(categoria.getNome()) != null) {
            return ResponseEntity.status(400).build();
        }

        categoria.setNumeroPromocoes(0);
        categoriaRepository.save(categoria);
        return ResponseEntity.ok().build();
    }


}
