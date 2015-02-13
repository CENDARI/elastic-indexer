ElasticSearch Indexer for CENDARI
=================================

Provide a Facade oject `fr.inria.aviz.elasticindexer.Indexer` to perform several operations:
- use the results of (extended) Tika to parse files and return textual contents plus metadata. The result is converted into type DocumentInfo.
- index the data in DocumentInfo by sending to to elasticsearch
- search for documents in elasticsearch
- create the mapping (Schema) in elasticsearch if needed.

