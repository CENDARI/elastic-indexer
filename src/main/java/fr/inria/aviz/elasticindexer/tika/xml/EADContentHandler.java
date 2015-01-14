package fr.inria.aviz.elasticindexer.tika.xml;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.xml.ElementMetadataHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.ContentHandler;

/**
 * Class EADContentHandler
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class EADContentHandler {
    private static ContentHandler getEADHandler(
            Metadata metadata, Property property, String element) {
        return new ElementMetadataHandler(
                null, element,
                metadata, property);
    }    //TODO
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
                getEADHandler(metadata, TikaCoreProperties.TITLE, "titleproper"),
                getEADHandler(metadata, TikaCoreProperties.KEYWORDS, "subject"),
                getEADHandler(metadata, TikaCoreProperties.CREATOR, "author"),
                getEADHandler(metadata, TikaCoreProperties.DESCRIPTION, "description"),
                getEADHandler(metadata, TikaCoreProperties.PUBLISHER, "publisher"),
                getEADHandler(metadata, TikaCoreProperties.CONTRIBUTOR, "contributor"),
                getEADHandler(metadata, TikaCoreProperties.CREATED, "date"),
                getEADHandler(metadata, TikaCoreProperties.TYPE, "type"),
                getEADHandler(metadata, TikaCoreProperties.FORMAT, "format"),
                getEADHandler(metadata, TikaCoreProperties.IDENTIFIER, "identifier"),
                getEADHandler(metadata, TikaCoreProperties.LANGUAGE, "language"),
                getEADHandler(metadata, TikaCoreProperties.RIGHTS, "rights"));
    }

}
