#!/bin/bash

#javac -cp ../luke.jar:../tika.jar Ejercicio1.java
#java -cp ../luke.jar:.:../tika.jar:. Ejercicio1 $1

javac -cp ../tika.jar:../lucene-8.2.0/core/lucene-core-8.2.0.jar:../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar Ejercicio1.java
java -cp ../tika.jar:.:../lucene-8.2.0/core/lucene-core-8.2.0.jar:.:../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:.:../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar:. Ejercicio1 $1

#javac -cp ../tika.jar:../../lucene-8.2.0/luke/lucene-luke-8.2.0.jar Ejercicio1.java
#java -cp ../tika.jar:.:../../lucene-8.2.0/luke/lucene-luke-8.2.0.jar:. Ejercicio1 $1
