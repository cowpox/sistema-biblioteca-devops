package br.uel.biblioteca.dao.impl;

import br.uel.biblioteca.dao.AlunoDAO;
import br.uel.biblioteca.model.Aluno;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class AlunoDAOImpl implements AlunoDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Aluno salvar(Aluno aluno) {
        em.persist(aluno);
        return aluno;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Aluno> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Aluno.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Aluno> listarTodos() {
        return em.createQuery("SELECT a FROM Aluno a", Aluno.class)
                .getResultList();
    }

    @Override
    public Aluno atualizar(Aluno aluno) {
        return em.merge(aluno);
    }

    @Override
    public void excluir(Long id) {
        Aluno aluno = em.find(Aluno.class, id);
        if (aluno != null) {
            em.remove(aluno);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Aluno> buscarPorMatricula(String matricula) {
        List<Aluno> resultado = em.createQuery(
                "SELECT a FROM Aluno a WHERE a.matricula = :matricula", Aluno.class)
                .setParameter("matricula", matricula)
                .getResultList();
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Aluno> buscarPorCpf(String cpf) {
        List<Aluno> resultado = em.createQuery(
                "SELECT a FROM Aluno a WHERE a.cpf = :cpf", Aluno.class)
                .setParameter("cpf", cpf)
                .getResultList();
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Aluno> buscarAtivos() {
        return em.createQuery(
                "SELECT a FROM Aluno a WHERE a.ativo = true", Aluno.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Aluno> buscarPorTermo(String termo) {
        String semAcento = Normalizer.normalize(termo.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "");
        String like = "%" + semAcento + "%";
        String cpfNormalizado = termo.replaceAll("[^0-9]", "");
        boolean buscaCpf = cpfNormalizado.length() == 11;

        // Native SQL: evita problema de inferência de tipo do Hibernate 6 com translate().
        // Busca por CPF só é ativada quando o termo (após strip de não-dígitos) tiver exatamente 11 dígitos.
        String sql = "SELECT * FROM aluno WHERE " +
                "translate(lower(nome), 'áàãâäéèêëíìîïóòõôöúùûüçñý', 'aaaaaeeeeiiiiooooouuuucny') LIKE :like" +
                (buscaCpf ? " OR cpf = :cpf" : "");

        var query = em.createNativeQuery(sql, Aluno.class)
                .setParameter("like", like);
        if (buscaCpf) {
            query.setParameter("cpf", cpfNormalizado);
        }
        return query.getResultList();
    }
}
