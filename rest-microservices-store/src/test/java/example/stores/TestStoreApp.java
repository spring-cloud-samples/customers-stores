package example.stores;

import java.util.List;
import java.util.Map;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Spencer Gibb
 */
@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableFeignClients
public class TestStoreApp {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = new SpringApplicationBuilder()
				.web(WebApplicationType.NONE).sources(TestStoreApp.class).run(args);
		StoreClient client = context.getBean(StoreClient.class);
		List<Map<String, ?>> stores = client.getStores();
		if (!stores.isEmpty()) {
			Map<String, ?> data = stores.get(0);
			Store store = client.getStore(data.get("id").toString(), "myval");
			System.out.println(store);
		}
	}

	@FeignClient(name = "stores", url = "http://localhost:8081")
	public interface StoreClient {
		@RequestMapping(method = RequestMethod.GET, value = "/simple/stores")
		List<Map<String, ?>> getStores();

		@RequestMapping(method = RequestMethod.GET, value = "/stores/{storeId}", produces = "application/hal+json")
		Store getStore(@PathVariable("storeId") String storeId,
				@RequestParam("myparam") String myparam);
	}
}
