# Delivery by drone

__Authors__:

* [ANAGONOU Patrick]()
* [ANIGLO Jonas]()
* [FRANCIS Anas]()
* [ZABOURDINE Soulaiman]()

This repository contains the implementation of services for delivery of packages by drone.

## Domain Description

The focus of our axe was battery management, charging dock networks with optimization (docking station are on the path
of heavydrones for long-distance deliveries, "à la TESLA")

## Repository Organization

* `backend` contains the services that make up the architecture.
* `deliverables` contain the reports and presentations

## References

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.5/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#using-boot-devtools)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#production-ready)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Rest Repositories](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#howto-use-exposing-spring-data-repositories-rest-endpoint)
* [Validation](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#boot-features-validation)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

https://attacomsian.com/blog/http-requests-resttemplate-spring-boot

https://www.postgresql.org/docs/8.0/sql-createuser.html

https://github.com/vishnubob/wait-for-it

## Launch a demo

Terminal 1 (back-end services)

```sh
./build.sh
./run.sh

```

Terminal 2

```sh
./start-drone1.sh
```

Terminal 3

```sh
./start-drone2.sh
```

Terminal 4

```sh
./client.sh
```

docker run -p 6379:6379 -d redis:6.2-alpine redis-server --requirepass "password"

docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.9-management