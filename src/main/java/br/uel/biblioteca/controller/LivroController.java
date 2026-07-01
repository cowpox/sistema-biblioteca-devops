package br.uel.biblioteca.controller;

import br.uel.biblioteca.model.Livro;
import br.uel.biblioteca.model.Titulo;
import br.uel.biblioteca.service.LivroService;
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
@RequestMapping("/livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("livros", livroService.listarTodos());
        return "livros/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Livro livro = new Livro();
        livro.setTitulo(new Titulo());
        model.addAttribute("livro", livro);
        return "livros/form";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) String matricula,
                         @RequestParam(required = false) String codigosPatrimonio,
                         Model model) {
        List<Livro> resultados = (q != null && !q.isBlank())
                ? livroService.buscarPorTermo(q.trim())
                : List.of();
        model.addAttribute("resultados", resultados);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("matricula", matricula != null ? matricula : "");
        model.addAttribute("codigosPatrimonio", codigosPatrimonio != null ? codigosPatrimonio : "");
        return "livros/buscar";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute Livro livro,
                         BindingResult result,
                         RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            return "livros/form";
        }
        try {
            livroService.cadastrar(livro);
            redirectAttrs.addFlashAttribute("sucesso", "Livro cadastrado com sucesso.");
        } catch (IllegalArgumentException e) {
            result.reject("erro.negocio", e.getMessage());
            return "livros/form";
        }
        return "redirect:/livros";
    }
}
