![ApacheMaven](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/Rabbitmq-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)

## 📦 Plaguv AMQP Spring Boot Starter

A standardized, opinionated messaging framework for RabbitMQ, designed for internal event-driven communication in Spring Boot applications.

This starter provides:
- Elimination of repetitive RabbitMQ setup
- Enforced domain consistency, type safety, and naming conventions across services
- A plug-and-play experience that is fully extensible

All components are exposed via interfaces, and the starter is fully autoconfigured using Spring Boot’s ``@AutoConfiguration``.

## ⚙️ Configuration

At minimum, each application must define its central exchange and central application, which act as namespaces.

````yaml
amqp:
  central-exchange: central
  central-application: starter # or ${spring.application.name}
````

Further details for configurations are available [here](.docs/CONFIGURATION.md)

## 📦 Installation

> Note: This dependency is currently not published to Maven Central.

#### Maven

````maven
<dependencies>
    <dependency>
        <groupId>io.github.plaguv</groupId>
        <artifactId>plaguv-amqp-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
````

#### Gradle

````gradle
dependencies {
    implementation 'io.github.plaguv:plaguv-amqp-spring-boot-starter:1.0.0'
}
````

## 🔧 Usage Examples

Usage examples are documented in detail:

- [Defining an event](.docs/EVENT-CREATION.MD)
- [Publishing an event](.docs/EVENT-PUBLISHING.MD)
- [Listening to an event](.docs/EVENT-LISTENING.MD)