package fr.inria.aviz.elasticindexer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;

/**
 * Singleton class, indexes documents.
 * 
 * Indexer is a Facade to elasticsearch for indexing documents.
 */
public class Indexer {
    private static Logger logger = Logger.getLogger(Indexer.class.getName());
    private static Indexer instance_;
    private final Tika tika = new Tika();
    private final Properties props = new Properties();
    private Client es;
    private Node node;
    private boolean esChecked;
    private ObjectMapper mapper = new ObjectMapper();
    
    /** Resource name for the main index */
    public static final String ES_INDEX = "elasticindexer.elasticsearch.index";
    /** Resource name for the document type */
    public static final String ES_TYPE = "elasticindexer.elasticsearch.type";
    /** Resource name for the elasticsearch host name, defaults to localhost */
    public static final String ES_HOST1 = "elasticindexer.elasticsearch.host1";
    /** Resource name for the elasticsearch port number, defaults to 9300 */
    public static final String ES_PORT1 = "elasticindexer.elasticsearch.port1";
    /** Resource name for the second elasticsearch host name, defaults to none */
    public static final String ES_HOST2 = "elasticindexer.elasticsearch.host2";
    /** Resource name for the second elasticsearch port number, defaults to 9300 */
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
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
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

    void _close() {
        esChecked = false;
    }

    
    /**
     * Delete the index. Use with caution.
     */
    void _deleteIndex() {
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
    
    void _deleteMapping() {
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
        
        try {
            JsonNode mappingRoot = mapper.readTree(mapping);
            JsonNode mddRoot = mapper.readTree(mdd.source().toString());
            if (! mappingRoot.equals(mddRoot)) {
                logger.error("Mappings differ, should update");
            }
        } catch (IOException e) {
            logger.error("Cannot create json from string", e);
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

    private String getMapping() {
        String mapping = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            mapping = IOUtils.toString(
                    classLoader.getResource("cendari_document_mapping.json"),
                    StandardCharsets.UTF_8);
        }
        catch(Exception e) {
            logger.error("Cannot get cendari mapping file", e);
        }

        return mapping;
    }

    /**
     * @return the index name
     */
    public String getIndexName() { return props.getProperty(ES_INDEX, "cendari"); }
    
    /**
     * @return the document type name
     */
    public String getTypeName() { return props.getProperty(ES_TYPE, "document"); }

    /**
     * Inserts the specific JSON representation of a DocumentInfo
     * @param document the json string
     * @return true if the entry has been created, false if it has been updated
     */
    public boolean indexDocument(String document) {
        checkESMapping();
        IndexResponse res = es.prepareIndex(getIndexName(), getTypeName())
                .setSource(document)
                .execute()
                .actionGet();
        return res.isCreated();
    }
    
    /**
     * Inserts the DocumentInfo
     * @param document the DocumentInfo
     * @return true if the entry has been created, false if it has been updated
     * @throws JsonProcessingException if the document has not been serialized correctly
     */
    public boolean indexDocument(DocumentInfo document) throws JsonProcessingException {
        return indexDocument(document.toJSON(mapper));
    }

};

