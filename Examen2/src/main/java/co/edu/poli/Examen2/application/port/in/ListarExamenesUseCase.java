package co.edu.poli.Examen2.application.port.in;

import co.edu.poli.Examen2.domain.model.ExamenMedico;

import java.util.List;

/**
 * Puerto de entrada: consultar los exámenes registrados ("Listar").
 */
public interface ListarExamenesUseCase {

    /**
     * Lista los exámenes en orden de registro.
     *
     * @return exámenes registrados; lista vacía si no hay ninguno
     */
    List<ExamenMedico> listar();
}
