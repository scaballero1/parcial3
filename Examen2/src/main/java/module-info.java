/**
 * Módulo de la aplicación "Exámenes de Sangre", organizado como arquitectura
 * hexagonal (domain, application, infrastructure).
 */
module co.edu.poli.Examen2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens co.edu.poli.Examen2.infrastructure.adapter.in.javafx to javafx.fxml;

    exports co.edu.poli.Examen2.infrastructure.adapter.in.javafx to javafx.graphics;
}
