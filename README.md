![ApacheMaven](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/Rabbitmq-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)

## 📦 Messaging AMQP Starter

This project provides a standardized, opinionated messaging template built on RabbitMQ for internal event-driven
communication.

Its primary goal is to eliminate repetitive AMQP setup while enforcing domain consistency, type safety, and naming
conventions across services.

The project is designed to be plug-and-play for most use cases, while remaining extensible for advanced scenarios.

## 🧩 Modules

#### 1. `contract`

The `contract` module defines the canonical messaging contracts shared across all services. It provides:

- `EventEnvelope` : standardized message container
- `EventMetadata` : technical metadata (ID, timestamp, version, producer)
- `EventRouting` : routing intent (event type, dispatch type)
- `EventDomain` : logical domain separation
- `EventDispatchType` : delivery semantics (`DIRECT`, `FANOUT`, `TOPIC`)
- Strongly typed event payloads (`EventInstance` implementations)

This module is **transport-agnostic** and contains no AMQP-specific logic.

#### 2. `messaging`

The `messaging` module provides the **RabbitMQ** implementation for both publishing and consuming events. It includes:

- 📤 **Publisher infrastructure**
    - `AmqpEventPublisher`
    - Mandatory publishing with return callbacks

- 📥 **Listener infrastructure**
    - Annotation-based listeners (`@AmqpListener`)
    - Automatic listener registration

- 🧭 **Routing & topology**
    - Convention-based exchange, queue, and routing-key resolution
    - **Just-in-time (JIT)** declaration of queues, exchanges, and bindings

## ⚙️ Configuration

At minimum, each application defines its logical exchange namespace:

````yaml
amqp:
  central_exchange: central
  central_application: application # or ${spring.application.name}
````

This namespace is used to derive exchanges, queues, and bindings consistently across services.

## 📦 Installation

Simply add the dependency using your preferred build tool (Maven or Gradle).

````maven
<dependencies>
    <dependency>
        <groupId>io.github.plaguv</groupId>
        <artifactId>messaging-amqp-starter</artifactId>
        <version>1.0.0-alpha-1</version>
    </dependency>
</dependencies>
````

````gradle
dependencies {
    implementation 'io.github.plaguv:messaging-amqp-starter:1.0.0-alpha-1'
}
````

## 🚀 Examples

To demonstrate the ease of use, here are some common use cases integrated into an everyday java project:

### 📤 Publishing an event

The starter automatically infers:

- the correct exchange

- the routing key

- message headers and encoding

It also handles serialization and error logging. 
At minimum, each EventEnvelope builder requires a producer and a payload.

````java
EventInstance eventInstance = new StoreOpenedEvent(5L);
EventEnvelope envelope = EventEnvelope.builder()
        .withProducer(StoreService.class)
        .ofPayload(eventInstance)
        .build();

// publisher is a spring-managed bean of type `EventPublisher`.
eventPublisher.publishMessage(envelope);
````

### 📤 Listening to an event

All required AMQP topology is derived and declared automatically.

````java

@AmqpListener
public void onStoreOpened(StoreOpenedEvent event) {
    // handle event
}
````