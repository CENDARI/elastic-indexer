package fr.inria.aviz.elasticindexer.tika.xml;


import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.xml.ElementMetadataHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.ContentHandler;

/**
 * Class TEIContentHandler
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class TEIContentHandler {
    private static final String NAMESPACE_URI_TEI = "http://www.tei-c.org/ns/1.0";
    
    private static ContentHandler getTEIHandler(
            Metadata metadata, Property property, String element) {
        return new ElementMetadataHandler(
                NAMESPACE_URI_TEI, element,
                metadata, property);
    }
    
    /**
     * 
     * @param defaultHandler
     * @param handler
     * @param metadata
     * @param context
     * @return a ContentHandler
     */
    static public ContentHandler getContentHandler(
            ContentHandler defaultHandler,
            ContentHandler handler, Metadata metadata, ParseContext context) {
        return new TeeContentHandler(
                defaultHandler,
                getTEIHandler(metadata, TikaCoreProperties.TITLE, "title"),
                getTEIHandler(metadata, TikaCoreProperties.KEYWORDS, "subject"),
                getTEIHandler(metadata, TikaCoreProperties.CREATOR, "creator"),
                getTEIHandler(metadata, TikaCoreProperties.DESCRIPTION, "description"),
                getTEIHandler(metadata, TikaCoreProperties.PUBLISHER, "publisher"),
                getTEIHandler(metadata, TikaCoreProperties.CONTRIBUTOR, "contributor"),
                getTEIHandler(metadata, TikaCoreProperties.CREATED, "date"),
                getTEIHandler(metadata, TikaCoreProperties.TYPE, "type"),
                getTEIHandler(metadata, TikaCoreProperties.FORMAT, "format"),
                getTEIHandler(metadata, TikaCoreProperties.IDENTIFIER, "identifier"),
                getTEIHandler(metadata, TikaCoreProperties.LANGUAGE, "language"),
                getTEIHandler(metadata, TikaCoreProperties.RIGHTS, "rights"));
    }
}
