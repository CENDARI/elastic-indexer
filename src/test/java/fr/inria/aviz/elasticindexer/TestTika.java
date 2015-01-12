package fr.inria.aviz.elasticindexer;

import java.io.IOException;

import com.fasterxml.jackson.databind.SerializationFeature;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * Class TestTika
 */
public class TestTika extends TestCase {

    static final String[] fileName = {
        "/package.json",
        "/library-of-castle-mikulov_draft;ead.xml",
        "/D9.1.docx",
        "/D9.1.pdf",
    };

    /**
     * Test the Tika indexer.
     */
    @Test
    public void test() {
        Indexer indexer = Indexer.instance();
        indexer.getMapper().enable(SerializationFeature.INDENT_OUTPUT);
        
        assertNotNull(indexer);
        
        for (String name : fileName) {
            byte[] content;
            try {
                content = IOUtils.toByteArray(getClass().getResource(name));
                DocumentInfo info = indexer.parseDocument(name, null, content, -1);
                System.out.println(indexer.toJSON(info));
                //indexer.indexDocument(info);
            }
            catch(IOException e) {
                fail("Cannot load file "+name); 
            }
        }
    }
}
