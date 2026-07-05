package br.uel.biblioteca.service;

import br.uel.biblioteca.dao.AlunoDAO;
import br.uel.biblioteca.dao.DebitoDAO;
import br.uel.biblioteca.dao.EmprestimoDAO;
import br.uel.biblioteca.dao.LivroDAO;
import br.uel.biblioteca.model.Aluno;
import br.uel.biblioteca.model.Debito;
import br.uel.biblioteca.model.Emprestimo;
import br.uel.biblioteca.model.ItemEmprestimo;
import br.uel.biblioteca.model.Livro;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * CBiblioteca — GRASP Controller/Facade (Cap. 9, §9.6).
 * Orquestra o caso de uso Emprestar Livro delegando persistência ao DAO (DIP — Cap. 10).
 */
@Service
@Transactional
public class EmprestimoService {

    static final BigDecimal MULTA_POR_DIA = BigDecimal.ONE;

    private final AlunoDAO alunoDAO;
    private final LivroDAO livroDAO;
    private final EmprestimoDAO emprestimoDAO;
    private final DebitoDAO debitoDAO;

    public EmprestimoService(AlunoDAO alunoDAO, LivroDAO livroDAO,
                              EmprestimoDAO emprestimoDAO, DebitoDAO debitoDAO) {
        this.alunoDAO = alunoDAO;
        this.livroDAO = livroDAO;
        this.emprestimoDAO = emprestimoDAO;
        this.debitoDAO = debitoDAO;
    }

    /**
     * Realiza o empréstimo de forma atômica: ou tudo é concluído, ou nada é persistido.
     * Implementa fluxo principal e alternativos do Caso de Uso Emprestar Livro.
     */
    public Emprestimo realizarEmprestimo(String matricula, List<String> codigosPatrimonio) {
        if (codigosPatrimonio == null || codigosPatrimonio.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um livro para o empréstimo.");
        }

        // FA-1: aluno não cadastrado
        Aluno aluno = alunoDAO.buscarPorMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aluno não encontrado com matrícula: " + matricula));

        if (!Boolean.TRUE.equals(aluno.getAtivo())) {
            throw new IllegalArgumentException("Aluno inativo. Empréstimo não permitido.");
        }

        // FA-2: aluno com débito ativo
        if (debitoDAO.alunoTemDebitoAtivo(aluno.getId())) {
            throw new IllegalArgumentException(
                    "Aluno possui débito ativo. Regularize a pendência antes de realizar novo empréstimo.");
        }

        // Detecta códigos de patrimônio duplicados na mesma solicitação
        Set<String> codigosUnicos = new LinkedHashSet<>();
        for (String codigo : codigosPatrimonio) {
            String normalizado = codigo.trim();
            if (!codigosUnicos.add(normalizado)) {
                throw new IllegalArgumentException(
                        "Código de patrimônio duplicado na solicitação: " + normalizado);
            }
        }

        // Valida todos os livros antes de persistir qualquer coisa (operação atômica)
        List<Livro> livros = new ArrayList<>();
        for (String codigo : codigosUnicos) {
            // FA-3: livro não encontrado
            Livro livro = livroDAO.buscarPorCodigoPatrimonio(codigo)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Livro não encontrado: " + codigo));

            // FA-4: exemplar de biblioteca
            if (Boolean.TRUE.equals(livro.getExemplarBiblioteca())) {
                throw new IllegalArgumentException(
                        "Livro " + codigo + " é exemplar de biblioteca e não pode ser emprestado.");
            }

            // FA-5: livro indisponível/emprestado
            if (!Boolean.TRUE.equals(livro.getDisponivel())) {
                throw new IllegalArgumentException(
                        "Livro " + codigo + " está indisponível para empréstimo.");
            }

            livros.add(livro);
        }

        // Calcula prazo conforme Exercício 1 (Cap. 9): maior prazo entre os livros + acréscimo
        int prazoFinalDias = calcularPrazoFinalDias(livros);
        LocalDate dataEmprestimo = LocalDate.now();
        LocalDate dataPrevistaDevolucao = dataEmprestimo.plusDays(prazoFinalDias);

        // Aluno cria Emprestimo — GRASP Creator (Cap. 9, §9.8.1)
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setAluno(aluno);
        emprestimo.setDataEmprestimo(dataEmprestimo);
        emprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
        emprestimo.setStatus("ATIVO");

        // Emprestimo cria ItemEmprestimo — GRASP Creator (Cap. 9, §9.8.1)
        for (Livro livro : livros) {
            ItemEmprestimo item = new ItemEmprestimo();
            item.setEmprestimo(emprestimo);
            item.setLivro(livro);
            item.setDataPrevistaDevolucao(dataPrevistaDevolucao);
            emprestimo.getItens().add(item);
        }

        emprestimoDAO.salvar(emprestimo);

        // Atualiza disponibilidade após persistir (garante ID gerado)
        for (Livro livro : livros) {
            livro.setDisponivel(false);
            livroDAO.atualizar(livro);
        }

        return emprestimo;
    }

