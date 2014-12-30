package fr.inria.aviz.elasticindexer;

import java.io.IOException;

import junit.framework.TestCase;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class TestDocumentJSON
 */
@SuppressWarnings("javadoc")
public class TestDocumentJSON extends TestCase {
    /** First simple document */
    public static String DOCUMENT_JSON_1 = "{\n" + 
            "   \"contributor\" : { \"name\" : \"John Smith\", \"email\" : \"john@smith.com\" },\n" + 
            "   \"creator\" : { \"name\" : \"John Smith\", \"email\" : \"john@smith.com\" },\n" + 
            "   \"date\" : \"2014-09-14\",\n" + 
            "   \"format\" : \"application/pdf\",\n" + 
            "   \"language\" : [ \"en\", \"fr\" ],\n" + 
            "   \"publisher\" : [ \"Cendari Project\", \"INRIA\" ],\n" + 
            "   \"title\" : \"Test for Cendari\",\n" + 
            "   \"uri\" : \"http://cendari.eu\",\n" + 
            "   \"application\" : \"example\",\n" + 
            "   \"text\" : \"This is a full text in English, et un autre en français\",\n" + 
            "   \"length\" : 55,\n" + 
            "   \"place\" : { \"name\" : \"London\", \"location\" : \"51.5072, -0.1275\" },\n" + 
            "   \"event\" : [ \"Travel\", \"Seminar\" ],\n" + 
            "   \"person\" : { \"name\" : \"John Smith\", \"email\" : \"john@smith.com\" },\n" + 
            "   \"tag\" : [ \"Cendari\", \"Deliverable\" ],\n" + 
            "   \"org\" : \"INRIA\",\n" + 
            "   \"artifact\" : \"Report\",\n" + 
            "   \"ref\" : \"http://www.cendari.eu/public-project-deliverables/D9.1.pdf\",\n" + 
            "   \"groups_allowed\": [\"inria\", \"greencadres\"]\n" + 
            "}\n"; 
    /** Second simple document */
    public static String DOCUMENT_JSON_2 = "{\n" + 
            "    \"contributor\" : { \"name\" : \"Jean-Daniel Fekete\", \"email\" : \"Jean-Daniel.Fekete@inria.fr\" },\n" + 
            "    \"creator\" : { \"name\" : \"Jean-Daniel Fekete\", \"email\" : \"Jean-Daniel.Fekete@inria.fr\" },\n" + 
            "    \"date\" : \"2014-10-01\",\n" + 
            "    \"format\" : \"application/xml\",\n" + 
            "    \"language\" : [ \"en\" ],\n" + 
            "    \"publisher\" : [ \"Gutenberg Project\" ],\n" + 
            "    \"title\" : \"The Hunting of the Snark\",\n" + 
            "    \"uri\" : \"http://www.gutenberg.org/files/13/13-h/13-h.htm\",\n" + 
            "    \"text\" : \"Fit the First THE LANDING Just the place for a Snark the Bellman cried As he landed his crew with care Supporting each man on the top of the tide By a finger entwined in his hair\",\n" + 
            "    \"length\" : 178,\n" + 
            "    \"event\" : [ \"Hunting\" ],\n" + 
            "    \"person\" : { \"name\" : \"Snark\" },\n" + 
            "    \"tag\" : [ \"fictious\" ],\n" + 
            "    \"org\" : \"Caroll\",\n" + 
            "    \"artifact\" : \"Tale\",\n" + 
            "    \"ref\" : \"http://www.gutenberg.org/files/12/12-h/12-h.htm\",\n" + 
            "    \"groups_allowed\": [\"gutenberg\", \"Caroll\"],\n" + 
            "     \"users_allowed\": [\"alice\", \"humpty\"]\n" + 
            "}\n"; 
    @Test
    public void test() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        DocumentInfo doc1 = DocumentInfo.fromJSON(DOCUMENT_JSON_1, mapper);
        assertNotNull(doc1);
        DocumentInfo doc2 = DocumentInfo.fromJSON(DOCUMENT_JSON_2, mapper);
        assertNotNull(doc2);
        DocumentInfo doc3 = DocumentInfo.fromJSON(DOCUMENT_JSON_1, mapper);
        assertEquals(doc1, doc3);
        DocumentInfo doc4 = DocumentInfo.fromJSON(DOCUMENT_JSON_2, mapper);
        assertEquals(doc2, doc4);
    }

}
