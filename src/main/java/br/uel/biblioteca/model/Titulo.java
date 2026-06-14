package br.uel.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "titulo")
public class Titulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título da obra é obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank(message = "ISBN é obrigatório")
    @Pattern(regexp = "^$|[0-9]{13}$", message = "ISBN deve conter exatamente 13 dígitos")
    @Column(name = "isbn", unique = true)
    private String isbn;

    // Prazo padrão de devolução em dias — usado por Livro.verPrazo() (Fig. 9.20, Cap. 9)
    @Min(value = 1, message = "Prazo de devolução deve ser um número inteiro positivo.")
    @Column(name = "prazo")
    private Integer prazo;

    // Autor mantido como String nesta fase; entidade Autor pode ser extraída futuramente
    @Column(name = "autor")
    private String autor;

    @Column(name = "editora")
    private String editora;

    @Column(name = "edicao")
    private String edicao;

    @Pattern(regexp = "^$|[0-9]{4}$", message = "Ano deve conter exatamente 4 dígitos numéricos")
    @Column(name = "ano")
    private String ano;

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

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }
}
