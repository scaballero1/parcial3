package co.edu.poli.Examen2.application.port.in;

import co.edu.poli.Examen2.application.exception.PersistenciaException;
import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;

/**
 * Puerto de entrada: guardar en almacenamiento duradero los exámenes
 * registrados ("Serializar").
 */
public interface SerializarExamenesUseCase {

    /**
     * Serializa todos los exámenes registrados.
     *
     * @return descripción del destino (por ejemplo, la ruta del archivo escrito)
     * @throws ReglaNegocioException si no hay exámenes que serializar
     * @throws PersistenciaException si falla la escritura
     */
    String serializar();
}
