# Backend Приложение с топ стэком для микросервесной архитектуры
## Микросервис - Main
[![en](https://img.shields.io/badge/lang-en-green.svg)](https://github.com/BornToNight/main/blob/main/README.md)

Цель проекта - потренироваться с самым популярным стэком для Java/Kotlin разработчика (ИМХО).

### Microservices:
- proto-common - https://github.com/BornToNight/proto-common
  - Базовые классы gRPC (**main** и **reader**).
- **main** - https://github.com/BornToNight/main :5001
  - Основной сервис, front (postman) общается **ТОЛЬКО** с main. Включает в себя **docker-compose** и **k8s** конфиги.
- writer - https://github.com/BornToNight/writer :5002
  - Микросервис для практики по работе с **Kafka**. Получает сообщения из Kafka и записывает в PostgreSQL.
- reader - https://github.com/BornToNight/reader :5003
  - Микросервис для практики по работе с **gRPC**. **main** может отправить запрос в **reader**.
- admin - https://github.com/BornToNight/admin :5004
  - Spring Admin UI для мониторинка сервисов.  

### Стэк:
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
- Prometeus + Grafana (Monitoring microservices)
- Docker + compose - лёгкий путь запуска
- Kubernetes - сложный путь запуска (Несколько инстансов приложения, рестарт умерших микросервисов, балансировщик, лёгкая настройка) (**main**)

### Стэк TODO/Пропущено:
- Camunda (**TODO**)
- S3 (Платно, **возможно потом**)
- SOAP (Устарело, **пропуск**)
- CI/CD (Оставим это для DevOps)
- Шардирование и репликация PostgreSQL (Отсавим это базистам)

## Как запустить

### 1. Спуллить все микросервисы

### 2. В **proto-common** выполнить команду в terminal/IDEA

```
gradle publishToMavenLocal
```

### 3. В Run/Debug Configuration добавить *.env* файл

### 4. Установить и запустить Docker machine

### 5. **Docker compose путь**
1. Запустить services в *docker-compose.yaml*
2. Запустить все микросервисы в IDEA или выполнить команду
```
gradle bootRun
```
3. Теперь всё работает! (ВСЕ ЛОГИНЫ И ПАРОЛИ ТЫ НАЙДЁШЬ В **.env** файле в **main** микросервисе). <br>
По умолчанию логин и пароль создаются в таблице "users". (Ты можешь поменять в файле - **insertAdmin.sql**) <br>
login ```admin``` <br>
password ```adminPassword```

### 6. **Kubernetes путь (Minikube + Docker)**
1. Установить minikube
2. Установить kubectl (или используй minikube kubectl)
3. Запустить minikube
```
minikube start
```
4. Включить ingress
```
minikube addons enable ingress
```
5. Включить dashboard
```
minikube addons enable dashboard
```
6. Перейти в директорию проекта в **main** микросервисе и выполнить команду
```
kubectl create secret generic my-secret --from-env-file=k8s.env
```
7. Перейти в директорию проекта в **main, writer, reader, admin** сервисах и выполнить команды (build Jar и buil Docker Image)
```
gradle bootJar
docker build -t username/serviceName:version .
```
  Пример
```
docker build -t borntonight/main:1 .
```
8. Push свои images микросервисов на Docker Hub или можно загрузить их в minikube с локального ПК с помощью команды
```
minikube image load borntonight/main:1
```
9. Повторить 7 и 8 шаг для Grafana (**main** микросервис -> config/grafana)
10. Загрузить конфиги в minikube с помощью команды
```
kubectl apply -f .\config\k8s
```
11. Вставить в **hosts** файл на своём ПК
```
127.0.0.1 pet.grafana
127.0.0.1 pet.prometheus
127.0.0.1 pet.kibana
127.0.0.1 pet.admin
127.0.0.1 pet.main
```
12. Выполнить команду для туннеля
```
minikube tunnel
```
13. Выполнить команду для открытия dashboard
```
minikube dashboard
```
14. Теперь всё работает! (ВСЕ ЛОГИНЫ И ПАРОЛИ ТЫ НАЙДЁШЬ В **.env** файле в **main** микросервисе)

### 7. Для вызова эндпоинтов можно использовать Postman
1. Отправить POST запрос на http://localhost:5001/api/auth/generate / http://pet.main/api/auth/generate <br>
2. Получить "token". Ты можешь изменить время протухания на сайте - https://jwt.io/ (подпись по умолчанию - "secret" (ты можешь изменить в **.evn** файле)) или изменить время протухания в .evn файле.

## 8. Ты можешь открыть
1. Spring admin (compose - http://localhost:5004, k8s - http://pet.admin)
2. Grafana (compose - http://localhost:3000, k8s - http://pet.grafana)
3. Prometheus (compose - http://localhost:9090, k8s - http://pet.prometheus)
4. Kibana (compose - http://localhost:5601, k8s - http://pet.kibana)
5. Kubernetes Dashboard (только k8s, открывается в браузере с помощью команды)

## Неявные случаи
1. Если ты используешь PostgreSQL на локальной машине - тебе нужно создать пользователя с логином "admindb"

## Контакты
Ты можешь задать вопросы или написать предложения по улучшению :) <br>
Telegram - [@BTNtelegram](https://t.me/BTNtelegram)