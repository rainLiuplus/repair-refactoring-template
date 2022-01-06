#!/bin/bash
mvn install:install-file -Dfile=./its-repair-refactoring/lib/its-interpreter-c.jar -DgroupId=sg.edu.nus.se.its -DartifactId=its-interpreter-c -Dversion=0.0.1 -Dpackaging=jar
mvn install:install-file -Dfile=./its-repair-refactoring/lib/its-errorlocalizer-cfg.jar -DgroupId=sg.edu.nus.se.its -DartifactId=its-errorlocalizer-cfg -Dversion=0.0.1 -Dpackaging=jar
mvn install:install-file -Dfile=./its-repair-refactoring/lib/its-alignment-cfg.jar -DgroupId=sg.edu.nus.se.its -DartifactId=its-alignment-cfg -Dversion=0.0.1 -Dpackaging=jar
