package br.uel.biblioteca.service;

import br.uel.biblioteca.dao.AlunoDAO;
import br.uel.biblioteca.dao.DebitoDAO;
import br.uel.biblioteca.dao.EmprestimoDAO;
import br.uel.biblioteca.dao.LivroDAO;
import br.uel.biblioteca.model.Aluno;
import br.uel.biblioteca.model.Emprestimo;
import br.uel.biblioteca.model.ItemEmprestimo;
import br.uel.biblioteca.model.Livro;
import br.uel.biblioteca.model.Titulo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock private AlunoDAO alunoDAO;
    @Mock private LivroDAO livroDAO;
    @Mock private EmprestimoDAO emprestimoDAO;
    @Mock private DebitoDAO debitoDAO;

    @InjectMocks
    private EmprestimoService service;

    private Aluno aluno;
    private Livro livro1;
    private Emprestimo emprestimo;
    private ItemEmprestimo item;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setMatricula("12345678");
        aluno.setNome("Maria Souza");
        aluno.setAtivo(true);

        Titulo titulo1 = new Titulo();
        titulo1.setNome("Clean Code");
        titulo1.setPrazo(7);

        livro1 = new Livro();
        livro1.setId(1L);
        livro1.setCodigoPatrimonio("000001");
        livro1.setDisponivel(true);
        livro1.setExemplarBiblioteca(false);
        livro1.setTitulo(titulo1);

        emprestimo = new Emprestimo();
        emprestimo.setId(10L);
        emprestimo.setAluno(aluno);
        emprestimo.setDataEmprestimo(LocalDate.now().minusDays(10));
        emprestimo.setStatus("ATIVO");

        item = new ItemEmprestimo();
        item.setId(100L);
        item.setEmprestimo(emprestimo);
        item.setLivro(livro1);
        item.setDataPrevistaDevolucao(LocalDate.now().plusDays(4));
        emprestimo.getItens().add(item);
    }

    // -----------------------------------------------------------------------
    // Cenários de sucesso
    // -----------------------------------------------------------------------

    @Test
    void realizarEmprestimo_deveRealizarEmprestimo_comUmLivro() {
        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(aluno));
        when(debitoDAO.alunoTemDebitoAtivo(anyLong())).thenReturn(false);
        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(emprestimoDAO.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        Emprestimo e = service.realizarEmprestimo("12345678", List.of("000001"));

        assertNotNull(e);
        assertEquals(aluno, e.getAluno());
        assertEquals(1, e.getItens().size());
        assertEquals("ATIVO", e.getStatus());
        assertEquals(LocalDate.now().plusDays(7), e.getDataPrevistaDevolucao());
        assertFalse(livro1.getDisponivel(), "Livro deve ficar indisponível após empréstimo");
        verify(emprestimoDAO).salvar(any());
    }

    @Test
    void realizarEmprestimo_deveRealizarEmprestimo_comDoisLivros() {
        Livro livro2 = criarLivro("000002", 10);

        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(aluno));
        when(debitoDAO.alunoTemDebitoAtivo(anyLong())).thenReturn(false);
        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(livroDAO.buscarPorCodigoPatrimonio("000002")).thenReturn(Optional.of(livro2));
        when(emprestimoDAO.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        Emprestimo e = service.realizarEmprestimo("12345678", List.of("000001", "000002"));

        assertEquals(2, e.getItens().size());
        // 2 livros, prazos 7 e 10: max=10, acrescimo=0 → 10 dias
        assertEquals(LocalDate.now().plusDays(10), e.getDataPrevistaDevolucao());
    }

    @Test
    void realizarEmprestimo_deveAplicarAcrescimoDeDoisDias_comTresLivros() {
        Livro livro2 = criarLivro("000002", 10);
        Livro livro3 = criarLivro("000003", 5);

        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(aluno));
        when(debitoDAO.alunoTemDebitoAtivo(anyLong())).thenReturn(false);
        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(livroDAO.buscarPorCodigoPatrimonio("000002")).thenReturn(Optional.of(livro2));
        when(livroDAO.buscarPorCodigoPatrimonio("000003")).thenReturn(Optional.of(livro3));
        when(emprestimoDAO.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        Emprestimo e = service.realizarEmprestimo("12345678", List.of("000001", "000002", "000003"));

        assertEquals(3, e.getItens().size());
        // 3 livros, prazos 7, 10, 5: max=10, acrescimo=(3-2)*2=2 → 12 dias
        assertEquals(LocalDate.now().plusDays(12), e.getDataPrevistaDevolucao());
    }

    @Test
    void realizarEmprestimo_deveAplicarAcrescimoDeQuatroDias_comQuatroLivros() {
        Livro livro2 = criarLivro("000002", 10);
        Livro livro3 = criarLivro("000003", 5);
        Livro livro4 = criarLivro("000004", 8);

        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(aluno));
        when(debitoDAO.alunoTemDebitoAtivo(anyLong())).thenReturn(false);
        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(livroDAO.buscarPorCodigoPatrimonio("000002")).thenReturn(Optional.of(livro2));
        when(livroDAO.buscarPorCodigoPatrimonio("000003")).thenReturn(Optional.of(livro3));
        when(livroDAO.buscarPorCodigoPatrimonio("000004")).thenReturn(Optional.of(livro4));
        when(emprestimoDAO.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        Emprestimo e = service.realizarEmprestimo("12345678",
                List.of("000001", "000002", "000003", "000004"));

        assertEquals(4, e.getItens().size());
        // 4 livros, prazos 7, 10, 5, 8: max=10, acrescimo=(4-2)*2=4 → 14 dias
        assertEquals(LocalDate.now().plusDays(14), e.getDataPrevistaDevolucao());
    }

    @Test
    void realizarEmprestimo_deveUsarMaiorPrazo_naCalculoPrazoFinal() {
        livro1.getTitulo().setPrazo(5);
        Livro livroB = criarLivro("000002", 15);

        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(aluno));
        when(debitoDAO.alunoTemDebitoAtivo(anyLong())).thenReturn(false);
        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(livroDAO.buscarPorCodigoPatrimonio("000002")).thenReturn(Optional.of(livroB));
        when(emprestimoDAO.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        Emprestimo e = service.realizarEmprestimo("12345678", List.of("000001", "000002"));

        // prazo do livro menor (5) não deve prevalecer; deve usar o maior (15)
        assertEquals(LocalDate.now().plusDays(15), e.getDataPrevistaDevolucao());
    }

    // -----------------------------------------------------------------------
    // Fluxos alternativos — aluno
    // -----------------------------------------------------------------------

    @Test
    void realizarEmprestimo_deveLancarExcecao_quandoAlunoNaoCadastrado() {
        when(alunoDAO.buscarPorMatricula("99999999")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.realizarEmprestimo("99999999", List.of("000001")));

        assertTrue(ex.getMessage().contains("99999999"));
        verify(emprestimoDAO, never()).salvar(any());
    }

    @Test
    void realizarEmprestimo_deveLancarExcecao_quandoAlunoInativo() {
        aluno.setAtivo(false);
        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(aluno));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.realizarEmprestimo("12345678", List.of("000001")));

        assertTrue(ex.getMessage().toLowerCase().contains("inativo"));
        verify(emprestimoDAO, never()).salvar(any());
    }

    @Test
    void realizarEmprestimo_deveLancarExcecao_quandoAlunoTemDebitoAtivo() {
        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(aluno));
        when(debitoDAO.alunoTemDebitoAtivo(anyLong())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.realizarEmprestimo("12345678", List.of("000001")));

        assertTrue(ex.getMessage().toLowerCase().contains("débito"));
        verify(emprestimoDAO, never()).salvar(any());
    }

    // -----------------------------------------------------------------------
    // Fluxos alternativos — livro
    // -----------------------------------------------------------------------

    @Test
    void realizarEmprestimo_deveLancarExcecao_quandoLivroNaoEncontrado() {
        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(aluno));
        when(debitoDAO.alunoTemDebitoAtivo(anyLong())).thenReturn(false);
        when(livroDAO.buscarPorCodigoPatrimonio("XXXXX")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.realizarEmprestimo("12345678", List.of("XXXXX")));

        assertTrue(ex.getMessage().contains("XXXXX"));
        verify(emprestimoDAO, never()).salvar(any());
    }

    @Test
    void realizarEmprestimo_deveLancarExcecao_quandoLivroIndisponivel() {
        livro1.setDisponivel(false);
        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(aluno));
        when(debitoDAO.alunoTemDebitoAtivo(anyLong())).thenReturn(false);
        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.realizarEmprestimo("12345678", List.of("000001")));

        assertTrue(ex.getMessage().toLowerCase().contains("indisponível")
                || ex.getMessage().toLowerCase().contains("indisponivel"));
        verify(emprestimoDAO, never()).salvar(any());
    }

    @Test
    void realizarEmprestimo_deveLancarExcecao_quandoLivroEExemplarBiblioteca() {
        livro1.setExemplarBiblioteca(true);
        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(aluno));
        when(debitoDAO.alunoTemDebitoAtivo(anyLong())).thenReturn(false);
        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.realizarEmprestimo("12345678", List.of("000001")));

        assertTrue(ex.getMessage().toLowerCase().contains("exemplar"));
        verify(emprestimoDAO, never()).salvar(any());
    }

    @Test
    void realizarEmprestimo_deveLancarExcecao_quandoCodigoPatrimonioDuplicado() {
        when(alunoDAO.buscarPorMatricula("12345678")).thenReturn(Optional.of(aluno));
        when(debitoDAO.alunoTemDebitoAtivo(anyLong())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.realizarEmprestimo("12345678", List.of("000001", "000001")));

        assertTrue(ex.getMessage().toLowerCase().contains("duplicado"));
        verify(emprestimoDAO, never()).salvar(any());
    }

    // -----------------------------------------------------------------------
    // devolverLivro — cenários de sucesso
    // -----------------------------------------------------------------------

    @Test
    void devolverLivro_deveDevolverLivro_semAtraso() {
        livro1.setDisponivel(false);
        LocalDate dataDevolucao = item.getDataPrevistaDevolucao(); // devolução no dia exato

        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(emprestimoDAO.buscarItemAtivoByCodigoPatrimonio("000001")).thenReturn(Optional.of(item));
        when(livroDAO.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(emprestimoDAO.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        ResultadoDevolucao res = service.devolverLivro("000001", dataDevolucao);

        assertEquals(0, res.getDiasAtraso());
        assertEquals(BigDecimal.ZERO, res.getValorMulta());
        assertEquals(dataDevolucao, res.getDataDevolucao());
        assertEquals(dataDevolucao, item.getDataDevolucao());
        assertTrue(livro1.getDisponivel(), "Livro deve voltar a ficar disponível");
        verify(debitoDAO, never()).salvar(any());
    }

    @Test
    void devolverLivro_deveDevolverLivro_comAtraso() {
        livro1.setDisponivel(false);
        item.setDataPrevistaDevolucao(LocalDate.now().minusDays(3));
        LocalDate dataDevolucao = LocalDate.now();

        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(emprestimoDAO.buscarItemAtivoByCodigoPatrimonio("000001")).thenReturn(Optional.of(item));
        when(livroDAO.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(emprestimoDAO.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(debitoDAO.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        ResultadoDevolucao res = service.devolverLivro("000001", dataDevolucao);

        assertEquals(3, res.getDiasAtraso());
        assertEquals(new BigDecimal("3"), res.getValorMulta());
        verify(debitoDAO).salvar(any());
    }

    @Test
    void devolverLivro_deveLiberarLivro_aposDevolver() {
        livro1.setDisponivel(false);
        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(emprestimoDAO.buscarItemAtivoByCodigoPatrimonio("000001")).thenReturn(Optional.of(item));
        when(livroDAO.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(emprestimoDAO.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.devolverLivro("000001", LocalDate.now());

        assertTrue(livro1.getDisponivel());
        verify(livroDAO).atualizar(livro1);
    }

    @Test
    void devolverLivro_deveEncerrarEmprestimo_quandoUnicoItemDevolvido() {
        livro1.setDisponivel(false);
        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(emprestimoDAO.buscarItemAtivoByCodigoPatrimonio("000001")).thenReturn(Optional.of(item));
        when(livroDAO.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(emprestimoDAO.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDate dataDevolucao = LocalDate.now();
        ResultadoDevolucao res = service.devolverLivro("000001", dataDevolucao);

        assertTrue(res.isEmprestimoEncerrado());
        assertEquals("ENCERRADO", emprestimo.getStatus());
        assertEquals(dataDevolucao, emprestimo.getDataDevolucao());
        verify(emprestimoDAO).atualizar(emprestimo);
    }

    @Test
    void devolverLivro_naoDeveEncerrarEmprestimo_quandoHouverOutroItemPendente() {
        livro1.setDisponivel(false);
        Livro livro2 = criarLivro("000002", 7);
        livro2.setDisponivel(false);
        ItemEmprestimo item2 = new ItemEmprestimo();
        item2.setId(101L);
        item2.setEmprestimo(emprestimo);
        item2.setLivro(livro2);
        item2.setDataPrevistaDevolucao(LocalDate.now().plusDays(4));
        emprestimo.getItens().add(item2); // emprestimo agora tem 2 itens

        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(emprestimoDAO.buscarItemAtivoByCodigoPatrimonio("000001")).thenReturn(Optional.of(item));
        when(livroDAO.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        ResultadoDevolucao res = service.devolverLivro("000001", LocalDate.now());

        assertFalse(res.isEmprestimoEncerrado());
        assertEquals("ATIVO", emprestimo.getStatus());
        assertNull(emprestimo.getDataDevolucao());
        verify(emprestimoDAO, never()).atualizar(any());
    }

    // -----------------------------------------------------------------------
    // devolverLivro — fluxos alternativos
    // -----------------------------------------------------------------------

    @Test
    void devolverLivro_deveLancarExcecao_quandoLivroNaoEncontrado() {
        when(livroDAO.buscarPorCodigoPatrimonio("999999")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.devolverLivro("999999", LocalDate.now()));

        assertTrue(ex.getMessage().contains("999999"));
        verify(emprestimoDAO, never()).buscarItemAtivoByCodigoPatrimonio(any());
        verify(debitoDAO, never()).salvar(any());
    }

    @Test
    void devolverLivro_deveLancarExcecao_quandoLivroNaoEmprestado() {
        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(emprestimoDAO.buscarItemAtivoByCodigoPatrimonio("000001")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.devolverLivro("000001", LocalDate.now()));

        assertTrue(ex.getMessage().toLowerCase().contains("empréstimo ativo")
                || ex.getMessage().toLowerCase().contains("emprestimo ativo"));
        verify(debitoDAO, never()).salvar(any());
        verify(livroDAO, never()).atualizar(any());
    }

    @Test
    void devolverLivro_naoDeveGerarDebito_quandoDevolucaoNoPrazo() {
        livro1.setDisponivel(false);
        item.setDataPrevistaDevolucao(LocalDate.now().plusDays(2)); // prazo ainda não vencido

        when(livroDAO.buscarPorCodigoPatrimonio("000001")).thenReturn(Optional.of(livro1));
        when(emprestimoDAO.buscarItemAtivoByCodigoPatrimonio("000001")).thenReturn(Optional.of(item));
        when(livroDAO.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(emprestimoDAO.atualizar(any())).thenAnswer(inv -> inv.getArgument(0));

        ResultadoDevolucao res = service.devolverLivro("000001", LocalDate.now());

        assertEquals(0, res.getDiasAtraso());
        verify(debitoDAO, never()).salvar(any());
    }

    // -----------------------------------------------------------------------
    // Auxiliar
    // -----------------------------------------------------------------------

    private Livro criarLivro(String codigo, int prazo) {
        Titulo t = new Titulo();
        t.setNome("Título " + codigo);
        t.setPrazo(prazo);

        Livro l = new Livro();
        l.setCodigoPatrimonio(codigo);
        l.setDisponivel(true);
        l.setExemplarBiblioteca(false);
        l.setTitulo(t);
        return l;
    }
}
