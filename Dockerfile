FROM bellsoft/liberica-openjdk-alpine-musl:17-35
COPY ./target/goodgame-monitoring-0.5.1.jar /home/goodgame-monitoring-0.5.1.jar
CMD ["java","-jar","/home/goodgame-monitoring-0.5.1.jar"]