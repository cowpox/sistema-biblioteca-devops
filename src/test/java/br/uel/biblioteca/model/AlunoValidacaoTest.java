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

class AlunoValidacaoTest {

    private static Validator validator;

    @BeforeAll
    static void configurar() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Set<String> mensagens(Set<ConstraintViolation<Aluno>> violations, String campo) {
        return violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals(campo))
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }

    private Aluno alunoBase() {
        Aluno a = new Aluno();
        a.setMatricula("12345678");
        a.setNome("Maria Souza");
        return a;
    }

    // ── Matrícula ──────────────────────────────────────────────────────────

    @Test
    void matricula_valida_comOitoDigitos() {
        Aluno a = alunoBase();
        assertTrue(validator.validateProperty(a, "matricula").isEmpty());
    }

    @Test
    void matricula_invalida_quandoVazia() {
        Aluno a = alunoBase();
        a.setMatricula("");
        Set<String> msgs = mensagens(validator.validateProperty(a, "matricula"), "matricula");
        assertTrue(msgs.contains("Matrícula é obrigatória"), "Esperava mensagem de obrigatoriedade");
    }

    @Test
    void matricula_invalida_quandoContemLetras() {
        Aluno a = alunoBase();
        a.setMatricula("RA123456");
        Set<String> msgs = mensagens(validator.validateProperty(a, "matricula"), "matricula");
        assertEquals(1, msgs.size(), "Deve exibir exatamente uma mensagem de erro");
        assertTrue(msgs.contains("Matrícula deve conter 8 dígitos numéricos"));
    }

    @Test
    void matricula_invalida_quandoMenosDeOitoDigitos() {
        Aluno a = alunoBase();
        a.setMatricula("1234567");
        Set<String> msgs = mensagens(validator.validateProperty(a, "matricula"), "matricula");
        assertEquals(1, msgs.size(), "Deve exibir exatamente uma mensagem de erro");
        assertTrue(msgs.contains("Matrícula deve conter 8 dígitos numéricos"));
    }

    @Test
    void matricula_invalida_quandoMaisDeOitoDigitos() {
        Aluno a = alunoBase();
        a.setMatricula("123456789");
        Set<String> msgs = mensagens(validator.validateProperty(a, "matricula"), "matricula");
        assertEquals(1, msgs.size(), "Deve exibir exatamente uma mensagem de erro");
        assertTrue(msgs.contains("Matrícula deve conter 8 dígitos numéricos"));
    }

    // ── CPF ────────────────────────────────────────────────────────────────

    @Test
    void cpf_valido_quandoVazio() {
        Aluno a = alunoBase();
        a.setCpf("");
        assertTrue(validator.validateProperty(a, "cpf").isEmpty(), "CPF vazio deve ser permitido");
    }

    @Test
    void cpf_valido_quandoNull() {
        Aluno a = alunoBase();
        a.setCpf(null);
        assertTrue(validator.validateProperty(a, "cpf").isEmpty(), "CPF null deve ser permitido");
    }

    @Test
    void cpf_valido_comOnzeDigitos() {
        Aluno a = alunoBase();
        a.setCpf("12345678901");
        assertTrue(validator.validateProperty(a, "cpf").isEmpty());
    }

    @Test
    void cpf_invalido_quandoContemLetras() {
        Aluno a = alunoBase();
        a.setCpf("1234567890A");
        Set<String> msgs = mensagens(validator.validateProperty(a, "cpf"), "cpf");
        assertEquals(1, msgs.size(), "Deve exibir exatamente uma mensagem de erro");
        assertTrue(msgs.contains("CPF deve conter exatamente 11 dígitos numéricos"));
    }

    @Test
    void cpf_invalido_quandoMenosDeOnzeDigitos() {
        Aluno a = alunoBase();
        a.setCpf("1234567890");
        Set<String> msgs = mensagens(validator.validateProperty(a, "cpf"), "cpf");
        assertEquals(1, msgs.size(), "Deve exibir exatamente uma mensagem de erro");
        assertTrue(msgs.contains("CPF deve conter exatamente 11 dígitos numéricos"));
    }

    @Test
    void cpf_invalido_quandoMaisDeOnzeDigitos() {
        Aluno a = alunoBase();
        a.setCpf("123456789012");
        Set<String> msgs = mensagens(validator.validateProperty(a, "cpf"), "cpf");
        assertEquals(1, msgs.size(), "Deve exibir exatamente uma mensagem de erro");
        assertTrue(msgs.contains("CPF deve conter exatamente 11 dígitos numéricos"));
    }
}
