# Getting Started

### Start service

cd wallet

./gradlew build

docker build -t wallet .

docker-compose -f service/src/main/docker/app.yml up


### Start client

* -c=<requestPerUser>    Requests per user
* -r=<rounds>            Rounds
* -u=<users>             Users

java -jar client/libs/client-0.0.1-SNAPSHOT.jar -c 10 -u 10 -r 20

Time taken : 4068 milliseconds.

Total (success&error) request count 1349

RPS = 1349 / 4068 * 1000 => 331 (GRPC blocking mode)