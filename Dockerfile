FROM armv7/armhf-java8
COPY build/libs/uModeler-0.0.1-SNAPSHOT.jar /uModeler.jar
CMD ["/usr/bin/java", "-jar", "/uModeler.jar"]