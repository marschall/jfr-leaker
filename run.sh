#!/bin/bash


JAR_FILE=$(dirname $0)/target/jfr-leaker-0.1.0-SNAPSHOT.jar

# mvn package
${JAVA_HOME}/bin/java -Xmx32m -Xms32m -XX:MetaspaceSize=32m -XX:MaxMetaspaceSize=32m -XX:+HeapDumpOnOutOfMemoryError -XX:+UseSerialGC -jar $JAR_FILE

