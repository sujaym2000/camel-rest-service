# Problem Statement

```
 Create a Sample REST API using Camel and Spring Boot and save it to GITHub, it should
include the following:
```
```
o Expose a Restful Service
o HTTP verb as POST and Media Type can be JSON or XML
o Define/Create a front end and backend schema’s (JSON or XML Schema’s) – A simple
schema should be sufficient.
o Validate a Message (using a front end JSON or XML Schema)
o Transform a Message (Front end JSON or XML format to a backend JSON or XML format)
o Validate a transformed message (using a backend JSON or XML Schema)
o Build a Mock Service to receive a message and return some sample response
o Unit Test
o Created a test that connects to the API you have created, through any Rest Client, and
returns a response
o One happy path use case (End to End flow)
o One unhappy path use case (End to End flow)
o Once the exercise is completed then it would be great to create a Docker file to create
an image of the microservice. (It’s optional, but if you are able to complete this it is
strongly preferred)
```
#### Solution Explanation

Front end exposed 2 REST APIs to create and get order details for customer. Internally It makes call to back end service which consumes xml data format. Solution makes json request validation and transforms request to downstream api expected xml format. Test are end to end integration test to cover for happy journey and negative scenarios. 

#### Exposed Endpoints
```
GET   /customers/{customerId}/orders
POST  /customers/orders
```

#### Backend Endpoints
```
GET   /backend/customers/{customerId}/orders
POST  /backend/customers/orders
```

#### Sample GET order details response 
```
{
  "custId": "c1b2a3",
  "prdctId": "mobile-apple",
  "ordStatus": "In-progress",
  "qty": "1"
}
```

#### Sample POST order details response 
```
{
  "custId": "c1b2a3",
  "prdctId": "mobile-apple",
  "ordStatus": "In-progress",
  "qty": "1"
}
```

##### Docker image generation 

Spring-boot mavne plugin provides goal to build docker image, which internally maintains DockerFile and builds image.

```
mvn spring-boot:build-image
```

After succesful completion of build you can run image using following docker command, which will start docker application, Application should be able to access from postman on port 8000,

```
docker run --publish 8000:8080 --detach --name camel-rest-service camel-rest-service:1.0-SNAPSHOT
```



