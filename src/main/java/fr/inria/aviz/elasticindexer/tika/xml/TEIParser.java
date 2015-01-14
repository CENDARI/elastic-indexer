package fr.inria.aviz.elasticindexer.tika.xml;

import java.util.Collections;
import java.util.Set;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.xml.AttributeMetadataHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.TextContentHandler;
import org.xml.sax.ContentHandler;
import fr.inria.aviz.elasticindexer.tika.CendariProperties;

/**
 * Class TEIParser
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class TEIParser extends AbstractXMLParser {
    private static final String NAMESPACE_URI_TEI = "http://www.tei-c.org/ns/1.0";
    
    private static final Set<MediaType> SUPPORTED_TYPES =
            Collections.singleton(MediaType.application("tei+xml"));
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }
    
    private static ContentHandler getTEIHandler(
            Metadata metadata, Property property, String element, 
            String ...context) {
        return new ContextualElementMetadataHandler(
                NAMESPACE_URI_TEI, element,
                metadata, property, context);
    }

//    private static ContentHandler getTEIAttrHandler(
//            Metadata metadata, Property property, String attr) {
//        return new AttributeMetadataHandler(
//                NAMESPACE_URI_TEI, attr, metadata, property);
//    }
    

    protected ContentHandler getContentHandler(
            ContentHandler handler, Metadata metadata, ParseContext context) {
        ContentHandler defaultContentHandler = new TextContentHandler(handler, true);
        return new TeeContentHandler(
                defaultContentHandler,
                getTEIHandler(metadata, TikaCoreProperties.TITLE, 
                        "title", "titleStmt"),
                getTEIHandler(metadata, TikaCoreProperties.KEYWORDS, 
                        "term", "keywords"),
                //getTEIHandler(metadata, TikaCoreProperties.CREATOR, "creator"),
                getTEIHandler(metadata, TikaCoreProperties.DESCRIPTION, 
                        "description"),
                getTEIHandler(metadata, TikaCoreProperties.PUBLISHER, 
                        "publisher"),
                getTEIHandler(metadata, TikaCoreProperties.CONTRIBUTOR, 
                        "name", "titleStmt", "respStmt"),
                //getTEIHandler(metadata, TikaCoreProperties.CREATED, "date"),
                //getTEIHandler(metadata, TikaCoreProperties.TYPE, "type"),
                //getTEIHandler(metadata, TikaCoreProperties.FORMAT, "format"),
                //getTEIHandler(metadata, TikaCoreProperties.IDENTIFIER, "identifier"),
                getTEIHandler(metadata, TikaCoreProperties.RIGHTS, "licence"),
                new AttributeMetadataHandler(NAMESPACE_URI_XML, "lang", metadata, 
                        CendariProperties.LANG)
                );
    }
}
