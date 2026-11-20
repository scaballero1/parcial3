package co.edu.poli.Examen2.infrastructure.adapter.in.javafx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Verifica, sin arrancar JavaFX, que {@code primary.fxml} y
 * {@link PrimaryController} están alineados: cada {@code onAction} tiene su
 * método, cada {@code fx:id} de campo tiene su atributo y el controlador
 * declarado es el correcto.
 */
class PrimaryControllerFxmlTest {

    private static final String FX = "http://javafx.com/fxml/1";
    private static Document fxml;

    @BeforeAll
    static void cargarFxml() throws Exception {
        try (InputStream in = PrimaryControllerFxmlTest.class.getResourceAsStream("/co/edu/poli/Examen2/primary.fxml")) {
            assertNotNull(in, "primary.fxml debe estar en el classpath");
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            fxml = f.newDocumentBuilder().parse(in);
        }
    }

    private static Set<String> atributos(String nombre, boolean fxNamespace) {
        Set<String> valores = new HashSet<>();
        NodeList todos = fxml.getElementsByTagName("*");
        for (int i = 0; i < todos.getLength(); i++) {
            Element e = (Element) todos.item(i);
            String valor = fxNamespace ? e.getAttributeNS(FX, nombre) : e.getAttribute(nombre);
            if (!valor.isEmpty()) {
                valores.add(valor);
            }
        }
        return valores;
    }

    @Test
    void declaraElControladorCorrecto() {
        assertEquals(PrimaryController.class.getName(),
                fxml.getDocumentElement().getAttributeNS(FX, "controller"));
    }

    @Test
    void cadaOnActionTieneSuMetodoEnElControlador() {
        Set<String> metodos = new HashSet<>();
        for (Method m : PrimaryController.class.getDeclaredMethods()) {
            metodos.add(m.getName());
        }
        Set<String> acciones = atributos("onAction", false);

        assertEquals(Set.of("#handleGuardar", "#handleListar", "#handleSerializar", "#handleDeserializar", "#handleLimpiar"),
                acciones);
        for (String accion : acciones) {
            assertTrue(metodos.contains(accion.substring(1)), "falta el método " + accion);
        }
    }

    @Test
    void cadaCampoFxmlDelControladorTieneSuFxIdEnElFxml() {
        Set<String> ids = atributos("id", true);
        for (Field campo : PrimaryController.class.getDeclaredFields()) {
            if (campo.isAnnotationPresent(javafx.fxml.FXML.class)) {
                assertTrue(ids.contains(campo.getName()), "falta fx:id=" + campo.getName() + " en el FXML");
            }
        }
    }
}
