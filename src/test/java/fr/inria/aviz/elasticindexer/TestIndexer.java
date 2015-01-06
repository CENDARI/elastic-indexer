package fr.inria.aviz.elasticindexer;

import java.io.IOException;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class TestIndexer
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class TestIndexer extends TestCase {
    /**
     * Setup the logger.
     */
    @BeforeClass
    public static void initLog() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    
    /**
     * Test document indexing
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     * @throws InterruptedException 
     */
    @Test
    public void testDocumentIndexing() throws JsonParseException, JsonMappingException, IOException, InterruptedException {
        Indexer indexer = Indexer.instance();

        //TODO: this test does not work in isolation. It seems that the index is not cleared on _deleteIndex
        assertNotNull(indexer);
        indexer._deleteIndex();
        Thread.sleep(2000);
        assertTrue(indexer.indexDocument(TestDocumentJSON.DOCUMENT_JSON_1));
        assertTrue(indexer.indexDocument(TestDocumentJSON.DOCUMENT_JSON_2));
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        DocumentInfo doc1 = DocumentInfo.fromJSON(TestDocumentJSON.DOCUMENT_JSON_1, mapper);
        assertNotNull(doc1);
        assertFalse(indexer.indexDocument(doc1));
        doc1.setApplication("anotherapp");
        assertFalse(indexer.indexDocument(doc1));
        indexer.refreshIndex();
        String[] ret = indexer.searchDocument(null, null);
        assertEquals(2, ret.length);
//        for (String s : ret) {
//            System.out.println(s);
//        }
        QueryBuilder langEn = QueryBuilders.termQuery("language", "en");
        ret = indexer.searchDocument(langEn, null);
        assertEquals(2, ret.length);
//        for (String s : ret) {
//            System.out.println(s);
//        }
        
    }

}
