#!/bin/bash

javac -cp ../../commons-csv-1.0.jar csvReader.java
java -cp ../../commons-csv-1.0.jar:. csvReader
