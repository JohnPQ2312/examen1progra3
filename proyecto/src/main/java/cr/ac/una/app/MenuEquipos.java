package cr.ac.una.app;

import cr.ac.una.controller.EquipoController;
import cr.ac.una.exception.DatosException;
import cr.ac.una.exception.ValidacionException;
import cr.ac.una.model.Equipo;
import cr.ac.una.model.EstadoEquipo;
import java.util.List;
import java.util.Optional;

public class MenuEquipos {

    private final EquipoController controller;

    public MenuEquipos(EquipoController controller) {
        this.controller = controller;
    }

    public void mostrar() {
        int opcion;
        do {
            Consola.titulo("Equipos");
            System.out.println("1. Registrar equipo");
            System.out.println("2. Consultar equipo por codigo");
            System.out.println("3. Listar equipos");
            System.out.println("4. Actualizar equipo");
            System.out.println("0. Volver");
            opcion = Consola.leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> registrar();
                    case 2 -> consultar();
                    case 3 -> listar();
                    case 4 -> actualizar();
                    case 0 -> { }
                    default -> System.out.println("Opción no valida.");
                }
            } catch (ValidacionException e) {
                System.out.println("  Rechazado: " + e.getMessage());
            } catch (DatosException e) {
                System.out.println("  Error de base de datos: " + e.getMessage()
                        + (e.getCause() != null ? " (" + e.getCause().getMessage().lines().findFirst().orElse("") + ")" : ""));
            }
        } while (opcion != 0);
    }

    private void registrar() {
        String codigo = Consola.leerTexto("Codigo: ");
        String descripcion = Consola.leerTexto("Descripcion: ");
        String tipo = Consola.leerTexto("Tipo (Laptop, Proyector, ...): ");
        controller.registrar(new Equipo(codigo, descripcion, tipo, EstadoEquipo.DISPONIBLE));
        System.out.println("  Equipo registrado como disponible.");
    }

    private void consultar() {
        String codigo = Consola.leerTexto("Codigo: ");
        Optional<Equipo> e = controller.buscarPorId(codigo);
        if (e.isPresent()) {
            imprimir(e.get());
        } else {
            System.out.println("  No existe un equipo con ese codigo.");
        }
    }

    private void listar() {
        List<Equipo> lista = controller.listar();
        if (lista.isEmpty()) {
            System.out.println("  No hay equipos registrados.");
        }
        lista.forEach(this::imprimir);
    }

    private void actualizar() {
        String codigo = Consola.leerTexto("Codigo del equipo a actualizar: ");
        Optional<Equipo> actual = controller.buscarPorId(codigo);
        if (actual.isEmpty()) {
            System.out.println("  No existe un equipo con ese codigo.");
            return;
        }
        Equipo e = actual.get();
        imprimir(e);
        System.out.println("  (Presione Enter para conservar el valor actual)");
        String descripcion = Consola.leerTexto("Nueva descripcion: ");
        String tipo = Consola.leerTexto("Nuevo tipo: ");
        if (!descripcion.isEmpty()) {
            e.setDescripcion(descripcion);
        }
        if (!tipo.isEmpty()) {
            e.setTipo(tipo);
        }
        String cambiarEstado = Consola.leerTexto("¿Cambiar estado? (s/n): ");
        if (cambiarEstado.equalsIgnoreCase("s")) {
            e.setEstado(leerEstado());
        }
        controller.actualizar(e);
        System.out.println("  Equipo actualizado.");
    }

    private EstadoEquipo leerEstado() {
        while (true) {
            int t = Consola.leerEntero("Estado (1 = DISPONIBLE, 2 = EN_REPARACION): ");
            if (t == 1) {
                return EstadoEquipo.DISPONIBLE;
            }
            if (t == 2) {
                return EstadoEquipo.EN_REPARACION;
            }
            System.out.println("  Digite 1 o 2.");
        }
    }

    private void imprimir(Equipo e) {
        System.out.printf("  %-8s | %-30s | %-10s | %s%n",
                e.getCodigo(), e.getDescripcion(), e.getTipo(), e.getEstado());
    }
}