package co.edu.poli.Examen2.infrastructure.adapter.in.javafx;

import co.edu.poli.Examen2.application.exception.PersistenciaException;
import co.edu.poli.Examen2.application.port.in.DeserializarExamenesUseCase;
import co.edu.poli.Examen2.application.port.in.GuardarExamenUseCase;
import co.edu.poli.Examen2.application.port.in.ListarExamenesUseCase;
import co.edu.poli.Examen2.application.port.in.SerializarExamenesUseCase;
import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Adaptador de entrada: traduce los eventos de la ventana (botones) en
 * llamadas a los puertos de entrada y muestra el resultado o el error en el
 * área de mensajes.
 * <p>
 * No contiene reglas de negocio ni sabe cómo se persiste: lee widgets,
 * delega la traducción al {@link ExamenFormMapper}, invoca un caso de uso y
 * escribe un mensaje.
 */
public class PrimaryController {

    private final GuardarExamenUseCase guardarExamen;
    private final ListarExamenesUseCase listarExamenes;
    private final SerializarExamenesUseCase serializarExamenes;
    private final DeserializarExamenesUseCase deserializarExamenes;
    private final ExamenFormMapper mapper = new ExamenFormMapper();
    private final MensajesPresentador presentador = new MensajesPresentador();

    @FXML
    private TextField txtCodigoExamen;
    @FXML
    private TextField txtNombrePaciente;
    @FXML
    private TextField txtCosto;
    @FXML
    private ComboBox<String> cmbTipoSangre;
    @FXML
    private RadioButton rbPositivo;
    @FXML
    private RadioButton rbNegativo;
    @FXML
    private ToggleGroup tgFactorRH;
    @FXML
    private TextArea txtAreaMensajes;

    /**
     * Crea el controlador con los puertos de entrada que necesita. Lo invoca la
     * fábrica de controladores configurada en {@link App}.
     *
     * @param guardarExamen        caso de uso "Guardar"
     * @param listarExamenes       caso de uso "Listar"
     * @param serializarExamenes   caso de uso "Serializar"
     * @param deserializarExamenes caso de uso "Deserializar"
     */
    public PrimaryController(GuardarExamenUseCase guardarExamen,
            ListarExamenesUseCase listarExamenes,
            SerializarExamenesUseCase serializarExamenes,
            DeserializarExamenesUseCase deserializarExamenes) {
        this.guardarExamen = Objects.requireNonNull(guardarExamen, "guardarExamen");
        this.listarExamenes = Objects.requireNonNull(listarExamenes, "listarExamenes");
        this.serializarExamenes = Objects.requireNonNull(serializarExamenes, "serializarExamenes");
        this.deserializarExamenes = Objects.requireNonNull(deserializarExamenes, "deserializarExamenes");
    }

    /** Inicializa la vista una vez cargado el FXML. */
    @FXML
    public void initialize() {
        txtAreaMensajes.clear();
    }

    /** Evento del botón "Guardar": registra el examen del formulario. */
    @FXML
    private void handleGuardar() {
        ejecutar(() -> {
            String mensaje = presentador.examenGuardado(guardarExamen.guardar(mapper.aDominio(leerFormulario())));
            restablecerCampos();
            return mensaje;
        });
    }

    /** Evento del botón "Limpiar": deja el formulario en su estado inicial. */
    @FXML
    private void handleLimpiar() {
        restablecerCampos();
        mostrar(presentador.formularioLimpio());
    }

    /** Evento del botón "Serializar": guarda los exámenes en el archivo. */
    @FXML
    private void handleSerializar() {
        ejecutar(() -> presentador.serializado(serializarExamenes.serializar()));
    }

    /** Evento del botón "Deserializar": recupera los exámenes del archivo. */
    @FXML
    private void handleDeserializar() {
        ejecutar(() -> presentador.deserializado(deserializarExamenes.deserializar()));
    }

    /** Evento del botón "Listar": muestra los exámenes registrados. */
    @FXML
    private void handleListar() {
        ejecutar(() -> presentador.listado(listarExamenes.listar()));
    }

    /**
     * Ejecuta una acción y muestra su mensaje; los errores de negocio y de
     * persistencia se presentan en lugar de propagarse.
     */
    private void ejecutar(Supplier<String> accion) {
        try {
            mostrar(accion.get());
        } catch (ReglaNegocioException | PersistenciaException e) {
            mostrar(presentador.error(e));
        }
    }

    private FormularioExamen leerFormulario() {
        return new FormularioExamen(
                txtCodigoExamen.getText(),
                txtNombrePaciente.getText(),
                txtCosto.getText(),
                cmbTipoSangre.getValue(),
                !rbNegativo.isSelected());
    }

    private void restablecerCampos() {
        txtCodigoExamen.clear();
        txtNombrePaciente.clear();
        txtCosto.clear();
        cmbTipoSangre.getSelectionModel().clearSelection();
        rbPositivo.setSelected(true);
    }

    private void mostrar(String mensaje) {
        txtAreaMensajes.appendText(mensaje + System.lineSeparator());
    }
}
