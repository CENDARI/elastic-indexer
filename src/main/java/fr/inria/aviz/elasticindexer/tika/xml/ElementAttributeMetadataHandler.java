package fr.inria.aviz.elasticindexer.tika.xml;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.parser.xml.AttributeMetadataHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class ElementAttributeMetadataHandler
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class ElementAttributeMetadataHandler extends AttributeMetadataHandler {
    private String elemName;
    
    public ElementAttributeMetadataHandler(
            String uri, String localName, Metadata metadata,
            Property property, String elemName) {
        super(uri, localName, metadata, property);
        this.elemName = elemName;
    }
    
    @Override
    public void startElement(
            String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (localName.equals(this.elemName))
            super.startElement(uri, localName, qName, attributes);
    }

}
