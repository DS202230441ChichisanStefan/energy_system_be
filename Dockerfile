FROM openjdk:17-alpine
ADD target/DS2022_30441_Chichisan_Stefan_1_Backend.jar DS2022_30441_Chichisan_Stefan_1_Backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/DS2022_30441_Chichisan_Stefan_1_Backend.jar"]