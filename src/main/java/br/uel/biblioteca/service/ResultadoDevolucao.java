package br.uel.biblioteca.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ResultadoDevolucao {

    private final Long itemId;
    private final String alunoNome;
    private final String alunoMatricula;
    private final String livroCodigoPatrimonio;
    private final String tituloNome;
    private final LocalDate dataDevolucao;
    private final LocalDate dataPrevistaDevolucao;
    private final long diasAtraso;
    private final BigDecimal valorMulta;
    private final boolean emprestimoEncerrado;

    public ResultadoDevolucao(Long itemId, String alunoNome, String alunoMatricula,
                               String livroCodigoPatrimonio, String tituloNome,
                               LocalDate dataDevolucao, LocalDate dataPrevistaDevolucao,
                               long diasAtraso, BigDecimal valorMulta,
                               boolean emprestimoEncerrado) {
        this.itemId = itemId;
        this.alunoNome = alunoNome;
        this.alunoMatricula = alunoMatricula;
        this.livroCodigoPatrimonio = livroCodigoPatrimonio;
        this.tituloNome = tituloNome;
        this.dataDevolucao = dataDevolucao;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.diasAtraso = diasAtraso;
        this.valorMulta = valorMulta;
        this.emprestimoEncerrado = emprestimoEncerrado;
    }

    public Long getItemId() { return itemId; }
    public String getAlunoNome() { return alunoNome; }
    public String getAlunoMatricula() { return alunoMatricula; }
    public String getLivroCodigoPatrimonio() { return livroCodigoPatrimonio; }
    public String getTituloNome() { return tituloNome; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public LocalDate getDataPrevistaDevolucao() { return dataPrevistaDevolucao; }
    public long getDiasAtraso() { return diasAtraso; }
    public BigDecimal getValorMulta() { return valorMulta; }
    public boolean isEmprestimoEncerrado() { return emprestimoEncerrado; }
}
