# A Demo of Dropwizard/Spring Integration

## Why would you want to integrate Dropwizard and Spring?

- I recommend using Spring Boot. This is for just in case you do not have that option, and you still
want to integrate Spring framework into your Dropwizard project.
- JavaConfig makes the integration much simpler.
- One good thing of this integration is that there is only one configuration file.

## Tech Stack

- Dropwizard (and then all the libraries that it brings)
- Spring
- jose4j (JWT authentication)
- Swagger UI (REST API Test Console. It is powerful, but its UI design is kind of ugly from a modern point of view ..)

## How to run it?

- Run the command: mvn clean package
- Run another command: java -jar target/dw-spring-0.0.1-SNAPSHOT.jar server conf/dw.yml
- Open following URL in a browser: http://localhost:7072/dw/
- Expand the echo section, and then verify that you can access the echo endpoint without a correct api key
- Expand the upperEcho section, and then verify that you cannot access the upperEcho endpoint without a correct api key
- Enter 'c001' as the api key in the upper-right text field, and then verify that you can access the upperEcho endpoint now

## Notes

- I am thinking of implement an OpenID Connect server (using MITREid Connect maybe :)
