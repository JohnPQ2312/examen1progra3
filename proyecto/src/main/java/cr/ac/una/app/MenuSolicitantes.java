package cr.ac.una.app;

import cr.ac.una.controller.SolicitanteController;
import cr.ac.una.exception.DatosException;
import cr.ac.una.exception.ValidacionException;
import cr.ac.una.model.Solicitante;
import cr.ac.una.model.TipoSolicitante;
import java.util.List;
import java.util.Optional;

public class MenuSolicitantes {

    private final SolicitanteController controller;

    public MenuSolicitantes(SolicitanteController controller) {
        this.controller = controller;
    }

    public void mostrar() {
        int opcion;
        do {
            Consola.titulo("Solicitantes");
            System.out.println("1. Registrar solicitante");
            System.out.println("2. Consultar solicitante por identificacion");
            System.out.println("3. Listar solicitantes");
            System.out.println("4. Actualizar solicitante");
            System.out.println("0. Volver");
            opcion = Consola.leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> registrar();
                    case 2 -> consultar();
                    case 3 -> listar();
                    case 4 -> actualizar();
                    case 0 -> { }
                    default -> System.out.println("Opcion no valida.");
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
        String id = Consola.leerTexto("Identificacion: ");
        String nombre = Consola.leerTexto("Nombre: ");
        String correo = Consola.leerTexto("Correo: ");
        TipoSolicitante tipo = leerTipo();
        controller.registrar(new Solicitante(id, nombre, correo, tipo));
        System.out.println("  Solicitante registrado.");
    }

    private void consultar() {
        String id = Consola.leerTexto("Identificacion: ");
        Optional<Solicitante> s = controller.buscarPorId(id);
        if (s.isPresent()) {
            imprimir(s.get());
        } else {
            System.out.println("  No existe un solicitante con esa identificacion.");
        }
    }

    private void listar() {
        List<Solicitante> lista = controller.listar();
        if (lista.isEmpty()) {
            System.out.println("  No hay solicitantes registrados.");
        }
        lista.forEach(this::imprimir);
    }

    private void actualizar() {
        String id = Consola.leerTexto("Identificación del solicitante a actualizar: ");
        Optional<Solicitante> actual = controller.buscarPorId(id);
        if (actual.isEmpty()) {
            System.out.println("  No existe un solicitante con esa identificacion.");
            return;
        }
        Solicitante s = actual.get();
        imprimir(s);
        System.out.println("  (Presione Enter para conservar el valor actual)");
        String nombre = Consola.leerTexto("Nuevo nombre: ");
        String correo = Consola.leerTexto("Nuevo correo: ");
        if (!nombre.isEmpty()) {
            s.setNombre(nombre);
        }
        if (!correo.isEmpty()) {
            s.setCorreo(correo);
        }
        String cambiarTipo = Consola.leerTexto("¿Cambiar tipo? (s/n): ");
        if (cambiarTipo.equalsIgnoreCase("s")) {
            s.setTipo(leerTipo());
        }
        controller.actualizar(s);
        System.out.println("  Solicitante actualizado.");
    }

    private TipoSolicitante leerTipo() {
        while (true) {
            int t = Consola.leerEntero("Tipo (1 = ESTUDIANTE, 2 = FUNCIONARIO): ");
            if (t == 1) {
                return TipoSolicitante.ESTUDIANTE;
            }
            if (t == 2) {
                return TipoSolicitante.FUNCIONARIO;
            }
            System.out.println("  Digite 1 o 2.");
        }
    }

    private void imprimir(Solicitante s) {
        System.out.printf("  %-12s | %-25s | %-30s | %s%n",
                s.getIdentificacion(), s.getNombre(), s.getCorreo(), s.getTipo());
    }
}