#!/bin/bash
javac -cp ../tika.jar:../lucene-8.2.0/core/lucene-core-8.2.0.jar:../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar  Ejercicio4.java last4CharFilter.java
java -cp ../tika.jar:.:../lucene-8.2.0/core/lucene-core-8.2.0.jar:.:../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:.:../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar:. Ejercicio4 $1
