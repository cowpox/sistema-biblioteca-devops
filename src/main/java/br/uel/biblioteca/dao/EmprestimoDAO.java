package br.uel.biblioteca.dao;

import br.uel.biblioteca.model.Emprestimo;

import java.util.List;
import java.util.Optional;

public interface EmprestimoDAO extends GenericDAO<Emprestimo> {

    List<Emprestimo> buscarPorAluno(Long alunoId);

    List<Emprestimo> buscarAtivos();

    // Carrega itens, livro e título em um único JOIN FETCH (necessário com open-in-view=false)
    Optional<Emprestimo> buscarComItens(Long id);
}
