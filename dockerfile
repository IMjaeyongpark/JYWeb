FROM bellsoft/liberica-openjdk-alpine:17

# 작업 디렉토리 설정
WORKDIR /app

# JAR 복사 (Gradle 빌드 산출물)
COPY build/libs/*.jar app.jar

# 외부 포트 노출
EXPOSE 8080

# JAR 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
