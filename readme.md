# REST based micro-services sample

- Three Spring Boot based Maven projects that are standalone applications:
  - Stores (MongoDB, exposing a few Starbucks shops across north america, geo-spatial functionality)
  - Customers (JPA)
  - Customers UI (Angular and Spring Boot CLI backend)
- The customers application tries to discover a search-by-location-resource and periodically verifying it's still available (see `StoreIntegration`).
- If the remote system is found the customers app includes a link to let clients follow to the remote system and thus find stores near the customer.
- Hystrix is used to monitor the availability of the remote system, so if it fails to connect 20 times in 5 seconds (by default) the circuit will open and no more attempts will be made until after a timeout.

## Running Instructions
- Before try to run the services, make sure you have Rabbitmq Server and MongoDB running on localhost.
- To run the services, just execute "mvn spring-boot:run" in each project subfolder, and "spring run app.groovy for the UI".
