package fr.inria.aviz.elasticindexer;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.tika.Tika;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * Singleton class, indexes documents.
 */
class Indexer {
    private static Indexer instance_;
    private final Tika tika = new Tika();
    private final Properties props = new Properties();
    private Client es;
    private Node node;

    private Indexer() {
        try {
            FileInputStream in = new FileInputStream("elasticindexer.properties");
            props.load(in);
            in.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
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
    
    private void close() {
        node.close();
    }
    
    public static Indexer instance() {
        if (instance_ == null) {
            instance_ = new Indexer();
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    instance_.close();
                    instance_ = null;
                }
            }));
        }
        return instance_;
    }
};

