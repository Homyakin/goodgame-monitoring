FROM bellsoft/liberica-openjdk-alpine-musl:21
RUN apk update && apk add tzdata
ENV TZ=Europe/Moscow
COPY ./target/goodgame-monitoring-0.6.0.jar /home/goodgame-monitoring-0.6.0.jar
CMD ["java","-jar","/home/goodgame-monitoring-0.6.0.jar"]