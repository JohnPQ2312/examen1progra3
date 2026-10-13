package cr.ac.una.app;

import cr.ac.una.config.ConexionBD;
import cr.ac.una.controller.EquipoController;
import cr.ac.una.controller.PrestamoController;
import cr.ac.una.controller.SolicitanteController;
import cr.ac.una.repository.EquipoRepository;
import cr.ac.una.repository.EquipoRepositoryJdbc;
import cr.ac.una.repository.PrestamoRepository;
import cr.ac.una.repository.PrestamoRepositoryJdbc;
import cr.ac.una.repository.SolicitanteRepository;
import cr.ac.una.repository.SolicitanteRepositoryJdbc;
import cr.ac.una.service.EquipoService;
import cr.ac.una.service.PrestamoService;
import cr.ac.una.service.SolicitanteService;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Punto de entrada de la aplicación (interfaz de consola).
 *
 * Responsabilidad de esta clase: mostrar menús, leer datos y presentar
 * resultados. No debe contener SQL ni reglas de negocio: debe delegar en
 * los Controllers del paquete controller.
 *
 * Flujo esperado:  App (consola) → Controller → Service → Repository → MySQL
 */
public class App {

    public static void main(String[] args) {
        SolicitanteRepository solicitanteRepo = new SolicitanteRepositoryJdbc();
        EquipoRepository equipoRepo = new EquipoRepositoryJdbc();
        PrestamoRepository prestamoRepo = new PrestamoRepositoryJdbc();

        // Servicios: reciben las interfaces por constructor
        SolicitanteService solicitanteService = new SolicitanteService(solicitanteRepo, prestamoRepo);
        EquipoService equipoService = new EquipoService(equipoRepo, prestamoRepo);
        PrestamoService prestamoService = new PrestamoService(solicitanteRepo, equipoRepo, prestamoRepo);

        // Menús
        MenuSolicitantes menuSolicitantes =
                new MenuSolicitantes(new SolicitanteController(solicitanteService));
        MenuEquipos menuEquipos = new MenuEquipos(new EquipoController(equipoService));
        MenuPrestamos menuPrestamos = new MenuPrestamos(new PrestamoController(prestamoService));

        int opcion;
        do {
            Consola.titulo("Prestamo de equipos tecnologicos");
            System.out.println("1. Probar conexion con la base de datos");
            System.out.println("2. Solicitantes");
            System.out.println("3. Equipos");
            System.out.println("4. Préstamos");
            System.out.println("0. Salir");
            opcion = Consola.leerEntero("Opcion: ");

            switch (opcion) {
                case 1 -> probarConexion();
                case 2 -> menuSolicitantes.mostrar();
                case 3 -> menuEquipos.mostrar();
                case 4 -> menuPrestamos.mostrar();
                case 0 -> System.out.println("Fin del programa.");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private static void probarConexion() {
        try (Connection con = ConexionBD.obtenerConexion()) {
            DatabaseMetaData meta = con.getMetaData();
            System.out.println("Conexion exitosa: " + meta.getDatabaseProductName()
                    + " " + meta.getDatabaseProductVersion()
                    + " — base de datos: " + con.getCatalog());
        } catch (SQLException e) {
            System.out.println("No fue posible conectar: " + e.getMessage());
        }
    }
}
