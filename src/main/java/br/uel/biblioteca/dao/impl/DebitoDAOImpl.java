package br.uel.biblioteca.dao.impl;

import br.uel.biblioteca.dao.DebitoDAO;
import br.uel.biblioteca.model.Debito;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class DebitoDAOImpl implements DebitoDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Debito salvar(Debito debito) {
        em.persist(debito);
        return debito;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Debito> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Debito.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Debito> listarTodos() {
        return em.createQuery("SELECT d FROM Debito d", Debito.class)
                .getResultList();
    }

    @Override
    public Debito atualizar(Debito debito) {
        return em.merge(debito);
    }

    @Override
    public void excluir(Long id) {
        Debito debito = em.find(Debito.class, id);
        if (debito != null) {
            em.remove(debito);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Debito> buscarDebitosAtivosPorAluno(Long alunoId) {
        return em.createQuery(
                "SELECT d FROM Debito d WHERE d.aluno.id = :alunoId" +
                " AND (d.pago IS NULL OR d.pago = false)",
                Debito.class)
                .setParameter("alunoId", alunoId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean alunoTemDebitoAtivo(Long alunoId) {
        Long count = em.createQuery(
                "SELECT COUNT(d) FROM Debito d WHERE d.aluno.id = :alunoId" +
                " AND (d.pago IS NULL OR d.pago = false)",
                Long.class)
                .setParameter("alunoId", alunoId)
                .getSingleResult();
        return count > 0;
    }
}
