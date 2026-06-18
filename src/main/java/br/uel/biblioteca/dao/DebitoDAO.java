package br.uel.biblioteca.dao;

import br.uel.biblioteca.model.Debito;

import java.util.List;

public interface DebitoDAO extends GenericDAO<Debito> {

    List<Debito> buscarDebitosAtivosPorAluno(Long alunoId);

    boolean alunoTemDebitoAtivo(Long alunoId);
}
