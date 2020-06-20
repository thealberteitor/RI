#!/bin/bash

if [ "$1" == "terminal" ]; then
	javac -cp ../../lucene-8.2.0/core/lucene-core-8.2.0.jar:../../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:../../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar:../../commons-csv-1.0.jar:../../commons-csv-1.0.jar:../../lucene-8.2.0/facet/lucene-facet-8.2.0.jar:../../hppc-0.8.1.jar Searcher.java
	java -cp ../../lucene-8.2.0/core/lucene-core-8.2.0.jar:.:../../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:.:../../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar:.:../../commons-csv-1.0.jar:.:../../lucene-8.2.0/facet/lucene-facet-8.2.0.jar:.:../../hppc-0.8.1.jar:. Searcher

elif [ "$1" == "interfaz" ]; then
	javac -cp ../../lucene-8.2.0/core/lucene-core-8.2.0.jar:../../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:../../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar:../../commons-csv-1.0.jar:../../commons-csv-1.0.jar:../../lucene-8.2.0/facet/lucene-facet-8.2.0.jar:../../hppc-0.8.1.jar GUI/Searcher_interfaz.java GUI/Buscatore.java
	java -cp ../../lucene-8.2.0/core/lucene-core-8.2.0.jar:.:../../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:.:../../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar:.:../../commons-csv-1.0.jar:.:../../lucene-8.2.0/facet/lucene-facet-8.2.0.jar:.:../../hppc-0.8.1.jar:.:./GUI/:. Searcher_interfaz

elif [ "$1" == "indice" ]; then
	javac -cp ../../lucene-8.2.0/core/lucene-core-8.2.0.jar:../../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:../../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar:../../commons-csv-1.0.jar:../../lucene-8.2.0/facet/lucene-facet-8.2.0.jar IndiceSimple.java
	java -cp ../../lucene-8.2.0/core/lucene-core-8.2.0.jar:.:../../lucene-8.2.0/analysis/common/lucene-analyzers-common-8.2.0.jar:.:../../lucene-8.2.0/queryparser/lucene-queryparser-8.2.0.jar:.:../../commons-csv-1.0.jar:.:../../lucene-8.2.0/facet/lucene-facet-8.2.0.jar:. IndiceSimple

else
	echo "Opción no válida ( ./IRsystem terminal , ./IRsystem interfaz ó ./IRsystem indice )"
fi
