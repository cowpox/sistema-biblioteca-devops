package br.uel.biblioteca.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interface genérica DAO — define o contrato CRUD para qualquer entidade.
 * Inspirada no Trecho 13.13 (Cap. 13, Menolli 2025): GenericDAO<T>.
 * Services devem depender desta interface (DIP — Cap. 10).
 */
public interface GenericDAO<T> {

    T salvar(T entity);

    Optional<T> buscarPorId(Long id);

    List<T> listarTodos();

    T atualizar(T entity);

    void excluir(Long id);
}
