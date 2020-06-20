#!/bin/bash

javac -cp ../tika.jar Ejercicio3.java NameAnalyzer.java

java -cp ../tika.jar:. Ejercicio3 $1
