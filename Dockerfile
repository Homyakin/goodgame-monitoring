FROM bellsoft/liberica-openjdk-alpine-musl:16.0.2-7
COPY ./target/goodgame-monitoring-0.5.1.jar /home/goodgame-monitoring-0.5.1.jar
CMD ["java","-jar","/home/goodgame-monitoring-0.5.1.jar"]