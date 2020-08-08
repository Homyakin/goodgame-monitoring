FROM bellsoft/liberica-openjdk-centos:14.0.2-13
COPY ./target/goodgame-monitoring-0.5.jar /home/goodgame-monitoring-0.5.jar
CMD ["java","-jar","/home/goodgame-monitoring-0.5.jar"]