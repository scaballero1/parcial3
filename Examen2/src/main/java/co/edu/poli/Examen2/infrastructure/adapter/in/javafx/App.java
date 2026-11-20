package co.edu.poli.Examen2.infrastructure.adapter.in.javafx;

import co.edu.poli.Examen2.infrastructure.config.ComposicionRaiz;
import co.edu.poli.Examen2.infrastructure.config.ConfiguracionAplicacion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de entrada de la aplicación JavaFX.
 * <p>
 * Arma la {@link ComposicionRaiz} y la entrega al {@link FXMLLoader} mediante
 * una fábrica de controladores, de modo que el controlador reciba sus puertos
 * por constructor en lugar de crearlos él mismo.
 */
public class App extends Application {

    private static final String VISTA_PRINCIPAL = "/co/edu/poli/Examen2/primary.fxml";

    /**
     * Crea la aplicación; JavaFX la instancia al lanzarla.
     */
    public App() {
        // sin inicialización necesaria
    }

    @Override
    public void start(Stage stage) throws IOException {
        ComposicionRaiz raiz = new ComposicionRaiz(ConfiguracionAplicacion.cargar());

        FXMLLoader cargador = new FXMLLoader(App.class.getResource(VISTA_PRINCIPAL));
        cargador.setControllerFactory(tipo -> new PrimaryController(
                raiz.guardarExamen(),
                raiz.listarExamenes(),
                raiz.serializarExamenes(),
                raiz.deserializarExamenes()));
        Parent contenido = cargador.load();

        stage.setTitle("Exámenes de Sangre");
        stage.setScene(new Scene(contenido, 640, 640));
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Lanza la aplicación.
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    public static void main(String[] args) {
        launch();
    }
}
