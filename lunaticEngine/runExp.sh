#!/bin/bash
if [[ ("$#" -ne 1 && "$#" -ne 2) ]]; then
    echo "Usage: runExp.sh <path_task.xml> [-chaseonly]"
    exit
fi
TASK_FILE=$1
OPTION=$2
if  [[ $TASK_FILE != "/"* ]] ;
then #Expading relative path
    CURR_PATH=$(pwd) 
    TASK_FILE="$CURR_PATH/$TASK_FILE"
fi
echo "Executing task "$TASK_FILE 
BASEDIR=$(dirname $0)
BUILDSCRIPT=$BASEDIR/build.xml
ant -e -f $BUILDSCRIPT build -q
java -cp $BASEDIR:$BASEDIR/build/classes:$BASEDIR/../lib/speedy-0.1.jar:$BASEDIR/../lib/slf4j-api-1.7.7.jar:$BASEDIR/../lib/logback-core-1.1.2.jar:$BASEDIR/../lib/logback-classic-1.1.2.jar:$BASEDIR/../lib/postgresql-9.1-903.jdbc3.jar:$BASEDIR/../lib/jdom.jar:$BASEDIR/../lib/xerces-2.8.0.jar:$BASEDIR/../lib/antlr-runtime-4.5.3.jar:$BASEDIR/../lib/antlr-3.1.1-runtime.jar:$BASEDIR/../lib/mybatis-3.1.1.jar:$BASEDIR/../lib/jep-2.4.1.jar:$BASEDIR/../lib/js.jar:$BASEDIR/../lib/js-engine.jar:$BASEDIR/../lib/simmetrics-1.6.2.jar:$BASEDIR/../lib/jgrapht-core-0.9.1.jar:$BASEDIR/../lib/c3p0-0.9.5.2.jar:$BASEDIR/../lib/mchange-commons-java-0.2.11.jar:$BASEDIR/../lib/commons-io-2.4.jar:$BASEDIR/../lib/spicyBenchmark-0.1.jar:$BASEDIR/../lib/spicyEngine-1.0.jar:$BASEDIR/../lib/commons-jcs-core-2.0-beta-1.jar:$BASEDIR/../lib/concurrent.jar:$BASEDIR/../lib/commons-logging-1.1.jar:$BASEDIR/../lib/commons-lang-2.6.jar:$BASEDIR/../lib/jackson-dataformat-csv-2.7.4.jar:$BASEDIR/../lib/jackson-databind-2.7.4.jar:$BASEDIR/../lib/jackson-core-2.7.4.jar:$BASEDIR/../lib/jackson-annotations-2.7.4.jar it.unibas.lunatic.run.MainExp "$TASK_FILE" $OPTION