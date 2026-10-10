/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.model;

import java.time.LocalDate;

/**
 *
 * @author jp570
 */
public class Prestamo {

    private Integer id;
    private Solicitante solicitante;
    private Equipo equipo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;
    private EstadoPrestamo estado;

    public Prestamo() {
    }

    public Prestamo(Integer id, Solicitante solicitante, Equipo equipo,
            LocalDate fechaPrestamo, LocalDate fechaDevolucionEsperada,
            LocalDate fechaDevolucionReal, EstadoPrestamo estado) {
        this.id = id;
        this.solicitante = solicitante;
        this.equipo = equipo;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Solicitante getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Solicitante solicitante) {
        this.solicitante = solicitante;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }

    public void setFechaDevolucionEsperada(LocalDate fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
    }

    public LocalDate getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Prestamo{"
                + "id=" + id
                + ", solicitante=" + solicitante
                + ", equipo=" + equipo
                + ", fechaPrestamo=" + fechaPrestamo
                + ", fechaDevolucionEsperada=" + fechaDevolucionEsperada
                + ", fechaDevolucionReal=" + fechaDevolucionReal
                + ", estado=" + estado
                + '}';
    }
}
