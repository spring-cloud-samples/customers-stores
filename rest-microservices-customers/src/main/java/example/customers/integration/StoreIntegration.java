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
import java.net.URISyntaxException;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * @author Oliver Gierke
 */
@Component
@Slf4j
@ConfigurationProperties("integration.stores")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreIntegration {

	private final LoadBalancerClient loadBalancer;

	@Getter
	@Setter
	private String uri = "http://localhost:8081/stores";

	@HystrixCommand(fallbackMethod = "defaultLink")
	public Link getStoresByLocationLink(Map<String, Object> parameters, String host, Integer port) {
		URI storesUri = URI.create(uri);

		ServiceInstance instance = null;
		try {
			instance = loadBalancer.choose("stores");
			if (instance != null) {
				storesUri = URI.create(String.format("http://%s:%s", instance.getHost(),
						instance.getPort()));
			}
		}
		catch (RuntimeException e) {
			// Eureka not available
		}

		log.info("Trying to access the stores system at {}…", storesUri);

		// TODO: all of the above could be replaced with restTemplate/ribbon
		// The uri would be http://stores
		// traverson.setRestOperations and stuff from Traverson.createDefaultTemplate
		Traverson traverson = new Traverson(storesUri, MediaTypes.HAL_JSON);

		Link link = traverson.follow("stores", "search", "by-location")
				.withTemplateParameters(parameters).asLink();

		String href = link.getHref();
 		if (host!=null && port!=null && instance != null) {
			href = reconstructURI(host, port, href);
		}
		log.info("Found stores-by-location link pointing to {}.", href);

		return new Link(href, link.getRel());
	}

	/**
	 *
	 * The original URL points to the store service's URL given by Eureka: 192.168.0.3:8081. Since this URL is used by
	 * the client application (on localhost:9900) to make an AJAX call to retrieve the nearby stores, the browser will
	 * not allow the call:
	 * <pre>
	 * XMLHttpRequest cannot load http://192.168.0.3:8081/stores/search/findByAddressLocationNear?location=42.3600825,-71.05888010000001&distance=50km.
	 * No 'Access-Control-Allow-Origin' header is present on the requested resource.
	 * Origin 'http://localhost:9900' is therefore not allowed access.
	 *</pre>
	 *
	 * This method rewrites the URL to point to the edge server: localhost:9900 and let it re-route the request.
	 *
	 * As it is stated on http://cloud.spring.io/spring-cloud-netflix/spring-cloud-netflix.html,
	 * <pre>
	 * Spring Cloud has created an embedded Zuul proxy to ease the development of a very common use case where a UI application
	 * wants to proxy calls to one or more back end services.
	 * This feature is useful for a user interface to proxy to the backend services it requires, avoiding the need
	 * to manage CORS and authentication concerns independently for all the backends.
	 * </pre>
	 *
	 */
	private String reconstructURI(String host, Integer port, String href) {
		URI original;
		try {
			original = new URI(href);
		}
		catch (URISyntaxException e) {
			throw new IllegalArgumentException("Cannot create URI from: " + href);
		}
		//No web server is listening on port 80
		//int port = 80;
		if ("https".equals(original.getScheme())) {
			port = 443;
		}
		if (host.contains(":")) {
			String[] pair = host.split(":");
			host = pair[0];
			port = Integer.valueOf(pair[1]);
		}
		if (host.equals(original.getHost()) && port == original.getPort()) {
			return href;
		}
		String scheme = original.getScheme();
		if (scheme == null) {
			scheme = port == 443 ? "https" : "http";
		}

		StringBuilder sb = new StringBuilder();
		sb.append(scheme).append("://");
		if (StringUtils.hasText(original.getRawUserInfo())) {
			sb.append(original.getRawUserInfo()).append("@");
		}
		sb.append(host);
		if (port >= 0) {
			sb.append(":").append(port);
		}
		sb.append(original.getRawPath());
		if (StringUtils.hasText(original.getRawQuery())) {
			sb.append("?").append(original.getRawQuery());
		}
		if (StringUtils.hasText(original.getRawFragment())) {
			sb.append("#").append(original.getRawFragment());
		}
		return sb.toString();
	}

	public Link defaultLink(Map<String, Object> parameters, String host, Integer port) {
		return null;
	}
}
