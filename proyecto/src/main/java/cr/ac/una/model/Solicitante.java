/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.model;

/**
 *
 * @author jp570
 */

public class Solicitante {

    private String identificacion;
    private String nombre;
    private String correo;
    private TipoSolicitante tipo;

    public Solicitante() {
    }

    public Solicitante(String identificacion, String nombre,
            String correo, TipoSolicitante tipo) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.correo = correo;
        this.tipo = tipo;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public TipoSolicitante getTipo() {
        return tipo;
    }

    public void setTipo(TipoSolicitante tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Solicitante{"
                + "identificacion='" + identificacion + '\''
                + ", nombre='" + nombre + '\''
                + ", correo='" + correo + '\''
                + ", tipo=" + tipo
                + '}';
    }
}
