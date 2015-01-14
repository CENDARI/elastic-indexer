package fr.inria.aviz.elasticindexer.tika.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.apache.tika.sax.OfflineContentHandler;
import org.apache.tika.sax.TaggedContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Class AbstractXMLParser
 * 
 * @author Jean-Daniel Fekete
 */
public abstract class AbstractXMLParser extends AbstractParser {
    protected static final String NAMESPACE_URI_XML = "http://www.w3.org/XML/1998/namespace";
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void parse(
            InputStream stream, ContentHandler handler,
            Metadata metadata, ParseContext context)
            throws IOException, SAXException, TikaException {
        if (metadata.get(Metadata.CONTENT_TYPE) == null) {
            Iterator<MediaType> iter = getSupportedTypes(context).iterator();
            metadata.set(Metadata.CONTENT_TYPE, iter.next().getType());
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
    
    protected abstract ContentHandler getContentHandler(
            ContentHandler handler, 
            Metadata metadata, 
            ParseContext context);


}
