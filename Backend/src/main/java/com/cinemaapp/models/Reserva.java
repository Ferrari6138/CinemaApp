package com.cinemaapp.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filme_id", nullable = false)
    private Filme filme;

    @Column(nullable = false)
    private LocalDateTime dataReserva;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private String status = "CONFIRMADA"; // Pode ser: CONFIRMADA, CANCELADA, FINALIZADA

    // Construtores
    public Reserva() {
        this.dataReserva = LocalDateTime.now();
    }

    public Reserva(Usuario usuario, Filme filme, Integer quantidade) {
        this();
        this.usuario = usuario;
        this.filme = filme;
        this.quantidade = quantidade;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public LocalDateTime getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDateTime dataReserva) {
        this.dataReserva = dataReserva;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Métodos utilitários
    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", usuario=" + usuario.getNome() +
                ", filme=" + filme.getTitulo() +
                ", dataReserva=" + dataReserva +
                ", quantidade=" + quantidade +
                ", status='" + status + '\'' +
                '}';
    }

    public Double getValorTotal() {
        return quantidade * 25.0;
    }
}