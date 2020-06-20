#!/bin/bash

javac -cp ../../lucene-8.2.0/core/lucene-core-8.2.0.jar:../../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:../../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar:../../commons-csv-1.0.jar:../../commons-csv-1.0.jar:../../lucene-8.2.0/facet/lucene-facet-8.2.0.jar:../../hppc-0.8.1.jar Searcher.java


java -cp ../../lucene-8.2.0/core/lucene-core-8.2.0.jar:.:../../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:.:../../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar:.:../../commons-csv-1.0.jar:.:../../lucene-8.2.0/facet/lucene-facet-8.2.0.jar:.:../../hppc-0.8.1.jar:. Searcher
