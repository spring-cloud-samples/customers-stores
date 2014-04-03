# REST based micro-services sample

## tl;dr

- Two Spring Boot based Maven projects that are standalone applications:
  - Stores (MongoDB, exposing a few Starbucks shops across north america, geo-spatial functionality)
  - Customers (JPA)
- The customers application tries to discover a search-by-location-resource and periodically verifying it's still available (see `StoreIntegration`).
- If the remote system is found the customers app includes a link to let clients follow to the remote system and thus find stores near the customer.