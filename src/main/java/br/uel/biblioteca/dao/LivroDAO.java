package br.uel.biblioteca.dao;

import br.uel.biblioteca.model.Livro;

import java.util.List;
import java.util.Optional;

public interface LivroDAO extends GenericDAO<Livro> {

    List<Livro> buscarDisponiveis();

    Optional<Livro> buscarPorCodigoPatrimonio(String codigoPatrimonio);

    Optional<Livro> buscarPorIsbn(String isbn);

    List<Livro> buscarPorTermo(String termo);
}
