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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;

/**
 * @author Oliver Gierke
 */
@Component
@Slf4j
@ConfigurationProperties("integration.stores")
public class StoreIntegration {

	private LoadBalancerClient loadBalancer;

    @Autowired
	public StoreIntegration(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
	}

	@Getter
	@Setter
	private String uri = "http://localhost:8081/stores";

	@HystrixCommand(fallbackMethod = "defaultLink")
	public Link getStoresByLocationLink(Map<String, Object> parameters, HttpHeaders headers) {
		URI storesUri = URI.create(uri);

		try {
            ServiceInstance instance = loadBalancer.choose("stores");
			storesUri = URI.create(String.format("http://%s:%s", instance.getHost(), instance.getPort()));
		}
		catch (RuntimeException e) {
			// Eureka not available
		}

		log.info("Trying to access the stores system at {}…", storesUri);

        //TODO: all of the above could be replaced with restTemplate/ribbon
        //The uri would be http://stores
        //traverson.setRestOperations and stuff from Traverson.createDefaultTemplate
		Traverson traverson = new Traverson(storesUri, MediaTypes.HAL_JSON);

        Link link = traverson.follow("stores", "search", "by-location")
                .withHeaders(headers)
				.withTemplateParameters(parameters).asLink();

		log.info("Found stores-by-location link pointing to {}.", link.getHref());

		return link;
	}

    @SuppressWarnings("unused")
	public Link defaultLink(Map<String, Object> parameters, HttpHeaders headers) {
		return null;
	}
}
