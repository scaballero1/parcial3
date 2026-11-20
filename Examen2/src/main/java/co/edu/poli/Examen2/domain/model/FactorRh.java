package co.edu.poli.Examen2.domain.model;

import java.util.Locale;
import java.util.Optional;

/**
 * Factor RH de un grupo sanguíneo.
 */
public enum FactorRh {
    /** Rh positivo. */
    POSITIVO("+"),
    /** Rh negativo. */
    NEGATIVO("-");

    private final String simbolo;

    FactorRh(String simbolo) {
        this.simbolo = simbolo;
    }

    /**
     * Devuelve el símbolo corto del factor.
     *
     * @return {@code "+"} o {@code "-"}
     */
    public String getSimbolo() {
        return simbolo;
    }

    /**
     * Interpreta un texto como factor RH. Acepta el símbolo ({@code +}/{@code -})
     * o el nombre ({@code positivo}/{@code negativo}), sin distinguir mayúsculas.
     *
     * @param texto texto a interpretar (puede ser {@code null})
     * @return el factor correspondiente, o vacío si no se reconoce
     */
    public static Optional<FactorRh> desdeTexto(String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        String normalizado = texto.trim().toUpperCase(Locale.ROOT);
        for (FactorRh factor : values()) {
            if (factor.simbolo.equals(normalizado) || factor.name().equals(normalizado)) {
                return Optional.of(factor);
            }
        }
        return Optional.empty();
    }
}
