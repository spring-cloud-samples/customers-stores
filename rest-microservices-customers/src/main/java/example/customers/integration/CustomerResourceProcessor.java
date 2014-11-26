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

import example.customers.Customer;
import example.customers.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Oliver Gierke
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerResourceProcessor implements ResourceProcessor<Resource<Customer>> {

    private static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    private final StoreIntegration storeIntegration;
    private final Provider<HttpServletRequest> requestProvider;

	@Override
	public Resource<Customer> process(Resource<Customer> resource) {

		Customer customer = resource.getContent();
		Location location = customer.getAddress().getLocation();

        HttpHeaders headers = new HttpHeaders();

        String header = requestProvider.get().getHeader(X_FORWARDED_HOST);
        if (header != null) {
            headers.put(X_FORWARDED_HOST, Collections.singletonList(header));
        }

        Map<String, Object> parameters = new HashMap<>();
    	parameters.put("location", String.format("%s,%s", location.getLatitude(), location.getLongitude()));
    	parameters.put("distance", "50");
		Link link = storeIntegration.getStoresByLocationLink(parameters, headers);
        if (link != null) {
            resource.add(link.withRel("stores-nearby"));
        }

		return resource;
	}
}
