package cr.ac.una.controller;

import cr.ac.una.model.Prestamo;
import cr.ac.una.service.PrestamoService;
import java.time.LocalDate;
import java.util.List;

public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    public Prestamo registrarPrestamo(
            String identificacion,
            String codigoEquipo,
            LocalDate fechaDevolucionEsperada) {
        return prestamoService.registrarPrestamo(
                identificacion,
                codigoEquipo,
                fechaDevolucionEsperada);
    }

    public List<Prestamo> listarActivosPorSolicitante(
            String identificacion) {
        return prestamoService.listarActivosPorSolicitante(
                identificacion);
    }

    public Prestamo registrarDevolucion(Integer idPrestamo) {
        return prestamoService.registrarDevolucion(idPrestamo);
    }
}