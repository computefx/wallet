#!/bin/sh
exec java -noverify $JAVA_OPTS -XX:+AlwaysPreTouch -Duser.timezone=UTC -Djava.security.egd=file:/dev/./urandom -jar /app.jar ${@}
