package handler.command;
import javax.xml.transform.TransformerException;
import kernel.BuildException;
import kernel.DomUtil;
import kernel.Generator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
/**
 *
 */
public class CommandHandlerGenerator implements Generator {
    public static final String ERROR_MESSAGE
          = "Les handler de type command ne sont plus generes par datagen, ils doivent etre definis dans le serveur et ajoutes au plugin de mad.";


    public GeneratorType getType() {
        return GeneratorType.JAVA;
    }


    public void generate(Document doc, String rootDir)
          throws BuildException, TransformerException {
        NodeList entityList =
              DomUtil.selectNodeList(doc, "//data/entity/feature/handler-command");

        if (entityList.getLength() != 0) {
            throw new BuildException(ERROR_MESSAGE);
        }
    }
}
