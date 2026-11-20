package co.edu.poli.Examen2.application.port.out;

import co.edu.poli.Examen2.application.exception.PersistenciaException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;

import java.util.List;

/**
 * Puerto de salida: almacén de exámenes que necesita la aplicación. Los casos
 * de uso dependen de esta interfaz, nunca de una implementación concreta.
 */
public interface ExamenRepositoryPort {

    /**
     * Agrega un examen al almacén de trabajo.
     *
     * @param examen examen a guardar
     * @return el examen guardado
     */
    ExamenMedico guardar(ExamenMedico examen);

    /**
     * Lista los exámenes del almacén de trabajo.
     *
     * @return copia de solo lectura, en orden de registro
     */
    List<ExamenMedico> listarTodos();

    /**
     * Persiste en el medio duradero todos los exámenes del almacén de trabajo.
     *
     * @return descripción del destino escrito
     * @throws PersistenciaException si falla la escritura
     */
    String serializar();

    /**
     * Lee el medio duradero y sustituye con su contenido el almacén de trabajo.
     *
     * @return los exámenes recuperados
     * @throws PersistenciaException si no existen datos o son ilegibles
     */
    List<ExamenMedico> deserializar();
}
