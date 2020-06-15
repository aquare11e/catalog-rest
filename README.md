# Catalog application

Catalog is a sample REST application created using Spring stack (Framework, Boot, Data, Security, etc), PostgreSQL, FlyWay, Orika and OpenAPI.

To run the application you need:
 - **PostgreSQL** (use _docker-compose up_ command from root directory or change _application-local.properties_ according to your DB instance);
 - Any **IDE** with Spring support (IntelliJ IDEA Ultimate or STS) and/or **JDK 11** or higher.

Use _local_ profile to start application with predefined database and security user (see _application-local.properties_ and _InMemoryUsersConfigurationProperties_ class).

Also, when application successfully started you can explore API using _Swagger UI_ available via **/api-ui** (and _/api-docs_ for JSON OpenAPI specification).