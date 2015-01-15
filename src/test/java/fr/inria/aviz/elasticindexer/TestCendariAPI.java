package fr.inria.aviz.elasticindexer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.inria.aviz.elasticindexer.ckan.CendariIndexer;
import fr.inria.aviz.elasticindexer.ckan.Resource;
import fr.inria.aviz.elasticindexer.ckan.ResourceList;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Class TestCendariAPI
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class TestCendariAPI extends TestCase {
    /**
     * Big test, only started when a key is specified using -Dkey='...'
     */
    @Test
    public void test() {
        Indexer indexer = Indexer.instance();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        String location = "http://localhost:42042/";
        String key = System.getProperty("key");
        if (key == null) return;
        CendariIndexer cendari = new CendariIndexer(location, key);
        List<Object> packages = cendari.getDataspaces();
        for (Object o: packages) {
            Map<String,Object> p = (Map<String,Object>)o;
            System.out.println("Package is:");
            try {
                System.out.println(mapper.writeValueAsString(p));
            } catch (JsonGenerationException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (JsonMappingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            ResourceList res = cendari.getResourceList((String)p.get("resources"));
            for (Resource r : res)
                try {
                    System.out.println(mapper.writeValueAsString(r));
                    byte[] content = cendari.getData((String)r.get("dataUrl"));
                    if (content == null) {
                        System.out.println("Cannot get content of dataUrl"+(String)r.get("dataUrl"));
                        continue;
                    }
                    DocumentInfo info = indexer.parseDocument((String)r.get("name"), null, content, -1);
                    System.out.println(mapper.writeValueAsString(info));
                    info.setGroups_allowed((String)p.get("name"));
                    indexer.indexDocument(info);
                    
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }

}
