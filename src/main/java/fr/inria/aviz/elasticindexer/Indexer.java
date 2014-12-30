package fr.inria.aviz.elasticindexer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        }
        return instance_;
    }

    private Indexer() {
        loadProps();
        //connectES();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.setSerializationInclusion(Include.NON_NULL);
        
    }

    private void connectES() {
        if (es != null) return;
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
        final Indexer that = this;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                logger.info("Closing Indexer at Shutdown");
                that.closeES();
            }
        }));
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

    /**
     * Close the ElasticSearch connection.
     */
    public void closeES() {
        if (es == null) return;
        node.close();
        es = null;
        _close();
    }

    void _close() {
        esChecked = false;
    }

    
    /**
     * Delete the index. Use with caution.
     */
    void _deleteIndex() {
        connectES();
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
        connectES();
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
        connectES();
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
     * Returns the JSON serialization of the specified DocumentInfo.
     * @param document the DocumentInfo
     * @return a JSON string
     * @throws JsonProcessingException if the serializer fails 
     */
    public String toJSON(DocumentInfo document) throws JsonProcessingException {
        return document.toJSON(mapper);
    }
    
    /**
     * Inserts the DocumentInfo
     * @param document the DocumentInfo
     * @return true if the entry has been created, false if it has been updated
     * @throws JsonProcessingException if the document has not been serialized correctly
     */
    public boolean indexDocument(DocumentInfo document) throws JsonProcessingException {
        return indexDocument(toJSON(document));
    }
    
    /**
     * Search for documents matching a specific query and filter.
     * @param query the query or null
     * @param filter the filter or null
     * @return a list of JSON records
     */
    public String[] searchDocument(QueryBuilder query, FilterBuilder filter) {
        checkESMapping();
        SearchRequestBuilder search = es.prepareSearch(getIndexName())
                .setTypes(getTypeName());
        if (query != null)
            search.setQuery(query);
        if (filter != null)
            search.setPostFilter(filter);
        SearchResponse res = search.execute().actionGet();
        String[] ret = new String[(int)res.getHits().getTotalHits()];
        int i = 0;
        for (SearchHit hit : res.getHits()) {
            ret[i++] = hit.getSourceAsString();
        }
        return ret;
    }
    
    /**
     * Parse a specified document using Tika and returns the related DocumentInfo.  
     * @param name document name
     * @param contentType document type or null if unknown
     * @param content document content in memory as a byte array
     * @param maxLength maximum length to parse or -1 to parse all
     * @return a DocumentInfo structure filled with the right contents of null if tika has not been able to parse it.
     */
    public DocumentInfo parseDocument(String name, String contentType, byte[] content, int maxLength) {
        Metadata metadata = new Metadata();
        if (name != null) {
            metadata.add(Metadata.RESOURCE_NAME_KEY, name);
        }
        if (contentType != null) {
            metadata.add(Metadata.CONTENT_TYPE, contentType);
        }

        String parsedContent;
        try {
            parsedContent = tika.parseToString(new BytesStreamInput(content, false), metadata, maxLength);
        } catch (IOException | TikaException e) {
            logger.error("Tika parse exception for document "+name, e);
            return null;
        }
        DocumentInfo info = new DocumentInfo();
        info.setText(parsedContent);
        info.setUri(name);
        info.setFormat(metadata.get(Metadata.CONTENT_TYPE));
        if (metadata.get(TikaCoreProperties.CREATED) != null)
            info.setDate(metadata.get(TikaCoreProperties.CREATED));
        if (metadata.get(TikaCoreProperties.TITLE) != null)
            info.setTitle(metadata.get(TikaCoreProperties.TITLE));
        if (metadata.get(TikaCoreProperties.CREATOR) != null)
            info.setCreatorName(metadata.get(TikaCoreProperties.CREATOR));
        if (metadata.get(TikaCoreProperties.CREATOR_TOOL) != null)
            info.setApplication(metadata.get(TikaCoreProperties.CREATOR_TOOL));
        if (metadata.get(TikaCoreProperties.KEYWORDS) != null)
            info.setTag(metadata.get(TikaCoreProperties.KEYWORDS));
        if (metadata.get(TikaCoreProperties.LANGUAGE) != null)
            info.setLanguage(metadata.get(TikaCoreProperties.LANGUAGE));
        if (metadata.get(TikaCoreProperties.LATITUDE)!= null && metadata.get(TikaCoreProperties.LONGITUDE) != null) {
            String latlon = 
                    metadata.get(TikaCoreProperties.LATITUDE) +
                    ", "+
                    metadata.get(TikaCoreProperties.LONGITUDE);
            info.setPlace(new Place(null, latlon));
        }
        if (metadata.get(TikaCoreProperties.DESCRIPTION) != null)
            info.put("description", metadata.get(TikaCoreProperties.DESCRIPTION));
        return info;
    }

};

