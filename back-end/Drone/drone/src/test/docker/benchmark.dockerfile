FROM maven:3.8.3-openjdk-17

WORKDIR /home

COPY src/test/docker/wait-for-it.sh wait-for-it.sh

COPY src/test/docker/benchmark.sh benchmark.sh
RUN chmod +x benchmark.sh
RUN chmod +x wait-for-it.sh

COPY . .

ENTRYPOINT ["./benchmark.sh"]