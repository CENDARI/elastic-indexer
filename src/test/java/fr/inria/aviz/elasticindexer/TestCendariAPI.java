package fr.inria.aviz.elasticindexer;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.inria.aviz.elasticindexer.ckan.CendariIndexer;
import fr.inria.aviz.elasticindexer.ckan.Resource;
import fr.inria.aviz.elasticindexer.ckan.ResourceList;
import fr.inria.aviz.tikaextensions.TikaExtensions;

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
        TikaExtensions tika = TikaExtensions.instance();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        String location = "http://localhost:42042/";
        String key = System.getProperty("key");
        if (key == null) return;
        CendariIndexer cendari = new CendariIndexer(location, key);
        List<Object> packages = cendari.getDataspaces();
        System.out.println("Read "+packages.size()+" dataspaces");
        for (Object o: packages) {
            Map<String,Object> p = (Map<String,Object>)o;
            System.out.println("Package is:");
            try {
                System.out.println(mapper.writeValueAsString(p));
            } catch (Exception e) {
                ; // ignore for now
            }

            ResourceList res = cendari.getResourceList((String)p.get("resources"));
            System.out.println("Read "+res.size()+" resources");
            byte[] content = null;
            for (Resource r : res)
                try {
                    System.out.println(mapper.writeValueAsString(r));
                    content = cendari.getData((String)r.get("dataUrl"));
                    if (content == null) {
                        System.out.println("Cannot get content of dataUrl "+(String)r.get("dataUrl"));
                        continue;
                    }
                    DocumentInfo info = indexer.convertMetadata(tika.parseDocument((String)r.get("name"), null, content, -1));
                    System.out.println(mapper.writeValueAsString(info));
                    info.setGroups_allowed((String)p.get("name"));
                    indexer.indexDocument(info);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    if (content != null) try {
                        FileOutputStream out = new FileOutputStream((String)r.get("name"));
                        out.write(content);
                        out.close();
                    }
                    catch(Exception e2) {
                        e2.printStackTrace();
                    }
                }
        }
    }

}
