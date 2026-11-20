package co.edu.poli.Examen2.application.strategy;

import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Política estricta. Incluye todo lo de la {@link ValidacionBasicaStrategy} y
 * además exige: código con formato {@code AAA-123} (tres letras, guion, al
 * menos tres dígitos), nombre solo con letras/espacios/puntos/apóstrofos/guiones,
 * costo con máximo dos decimales y no superior a {@value #COSTO_MAXIMO}.
 */
public class ValidacionEstrictaStrategy implements ValidacionExamenStrategy {

    /** Costo máximo aceptado por la política estricta. */
    public static final double COSTO_MAXIMO = 100_000_000d;

    private static final Pattern CODIGO = Pattern.compile("^[A-Z]{3}-\\d{3,}$");
    private static final Pattern NOMBRE = Pattern.compile("^\\p{L}[\\p{L}.'\\- ]*$");
    private static final int MAX_DECIMALES = 2;

    private final ValidacionExamenStrategy base;

    /**
     * Crea la estrategia apoyada en una {@link ValidacionBasicaStrategy}.
     */
    public ValidacionEstrictaStrategy() {
        this(new ValidacionBasicaStrategy());
    }

    /**
     * Crea la estrategia apoyada en la política base indicada.
     *
     * @param base política cuyas reglas también deben cumplirse; no puede ser {@code null}
     */
    public ValidacionEstrictaStrategy(ValidacionExamenStrategy base) {
        this.base = Objects.requireNonNull(base, "base");
    }

    @Override
    public void validar(ExamenMedico examen) {
        List<String> errores = new ArrayList<>();
        try {
            base.validar(examen);
        } catch (ReglaNegocioException e) {
            errores.add(e.getMessage());
        }
        if (!CODIGO.matcher(examen.getCodigoExamen()).matches()) {
            errores.add("El código debe tener el formato AAA-123 (tres letras, guion y al menos tres dígitos).");
        }
        if (!NOMBRE.matcher(examen.getNombrePaciente()).matches()) {
            errores.add("El nombre del paciente solo puede contener letras y espacios.");
        }
        if (examen.getCosto() > COSTO_MAXIMO) {
            errores.add("El costo no puede superar " + (long) COSTO_MAXIMO + ".");
        }
        if (BigDecimal.valueOf(examen.getCosto()).stripTrailingZeros().scale() > MAX_DECIMALES) {
            errores.add("El costo admite como máximo dos decimales.");
        }
        if (!errores.isEmpty()) {
            throw new ReglaNegocioException(String.join(System.lineSeparator(), errores));
        }
    }
}
