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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryManager;

/**
 * @author Oliver Gierke
 */
@Component
@Slf4j
@ConfigurationProperties("integration.stores")
public class StoreIntegration {

	@Getter
	@Setter
	private String uri = "http://localhost:8081/stores";

	private Link link;

	private long timestamp = System.currentTimeMillis();

	public Link getStoresByLocationLink() {
		if (System.currentTimeMillis() - timestamp > 5000) {
			this.link = discoverByLocationLink();
			timestamp = System.currentTimeMillis();
		}
		return this.link;
	}

	private boolean verify(Link link) {

		if (link == null) {
			return false;
		}

		try {
			log.info("Verifying stores-nearby link pointing to {}…", link);
			new RestTemplate().headForHeaders(link.expand().getHref());
			log.info("Successfully verified link!");
			return true;
		}
		catch (RestClientException o_O) {
			log.info("Verification failed, marking as outdated!");
		}
		return false;
	}

	private Link discoverByLocationLink() {

		URI storesUri = URI.create(uri);

		try {
			InstanceInfo instance = DiscoveryManager.getInstance().getDiscoveryClient()
					.getNextServerFromEureka("stores.mydomain.net", false);
			storesUri = URI.create(instance.getHomePageUrl());
		}
		catch (RuntimeException e) {
			// Eureka not available
		}

		try {
			log.info("Trying to access the stores system at {}…", storesUri);

			Traverson traverson = new Traverson(storesUri, MediaTypes.HAL_JSON);
			Link link = traverson.follow("stores", "search", "by-location").asLink();

			log.info("Found stores-by-location link pointing to {}.", link.getHref());

			return link;

		}
		catch (RuntimeException o_O) {
			log.info("Stores system unavailable. Got: ", o_O.getMessage());
		}
		return null;
	}

	public boolean isStoresAvailable() {
		return verify(getStoresByLocationLink());
	}
}
