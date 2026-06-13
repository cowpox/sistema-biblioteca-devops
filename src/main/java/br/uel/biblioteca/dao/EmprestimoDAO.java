package br.uel.biblioteca.dao;

import br.uel.biblioteca.model.Emprestimo;

import java.util.List;

public interface EmprestimoDAO extends GenericDAO<Emprestimo> {

    List<Emprestimo> buscarPorAluno(Long alunoId);

    List<Emprestimo> buscarAtivos();
}
