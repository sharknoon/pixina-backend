FROM scratch as build-stage
RUN ./gradlew installDist

FROM amazoncorretto:11 as production-stage
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/install/pixina-backend/ /app/
WORKDIR /app/bin
CMD ["./pixina-backend"]