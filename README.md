# cs293s-proj1

#To Index:
make Indexer a=NUM_FILES_TO_INDEX

#To Query:
make QueryString a=QUERY_STRING b=NUM_FILES_TO_USE_FROM_INDEXER

#Example

make
make Indexer a=100
make QueryString a=mexican b=100
