FROM balangandio/jdk8-zoned

VOLUME /tmp
COPY *.jar app.jar
#ENTRYPOINT ["java","-Dspring.profiles.active=devmem","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
ENTRYPOINT ["java", "-Xms128m", "-Xmx512m", "-Duser.timezone=America/Sao_Paulo", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
