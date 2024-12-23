val springBootStarterVer: String by project // 3.3.1-SNAPSHOT
val springSecurityVer: String by project // 6.2.3
val springAdminVer: String by project // 3.2.3
val postgreSQLVer: String by project // 42.7.3
val liquibaseVer: String by project // 4.26.0
val openApiVer: String by project // 2.4.0
val javaJwtVer: String by project // 4.4.0
val micrometerPrometheusVer: String by project // 1.12.4
val lombokVer: String by project // 1.18.32
val kafkaVer: String by project // 3.2.0
val junitVer: String by project // 1.11.0-M2
val protoCommonVer: String by project // 0.0.1
val grpcVer: String by project // 3.1.0.RELEASE
val jpamodelgenVer: String by project // 6.4.4.Final
val graphQlTestVer: String by project // 1.3.2
val testcontainersJunitVer: String by project // 1.20.0
val logstashEncoderVer: String by project // 8.0
val shedlockVer: String by project // 5.15.1
val mapStructVer: String by project // 1.6.3

plugins {
    java
    id("org.springframework.boot") version "3.3.1-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "ru.pachan"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootStarterVer")
    implementation("org.springframework.boot:spring-boot-starter-security:$springBootStarterVer")
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootStarterVer")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootStarterVer")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:$springBootStarterVer")
    implementation("org.springframework.boot:spring-boot-starter-graphql:$springBootStarterVer")
    implementation("de.codecentric:spring-boot-admin-starter-client:$springAdminVer")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiVer")
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerPrometheusVer")
    implementation("org.liquibase:liquibase-core:$liquibaseVer")
    implementation("com.auth0:java-jwt:$javaJwtVer")
    implementation("org.springframework.kafka:spring-kafka:$kafkaVer")
    implementation("ru.pachan:proto-common:$protoCommonVer")
    implementation("net.devh:grpc-client-spring-boot-starter:$grpcVer")
//	EXPLAIN_V Для интеграционных тестов
    implementation("org.springframework.boot:spring-boot-starter-webflux:$springBootStarterVer")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashEncoderVer")
    implementation("net.javacrumbs.shedlock:shedlock-spring:$shedlockVer")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:$shedlockVer")
    implementation("org.mapstruct:mapstruct:$mapStructVer")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVer")
    annotationProcessor("org.projectlombok:lombok:$lombokVer")
    annotationProcessor("org.hibernate:hibernate-jpamodelgen:$jpamodelgenVer")
    // EXPLAIN_V Генерация класса Entity с полями для Criteria
    compileOnly("org.hibernate:hibernate-jpamodelgen:$jpamodelgenVer")
    compileOnly("org.projectlombok:lombok:$lombokVer")
    runtimeOnly("org.postgresql:postgresql:$postgreSQLVer")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootStarterVer")
    testImplementation("org.springframework.kafka:spring-kafka-test:$kafkaVer")
    testImplementation("org.springframework.security:spring-security-test:$springSecurityVer")
    testImplementation("org.springframework.graphql:spring-graphql-test:$graphQlTestVer")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersJunitVer")
    testImplementation("org.testcontainers:postgresql:$testcontainersJunitVer")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitVer")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
