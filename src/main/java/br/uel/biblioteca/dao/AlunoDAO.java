package br.uel.biblioteca.dao;

import br.uel.biblioteca.model.Aluno;

import java.util.List;
import java.util.Optional;

public interface AlunoDAO extends GenericDAO<Aluno> {

    Optional<Aluno> buscarPorMatricula(String matricula);

    List<Aluno> buscarAtivos();
}
