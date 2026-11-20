package co.edu.poli.Examen2.infrastructure.adapter.in.javafx;

/**
 * Datos del formulario "Registro de Examen" tal como los escribió el usuario,
 * sin interpretar. Desacopla al {@link ExamenFormMapper} de los widgets de
 * JavaFX, lo que permite probarlo sin interfaz gráfica.
 *
 * @param codigoExamen   texto del campo código
 * @param nombrePaciente texto del campo nombre
 * @param costo          texto del campo costo
 * @param tipoSangre     valor elegido en el combo ({@code null} si no hay selección)
 * @param factorPositivo {@code true} si está marcado Rh positivo; {@code false} si Rh negativo
 */
public record FormularioExamen(String codigoExamen, String nombrePaciente, String costo,
        String tipoSangre, boolean factorPositivo) {
}
