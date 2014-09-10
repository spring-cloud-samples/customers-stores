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

import java.net.URI;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.stereotype.Component;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * @author Oliver Gierke
 */
@Component
@Slf4j
@ConfigurationProperties("integration.stores")
public class StoreIntegration {

	private DiscoveryClient discoveryClient;

	@Autowired
	public StoreIntegration(DiscoveryClient discoveryClient) {
		this.discoveryClient = discoveryClient;
		System.out.println("Creating " + getClass());
	}

	@Getter
	@Setter
	private String uri = "http://localhost:8081/stores";

	@HystrixCommand(fallbackMethod = "defaultLink")
	public Link getStoresByLocationLink(Map<String, Object> parameters) {
		URI storesUri = URI.create(uri);

		try {
			InstanceInfo instance = discoveryClient.getNextServerFromEureka("stores",
					false);
			storesUri = URI.create(instance.getHomePageUrl());
		}
		catch (RuntimeException e) {
			// Eureka not available
		}

		log.info("Trying to access the stores system at {}…", storesUri);

		Traverson traverson = new Traverson(storesUri, MediaTypes.HAL_JSON);
		Link link = traverson.follow("stores", "search", "by-location")
				.withTemplateParameters(parameters).asLink();

		log.info("Found stores-by-location link pointing to {}.", link.getHref());

		return link;
	}

	public Link defaultLink(Map<String, Object> parameters) {
		return null;
	}
}
