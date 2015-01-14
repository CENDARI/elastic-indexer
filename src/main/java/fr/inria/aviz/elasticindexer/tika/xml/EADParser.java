package fr.inria.aviz.elasticindexer.tika.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import fr.inria.aviz.elasticindexer.tika.CendariProperties;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.xml.AttributeMetadataHandler;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.apache.tika.sax.OfflineContentHandler;
import org.apache.tika.sax.TaggedContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.TextContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Class EADParser
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class EADParser extends AbstractXMLParser {
    private static final String NAMESPACE_URI_EAD = "";
    
    private static final Set<MediaType> SUPPORTED_TYPES =
            Collections.singleton(MediaType.application("ead+xml"));
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }
    
    private static ContentHandler getEADHandler(
            Metadata metadata, Property property, String element, 
            String ...context) {
        return new ContextualElementMetadataHandler(
                NAMESPACE_URI_EAD, element,
                metadata, property, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void parse(
            InputStream stream, ContentHandler handler,
            Metadata metadata, ParseContext context)
            throws IOException, SAXException, TikaException {
        if (metadata.get(Metadata.CONTENT_TYPE) == null) {
            metadata.set(Metadata.CONTENT_TYPE, "application/tei+xml");
        }

        final XHTMLContentHandler xhtml =
            new XHTMLContentHandler(handler, metadata);
        xhtml.startDocument();
        xhtml.startElement("p");

        TaggedContentHandler tagged = new TaggedContentHandler(handler);
        try {
            context.getSAXParser().parse(
                    new CloseShieldInputStream(stream),
                    new OfflineContentHandler(new EmbeddedContentHandler(
                            getContentHandler(tagged, metadata, context))));
        } catch (SAXException e) {
            tagged.throwIfCauseOf(e);
            throw new TikaException("XML parse error", e);
        } finally {
            xhtml.endElement("p");
            xhtml.endDocument();
        }
    }

    protected ContentHandler getContentHandler(
            ContentHandler handler, Metadata metadata, ParseContext context) {
        ContentHandler defaultContentHandler = new TextContentHandler(handler, true);
        return new TeeContentHandler(
                defaultContentHandler,
                getEADHandler(metadata, TikaCoreProperties.TITLE, 
                        "titleproper", "titlestmt"),
                //TODO check the specific EAD elements
                getEADHandler(metadata, TikaCoreProperties.KEYWORDS, 
                        "term", "keywords"),
                //getTEIHandler(metadata, TikaCoreProperties.CREATOR, "creator"),
                getEADHandler(metadata, TikaCoreProperties.DESCRIPTION, 
                        "description"),
                //getEADHandler(metadata, TikaCoreProperties.PUBLISHER, "publisher"),
                getEADHandler(metadata, TikaCoreProperties.CONTRIBUTOR, 
                        "name", "titleStmt", "respStmt"),
                //getEADHandler(metadata, TikaCoreProperties.CREATED, "date"),
                //getEADHandler(metadata, TikaCoreProperties.TYPE, "type"),
                //getEADHandler(metadata, TikaCoreProperties.FORMAT, "format"),
                //getEADHandler(metadata, TikaCoreProperties.IDENTIFIER, "identifier"),
                getEADHandler(metadata, TikaCoreProperties.RIGHTS, "licence"),
                new AttributeMetadataHandler(NAMESPACE_URI_XML, "lang", metadata, 
                        CendariProperties.LANG),
                new AttributeMetadataHandler(NAMESPACE_URI_EAD, "langcode", metadata, 
                        CendariProperties.LANG)
        );
    }

}
