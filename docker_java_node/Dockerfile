FROM eclipse-temurin:17-jdk

ARG uid
ARG gid

# These files can become huge, gigabytes
# See: https://github.com/sagemathinc/cocalc/issues/2287
RUN rm /var/log/faillog /var/log/lastlog

RUN apt-get update && apt-get install -y nodejs npm yarnpkg
RUN apt-get update && apt-get install -y vim-tiny

RUN apt-get autoclean -y && rm -rf /var/lib/apt/lists/* && rm -rf /tmp/pear/

RUN groupadd -g $gid mygroup
RUN useradd -G root -u $uid -g $gid -d /home/user -m -s /bin/bash myuser
RUN mkdir -p /home/user/work && chown -R myuser:mygroup /home/user/work

USER myuser:mygroup
WORKDIR /home/user/work

HEALTHCHECK CMD curl --fail http://localhost:8000 || exit 1
STOPSIGNAL SIGINT
CMD ["./mvnw", "spring-boot:run"]
