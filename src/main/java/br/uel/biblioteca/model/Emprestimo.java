package br.uel.biblioteca.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emprestimo")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Aluno cria Emprestimo — GRASP Creator (Cap. 9, §9.8.1)
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(name = "data_emprestimo", nullable = false)
    private LocalDate dataEmprestimo;

    @Column(name = "data_prevista_devolucao")
    private LocalDate dataPrevistaDevolucao;

    // Preenchida somente quando todos os itens forem devolvidos
    @Column(name = "data_devolucao")
    private LocalDate dataDevolucao;

    @Column(name = "status")
    private String status;

    // Emprestimo cria e contém ItemEmprestimo — GRASP Creator (Cap. 9, §9.8.1)
    @OneToMany(mappedBy = "emprestimo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEmprestimo> itens = new ArrayList<>();

    public Emprestimo() {
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

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ItemEmprestimo> getItens() {
        return itens;
    }

    public void setItens(List<ItemEmprestimo> itens) {
        this.itens = itens;
    }
}
