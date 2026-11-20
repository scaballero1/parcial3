package co.edu.poli.Examen2.application.strategy;

import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;

import java.util.ArrayList;
import java.util.List;

/**
 * Política permisiva: el código debe medir entre 3 y 20 caracteres y el nombre
 * del paciente entre 3 y 100.
 */
public class ValidacionBasicaStrategy implements ValidacionExamenStrategy {

    private static final int CODIGO_MIN = 3;
    private static final int CODIGO_MAX = 20;
    private static final int NOMBRE_MIN = 3;
    private static final int NOMBRE_MAX = 100;

    /**
     * Crea la estrategia; no tiene estado.
     */
    public ValidacionBasicaStrategy() {
        // sin inicialización necesaria
    }

    @Override
    public void validar(ExamenMedico examen) {
        List<String> errores = new ArrayList<>();
        int largoCodigo = examen.getCodigoExamen().length();
        if (largoCodigo < CODIGO_MIN || largoCodigo > CODIGO_MAX) {
            errores.add("El código debe tener entre 3 y 20 caracteres.");
        }
        int largoNombre = examen.getNombrePaciente().length();
        if (largoNombre < NOMBRE_MIN || largoNombre > NOMBRE_MAX) {
            errores.add("El nombre del paciente debe tener entre 3 y 100 caracteres.");
        }
        if (!errores.isEmpty()) {
            throw new ReglaNegocioException(String.join(System.lineSeparator(), errores));
        }
    }
}
