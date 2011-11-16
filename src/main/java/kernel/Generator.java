/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package kernel;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
/**
 * Définition d'un générateur.
 */
public interface Generator {
    enum GeneratorType {
        JAVA,
        SQL,
        CONFIGURATION
    }


    GeneratorType getType();


    void generate(Document doc, String rootDir) throws IOException, TransformerException, BuildException;
}
