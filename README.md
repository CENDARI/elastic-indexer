ElasticSearch Indexer for CENDARI
=================================

Provide a Facade oject fr.inria.aviz.elasticindexer.Indexer to perform several operations:
- use an extended version of Tika to parse files and return textual contents plus metadata. The result has type DocumentInfo.
- index the data in DocumentInfo by sending to to elasticsearch
- search for documents in elasticsearch
- create the mapping (Schema) in elasticsearch if needed.

To create a specific parser for a new XML schema, do the following steps:
1) Create a Parser class, just like fr.inria.aviz.elasticindexer.tika.xml.TEIParser
2) Declare the new class in (at the end of) src/main/resources/org/apache/tika/META-INF/services/org.apache.tika.parser.Parser
3) Add a test case in src/test/java/fr/inria/aviz/elasticindex/TextTika 

