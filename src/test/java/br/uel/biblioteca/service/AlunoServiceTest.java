package br.uel.biblioteca.service;

import br.uel.biblioteca.dao.AlunoDAO;
import br.uel.biblioteca.model.Aluno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoDAO alunoDAO;

    @InjectMocks
    private AlunoService alunoService;

    private Aluno aluno;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setMatricula("12345678");
        aluno.setNome("Maria Souza");
    }

    @Test
    void cadastrar_deveSalvarAluno_quandoMatriculaNaoExiste() {
        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.empty());
        when(alunoDAO.salvar(any(Aluno.class))).thenAnswer(inv -> inv.getArgument(0));

        Aluno salvo = alunoService.cadastrar(aluno);

        assertNotNull(salvo);
        assertTrue(salvo.getAtivo());
        verify(alunoDAO).salvar(aluno);
    }

    @Test
    void cadastrar_deveLancarExcecao_quandoMatriculaJaCadastrada() {
        Aluno existente = new Aluno();
        existente.setMatricula("12345678");
        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(existente));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> alunoService.cadastrar(aluno)
        );

        assertTrue(ex.getMessage().contains("12345678"));
        verify(alunoDAO, never()).salvar(any());
    }

    @Test
    void cadastrar_deveLancarExcecao_quandoCpfJaCadastrado() {
        aluno.setCpf("12345678901");
        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.empty());
        when(alunoDAO.buscarPorCpf("12345678901")).thenReturn(Optional.of(new Aluno()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> alunoService.cadastrar(aluno)
        );

        assertTrue(ex.getMessage().contains("CPF"));
        verify(alunoDAO, never()).salvar(any());
    }

    @Test
    void cadastrar_deveNormalizarCamposOpcionaisVaziosParaNull() {
        aluno.setCpf("");
        aluno.setEmail("  ");
        aluno.setEndereco("");
        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.empty());
        when(alunoDAO.salvar(any(Aluno.class))).thenAnswer(inv -> inv.getArgument(0));

        Aluno salvo = alunoService.cadastrar(aluno);

        assertNull(salvo.getCpf(), "CPF vazio deve ser null");
        assertNull(salvo.getEmail(), "E-mail em branco deve ser null");
        assertNull(salvo.getEndereco(), "Endereço vazio deve ser null");
    }
}
