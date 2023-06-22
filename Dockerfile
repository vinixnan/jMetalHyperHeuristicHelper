FROM maven:3.8.3-openjdk-17 AS build

ARG GIT_HUB_KEY

RUN mkdir -p /workspace
WORKDIR /workspace
COPY . /workspace/
RUN mvn -B package --file pom.xml -s settings.xml -DGIT_HUB_KEY=$GIT_HUB_KEY

CMD ["bash"]