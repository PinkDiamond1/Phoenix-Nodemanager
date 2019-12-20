# APEX-Nodemanager
This application provides a graphical interface to manage Supernodes
# Maven & Java
##### Run
From the project directory execute:
##### mvn spring-boot:run
The application enforces HTTPS and runs on https://localhost:9100
# Docker
##### Build & Run
docker image build -t nodemanager:1.0 . && docker container run -it --publish 9090:9090 --publish 9100:9100 --detach --name nodemanager nodemanager:1.0
##### Bash into Image
docker exec -it nodemanager /bin/bash
##### Container logs
docker logs -f nodemanager
##### Stop
docker stop nodemanager
##### Start
docker start nodemanager
