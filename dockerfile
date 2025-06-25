FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

# JAR 파일 복사
COPY build/libs/*.jar JYWeb.jar

# 외부로 노출할 포트
EXPOSE 8080

