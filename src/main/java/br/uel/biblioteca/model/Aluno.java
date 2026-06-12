package br.uel.biblioteca.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aluno")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chave de negócio — identificador único do aluno (Fig. 9.20, Cap. 9)
    @Column(name = "matricula", nullable = false, unique = true)
    private String matricula;

    @Column(name = "nome", nullable = false)
    private String nome;

    // Atributos presentes no modelo do livro (Fig. 9.20, Cap. 9)
    @Column(name = "cpf", unique = true, length = 11)
    private String cpf;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "email")
    private String email;

    @Column(name = "ativo")
    private Boolean ativo;

    // Aluno registra Emprestimos — GRASP Creator (Cap. 9, §9.8.1)
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL)
    private List<Emprestimo> emprestimos = new ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL)
    private List<Debito> debitos = new ArrayList<>();

    public Aluno() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
    }

    public List<Debito> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<Debito> debitos) {
        this.debitos = debitos;
    }
}
