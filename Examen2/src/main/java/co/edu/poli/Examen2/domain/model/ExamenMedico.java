package co.edu.poli.Examen2.domain.model;

import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Entidad de dominio: un examen de sangre practicado a un paciente.
 * <p>
 * Es inmutable y su identidad es el {@linkplain #getCodigoExamen() código}.
 * Solo puede crearse con su {@link Builder} (patrón <b>Builder</b>), que
 * garantiza los invariantes: ninguna instancia existe con campos vacíos, costo
 * no positivo o grupo sanguíneo incompleto.
 */
public final class ExamenMedico {

    private final String codigoExamen;
    private final String nombrePaciente;
    private final double costo;
    private final TipoSangre tipoSangre;
    private final FactorRh factorRh;

    private ExamenMedico(Builder b) {
        this.codigoExamen = b.codigoExamen;
        this.nombrePaciente = b.nombrePaciente;
        this.costo = b.costo;
        this.tipoSangre = b.tipoSangre;
        this.factorRh = b.factorRh;
    }

    /**
     * Inicia la construcción de un examen.
     *
     * @return un nuevo {@link Builder} vacío
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Devuelve el código del examen.
     *
     * @return código único, en mayúsculas y sin espacios laterales
     */
    public String getCodigoExamen() {
        return codigoExamen;
    }

    /**
     * Devuelve el nombre del paciente.
     *
     * @return nombre sin espacios sobrantes
     */
    public String getNombrePaciente() {
        return nombrePaciente;
    }

    /**
     * Devuelve el costo del examen.
     *
     * @return costo, siempre mayor que cero
     */
    public double getCosto() {
        return costo;
    }

    /**
     * Devuelve el tipo de sangre.
     *
     * @return tipo de sangre, nunca {@code null}
     */
    public TipoSangre getTipoSangre() {
        return tipoSangre;
    }

    /**
     * Devuelve el factor RH.
     *
     * @return factor RH, nunca {@code null}
     */
    public FactorRh getFactorRh() {
        return factorRh;
    }

    /**
     * Grupo sanguíneo completo, por ejemplo {@code "AB-"}.
     *
     * @return tipo de sangre seguido del símbolo del factor RH
     */
    public String getGrupoSanguineo() {
        return tipoSangre.name() + factorRh.getSimbolo();
    }

    /** Dos exámenes son iguales si tienen el mismo código. */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof ExamenMedico)) {
            return false;
        }
        return codigoExamen.equals(((ExamenMedico) otro).codigoExamen);
    }

    @Override
    public int hashCode() {
        return codigoExamen.hashCode();
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s | %s | %s | $%,.2f",
                codigoExamen, nombrePaciente, getGrupoSanguineo(), costo);
    }

    /**
     * Constructor paso a paso de {@link ExamenMedico} (patrón <b>Builder</b>).
     * <p>
     * Normaliza los textos y, en {@link #build()}, verifica <b>todos</b> los
     * invariantes, informándolos juntos en una sola {@link ReglaNegocioException}.
     */
    public static final class Builder {

        private String codigoExamen;
        private String nombrePaciente;
        private double costo = Double.NaN;
        private TipoSangre tipoSangre;
        private FactorRh factorRh;

        private Builder() {
        }

        /**
         * Establece el código del examen.
         *
         * @param codigoExamen código (se recorta y pasa a mayúsculas)
         * @return este constructor
         */
        public Builder codigoExamen(String codigoExamen) {
            this.codigoExamen = codigoExamen == null ? null : codigoExamen.trim().toUpperCase(Locale.ROOT);
            return this;
        }

        /**
         * Establece el nombre del paciente.
         *
         * @param nombrePaciente nombre (se recorta y se colapsan espacios repetidos)
         * @return este constructor
         */
        public Builder nombrePaciente(String nombrePaciente) {
            this.nombrePaciente = nombrePaciente == null ? null : nombrePaciente.trim().replaceAll("\\s+", " ");
            return this;
        }

        /**
         * Establece el costo. {@code NaN} equivale a "sin definir".
         *
         * @param costo costo del examen
         * @return este constructor
         */
        public Builder costo(double costo) {
            this.costo = costo;
            return this;
        }

        /**
         * Establece el tipo de sangre.
         *
         * @param tipoSangre tipo de sangre
         * @return este constructor
         */
        public Builder tipoSangre(TipoSangre tipoSangre) {
            this.tipoSangre = tipoSangre;
            return this;
        }

        /**
         * Establece el factor RH.
         *
         * @param factorRh factor RH
         * @return este constructor
         */
        public Builder factorRh(FactorRh factorRh) {
            this.factorRh = factorRh;
            return this;
        }

        /**
         * Verifica los invariantes y crea el examen.
         *
         * @return un {@link ExamenMedico} válido
         * @throws ReglaNegocioException si falta algún dato o el costo no es un número positivo;
         *         el mensaje contiene un error por línea
         */
        public ExamenMedico build() {
            List<String> errores = new ArrayList<>();
            if (codigoExamen == null || codigoExamen.isEmpty()) {
                errores.add("El código del examen es obligatorio.");
            }
            if (nombrePaciente == null || nombrePaciente.isEmpty()) {
                errores.add("El nombre del paciente es obligatorio.");
            }
            if (!Double.isFinite(costo) || costo <= 0) {
                errores.add("El costo debe ser un número mayor que cero.");
            }
            if (tipoSangre == null) {
                errores.add("Debe seleccionar un tipo de sangre válido (A, B, AB u O).");
            }
            if (factorRh == null) {
                errores.add("Debe seleccionar un factor RH válido (positivo o negativo).");
            }
            if (!errores.isEmpty()) {
                throw new ReglaNegocioException(String.join(System.lineSeparator(), errores));
            }
            return new ExamenMedico(this);
        }
    }
}
