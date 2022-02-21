package com.gabriel.promocoes.service;

import com.gabriel.promocoes.model.Categoria;
import com.gabriel.promocoes.repository.CategoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

public class CategoriaDataTablesService {

    private String[] cols = {
            "id", "nome", "numeroPromocoes",
    };

    public Map<String, Object> execute(CategoriaRepository repository, HttpServletRequest request) {

        int start = Integer.parseInt(request.getParameter("start"));
        int length = Integer.parseInt(request.getParameter("length"));
        int draw = Integer.parseInt(request.getParameter("draw"));
        int current = currentPage(start, length);

        String column = columnName(request);
        Sort.Direction direction = orderBy(request);
        String search = searchBy(request);

        Pageable pageable = PageRequest.of(current, length, direction, column);
        Page<Categoria> page = queryBy(search, repository, pageable);

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("draw", draw);
        json.put("recordsTotal", page.getTotalElements());
        json.put("recordsFiltered", page.getTotalElements());
        json.put("data", page.getContent());
        return json;
    }

    private String searchBy(HttpServletRequest request) {
        return request.getParameter("search[value]").isEmpty() ? "" : request.getParameter("search[value]");
    }

    private Page<Categoria> queryBy(String search, CategoriaRepository repository, Pageable pageable) {
        if (search.isEmpty()) {
            return repository.findAll(pageable);
        }

        return repository.findBySearch(search, pageable);
    }

    private Sort.Direction orderBy(HttpServletRequest request) {
        String order = request.getParameter("order[0][dir]");
        Sort.Direction sort = Sort.Direction.ASC;
        if (order.equalsIgnoreCase("desc")) {
            sort = Sort.Direction.DESC;
        }
        return sort;
    }

    private String columnName(HttpServletRequest request) {
        int iCol = Integer.parseInt(request.getParameter("order[0][column]"));
        return cols[iCol];
    }

    private int currentPage(int start, int lenght) {
        return start / lenght;
    }
}
