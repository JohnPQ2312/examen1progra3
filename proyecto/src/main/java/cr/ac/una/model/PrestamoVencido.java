package cr.ac.una.model;

public class PrestamoVencido {

    private final String nombreSolicitante;
    private final String codigoEquipo;
    private final long diasAtraso;

    public PrestamoVencido(String nombreSolicitante, String codigoEquipo, long diasAtraso) {
        this.nombreSolicitante = nombreSolicitante;
        this.codigoEquipo = codigoEquipo;
        this.diasAtraso = diasAtraso;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public long getDiasAtraso() {
        return diasAtraso;
    }

    @Override
    public String toString() {
        return "PrestamoVencido{"
                + "nombreSolicitante='" + nombreSolicitante + '\''
                + ", codigoEquipo='" + codigoEquipo + '\''
                + ", diasAtraso=" + diasAtraso
                + '}';
    }
}