package fr.inria.aviz.elasticindexer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Singleton class, indexes documents.
 */
class Indexer {
    private static Logger logger = Logger.getLogger(Indexer.class.getName());
    private static Indexer instance_;
    private final Tika tika = new Tika();
    private final Properties props = new Properties();
    private Client es;
    private Node node;
    private boolean esChecked;
    private ObjectMapper mapper = new ObjectMapper();
    
    public static final String ES_INDEX = "elasticindexer.elasticsearch.index";
    public static final String ES_TYPE = "elasticindexer.elasticsearch.type";
    public static final String ES_HOST1 = "elasticindexer.elasticsearch.host1";
    public static final String ES_PORT1 = "elasticindexer.elasticsearch.port1";
    public static final String ES_HOST2 = "elasticindexer.elasticsearch.host2";
    public static final String ES_PORT2 = "elasticindexer.elasticsearch.port2";
    

    /**
     * @return the Indexer singleton instance, creating it if necessary. 
     */
    public static Indexer instance() {
        if (instance_ == null) {
            instance_ = new Indexer();
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    logger.info("Closing Indexer at Shutdown");
                    instance_.close();
                    instance_ = null;
                }
            }));
        }
        return instance_;
    }

    private Indexer() {
        loadProps();
        connectES();
    }

    private void connectES() {
        //        TransportClient tes = new TransportClient();
        //        for (int i = 1; i < 100; i++) {
        //            String prop = "host"+i, 
        //                    host = props.getProperty(prop);
        //            if (host == null) break;
        //            tes.addTransportAddress(new InetSocketTransportAddress(host, 9300));
        //        }
        //        es = tes;
        node = NodeBuilder.nodeBuilder().node();
        es = node.client();
    }

    private void loadProps() {
        try {
            InputStream in = getClass().getResourceAsStream("/elasticindexer.properties");
            props.load(in);
            in.close();
        }
        catch(Exception e) {
            logger.error("Elasticindexer properties not found", e);
        }
    }
    
    private void close() {
        node.close();
        _close();
    }
    
    public void _close() {
        esChecked = false;
    }

    
    /**
     * Delete the index. Use with caution.
     */
    public void _deleteIndex() {
        esChecked = false;
        DeleteIndexResponse rep = null;
        try {
            rep = es.admin().
                         indices().
                         prepareDelete(getIndexName()).
                         execute().
                         actionGet();
        }
        catch (IndexMissingException e) {
            return;
        }
         
        if (! rep.isAcknowledged()) {
            logger.error("Index "+getIndexName()+" has not been deleted");
        }
    }
    
    public void _deleteMapping() {
        esChecked = false;
        DeleteMappingResponse rep = null;
        rep = es.admin().indices().
                prepareDeleteMapping(getIndexName()).
                setType(getTypeName()).
                execute().
                actionGet();
         
        if (! rep.isAcknowledged()) {
            logger.error("Mapping "+getTypeName()+" has not been deleted");
        }
    }

    /**
     * Test the ElasticSearch mapping.
     */
    public void checkESMapping() {
        if (esChecked)
            return;
        esChecked = true;
        String mapping = getMapping();
        IndexMetaData imd = null;
        
        if (es.admin()
                .indices()
                .prepareExists(getIndexName())
                .execute()
                .actionGet()
                .isExists()) {
            try {
                ClusterState cs = es.admin().
                                 cluster().
                                 prepareState().
                                 setIndices(getIndexName()).
                                 execute().
                                 actionGet().
                                 getState();
            
                imd = cs.getMetaData().index(getIndexName());
            }
            catch (IndexMissingException e) {
                logger.warn("Cluster state report no index for "+getIndexName()+" when indices was found, recreating...", e);
                createIndex(mapping);
                return;
            }
        }
        else {
            createIndex(mapping);
            return;
        }
        
        if (imd == null || imd.mapping(getTypeName()) == null) {
            PutMappingResponse response=es.admin().
                    indices().
                    preparePutMapping(getIndexName()).
                    setType(getTypeName()).
                    setSource(mapping).
                    execute().
                    actionGet();

            if (! response.isAcknowledged()) {
                logger.error("Cannot create mapping in "+getIndexName());
            }
            return;
        }
        MappingMetaData mdd = imd.mapping(getTypeName());
        
//        if (json2json(mdd.source().toString()) != json2json(mapping)) {
//            logger.error("Mappings differ, should update");
//        }
        try {
            JsonNode mappingRoot = mapper.readTree(mapping);
            JsonNode mddRoot = mapper.readTree(mdd.source().toString());
            if (! mappingRoot.equals(mddRoot)) {
                logger.error("Mappings differ, should update");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createIndex(String mapping) throws ElasticsearchException {
        logger.info("Creating index "+getIndexName());
        final CreateIndexRequestBuilder createIndexRequestBuilder = es.admin().indices().prepareCreate(getIndexName());
        createIndexRequestBuilder.addMapping(getTypeName(), mapping);
        if (! createIndexRequestBuilder
                .execute()
                .actionGet()
                .isAcknowledged()) {
            logger.error("Index/Mapping not created for index "+getIndexName());
        }
    }
    
//    static public String json2json(String json) {
//        try {
//            XContentType xContentType = XContentType.JSON;
//            XContentParser parser = xContentType.xContent().createParser(json);
//            XContentBuilder builder = XContentFactory.jsonBuilder();
//            builder.copyCurrentStructure(parser);
//            return builder.string();
//        }
//        finally {
//            return json;
//        }
//    }

    private String getMapping() {
        String mapping = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            byte encoded[] = Files.readAllBytes(Paths.get(classLoader.getResource("cendari_document_mapping.json").getPath()));
            mapping = new String(encoded, StandardCharsets.UTF_8);
        }
        catch(Exception e) {
            logger.error("Cannot get cendari mapping file", e);
        }

        return mapping;
    }
    
    public String getIndexName() { return props.getProperty(ES_INDEX, "cendari"); }
    public String getTypeName() { return props.getProperty(ES_TYPE, "document"); }
    
};

