package co.edu.poli.Examen2.application.port.in;

import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;

/**
 * Puerto de entrada: registrar un examen ("Guardar").
 */
public interface GuardarExamenUseCase {

    /**
     * Valida el examen con la estrategia vigente, comprueba que su código no
     * exista y lo registra.
     *
     * @param examen examen ya construido
     * @return el examen registrado
     * @throws ReglaNegocioException si incumple la validación o el código ya existe
     */
    ExamenMedico guardar(ExamenMedico examen);
}
