# Backend Application with Top Stack for Microservices Architecture  
## Microservice - Main  
[![ru](https://img.shields.io/badge/lang-ru-green.svg)](https://github.com/BornToNight/Digital-Photo-Frame/blob/main/README.md)

The goal of this project is to practice with the most popular stack for Java/Kotlin developers (IMHO).

### Microservices:
- proto-common - https://github.com/BornToNight/proto-common  
  - Basic gRPC classes (**main** and **reader**).
- **main** - https://github.com/BornToNight/main :5001  
  - The main service, front (postman) communicates **ONLY** with main. Includes **docker-compose** and **k8s** configurations.
- writer - https://github.com/BornToNight/writer :5002  
  - Microservice for Kafka practice. Receives messages from Kafka and writes them to PostgreSQL.
- reader - https://github.com/BornToNight/reader :5003  
  - Microservice for gRPC practice. **main** can send requests to **reader**.
- admin - https://github.com/BornToNight/admin :5004  
  - Spring Admin UI for service monitoring.

### Stack:
- Java 21
- Spring 3.3.1
- Hibernate (@Query, Specification, @EntityGraph)
- Scheduler (+ @SchedulerLock)
- Swagger (**main**)
- Tests (Testcontainers (integration), AutoConfigureGraphQlTester) (**main**)
- PostgreSQL
- Liquibase (**main**)
- Kafka (**main** -> **writer**)
- Redis (Cache) (**main**, Organization Entity)
- gRPC (**main** -> **reader**)
- GraphQL (**main**)
- ELK (Observe logs (**main** and **writer** write logs))
- Prometheus + Grafana (Monitoring microservices)
- Docker + compose - easy way to run
- Kubernetes - hard way to run (Multiple app instances, restart dead microservices, load balancer, easy configuration) (**main**)

### Stack TODO/Missed:
- Camunda (**TODO**)
- S3 (Paid, **maybe later**)
- SOAP (Deprecated, **skipped**)
- CI/CD (Leave it for DevOps)
- Sharding and replication for PostgreSQL (Leave it for DB admins)

## How to Run

### 1. Pull all microservices

### 2. In **proto-common**, run the following command in terminal/IDEA:

```
gradle publishToMavenLocal
```

### 3. In Run/Debug Configuration, add the *.env* file

### 4. Install and run Docker machine

### 5. **Docker Compose Way**
1. Run services in *docker-compose.yaml*
2. Start all microservices in IDEA or run the command:
```
gradle bootRun
```
3. Now everything works! (ALL LOGINS AND PASSWORDS CAN BE FOUND IN THE **.env** FILE IN THE **main** microservice). <br>
By default, login and password are created in the "users" table. (You can change them in the **insertAdmin.sql** file) <br>
login ```admin``` <br>
password ```adminPassword```

### 6. **Kubernetes Way (Minikube + Docker)**
1. Install minikube
2. Install kubectl (or use minikube kubectl)
3. Start minikube:
```
minikube start
```
4. Enable ingress:
```
minikube addons enable ingress
```
5. Enable dashboard:
```
minikube addons enable dashboard
```
6. Go to the **main** microservice directory and run the command:
```
kubectl create secret generic my-secret --from-env-file=k8s.env
```
7. Go to the project directories of **main, writer, reader, admin** services and run commands (build Jar and build Docker Image):
```
gradle bootJar
docker build -t username/serviceName:version .
```
  Example:
```
docker build -t borntonight/main:1 .
```
8. Push your microservice images to Docker Hub or load them into minikube from your local machine using:
```
minikube image load borntonight/main:1
```
9. Repeat steps 7 and 8 for Grafana (**main** microservice -> config/grafana)
10. Upload configurations to minikube using:
```
kubectl apply -f .\config\k8s
```
11. Add to the **hosts** file on your machine:
```
127.0.0.1 pet.grafana
127.0.0.1 pet.prometheus
127.0.0.1 pet.kibana
127.0.0.1 pet.admin
127.0.0.1 pet.main
```
12. Run the tunnel command:
```
minikube tunnel
```
13. Run the command to open the dashboard:
```
minikube dashboard
```
14. Now everything works! (ALL LOGINS AND PASSWORDS CAN BE FOUND IN THE **.env** FILE IN THE **main** microservice)

### 7. To call endpoints, you can use Postman
1. Send a POST request to http://localhost:5001/api/auth/generate / http://pet.main/api/auth/generate <br>
2. Receive the "token". You can change the expiration time on the website - https://jwt.io/ (default signature - "secret" (you can change it in the **.env** file)) or modify the expiration time in the **.env** file.

## 8. You can open:
1. Spring admin (compose - http://localhost:5004, k8s - http://pet.admin)
2. Grafana (compose - http://localhost:3000, k8s - http://pet.grafana)
3. Prometheus (compose - http://localhost:9090, k8s - http://pet.prometheus)
4. Kibana (compose - http://localhost:5601, k8s - http://pet.kibana)
5. Kubernetes Dashboard (only k8s, opens in the browser via the command)

## Implicit cases
1. If you are using PostgreSQL on your local machine, you need to create a user with the login "admindb"

## Contacts
Feel free to ask questions or suggest improvements :) <br>
Telegram - [@BTNtelegram](https://t.me/BTNtelegram)