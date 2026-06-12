package br.uel.biblioteca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "titulo")
public class Titulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "isbn", unique = true)
    private String isbn;

    // Prazo padrão de devolução em dias — usado por Livro.verPrazo() (Fig. 9.20, Cap. 9)
    @Column(name = "prazo")
    private Integer prazo;

    // Autor mantido como String nesta fase; entidade Autor pode ser extraída futuramente
    @Column(name = "autor")
    private String autor;

    @Column(name = "editora")
    private String editora;

    @Column(name = "edicao")
    private String edicao;

    @Column(name = "ano")
    private Integer ano;

    public Titulo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPrazo() {
        return prazo;
    }

    public void setPrazo(Integer prazo) {
        this.prazo = prazo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public String getEdicao() {
        return edicao;
    }

    public void setEdicao(String edicao) {
        this.edicao = edicao;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}
