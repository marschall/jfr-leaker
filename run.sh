#!/bin/bash


JAR_FILE=$(dirname $0)/target/jfr-leaker-0.1.0-SNAPSHOT.jar

# mvn package
${JAVA_HOME}/bin/java -Xmx64m -Xms64m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=64m -XX:+HeapDumpOnOutOfMemoryError -XX:+UseSerialGC -jar $JAR_FILE

