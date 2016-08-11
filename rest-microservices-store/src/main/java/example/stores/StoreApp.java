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
package example.stores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring configuration class main application bootstrap point.
 * 
 * @author Oliver Gierke
 */
@SpringBootApplication
@EnableDiscoveryClient
public class StoreApp extends RepositoryRestConfigurerAdapter {


	@Bean
	public RedisTemplate<?, ?> redisTemplate(
			RedisConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
		RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
		/*RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());*/
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	@Bean
	public RedisConnectionFactory connectionFactory() {
		return new JedisConnectionFactory();
	}

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Store.class);
    }

    @PostConstruct
	public void exposeIds() {
	}

	public static void main(String[] args) {
		SpringApplication.run(StoreApp.class, args);
	}

    @Controller
    public static class SimpleStoresController {
        @Autowired
        StoreRepository repository;

		@RequestMapping("/stores/search/findnear")
		@ResponseBody
		List<?> findNear(@RequestParam("location") Point location, @RequestParam("distance") Distance distance) {
			return this.repository.findNear(location, distance);
		}

        @RequestMapping("/simple/stores")
        @ResponseBody
        List<Store> getStores() {
            // Page<Store> all = repository.findAll(new PageRequest(0, 10));
			// return all.getContent();
			ArrayList<Store> stores = new ArrayList<>();
			for (Store store : repository.findAll()) {
				stores.add(store);
				if (stores.size() >= 20) {
					break;
				}
			}
			return stores;
        }
    }
    
    @Configuration
    @Profile("cloud")
    protected static class CloudFoundryConfiguration {
    	
    	@Bean
    	  public Cloud cloud() {
    	    return new CloudFactory().getCloud();
    	  }

    }

}
