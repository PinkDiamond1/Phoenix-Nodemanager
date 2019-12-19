FROM adoptopenjdk/openjdk12:jdk-12.0.1_12-slim

RUN     apt-get update -y && \
        apt-get install unzip -y && \
        apt-get install git -y && \
        apt-get install maven -y && \
        apt-get install wget -y

RUN     cd /usr/local && \
        curl -L https://services.gradle.org/distributions/gradle-4.10.3-bin.zip -o gradle-4.10.3-bin.zip && \
        unzip gradle-4.10.3-bin.zip && \
        rm gradle-4.10.3-bin.zip

ENV     GRADLE_HOME=/usr/local/gradle-4.10.3
ENV     PATH=$PATH:$GRADLE_HOME/bin

WORKDIR "~/"

RUN     git clone https://github.com/yuomii/APEX-Nodemanager.git && \
        git clone https://github.com/APEX-Network/APEX-Blockchain-Core.git

WORKDIR "~/APEX-Nodemanager"
CMD ["mvn", "spring-boot:run"]