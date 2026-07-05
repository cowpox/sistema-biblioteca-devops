package br.uel.biblioteca.controller;

import br.uel.biblioteca.model.Aluno;
import br.uel.biblioteca.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("alunos", alunoService.listarTodos());
        return "alunos/lista";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) String matricula,
                         @RequestParam(required = false) String codigosPatrimonio,
                         Model model) {
        List<Aluno> resultados = (q != null && !q.isBlank())
                ? alunoService.buscarPorTermo(q.trim())
                : List.of();
        model.addAttribute("resultados", resultados);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("matricula", matricula != null ? matricula : "");
        model.addAttribute("codigosPatrimonio", codigosPatrimonio != null ? codigosPatrimonio : "");
        return "alunos/buscar";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "alunos/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute Aluno aluno,
                         BindingResult result,
                         RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            return "alunos/form";
        }
        try {
            alunoService.cadastrar(aluno);
            redirectAttrs.addFlashAttribute("sucesso", "Aluno cadastrado com sucesso.");
        } catch (IllegalArgumentException e) {
            result.reject("erro.negocio", e.getMessage());
            return "alunos/form";
        }
        return "redirect:/alunos";
    }
}
