package com.cinemaapp.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "generos")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    public Genero() {}

    public Genero(String nome) {
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    // Sem isso, #lists.contains(filme.generos, g) no formulário de edição sempre
    // retorna false (entidades JPA usam igualdade por referência por padrão), então
    // os checkboxes de gênero nunca aparecem marcados e salvar sem remarcar todos
    // apaga os gêneros do filme silenciosamente.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genero other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
