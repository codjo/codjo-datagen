/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package role;
import javax.xml.transform.TransformerConfigurationException;
import kernel.DomUtil;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
/**
 * Classe de teste de {@link RoleGenerator}.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.3 $
 */
public class RoleGeneratorTest extends GeneratorTestCase {

    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new RoleGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.CONFIGURATION;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "role.xml";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/role/RoleTest.xml";
    }


    @Test
    public void test_no_generate_cause_no_role() throws Exception {
        getGeneratedFile().delete();
        createGenerator().generate(DomUtil.toDocument("src/test/resources/role/NoRoleTest.xml"), ROOT);
        assertFalse("Aucun fichier produit.", getGeneratedFile().exists());
    }
}
