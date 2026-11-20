package co.edu.poli.Examen2.application.exception;

/**
 * Fallo al leer o escribir en el medio de almacenamiento. Los adaptadores de
 * salida traducen sus errores técnicos (E/S, archivo corrupto...) a esta
 * excepción para que el núcleo no dependa de detalles de infraestructura.
 */
public class PersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción con un mensaje.
     *
     * @param mensaje descripción legible del problema
     */
    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea la excepción con un mensaje y su causa técnica.
     *
     * @param mensaje descripción legible del problema
     * @param causa   excepción original
     */
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
