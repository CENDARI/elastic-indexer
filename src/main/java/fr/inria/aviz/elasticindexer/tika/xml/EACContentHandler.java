package fr.inria.aviz.elasticindexer.tika.xml;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.ContentHandler;

/**
 * Class EACContentHandler
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class EACContentHandler {
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
                defaultHandler);
    }
}
