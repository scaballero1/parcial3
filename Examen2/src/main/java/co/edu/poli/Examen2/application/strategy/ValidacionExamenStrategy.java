package co.edu.poli.Examen2.application.strategy;

import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;

/**
 * Estrategia de validación (patrón <b>Strategy</b>): define una política
 * intercambiable para decidir si un {@link ExamenMedico} es aceptable.
 * <p>
 * Los invariantes mínimos (campos presentes, costo positivo) ya los garantiza
 * el Builder; las estrategias aplican la política de calidad del negocio, que
 * puede variar (básica, estricta, otra futura) sin tocar el servicio.
 */
@FunctionalInterface
public interface ValidacionExamenStrategy {

    /**
     * Valida el examen.
     *
     * @param examen examen a validar; no es {@code null}
     * @throws ReglaNegocioException si el examen incumple la política
     */
    void validar(ExamenMedico examen);
}
