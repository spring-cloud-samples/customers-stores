#!/bin/sh
URL_BASE=${1:-http://localhost:9000}
curl -i -H "Content-Type: application/json" ${URL_BASE}/customers -d @rest-microservices-customers/src/test/resources/customers.json
