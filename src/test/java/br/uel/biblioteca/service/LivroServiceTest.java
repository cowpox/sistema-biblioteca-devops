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

import java.util.Collections;
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
    void cadastrar_deveReutilizarTitulo_quandoIsbnJaExiste() {
        Titulo tituloExistente = new Titulo();
        tituloExistente.setId(99L);
        tituloExistente.setNome("Clean Code");
        tituloExistente.setIsbn("9788576082705");
        tituloExistente.setPrazo(7);

        Livro livroExistente = new Livro();
        livroExistente.setCodigoPatrimonio("000001");
        livroExistente.setTitulo(tituloExistente);

        livro.getTitulo().setIsbn("9788576082705");
        when(livroDAO.buscarPorCodigoPatrimonio("123456")).thenReturn(Optional.empty());
        when(livroDAO.buscarPorIsbn("9788576082705")).thenReturn(Optional.of(livroExistente));
        when(livroDAO.salvar(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro salvo = livroService.cadastrar(livro);

        assertSame(tituloExistente, salvo.getTitulo(), "Deve reutilizar o Titulo existente, não criar um novo");
        assertEquals(99L, salvo.getTitulo().getId());
        verify(livroDAO).salvar(livro);
    }

    @Test
    void cadastrar_naoDeveCriarTituloDuplicado_quandoIsbnJaExiste() {
        Titulo tituloExistente = new Titulo();
        tituloExistente.setId(99L);
        tituloExistente.setIsbn("9788576082705");

        Livro livroExistente = new Livro();
        livroExistente.setTitulo(tituloExistente);

        Livro novoExemplar = new Livro();
        novoExemplar.setCodigoPatrimonio("000002");
        Titulo tituloDuplicado = new Titulo();
        tituloDuplicado.setIsbn("9788576082705");
        tituloDuplicado.setNome("Clean Code");
        novoExemplar.setTitulo(tituloDuplicado);

        when(livroDAO.buscarPorCodigoPatrimonio("000002")).thenReturn(Optional.empty());
        when(livroDAO.buscarPorIsbn("9788576082705")).thenReturn(Optional.of(livroExistente));
        when(livroDAO.salvar(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro salvo = livroService.cadastrar(novoExemplar);

        assertSame(tituloExistente, salvo.getTitulo(), "Titulo do form deve ser substituído pelo existente no banco");
    }

    @Test
    void cadastrar_devePermitirExemplarEmprestavelEBiblioteca_comMesmoIsbn() {
        Titulo tituloExistente = new Titulo();
        tituloExistente.setId(99L);
        tituloExistente.setIsbn("9788576082705");
        tituloExistente.setPrazo(7);

        Livro livroExistente = new Livro();
        livroExistente.setCodigoPatrimonio("000001");
        livroExistente.setTitulo(tituloExistente);
        livroExistente.setExemplarBiblioteca(false);

        Livro novoExemplar = new Livro();
        novoExemplar.setCodigoPatrimonio("000002");
        novoExemplar.setExemplarBiblioteca(true);
        Titulo tituloDuplicado = new Titulo();
        tituloDuplicado.setIsbn("9788576082705");
        novoExemplar.setTitulo(tituloDuplicado);

        when(livroDAO.buscarPorCodigoPatrimonio("000002")).thenReturn(Optional.empty());
        when(livroDAO.buscarPorIsbn("9788576082705")).thenReturn(Optional.of(livroExistente));
        when(livroDAO.salvar(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro salvo = livroService.cadastrar(novoExemplar);

        assertTrue(salvo.getExemplarBiblioteca(), "Novo exemplar deve manter sua própria classificação");
        assertSame(tituloExistente, salvo.getTitulo(), "Deve compartilhar o mesmo Titulo");
        verify(livroDAO).salvar(novoExemplar);
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

    @Test
    void buscarPorTermo_delegaAoDAO() {
        when(livroDAO.buscarPorTermo("clean")).thenReturn(List.of(livro));

        List<Livro> resultado = livroService.buscarPorTermo("clean");

        assertEquals(1, resultado.size());
        verify(livroDAO).buscarPorTermo("clean");
    }

    @Test
    void buscarPorTermo_retornaListaVazia_quandoSemResultado() {
        when(livroDAO.buscarPorTermo("inexistente")).thenReturn(Collections.emptyList());

        List<Livro> resultado = livroService.buscarPorTermo("inexistente");

        assertTrue(resultado.isEmpty());
        verify(livroDAO).buscarPorTermo("inexistente");
    }

    @Test
    void buscarPorTermo_retornaExemplaresIndisponiveis() {
        livro.setDisponivel(false);
        when(livroDAO.buscarPorTermo("clean")).thenReturn(List.of(livro));

        List<Livro> resultado = livroService.buscarPorTermo("clean");

        assertFalse(resultado.get(0).getDisponivel(), "Service não deve filtrar indisponíveis");
    }

    @Test
    void buscarPorTermo_retornaExemplaresDeBiblioteca() {
        livro.setExemplarBiblioteca(true);
        when(livroDAO.buscarPorTermo("clean")).thenReturn(List.of(livro));

        List<Livro> resultado = livroService.buscarPorTermo("clean");

        assertTrue(resultado.get(0).getExemplarBiblioteca(), "Service não deve filtrar exemplares de biblioteca");
    }

    // Nota: os dois testes abaixo verificam a delegação correta ao DAO com termo sem acento.
    // A normalização real (via FUNCTION('translate') no JPQL) é exercitada apenas em teste
    // de integração com banco — não há teste de DAO no projeto.

    @Test
    void buscarPorTermo_retornaExemplares_porNomeSemAcento() {
        livro.getTitulo().setNome("Cálculo Vol. 1");
        when(livroDAO.buscarPorTermo("calculo")).thenReturn(List.of(livro));

        List<Livro> resultado = livroService.buscarPorTermo("calculo");

        assertFalse(resultado.isEmpty());
        assertEquals("Cálculo Vol. 1", resultado.get(0).getTitulo().getNome());
        verify(livroDAO).buscarPorTermo("calculo");
    }

    @Test
    void buscarPorTermo_retornaExemplares_porAutorSemAcento() {
        livro.getTitulo().setAutor("Machado de Assis");
        when(livroDAO.buscarPorTermo("assis")).thenReturn(List.of(livro));

        List<Livro> resultado = livroService.buscarPorTermo("assis");

        assertFalse(resultado.isEmpty());
        assertEquals("Machado de Assis", resultado.get(0).getTitulo().getAutor());
        verify(livroDAO).buscarPorTermo("assis");
    }
}
