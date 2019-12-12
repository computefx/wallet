FROM adoptopenjdk/openjdk11:alpine-jre

VOLUME /tmp

COPY run.sh .
RUN chmod +x /run.sh

COPY service/build/libs/*.jar app.jar

RUN addgroup -S app && adduser -S app -G app

RUN chown app:app /run.sh && chmod 744 /run.sh

USER app

ENTRYPOINT ["/run.sh"]
