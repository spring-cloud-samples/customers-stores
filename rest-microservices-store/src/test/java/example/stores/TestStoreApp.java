package example.stores;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.FeignConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author Spencer Gibb
 */
@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
public class TestStoreApp extends FeignConfigurer {

    @Bean
    public StoreClient storeClient() {
        return feign().target(StoreClient.class, "http://localhost:8081");
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder().web(false).sources(TestStoreApp.class).run(args);
        StoreClient client = context.getBean(StoreClient.class);
        List stores = client.getStores();
        if (!stores.isEmpty()) {
            Map data = (Map)stores.get(0);
            Store store = client.getStore(data.get("id").toString(), "myval");
            System.out.println(store);
        }
    }

    public interface StoreClient {
        @RequestMapping(method = RequestMethod.GET, value = "/simple/stores")
        List getStores();

        @RequestMapping(method = RequestMethod.GET, value = "/stores/{storeId}", produces = "application/hal+json")
        Store getStore(@PathVariable("storeId") String storeId, @RequestParam("myparam") String myparam);
    }
}
