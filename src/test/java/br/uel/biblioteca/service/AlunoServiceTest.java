package br.uel.biblioteca.service;

import br.uel.biblioteca.dao.AlunoDAO;
import br.uel.biblioteca.model.Aluno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
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

    // Nota: os testes abaixo verificam delegação e contrato comportamental.
    // A normalização de acentos e o strip de CPF ocorrem em AlunoDAOImpl (native SQL)
    // e não são exercitados por testes de service com mock de DAO.

    @Test
    void buscarPorTermo_delegaAoDAO() {
        when(alunoDAO.buscarPorTermo("maria")).thenReturn(List.of(aluno));

        List<Aluno> resultado = alunoService.buscarPorTermo("maria");

        assertEquals(1, resultado.size());
        verify(alunoDAO).buscarPorTermo("maria");
    }

    @Test
    void buscarPorTermo_retornaListaVazia_quandoSemResultado() {
        when(alunoDAO.buscarPorTermo("inexistente")).thenReturn(Collections.emptyList());

        List<Aluno> resultado = alunoService.buscarPorTermo("inexistente");

        assertTrue(resultado.isEmpty());
        verify(alunoDAO).buscarPorTermo("inexistente");
    }

    @Test
    void buscarPorTermo_retornaAlunosInativos() {
        aluno.setAtivo(false);
        when(alunoDAO.buscarPorTermo("maria")).thenReturn(List.of(aluno));

        List<Aluno> resultado = alunoService.buscarPorTermo("maria");

        assertFalse(resultado.get(0).getAtivo(), "Service não deve filtrar inativos — filtro está na view");
        verify(alunoDAO).buscarPorTermo("maria");
    }

    @Test
    void buscarPorTermo_retornaAlunos_porNomeParcial() {
        when(alunoDAO.buscarPorTermo("mar")).thenReturn(List.of(aluno));

        List<Aluno> resultado = alunoService.buscarPorTermo("mar");

        assertFalse(resultado.isEmpty());
        verify(alunoDAO).buscarPorTermo("mar");
    }

    @Test
    void buscarPorTermo_retornaAlunos_porNomeSemAcento() {
        aluno.setNome("João Silva");
        when(alunoDAO.buscarPorTermo("joao")).thenReturn(List.of(aluno));

        List<Aluno> resultado = alunoService.buscarPorTermo("joao");

        assertFalse(resultado.isEmpty());
        assertEquals("João Silva", resultado.get(0).getNome());
        verify(alunoDAO).buscarPorTermo("joao");
    }

    @Test
    void buscarPorTermo_retornaAluno_porCpfExatoSemPontuacao() {
        aluno.setCpf("12345678900");
        when(alunoDAO.buscarPorTermo("12345678900")).thenReturn(List.of(aluno));

        List<Aluno> resultado = alunoService.buscarPorTermo("12345678900");

        assertFalse(resultado.isEmpty());
        assertEquals("12345678900", resultado.get(0).getCpf());
        verify(alunoDAO).buscarPorTermo("12345678900");
    }

    @Test
    void buscarPorTermo_retornaAluno_porCpfExatoComPontuacao() {
        // O strip de pontuação do CPF ocorre no AlunoDAOImpl; o service repassa o termo original
        aluno.setCpf("12345678900");
        when(alunoDAO.buscarPorTermo("123.456.789-00")).thenReturn(List.of(aluno));

        List<Aluno> resultado = alunoService.buscarPorTermo("123.456.789-00");

        assertFalse(resultado.isEmpty());
        assertEquals("12345678900", resultado.get(0).getCpf());
        verify(alunoDAO).buscarPorTermo("123.456.789-00");
    }

    @Test
    void buscarPorTermo_naoBuscaCpfParcial() {
        // Trecho de CPF com menos de 11 dígitos não deve retornar por match parcial de CPF
        when(alunoDAO.buscarPorTermo("456789")).thenReturn(Collections.emptyList());

        List<Aluno> resultado = alunoService.buscarPorTermo("456789");

        assertTrue(resultado.isEmpty(), "Trecho de CPF não deve retornar alunos por match parcial");
        verify(alunoDAO).buscarPorTermo("456789");
    }
}
