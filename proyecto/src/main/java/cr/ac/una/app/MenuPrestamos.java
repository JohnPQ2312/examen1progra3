package cr.ac.una.app;

import cr.ac.una.controller.PrestamoController;
import cr.ac.una.exception.DatosException;
import cr.ac.una.exception.EquipoNoDisponibleException;
import cr.ac.una.exception.LimitePrestamosException;
import cr.ac.una.exception.RenovacionNoPermitidaException;
import cr.ac.una.exception.ValidacionException;
import cr.ac.una.model.Prestamo;
import cr.ac.una.model.PrestamoVencido;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MenuPrestamos {

    private final PrestamoController controller;

    public MenuPrestamos(PrestamoController controller) {
        this.controller = controller;
    }

    public void mostrar() {
        int opcion;
        do {
            Consola.titulo("Prestamos");
            System.out.println("1. Registrar prestamo");
            System.out.println("2. Registrar devolucion");
            System.out.println("3. Consultar préstamos activos de un solicitante");
            System.out.println("4. Renovar prestamo");
            System.out.println("5. Listar prestamos activos vencidos");
            System.out.println("0. Volver");
            opcion = Consola.leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> registrarPrestamo();
                    case 2 -> registrarDevolucion();
                    case 3 -> listarActivos();
                    case 4 -> renovarPrestamo();
                    case 5 -> listarVencidos();
                    case 0 -> { }
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (ValidacionException | EquipoNoDisponibleException
                     | LimitePrestamosException
                     | RenovacionNoPermitidaException e) {
                System.out.println("  Rechazado: " + e.getMessage());
            } catch (DatosException e) {
                System.out.println("  Error de base de datos: " + e.getMessage()
                        + (e.getCause() != null ? " (" + e.getCause().getMessage().lines().findFirst().orElse("") + ")" : ""));
            }
        } while (opcion != 0);
    }

    private void registrarPrestamo() {
        String id = Consola.leerTexto("Identificación del solicitante: ");
        String codigo = Consola.leerTexto("Codigo del equipo: ");
        LocalDate fecha = leerFecha("Fecha esperada de devolución (AAAA-MM-DD): ");
        Prestamo p = controller.registrarPrestamo(id, codigo, fecha);
        System.out.println("  Prestamo #" + p.getId() + " registrado. El equipo "
                + p.getEquipo().getCodigo() + " quedo PRESTADO.");
    }

    private void registrarDevolucion() {
        int id = Consola.leerEntero("Numero de prestamo: ");
        Prestamo p = controller.registrarDevolucion(id);
        System.out.println("  Devolucion registrada el " + p.getFechaDevolucionReal()
                + ". El equipo " + p.getEquipo().getCodigo() + " quedo DISPONIBLE.");
    }

    private void listarActivos() {
        String id = Consola.leerTexto("Identificacion del solicitante: ");
        List<Prestamo> lista = controller.listarActivosPorSolicitante(id);
        if (lista.isEmpty()) {
            System.out.println("  El solicitante no tiene prestamos activos.");
            return;
        }
        for (Prestamo p : lista) {
            System.out.printf("  #%-4d | %-8s %-28s | prestado %s | devolver %s%n",
                    p.getId(), p.getEquipo().getCodigo(), p.getEquipo().getDescripcion(),
                    p.getFechaPrestamo(), p.getFechaDevolucionEsperada());
        }
    }
    
        private void renovarPrestamo() {
        int id = Consola.leerEntero("Numero de prestamo a renovar: ");
        Prestamo p = controller.renovarPrestamo(id);
        System.out.println("  Prestamo #" + p.getId() + " renovado. Nueva fecha de devolucion: "
                + p.getFechaDevolucionEsperada() + ".");
    }

    private void listarVencidos() {
        List<PrestamoVencido> lista = controller.listarActivosVencidos();
        if (lista.isEmpty()) {
            System.out.println("  No hay prestamos activos vencidos.");
            return;
        }
        for (PrestamoVencido v : lista) {
            System.out.printf("  %-28s | equipo %-8s | %d dia(s) de atraso%n",
                    v.getNombreSolicitante(), v.getCodigoEquipo(), v.getDiasAtraso());
        }
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            String texto = Consola.leerTexto(mensaje);
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("  Fecha no valida. Use el formato AAAA-MM-DD, por ejemplo 2026-10-20.");
            }
        }
    }
}