package co.edu.poli.Examen2.infrastructure.config;

import co.edu.poli.Examen2.application.port.in.DeserializarExamenesUseCase;
import co.edu.poli.Examen2.application.port.in.GuardarExamenUseCase;
import co.edu.poli.Examen2.application.port.in.ListarExamenesUseCase;
import co.edu.poli.Examen2.application.port.in.SerializarExamenesUseCase;
import co.edu.poli.Examen2.application.port.out.ExamenRepositoryPort;
import co.edu.poli.Examen2.application.strategy.ValidacionBasicaStrategy;
import co.edu.poli.Examen2.application.strategy.ValidacionEstrictaStrategy;
import co.edu.poli.Examen2.application.strategy.ValidacionExamenStrategy;
import co.edu.poli.Examen2.application.usecase.GestionExamenService;
import co.edu.poli.Examen2.infrastructure.adapter.out.file.ArchivoExamenAdapter;

import java.util.Objects;

/**
 * Raíz de composición: único lugar que conoce las implementaciones concretas y
 * las conecta (inyección de dependencias manual). Ni el dominio, ni los casos
 * de uso, ni los adaptadores de entrada hacen {@code new} de un adaptador de
 * salida ni de una estrategia.
 */
public final class ComposicionRaiz {

    private final GestionExamenService servicio;

    /**
     * Ensambla el grafo de objetos completo según la configuración.
     *
     * @param configuracion parámetros de arranque
     */
    public ComposicionRaiz(ConfiguracionAplicacion configuracion) {
        Objects.requireNonNull(configuracion, "configuracion");
        ExamenRepositoryPort repositorio = new ArchivoExamenAdapter(configuracion.archivoPersistencia());
        this.servicio = new GestionExamenService(repositorio, crearEstrategia(configuracion));
    }

    private static ValidacionExamenStrategy crearEstrategia(ConfiguracionAplicacion configuracion) {
        return configuracion.validacion() == ConfiguracionAplicacion.ModoValidacion.BASICA
                ? new ValidacionBasicaStrategy()
                : new ValidacionEstrictaStrategy();
    }

    /**
     * Entrega el puerto de entrada "Guardar".
     *
     * @return caso de uso de guardar
     */
    public GuardarExamenUseCase guardarExamen() {
        return servicio;
    }

    /**
     * Entrega el puerto de entrada "Listar".
     *
     * @return caso de uso de listar
     */
    public ListarExamenesUseCase listarExamenes() {
        return servicio;
    }

    /**
     * Entrega el puerto de entrada "Serializar".
     *
     * @return caso de uso de serializar
     */
    public SerializarExamenesUseCase serializarExamenes() {
        return servicio;
    }

    /**
     * Entrega el puerto de entrada "Deserializar".
     *
     * @return caso de uso de deserializar
     */
    public DeserializarExamenesUseCase deserializarExamenes() {
        return servicio;
    }
}
