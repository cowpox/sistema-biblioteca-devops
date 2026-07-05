package br.uel.biblioteca.dao.impl;

import br.uel.biblioteca.dao.EmprestimoDAO;
import br.uel.biblioteca.model.Emprestimo;
import br.uel.biblioteca.model.ItemEmprestimo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class EmprestimoDAOImpl implements EmprestimoDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Emprestimo salvar(Emprestimo emprestimo) {
        em.persist(emprestimo);
        return emprestimo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Emprestimo> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Emprestimo.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Emprestimo> listarTodos() {
        return em.createQuery("SELECT e FROM Emprestimo e", Emprestimo.class)
                .getResultList();
    }

    @Override
    public Emprestimo atualizar(Emprestimo emprestimo) {
        return em.merge(emprestimo);
    }

    @Override
    public void excluir(Long id) {
        Emprestimo emprestimo = em.find(Emprestimo.class, id);
        if (emprestimo != null) {
            em.remove(emprestimo);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Emprestimo> buscarPorAluno(Long alunoId) {
        return em.createQuery(
                "SELECT e FROM Emprestimo e WHERE e.aluno.id = :alunoId", Emprestimo.class)
                .setParameter("alunoId", alunoId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Emprestimo> buscarAtivos() {
        return em.createQuery(
                "SELECT e FROM Emprestimo e WHERE e.dataDevolucao IS NULL", Emprestimo.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Emprestimo> buscarComItens(Long id) {
        List<Emprestimo> resultado = em.createQuery(
                "SELECT DISTINCT e FROM Emprestimo e" +
                " LEFT JOIN FETCH e.itens i" +
                " LEFT JOIN FETCH i.livro l" +
                " LEFT JOIN FETCH l.titulo" +
                " WHERE e.id = :id",
                Emprestimo.class)
                .setParameter("id", id)
                .getResultList();
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ItemEmprestimo> buscarItemAtivoByCodigoPatrimonio(String codigoPatrimonio) {
        List<ItemEmprestimo> resultado = em.createQuery(
                "SELECT DISTINCT i FROM ItemEmprestimo i" +
                " JOIN FETCH i.emprestimo e" +
                " JOIN FETCH e.aluno" +
                " LEFT JOIN FETCH e.itens" +
                " JOIN FETCH i.livro l" +
                " JOIN FETCH l.titulo" +
                " WHERE l.codigoPatrimonio = :codigo" +
                " AND i.dataDevolucao IS NULL",
                ItemEmprestimo.class)
                .setParameter("codigo", codigoPatrimonio)
                .getResultList();
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }
}
