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
        "/data/oai-pmh.xml",
        "/data/frlacinemathequedetoulouse.eag.xml",
        "/data/library-of-castle-mikulov_draft;ead.xml",
        "/data/B360446201_B343_2_tei.xml",
        "/data/package.json",
        "/data/D9.1.docx",
        "/data/D9.1.pdf",
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
                String text = info.getText();
//                if (text.length() > 100)
//                    info.setText(text.substring(0, 100));
                System.out.println(indexer.toJSON(info));
                //indexer.indexDocument(info);
            }
            catch(IOException e) {
                fail("Cannot load file "+name); 
            }
        }
    }
}
