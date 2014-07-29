/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.customers.integration;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import example.customers.Customer;
import example.customers.Location;

/**
 * @author Oliver Gierke
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerResourceProcessor implements ResourceProcessor<Resource<Customer>> {

	private final StoreIntegration storeIntegration;

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.ResourceProcessor#process(org.springframework.hateoas.ResourceSupport)
	 */
	@Override
	public Resource<Customer> process(Resource<Customer> resource) {

		Customer customer = resource.getContent();
		Location location = customer.getAddress().getLocation();

		Link link = storeIntegration.getStoresByLocationLink();
        if (link != null) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("location", String.format("%s,%s", location.getLatitude(), location.getLongitude()));
            parameters.put("distance", "50km");

            resource.add(link.expand(parameters).withRel("stores-nearby"));
        }

		return resource;
	}
}
