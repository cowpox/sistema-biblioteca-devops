package br.uel.biblioteca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "livro")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_patrimonio", nullable = false, unique = true)
    private String codigoPatrimonio;

    // Indica se o exemplar está disponível para empréstimo (Fig. 9.20, Cap. 9)
    @Column(name = "disponivel")
    private Boolean disponivel;

    // Exemplar de biblioteca não pode ser emprestado (Fig. 9.20, Cap. 9 — FA-4 do CU Emprestar)
    @Column(name = "exemplar_biblioteca")
    private Boolean exemplarBiblioteca;

    @ManyToOne
    @JoinColumn(name = "titulo_id", nullable = false)
    private Titulo titulo;

    public Livro() {
    }

    // Delega prazo ao Titulo — GRASP Expert (Cap. 9, §9.7.1)
    public Integer verPrazo() {
        return titulo != null ? titulo.getPrazo() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoPatrimonio() {
        return codigoPatrimonio;
    }

    public void setCodigoPatrimonio(String codigoPatrimonio) {
        this.codigoPatrimonio = codigoPatrimonio;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }

    public Boolean getExemplarBiblioteca() {
        return exemplarBiblioteca;
    }

    public void setExemplarBiblioteca(Boolean exemplarBiblioteca) {
        this.exemplarBiblioteca = exemplarBiblioteca;
    }

    public Titulo getTitulo() {
        return titulo;
    }

    public void setTitulo(Titulo titulo) {
        this.titulo = titulo;
    }
}
