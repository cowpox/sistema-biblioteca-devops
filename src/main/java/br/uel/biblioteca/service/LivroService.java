package br.uel.biblioteca.service;

import br.uel.biblioteca.dao.LivroDAO;
import br.uel.biblioteca.model.Livro;
import br.uel.biblioteca.model.Titulo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LivroService {

    private final LivroDAO livroDAO;

    public LivroService(LivroDAO livroDAO) {
        this.livroDAO = livroDAO;
    }

    public Livro cadastrar(Livro livro) {
        livroDAO.buscarPorCodigoPatrimonio(livro.getCodigoPatrimonio()).ifPresent(existente -> {
            throw new IllegalArgumentException(
                    "Ja existe livro com o codigo de patrimonio: " + livro.getCodigoPatrimonio());
        });
        String isbn = livro.getTitulo() != null ? livro.getTitulo().getIsbn() : null;
        if (isbn != null && !isbn.isBlank()) {
            livroDAO.buscarPorIsbn(isbn).ifPresent(existente ->
                    livro.setTitulo(existente.getTitulo()));
        }
        Titulo titulo = livro.getTitulo();
        if (titulo != null) {
            if (titulo.getPrazo() == null) {
                titulo.setPrazo(7);
            } else if (titulo.getPrazo() < 1) {
                throw new IllegalArgumentException("Prazo de devolução deve ser um número inteiro positivo.");
            }
        }
        livro.setDisponivel(true);
        if (livro.getExemplarBiblioteca() == null) {
            livro.setExemplarBiblioteca(false);
        }
        return livroDAO.salvar(livro);
    }

    @Transactional(readOnly = true)
    public List<Livro> listarTodos() {
        return livroDAO.listarTodos();
    }

    @Transactional(readOnly = true)
    public Optional<Livro> buscarPorId(Long id) {
        return livroDAO.buscarPorId(id);
    }

    @Transactional(readOnly = true)
    public List<Livro> buscarPorTermo(String termo) {
        return livroDAO.buscarPorTermo(termo);
    }
}
