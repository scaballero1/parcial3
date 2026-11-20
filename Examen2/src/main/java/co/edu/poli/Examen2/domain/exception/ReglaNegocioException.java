package co.edu.poli.Examen2.domain.exception;

/**
 * Se lanza cuando se incumple una regla o invariante del negocio (datos de un
 * examen inválidos, código duplicado, operación sin datos, etc.).
 * <p>
 * Es una excepción sin marcar: los adaptadores de entrada la capturan y
 * muestran su mensaje al usuario.
 */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción con el mensaje que verá el usuario.
     *
     * @param mensaje descripción legible de la regla incumplida
     */
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
