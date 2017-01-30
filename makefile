JFLAGS = -g
JC = javac


all: Indexer.class

run:
	java Indexer

Indexer.class: Indexer.java
	javac -cp Resources/lucene-6.4.0/core/lucene-core-6.4.0.jar:Resources/lucene-6.4.0/queryparser/lucene-queryparser-6.4.0.jar:Resources/lucene-6.4.0/analysis/common/lucene-analyzers-common-6.4.0.jar Indexer.java

clean:
	rm -rf *.class