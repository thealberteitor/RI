#!/bin/bash

javac -cp ../tika.jar:../lucene-8.2.0/core/lucene-core-8.2.0.jar:../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar MyAnalyzer.java

java -cp ../tika.jar:.:../lucene-8.2.0/core/lucene-core-8.2.0.jar:.:../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:.:../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar:. MyAnalyzer $1
