Rewards
=======

A customer receives 2 points for every dollar spent over $100 in each transaction, plus 1 point for every dollar spent over $50 in each transaction
(e.g. a $120 purchase = 2x$20 + 1x$50 = 90 points).

If you have maven installed and under linux/mac:

    ./mvnw clean install

And for windows

    mvnw.cmd clean install

If we don't have the specified Maven in the wrapper properties, it'll be downloaded and installed in the folder $USER_HOME/.m2/wrapper/dists of the system.

To run our Spring-Boot project:

    ./mvnw spring-boot:run

After the server is running, go to

```
http://localhost:8080/api/v1/rewards/points/1
```

Components
==========
- Java 11, Spring Boot 2.7.2, H2 DB (in-memory hql),Spring MVC, Spring Data JPA, Junit 5
