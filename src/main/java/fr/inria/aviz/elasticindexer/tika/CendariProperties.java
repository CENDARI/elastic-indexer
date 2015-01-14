package fr.inria.aviz.elasticindexer.tika;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;

/**
 * Class Cendari
 * 
 * A collection of Metadata properties recognized by Cendari indexer
 * 
 * @author Jean-Daniel Fekete
 */
public class CendariProperties {
    //private static final String NAMESPACE_URI_XML = "http://www.w3.org/XML/1998/namespace";
    private static final String PREFIX_XML = "xml";
    
    /**
     * A language of the intellectual content of the resource. Recommended
     * best practice is to use RFC 3066 [RFC3066], which, in conjunction
     * with ISO 639 [ISO639], defines two- and three-letter primary language
     * tags with optional subtags. Examples include "en" or "eng" for English,
     * "akk" for Akkadian, and "en-GB" for English used in the United Kingdom.
     */
    public static final Property LANG = Property.internalTextBag(
            PREFIX_XML + Metadata.NAMESPACE_PREFIX_DELIMITER + "lang");
    
}