    @Transactional(readOnly = true)
    public Optional<Emprestimo> buscarComItens(Long id) {
        return emprestimoDAO.buscarComItens(id);
    }

    public ResultadoDevolucao devolverLivro(String codigoPatrimonio, LocalDate dataDevolucao) {
        // FA-1: livro não cadastrado
        livroDAO.buscarPorCodigoPatrimonio(codigoPatrimonio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Livro não encontrado: " + codigoPatrimonio));

        // FA-2: livro não possui empréstimo ativo
        ItemEmprestimo item = emprestimoDAO.buscarItemAtivoByCodigoPatrimonio(codigoPatrimonio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Livro " + codigoPatrimonio + " não possui empréstimo ativo."));

        Emprestimo emprestimo = item.getEmprestimo();
        Livro livro = item.getLivro();

        // Registra data de devolução no item
        item.setDataDevolucao(dataDevolucao);

        // Calcula atraso em dias
        long diasAtraso = 0;
        if (item.getDataPrevistaDevolucao() != null
                && dataDevolucao.isAfter(item.getDataPrevistaDevolucao())) {
            diasAtraso = ChronoUnit.DAYS.between(item.getDataPrevistaDevolucao(), dataDevolucao);
        }

        // Cria débito se houver atraso
        BigDecimal valorMulta = BigDecimal.ZERO;
        if (diasAtraso > 0) {
            valorMulta = MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso));
            Debito debito = new Debito();
            debito.setAluno(emprestimo.getAluno());
            debito.setEmprestimo(emprestimo);
            debito.setValor(valorMulta);
            debito.setDataGeracao(dataDevolucao);
            debito.setPago(false);
            debitoDAO.salvar(debito);
        }

        // Libera o livro
        livro.setDisponivel(true);
        livroDAO.atualizar(livro);

        // Se todos os itens do empréstimo foram devolvidos, encerra o empréstimo
        boolean todosDevolvidos = emprestimo.getItens().stream()
                .allMatch(i -> i.getDataDevolucao() != null);
        if (todosDevolvidos) {
            emprestimo.setDataDevolucao(dataDevolucao);
            emprestimo.setStatus("ENCERRADO");
            emprestimoDAO.atualizar(emprestimo);
        }

        return new ResultadoDevolucao(
                item.getId(),
                emprestimo.getAluno().getNome(),
                emprestimo.getAluno().getMatricula(),
                livro.getCodigoPatrimonio(),
                livro.getTitulo() != null ? livro.getTitulo().getNome() : "",
                dataDevolucao,
                item.getDataPrevistaDevolucao(),
                diasAtraso,
                valorMulta,
                todosDevolvidos);
    }

    /**
     * Calcula o prazo final em dias conforme regra do Exercício 1 (Cap. 9, Menolli 2025):
     * prazo = maior prazo entre os livros + 2 dias por cada livro após o segundo.
     * Exemplos: 1 livro → max; 2 livros → max; 3 → max+2; 4 → max+4.
     */
    int calcularPrazoFinalDias(List<Livro> livros) {
        int maiorPrazo = livros.stream()
                .mapToInt(l -> l.verPrazo() != null ? l.verPrazo() : 7)
                .max()
                .orElse(7);
        int acrescimo = livros.size() > 2 ? (livros.size() - 2) * 2 : 0;
        return maiorPrazo + acrescimo;
    }
}
