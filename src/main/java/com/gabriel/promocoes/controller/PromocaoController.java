package com.gabriel.promocoes.controller;

import com.gabriel.promocoes.dto.PromocaoDTO;
import com.gabriel.promocoes.model.Categoria;
import com.gabriel.promocoes.model.Promocao;
import com.gabriel.promocoes.repository.CategoriaRepository;
import com.gabriel.promocoes.repository.PromocaoRepository;
import com.gabriel.promocoes.service.PromocaoDataTablesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/promocao")
public class PromocaoController {

    private static Logger log = LoggerFactory.getLogger(PromocaoController.class);

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PromocaoRepository promocaoRepository;

    @ModelAttribute("categorias")
    public List<Categoria> getCategorias() {
        return categoriaRepository.findAll();
    }

    @GetMapping("/list")
    public String listarPromocoes(ModelMap model) {
        PageRequest pageRequest = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "dtCadastro"));
        model.addAttribute("promocoes", promocaoRepository.findAll(pageRequest));
        return "promo-list";
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<?> adicionarLikes(@PathVariable("id") Long id) {
        promocaoRepository.updateLikes(id);
        int likes = promocaoRepository.findLikesById(id);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/site/list")
    public String listarPorSite(@RequestParam("site") String site, ModelMap model) {
        PageRequest pageRequest = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "dtCadastro"));

        if (site.isEmpty()) {
            model.addAttribute("promocoes", promocaoRepository.findAll(pageRequest));
            return "promo-card";
        }

        model.addAttribute("promocoes", promocaoRepository.findBySite(site, pageRequest));
        return "promo-card";
    }

    @GetMapping("/site")
    public ResponseEntity<?> autocompleteByTermo(@RequestParam("termo") String termo) {

        List<String> sites = promocaoRepository.findSiteByTermo(termo);
        return ResponseEntity.ok(sites);
    }

    @GetMapping("/list/ajax")
    public String listarCards(@RequestParam(name = "page", defaultValue = "1") int page,
                              @RequestParam(name = "site", defaultValue = "") String site,
                              ModelMap model) {
        PageRequest pageRequest = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "dtCadastro"));
        if (site.isEmpty()) {
            model.addAttribute("promocoes", promocaoRepository.findAll(pageRequest));
        } else {
            model.addAttribute("promocoes", promocaoRepository.findBySite(site, pageRequest));
        }
        return "promo-card";
    }

    @GetMapping("/add")
    public String abrirCadastro() {
        return "promo-add";
    }

    @GetMapping("/tabela")
    public String showTebela() {
        return "promo-datatables";
    }

    @GetMapping("/datatables/server")
    public ResponseEntity<?> dataTables(HttpServletRequest request) {
        Map<String, Object> data = new PromocaoDataTablesService().execute(promocaoRepository, request);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> removerPromocao(@PathVariable("id") Long id) {
        promocaoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    //popula o form de edit
    @GetMapping("/edit/{id}")
    public ResponseEntity<?> preEditarPromocao(@PathVariable("id") Long id) {
        Promocao promo = promocaoRepository.findById(id).get();
        return ResponseEntity.ok(promo);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editarPromocao(@Valid PromocaoDTO dto, BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.unprocessableEntity().body(errors);
        }

        Promocao promo = promocaoRepository.findById(dto.getId()).get();
        promo.setCategoria(dto.getCategoria());
        promo.setDescricao(dto.getDescricao());
        promo.setLinkImagem(dto.getLinkImagem());
        promo.setPreco(dto.getPreco());
        promo.setTitulo(dto.getTitulo());

        Categoria categoria = promo.getCategoria();
        promocaoRepository.save(promo);
        updateAllNumeroPromocoes();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save")
    public ResponseEntity<?> cadastrarPromocao(@Valid Promocao promocao, BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.unprocessableEntity().body(errors);
        }

        promocao.setDtCadastro(LocalDateTime.now());
        Categoria categoria = promocao.getCategoria();
        promocaoRepository.save(promocao);
        int num = categoriaRepository.getNumPromocoes(categoria.getId());
        categoriaRepository.updatePromocoes(categoria.getId(), num);
        return ResponseEntity.ok().build();
    }

    public void updateAllNumeroPromocoes() {
        List<Categoria> categorias = categoriaRepository.findAll();

        for (Categoria c : categorias) {
            int num = categoriaRepository.getNumPromocoes(c.getId());
            categoriaRepository.updatePromocoes(c.getId(), num);
        }
    }
}
