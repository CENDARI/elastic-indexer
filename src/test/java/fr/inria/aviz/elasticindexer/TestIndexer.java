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
    /**
     * Setup the logger.
     */
    @BeforeClass
    public static void initLog() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    /**
     * Regression tests for Indexer
     * @throws InterruptedException if the sleep fails...
     */
    @Test
    public void test() throws InterruptedException {
        Indexer indexer = Indexer.instance();
        
        assertNotNull(indexer);
        indexer._deleteIndex();
        Thread.sleep(2000);
        indexer.checkESMapping();
        indexer._deleteMapping();
        Thread.sleep(2000);
        indexer.checkESMapping();
        indexer._close();
        Thread.sleep(2000);
        indexer.checkESMapping();
    }

}
