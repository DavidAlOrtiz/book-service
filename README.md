# book service

API REST de libros, autores, categorías y favoritos. Spring Boot 4, JPA y H2.

Este servicio no tiene autenticación propia. el api-gateway sirve como resource server.

## Requisitos

- Java 21
- Maven (o `./mvnw`)

## EJECUTAR 

```bash
./mvnw spring-boot:run
```

expone :  `http://localhost:8081`.

actualmente no tiene perfiles en la configuración


## Base de datos

La base de datos de este servicio esta en memoria con H2
`schema.sql` crea las tablas y `data.sql` carga datos de ejemplo al arrancar.

consola de administración

- http://localhost:8081/h2-console
- JDBC: jdbc:h2:mem:bookstore
- usuario: sa
- pasword: 

## Documentación

El servicio cuenta con sgweer, que expone la documentación de los endponts
url :  `http://localhost:8081/swagger-ui/index.html`.

## Despligue


Dockerfile incluido, se despligua en openshift