JFLAGS = -g
JC = javac


all: PartOne.class

run:
	java PartOne

PartOne.class: PartOne.java
	javac -cp Resources/lucene-6.4.0/core/lucene-core-6.4.0.jar:Resources/lucene-6.4.0/queryparser/lucene-queryparser-6.4.0.jar:Resources/lucene-6.4.0/analysis/common/lucene-analyzers-common-6.4.0.jar PartOne.java

clean:
	rm -rf *.class