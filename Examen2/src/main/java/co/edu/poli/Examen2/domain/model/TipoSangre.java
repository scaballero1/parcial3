package co.edu.poli.Examen2.domain.model;

import java.util.Locale;
import java.util.Optional;

/**
 * Grupos sanguíneos del sistema ABO. El factor RH se modela en {@link FactorRh}.
 */
public enum TipoSangre {
    /** Grupo A. */
    A,
    /** Grupo B. */
    B,
    /** Grupo AB. */
    AB,
    /** Grupo O. */
    O;

    /**
     * Interpreta un texto como tipo de sangre, ignorando mayúsculas y espacios laterales.
     *
     * @param texto texto a interpretar (puede ser {@code null})
     * @return el tipo correspondiente, o vacío si no se reconoce
     */
    public static Optional<TipoSangre> desdeTexto(String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        String normalizado = texto.trim().toUpperCase(Locale.ROOT);
        for (TipoSangre tipo : values()) {
            if (tipo.name().equals(normalizado)) {
                return Optional.of(tipo);
            }
        }
        return Optional.empty();
    }
}
