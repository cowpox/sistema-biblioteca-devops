package br.uel.biblioteca.controller;

import br.uel.biblioteca.model.Emprestimo;
import br.uel.biblioteca.service.EmprestimoService;
import br.uel.biblioteca.service.ResultadoDevolucao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
    public String novo(@RequestParam(required = false) String codigosPatrimonio,
                       @RequestParam(required = false) String matricula,
                       Model model) {
        EmprestimoForm form = new EmprestimoForm();
        if (codigosPatrimonio != null && !codigosPatrimonio.isBlank()) {
            form.setCodigosPatrimonio(codigosPatrimonio.trim());
        }
        if (matricula != null && !matricula.isBlank()) {
            form.setMatricula(matricula.trim());
        }
        model.addAttribute("form", form);
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

    @GetMapping("/devolver")
    public String mostrarFormularioDevolucao() {
        return "emprestimos/devolver";
    }

    @PostMapping("/devolver")
    public String realizarDevolucao(@RequestParam String codigoPatrimonio,
                                     RedirectAttributes redirectAttrs, Model model) {
        try {
            ResultadoDevolucao resultado = emprestimoService.devolverLivro(
                    codigoPatrimonio.trim(), LocalDate.now());
            redirectAttrs.addFlashAttribute("resultado", resultado);
            return "redirect:/emprestimos/devolver/resultado";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("codigoPatrimonio", codigoPatrimonio);
            return "emprestimos/devolver";
        }
    }

    @GetMapping("/devolver/resultado")
    public String resultadoDevolucao(Model model) {
        if (!model.containsAttribute("resultado")) {
            return "redirect:/emprestimos/devolver";
        }
        return "emprestimos/comprovante-devolucao";
    }

    private List<String> parseCodigos(String input) {
        if (input == null || input.isBlank()) return List.of();
        return Arrays.stream(input.split("[,\n\r]+"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }
}
