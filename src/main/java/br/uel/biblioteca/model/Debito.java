package br.uel.biblioteca.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "debito")
public class Debito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Aluno cria Debito — GRASP Creator (Cap. 9, §9.8.1)
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "emprestimo_id")
    private Emprestimo emprestimo;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_geracao", nullable = false)
    private LocalDate dataGeracao;

    @Column(name = "pago")
    private Boolean pago;

    public Debito() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Emprestimo getEmprestimo() {
        return emprestimo;
    }

    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDate dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Boolean getPago() {
        return pago;
    }

    public void setPago(Boolean pago) {
        this.pago = pago;
    }
}
