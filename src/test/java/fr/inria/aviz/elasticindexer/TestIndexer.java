package fr.inria.aviz.elasticindexer;

import static org.junit.Assert.assertNotNull;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Class TestIndexer
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class TestIndexer {
    @BeforeClass
    public static void initLog() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    /**
     * Regression tests for Indexer
     */
    @Test
    public void test() {
        Indexer indexer = Indexer.instance();
        
        assertNotNull(indexer);
        indexer._deleteIndex();
        indexer.checkESMapping();
    }

}
