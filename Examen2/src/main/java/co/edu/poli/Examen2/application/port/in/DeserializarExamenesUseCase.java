package co.edu.poli.Examen2.application.port.in;

import co.edu.poli.Examen2.application.exception.PersistenciaException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;

import java.util.List;

/**
 * Puerto de entrada: recuperar los exámenes guardados ("Deserializar").
 * Reemplaza los exámenes que hubiera en memoria.
 */
public interface DeserializarExamenesUseCase {

    /**
     * Recupera los exámenes serializados.
     *
     * @return los exámenes recuperados, que pasan a ser los registrados
     * @throws PersistenciaException si no hay datos o están corruptos
     */
    List<ExamenMedico> deserializar();
}
