package br.uel.biblioteca.service;

import br.uel.biblioteca.dao.LivroDAO;
import br.uel.biblioteca.model.Livro;
import br.uel.biblioteca.model.Titulo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroDAO livroDAO;

    @InjectMocks
    private LivroService livroService;

    private Livro livro;

    @BeforeEach
    void setUp() {
        Titulo titulo = new Titulo();
        titulo.setNome("Clean Code");

        livro = new Livro();
        livro.setCodigoPatrimonio("123456");
        livro.setTitulo(titulo);
    }

    @Test
    void cadastrar_deveSalvarLivro_quandoCodigoNaoExiste() {
        when(livroDAO.buscarPorCodigoPatrimonio("123456")).thenReturn(Optional.empty());
        when(livroDAO.salvar(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro salvo = livroService.cadastrar(livro);

        assertNotNull(salvo);
        assertTrue(salvo.getDisponivel());
        verify(livroDAO).salvar(livro);
    }

    @Test
    void cadastrar_deveLancarExcecao_quandoCodigoDuplicado() {
        when(livroDAO.buscarPorCodigoPatrimonio("123456")).thenReturn(Optional.of(new Livro()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.cadastrar(livro)
        );

        assertTrue(ex.getMessage().contains("123456"));
        verify(livroDAO, never()).salvar(any());
    }

    @Test
    void cadastrar_deveLancarExcecao_quandoIsbnDuplicado() {
        livro.getTitulo().setIsbn("9788576082705");
        when(livroDAO.buscarPorCodigoPatrimonio("123456")).thenReturn(Optional.empty());
        when(livroDAO.buscarPorIsbn("9788576082705")).thenReturn(Optional.of(new Livro()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.cadastrar(livro)
        );

        assertTrue(ex.getMessage().contains("ISBN"));
        verify(livroDAO, never()).salvar(any());
    }

    @Test
    void cadastrar_deveDefinirDisponivelTrue_aoSalvar() {
        livro.setDisponivel(null);
        when(livroDAO.buscarPorCodigoPatrimonio("123456")).thenReturn(Optional.empty());
        when(livroDAO.salvar(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro salvo = livroService.cadastrar(livro);

        assertTrue(salvo.getDisponivel(), "Novo livro deve iniciar disponivel");
    }

    @Test
    void cadastrar_deveDefinirExemplarBibliotecaFalse_quandoNaoInformado() {
        livro.setExemplarBiblioteca(null);
        when(livroDAO.buscarPorCodigoPatrimonio("123456")).thenReturn(Optional.empty());
        when(livroDAO.salvar(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro salvo = livroService.cadastrar(livro);

        assertFalse(salvo.getExemplarBiblioteca(), "Padrao deve ser nao-exemplar de biblioteca");
    }

    @Test
    void cadastrar_deveRespeitarExemplarBibliotecaTrue_quandoInformado() {
        livro.setExemplarBiblioteca(true);
        when(livroDAO.buscarPorCodigoPatrimonio("123456")).thenReturn(Optional.empty());
        when(livroDAO.salvar(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro salvo = livroService.cadastrar(livro);

        assertTrue(salvo.getExemplarBiblioteca(), "Exemplar de biblioteca true deve ser preservado");
    }

    @Test
    void listarTodos_deveRetornarTodosOsLivrosCadastrados() {
        Livro outro = new Livro();
        outro.setCodigoPatrimonio("654321");
        when(livroDAO.listarTodos()).thenReturn(List.of(livro, outro));

        List<Livro> resultado = livroService.listarTodos();

        assertEquals(2, resultado.size());
        verify(livroDAO).listarTodos();
    }

    @Test
    void cadastrar_deveAplicarPrazoDefault_quandoPrazoNulo() {
        livro.getTitulo().setPrazo(null);
        when(livroDAO.buscarPorCodigoPatrimonio("123456")).thenReturn(Optional.empty());
        when(livroDAO.salvar(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro salvo = livroService.cadastrar(livro);

        assertEquals(7, salvo.getTitulo().getPrazo(), "Prazo nulo deve resultar no default 7");
    }

    @Test
    void cadastrar_deveManterPrazo_quandoPrazoInformado() {
        livro.getTitulo().setPrazo(14);
        when(livroDAO.buscarPorCodigoPatrimonio("123456")).thenReturn(Optional.empty());
        when(livroDAO.salvar(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro salvo = livroService.cadastrar(livro);

        assertEquals(14, salvo.getTitulo().getPrazo(), "Prazo informado deve ser preservado");
    }

    @Test
    void cadastrar_deveLancarExcecao_quandoPrazoZero() {
        livro.getTitulo().setPrazo(0);
        when(livroDAO.buscarPorCodigoPatrimonio("123456")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.cadastrar(livro)
        );

        assertTrue(ex.getMessage().contains("Prazo"));
        verify(livroDAO, never()).salvar(any());
    }

    @Test
    void cadastrar_deveLancarExcecao_quandoPrazoNegativo() {
        livro.getTitulo().setPrazo(-1);
        when(livroDAO.buscarPorCodigoPatrimonio("123456")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.cadastrar(livro)
        );

        assertTrue(ex.getMessage().contains("Prazo"));
        verify(livroDAO, never()).salvar(any());
    }
}
