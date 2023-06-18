FROM eclipse-temurin:17-jre
COPY target/*.jar /usr/app/app.jar

RUN useradd -m starters && \
    mkdir /data && \
    chown starters:starters -R /data

WORKDIR /data
USER starters

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar"]
