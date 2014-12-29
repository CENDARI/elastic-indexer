package fr.inria.aviz.elasticindexer;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Class TestIndexer
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class TestIndexer {

    @Test
    public void test() {
        Indexer indexer = Indexer.instance();
        
        assertNotNull(indexer);
        
    }

}
