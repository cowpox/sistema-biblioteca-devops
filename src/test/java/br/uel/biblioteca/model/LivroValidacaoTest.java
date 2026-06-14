package br.uel.biblioteca.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LivroValidacaoTest {

    private static Validator validator;

    @BeforeAll
    static void configurar() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Set<String> mensagens(Set<ConstraintViolation<Livro>> violations, String campo) {
        return violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals(campo))
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }

    private Livro livroBase() {
        Titulo titulo = new Titulo();
        titulo.setNome("Clean Code");
        titulo.setIsbn("9788576082705");

        Livro livro = new Livro();
        livro.setCodigoPatrimonio("123456");
        livro.setTitulo(titulo);
        return livro;
    }

    // ── codigoPatrimonio ───────────────────────────────────────────────────

    @Test
    void codigoPatrimonio_valido() {
        assertTrue(validator.validate(livroBase()).isEmpty());
    }

    @Test
    void codigoPatrimonio_invalido_quandoVazio() {
        Livro l = livroBase();
        l.setCodigoPatrimonio("");
        Set<String> msgs = mensagens(validator.validate(l), "codigoPatrimonio");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("Código de patrimônio é obrigatório"));
    }

    @Test
    void codigoPatrimonio_invalido_quandoNull() {
        Livro l = livroBase();
        l.setCodigoPatrimonio(null);
        Set<String> msgs = mensagens(validator.validate(l), "codigoPatrimonio");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("Código de patrimônio é obrigatório"));
    }

    @Test
    void codigoPatrimonio_invalido_quandoContemLetras() {
        Livro l = livroBase();
        l.setCodigoPatrimonio("PAT001");
        Set<String> msgs = mensagens(validator.validate(l), "codigoPatrimonio");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("Código de patrimônio deve conter exatamente 6 dígitos numéricos"));
    }

    @Test
    void codigoPatrimonio_invalido_quandoMenosDe6Digitos() {
        Livro l = livroBase();
        l.setCodigoPatrimonio("12345");
        Set<String> msgs = mensagens(validator.validate(l), "codigoPatrimonio");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("Código de patrimônio deve conter exatamente 6 dígitos numéricos"));
    }

    // ── titulo.nome (cascata @Valid) ───────────────────────────────────────

    @Test
    void tituloNome_invalido_quandoVazio() {
        Livro l = livroBase();
        l.getTitulo().setNome("");
        Set<String> msgs = mensagens(validator.validate(l), "titulo.nome");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("Título da obra é obrigatório"));
    }

    @Test
    void tituloNome_invalido_quandoNull() {
        Livro l = livroBase();
        l.getTitulo().setNome(null);
        Set<String> msgs = mensagens(validator.validate(l), "titulo.nome");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("Título da obra é obrigatório"));
    }

    // ── titulo.isbn (cascata @Valid) ───────────────────────────────────────

    @Test
    void isbn_invalido_quandoVazio() {
        Livro l = livroBase();
        l.getTitulo().setIsbn("");
        Set<String> msgs = mensagens(validator.validate(l), "titulo.isbn");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("ISBN é obrigatório"));
    }

    @Test
    void isbn_invalido_quandoNull() {
        Livro l = livroBase();
        l.getTitulo().setIsbn(null);
        Set<String> msgs = mensagens(validator.validate(l), "titulo.isbn");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("ISBN é obrigatório"));
    }

    @Test
    void isbn_invalido_quandoMenosDe13Digitos() {
        Livro l = livroBase();
        l.getTitulo().setIsbn("978012345678");
        Set<String> msgs = mensagens(validator.validate(l), "titulo.isbn");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("ISBN deve conter exatamente 13 dígitos"));
    }

    @Test
    void isbn_invalido_quandoMaisDe13Digitos() {
        Livro l = livroBase();
        l.getTitulo().setIsbn("97801234567890");
        Set<String> msgs = mensagens(validator.validate(l), "titulo.isbn");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("ISBN deve conter exatamente 13 dígitos"));
    }

    // ── titulo.prazo (cascata @Valid) ─────────────────────────────────────

    @Test
    void prazo_valido_quandoNull() {
        Livro l = livroBase();
        l.getTitulo().setPrazo(null);
        assertTrue(mensagens(validator.validate(l), "titulo.prazo").isEmpty());
    }

    @Test
    void prazo_valido_quandoPositivo() {
        Livro l = livroBase();
        l.getTitulo().setPrazo(14);
        assertTrue(mensagens(validator.validate(l), "titulo.prazo").isEmpty());
    }

    @Test
    void prazo_invalido_quandoZero() {
        Livro l = livroBase();
        l.getTitulo().setPrazo(0);
        Set<String> msgs = mensagens(validator.validate(l), "titulo.prazo");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("Prazo de devolução deve ser um número inteiro positivo."));
    }

    @Test
    void prazo_invalido_quandoNegativo() {
        Livro l = livroBase();
        l.getTitulo().setPrazo(-1);
        Set<String> msgs = mensagens(validator.validate(l), "titulo.prazo");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("Prazo de devolução deve ser um número inteiro positivo."));
    }

    // ── titulo.ano (cascata @Valid) ────────────────────────────────────────

    @Test
    void ano_valido_quandoVazio() {
        Livro l = livroBase();
        l.getTitulo().setAno("");
        assertTrue(mensagens(validator.validate(l), "titulo.ano").isEmpty());
    }

    @Test
    void ano_valido_quandoNull() {
        Livro l = livroBase();
        l.getTitulo().setAno(null);
        assertTrue(mensagens(validator.validate(l), "titulo.ano").isEmpty());
    }

    @Test
    void ano_valido_quandoCom4Digitos() {
        Livro l = livroBase();
        l.getTitulo().setAno("2025");
        assertTrue(mensagens(validator.validate(l), "titulo.ano").isEmpty());
    }

    @Test
    void ano_invalido_quandoMenosDe4Digitos() {
        Livro l = livroBase();
        l.getTitulo().setAno("202");
        Set<String> msgs = mensagens(validator.validate(l), "titulo.ano");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("Ano deve conter exatamente 4 dígitos numéricos"));
    }

    @Test
    void ano_invalido_quandoMaisDe4Digitos() {
        Livro l = livroBase();
        l.getTitulo().setAno("20255");
        Set<String> msgs = mensagens(validator.validate(l), "titulo.ano");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("Ano deve conter exatamente 4 dígitos numéricos"));
    }

    @Test
    void ano_invalido_quandoContemLetras() {
        Livro l = livroBase();
        l.getTitulo().setAno("20a5");
        Set<String> msgs = mensagens(validator.validate(l), "titulo.ano");
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains("Ano deve conter exatamente 4 dígitos numéricos"));
    }
}
