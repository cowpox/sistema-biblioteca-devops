package br.uel.biblioteca.dao.impl;

import br.uel.biblioteca.dao.LivroDAO;
import br.uel.biblioteca.model.Livro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class LivroDAOImpl implements LivroDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Livro salvar(Livro livro) {
        em.persist(livro);
        return livro;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Livro> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Livro.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Livro> listarTodos() {
        return em.createQuery("SELECT l FROM Livro l", Livro.class)
                .getResultList();
    }

    @Override
    public Livro atualizar(Livro livro) {
        return em.merge(livro);
    }

    @Override
    public void excluir(Long id) {
        Livro livro = em.find(Livro.class, id);
        if (livro != null) {
            em.remove(livro);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Livro> buscarDisponiveis() {
        return em.createQuery(
                "SELECT l FROM Livro l WHERE l.disponivel = true" +
                " AND (l.exemplarBiblioteca IS NULL OR l.exemplarBiblioteca = false)",
                Livro.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Livro> buscarPorCodigoPatrimonio(String codigoPatrimonio) {
        List<Livro> resultado = em.createQuery(
                "SELECT l FROM Livro l WHERE l.codigoPatrimonio = :codigo", Livro.class)
                .setParameter("codigo", codigoPatrimonio)
                .getResultList();
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Livro> buscarPorIsbn(String isbn) {
        List<Livro> resultado = em.createQuery(
                "SELECT l FROM Livro l WHERE l.titulo.isbn = :isbn", Livro.class)
                .setParameter("isbn", isbn)
                .getResultList();
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Livro> buscarPorTermo(String termo) {
        String semAcento = Normalizer.normalize(termo.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "");
        String like = "%" + semAcento + "%";
        // Native SQL: FUNCTION('translate') em JPQL retorna Object no Hibernate 6, sem suporte a LIKE.
        // Native query evita inferência de tipo e é compatível com H2 (testes) e PostgreSQL (produção).
        return em.createNativeQuery(
                "SELECT l.* FROM livro l JOIN titulo t ON l.titulo_id = t.id WHERE " +
                "translate(lower(t.nome), 'áàãâäéèêëíìîïóòõôöúùûüçñý', 'aaaaaeeeeiiiiooooouuuucny') LIKE :like " +
                "OR translate(lower(t.autor), 'áàãâäéèêëíìîïóòõôöúùûüçñý', 'aaaaaeeeeiiiiooooouuuucny') LIKE :like " +
                "OR t.isbn = :isbn",
                Livro.class)
                .setParameter("like", like)
                .setParameter("isbn", termo)
                .getResultList();
    }
}
