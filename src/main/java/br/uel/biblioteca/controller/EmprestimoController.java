package br.uel.biblioteca.controller;

import br.uel.biblioteca.model.Emprestimo;
import br.uel.biblioteca.service.EmprestimoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new EmprestimoForm());
        return "emprestimos/form";
    }

    @PostMapping
    public String realizar(@ModelAttribute("form") EmprestimoForm form, Model model) {
        List<String> codigos = parseCodigos(form.getCodigosPatrimonio());
        try {
            Emprestimo emprestimo = emprestimoService.realizarEmprestimo(form.getMatricula(), codigos);
            return "redirect:/emprestimos/" + emprestimo.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("form", form);
            return "emprestimos/form";
        }
    }

    @GetMapping("/{id}")
    public String comprovante(@PathVariable Long id, Model model) {
        Emprestimo emprestimo = emprestimoService.buscarComItens(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado: " + id));
        long prazoFinalDias = ChronoUnit.DAYS.between(
                emprestimo.getDataEmprestimo(), emprestimo.getDataPrevistaDevolucao());
        model.addAttribute("emprestimo", emprestimo);
        model.addAttribute("prazoFinalDias", prazoFinalDias);
        return "emprestimos/comprovante";
    }

    private List<String> parseCodigos(String input) {
        if (input == null || input.isBlank()) return List.of();
        return Arrays.stream(input.split("[,\n\r]+"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }
}
