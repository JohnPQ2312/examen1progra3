/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.model;

/**
 *
 * @author jp570
 */
public class Equipo {

    private String codigo;
    private String descripcion;
    private String tipo;
    private EstadoEquipo estado;

    public Equipo() {
    }

    public Equipo(String codigo, String descripcion,
            String tipo, EstadoEquipo estado) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public EstadoEquipo getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Equipo{"
                + "codigo='" + codigo + '\''
                + ", descripcion='" + descripcion + '\''
                + ", tipo='" + tipo + '\''
                + ", estado=" + estado
                + '}';
    }
}