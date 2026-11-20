package co.edu.poli.Examen2.infrastructure.adapter.in.javafx;

import co.edu.poli.Examen2.domain.model.ExamenMedico;

import java.util.List;

/**
 * Convierte los resultados y errores de los casos de uso en los textos que se
 * muestran en el área de mensajes. No depende de JavaFX, por lo que se prueba
 * sin interfaz gráfica.
 */
public class MensajesPresentador {

    /**
     * Crea el presentador; no tiene estado.
     */
    public MensajesPresentador() {
        // sin inicialización necesaria
    }

    /**
     * Mensaje de confirmación tras registrar un examen.
     *
     * @param examen examen registrado
     * @return texto para el usuario
     */
    public String examenGuardado(ExamenMedico examen) {
        return "✔ Examen registrado: " + examen;
    }

    /**
     * Listado numerado de exámenes.
     *
     * @param examenes exámenes a mostrar
     * @return listado, o un aviso si está vacío
     */
    public String listado(List<ExamenMedico> examenes) {
        if (examenes.isEmpty()) {
            return "No hay exámenes registrados.";
        }
        StringBuilder texto = new StringBuilder("Exámenes registrados (").append(examenes.size()).append("):");
        int numero = 1;
        for (ExamenMedico examen : examenes) {
            texto.append(System.lineSeparator()).append("  ").append(numero++).append(". ").append(examen);
        }
        return texto.toString();
    }

    /**
     * Mensaje de confirmación tras serializar.
     *
     * @param destino descripción del destino escrito
     * @return texto para el usuario
     */
    public String serializado(String destino) {
        return "✔ Exámenes serializados en: " + destino;
    }

    /**
     * Mensaje de confirmación tras deserializar, seguido del listado.
     *
     * @param examenes exámenes recuperados
     * @return texto para el usuario
     */
    public String deserializado(List<ExamenMedico> examenes) {
        int cantidad = examenes.size();
        String resumen = cantidad == 1 ? "✔ Se recuperó 1 examen." : "✔ Se recuperaron " + cantidad + " exámenes.";
        return resumen + System.lineSeparator() + listado(examenes);
    }

    /**
     * Mensaje que se muestra al limpiar el formulario.
     *
     * @return texto para el usuario
     */
    public String formularioLimpio() {
        return "Formulario limpio.";
    }

    /**
     * Formatea un error. Si el mensaje tiene varias líneas (varios errores de
     * validación), las presenta como viñetas.
     *
     * @param error excepción capturada
     * @return texto para el usuario
     */
    public String error(RuntimeException error) {
        String mensaje = String.valueOf(error.getMessage());
        String[] lineas = mensaje.split("\\R");
        if (lineas.length == 1) {
            return "✖ " + mensaje;
        }
        StringBuilder texto = new StringBuilder("✖ Se encontraron problemas:");
        for (String linea : lineas) {
            texto.append(System.lineSeparator()).append("  - ").append(linea);
        }
        return texto.toString();
    }
}
