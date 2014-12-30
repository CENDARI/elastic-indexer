package fr.inria.aviz.elasticindexer;

/**
 * Test Suite
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * Run all tests in the right order
 */
@RunWith(Suite.class)
@SuiteClasses({ TestDocumentJSON.class, TestIndexer.class })
public class AllTests {

} 

