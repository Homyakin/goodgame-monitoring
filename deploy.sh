mvn package spring-boot:repackage &&
docker build -f Dockerfile -t goodgame-bot-img . &&
docker stop goodgame-bot || true &&
docker rm goodgame-bot || true &&
docker run --name gwent-bot -d goodgame-bot-img