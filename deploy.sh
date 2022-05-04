mvn package spring-boot:repackage && \
sudo docker build -f Dockerfile -t goodgame-bot-img .
sudo docker stop goodgame-bot
sudo docker rm goodgame-bot
sudo docker run --name goodgame-bot -d goodgame-bot-img