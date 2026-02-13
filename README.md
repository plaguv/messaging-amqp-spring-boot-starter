![ApacheMaven](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/Rabbitmq-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)

## 📦 Messaging AMQP Spring Boot Starter

A standardized, opinionated messaging template for RabbitMQ, designed for internal event-driven communication in Spring
Boot applications.

This starter aims to:

- Eliminate repetitive RabbitMQ setup
- Enforce domain consistency, type safety, and naming conventions across services
- Provide a plug-and-play experience while remaining fully extensible

All components are exposed via interfaces, and the starter is fully auto-configured using Spring Boot’s
``@AutoConfiguration``.

## 🧩 Modules

1. Contract

   Defines the canonical messaging contracts shared across services:

    - All events and their payloads
    - The canonical container for messages: ``EventEnvelope``, which includes:
        - ``EventMetadata``
        - ``EventRouting``
        - ``EventPayload``

   This ensures consistent message structure across all services.

2. Messaging

   Provides the RabbitMQ implementation for publishing and consuming events:

- ``AutoConfiguration`` and properties for all messaging components
- ``EventRouter`` for deterministic routing based on EventEnvelope
- ``ListenerDiscoverer`` and ``TopologyDeclarer`` for automatic queue/exchange/binding setup
- ``EventEnvelope`` publisher component

## ⚙️ Configuration

At minimum, each application must define its central exchange and central application, which act as namespaces:

````yaml
amqp:
  central-exchange: central
  central-application: starter # or ${spring.application.name}
````

These values are used to derive:

- Exchanges
- Queues
- Routing keys
- Bindings

Automatically, ensuring consistent naming and topology across services.

## 📦 Installation

> Note: This dependency is currently not published to Maven Central.

#### Maven

````maven
<dependencies>
    <dependency>
        <groupId>io.github.plaguv</groupId>
        <artifactId>messaging-amqp-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
````

#### Gradle

````gradle
dependencies {
    implementation 'io.github.plaguv:messaging-amqp-spring-boot-starter:1.0.0'
}
````

## 🚀 Usage Examples

#### ✉️ Defining an event

To define an event recognized by the starter, simply create a class or record and annotate it with ``@Event``.
The ``@Event`` annotation requires a ``domain`` and optionally a version (defaults to ``1.0.0``).

````java

@Event(domain = EventDomain.STORE)
public record StoreOpenedEvent(
        long storeId,
        Instant openedAt
) {
}
````

#### 📤 Publishing an event

Publishing an event is straightforward:

1. Inject the ``EventPublisher`` bean.
2. Build an ``EventEnvelope`` with metadata, routing, and payload.
3. Call ``publishMessage()`` - the starter handles everything else (exchange, queue, bindings, routing).

````java
EventPayload payload = EventPayload.valueOf(
        new StoreOpenedEvent(5L)
);

EventEnvelope envelope = EventEnvelopeBuilder.defaults()
        .ofEventPayload(payload)
        .built();

// Inject as a Spring Bean via Constructor injection or @Autowired field
EventPublisher publisher;
publisher.publishMessage(eventEnvelope);
````

> The builder infers all required routing and metadata based on the payload type.

#### 📤 Listening to an event

Listening to events is just as simple:

- Annotate a method with ``@AmqpListener``
- The method parameter type determines which event is listened to.
- **Exactly one parameter** is required, and it must be annotated with ``@Event``.

````java

@AmqpListener
public void onStoreOpened(StoreOpenedEvent event) {
    // handle event
}
````

> The starter automatically declares the necessary queues, exchanges, and bindings based on the event type and namespace.