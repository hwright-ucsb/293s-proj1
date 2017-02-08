#!/bin/bash


#javac -cp .:./src:./Resources/lucene-6.4.0/core/lucene-core-6.4.0.jar:./Resources/lucene-6.4.0/queryparser/lucene-queryparser-6.4.0.jar:./Resources/lucene-6.4.0/analysis/common/lucene-analyzers-common-6.4.0.jar src/BatchSearch.java

#java -cp .:./src:./Resources/lucene-6.4.0/core/lucene-core-6.4.0.jar:./Resources/lucene-6.4.0/queryparser/lucene-queryparser-6.4.0.jar:./Resources/lucene-6.4.0/analysis/common/lucene-analyzers-common-6.4.0.jar BatchSearch -index index -queries test-data/title-queries.301-450 -simfn bm25 > bm25.out

#java -cp .:./src:./Resources/lucene-6.4.0/core/lucene-core-6.4.0.jar:./Resources/lucene-6.4.0/queryparser/lucene-queryparser-6.4.0.jar:./Resources/lucene-6.4.0/analysis/common/lucene-analyzers-common-6.4.0.jar BatchSearch -index index -queries test-data/title-queries.301-450 -simfn default > default.out

make QueryString a="default" > default.out
make QueryString a="bm25" > bm25.out


trec_eval.9.0/trec_eval -m all_trec -M1000 test-data/qrels.trec6-8.nocr bm25.out > bm25.eval

trec_eval.9.0/trec_eval -m all_trec -M1000 test-data/qrels.trec6-8.nocr default.out > default.eval