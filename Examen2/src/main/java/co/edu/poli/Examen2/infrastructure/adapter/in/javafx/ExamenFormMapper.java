package co.edu.poli.Examen2.infrastructure.adapter.in.javafx;

import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;
import co.edu.poli.Examen2.domain.model.FactorRh;
import co.edu.poli.Examen2.domain.model.TipoSangre;

/**
 * Adaptador de datos del formulario (patrón <b>Adapter</b>): traduce los
 * textos y controles de la pantalla al modelo de dominio, usando el Builder de
 * {@link ExamenMedico}.
 * <p>
 * Un costo que no es numérico no lanza error aquí: se entrega como
 * {@code NaN} para que el Builder lo reporte junto con el resto de problemas.
 */
public class ExamenFormMapper {

    /**
     * Crea el mapper; no tiene estado.
     */
    public ExamenFormMapper() {
        // sin inicialización necesaria
    }

    /**
     * Convierte los datos del formulario en un examen válido.
     *
     * @param formulario datos escritos por el usuario
     * @return el examen construido
     * @throws ReglaNegocioException si algún dato falta o es inválido (todos los errores juntos)
     */
    public ExamenMedico aDominio(FormularioExamen formulario) {
        return ExamenMedico.builder()
                .codigoExamen(formulario.codigoExamen())
                .nombrePaciente(formulario.nombrePaciente())
                .costo(parsearCosto(formulario.costo()))
                .tipoSangre(TipoSangre.desdeTexto(formulario.tipoSangre()).orElse(null))
                .factorRh(formulario.factorPositivo() ? FactorRh.POSITIVO : FactorRh.NEGATIVO)
                .build();
    }

    private static double parsearCosto(String texto) {
        if (texto == null || texto.isBlank()) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}
