package br.uel.biblioteca.dao;

import br.uel.biblioteca.model.Emprestimo;
import br.uel.biblioteca.model.ItemEmprestimo;

import java.util.List;
import java.util.Optional;

public interface EmprestimoDAO extends GenericDAO<Emprestimo> {

    List<Emprestimo> buscarPorAluno(Long alunoId);

    List<Emprestimo> buscarAtivos();

    // Carrega itens, livro e título em um único JOIN FETCH (necessário com open-in-view=false)
    Optional<Emprestimo> buscarComItens(Long id);

    // Localiza o item de empréstimo ativo (não devolvido) de um livro pelo patrimônio.
    // Carrega item + emprestimo + aluno + todos os itens do emprestimo + livro + titulo via JOIN FETCH.
    Optional<ItemEmprestimo> buscarItemAtivoByCodigoPatrimonio(String codigoPatrimonio);
}
