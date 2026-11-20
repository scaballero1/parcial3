package co.edu.poli.Examen2.infrastructure.adapter.out.file;

import co.edu.poli.Examen2.domain.model.ExamenMedico;
import co.edu.poli.Examen2.domain.model.FactorRh;
import co.edu.poli.Examen2.domain.model.TipoSangre;

import java.io.Serializable;

/**
 * Representación persistente de un {@link ExamenMedico}.
 * <p>
 * Evita que la entidad de dominio deba implementar {@link Serializable} y
 * quede atada al formato de archivo. Al leer, el examen se reconstruye con el
 * Builder, de modo que un archivo manipulado no puede introducir datos inválidos.
 *
 * @param codigoExamen   código del examen
 * @param nombrePaciente nombre del paciente
 * @param costo          costo del examen
 * @param tipoSangre     tipo de sangre como texto
 * @param factorRh       factor RH como símbolo ({@code +} o {@code -})
 */
public record ExamenRegistro(String codigoExamen, String nombrePaciente, double costo,
        String tipoSangre, String factorRh) implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Convierte una entidad en su registro persistente.
     *
     * @param examen entidad de dominio
     * @return registro equivalente
     */
    public static ExamenRegistro desde(ExamenMedico examen) {
        return new ExamenRegistro(examen.getCodigoExamen(), examen.getNombrePaciente(), examen.getCosto(),
                examen.getTipoSangre().name(), examen.getFactorRh().getSimbolo());
    }

    /**
     * Reconstruye la entidad pasando por los invariantes del Builder.
     *
     * @return entidad de dominio
     * @throws co.edu.poli.Examen2.domain.exception.ReglaNegocioException si los datos guardados son inválidos
     */
    public ExamenMedico aDominio() {
        return ExamenMedico.builder()
                .codigoExamen(codigoExamen)
                .nombrePaciente(nombrePaciente)
                .costo(costo)
                .tipoSangre(TipoSangre.desdeTexto(tipoSangre).orElse(null))
                .factorRh(FactorRh.desdeTexto(factorRh).orElse(null))
                .build();
    }
}
