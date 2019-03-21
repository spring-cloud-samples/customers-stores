/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.stores;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Component initializing a hand full of Starbucks stores and persisting them through a {@link StoreRepository}.
 * 
 * @author Oliver Gierke
 */
@Slf4j
@Component
public class StoreInitializer {

	@Autowired
	public StoreInitializer(StoreRepository repository, @Qualifier("redisTemplate") RedisOperations operations) throws Exception {

		if (repository.count() != 0) {
			return;
		}

		List<Store> stores = readStores();
		log.info("Importing {} stores using {}…", stores.size(), operations.getClass().getSimpleName());
		repository.save(stores);
		saveAddressGeo(stores, operations);
		log.info("Successfully imported {} stores.", repository.count());
	}

	private void saveAddressGeo(List<Store> stores, RedisOperations operations) {
		BoundGeoOperations geoOps = operations.boundGeoOps("stores_geo");
		for (Store store : stores) {
			geoOps.geoAdd(store.getAddress().getLocation(), store);
		}
	}

	/**
	 * Reads a file {@code starbucks.csv} from the class path and parses it into {@link Store} instances about to
	 * persisted.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<Store> readStores() throws Exception {

		ClassPathResource resource = new ClassPathResource("starbucks.csv");
		Scanner scanner = new Scanner(resource.getInputStream());
		String line = scanner.nextLine();
		scanner.close();

		FlatFileItemReader<Store> itemReader = new FlatFileItemReader<Store>();
		itemReader.setResource(resource);

		// DelimitedLineTokenizer defaults to comma as its delimiter
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(line.split(","));
		tokenizer.setStrict(false);

		DefaultLineMapper<Store> lineMapper = new DefaultLineMapper<Store>();
		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(StoreFieldSetMapper.INSTANCE);
		itemReader.setLineMapper(lineMapper);
		itemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
		itemReader.setLinesToSkip(1);
		itemReader.open(new ExecutionContext());

		List<Store> stores = new ArrayList<>();
		Store store = null;

		do {

			store = itemReader.read();

			if (store != null) {
				stores.add(store);
			}

		} while (store != null);

		return stores;
	}

	private enum StoreFieldSetMapper implements FieldSetMapper<Store> {

		INSTANCE;

		/* 
		 * (non-Javadoc)
		 * @see org.springframework.batch.item.file.mapping.FieldSetMapper#mapFieldSet(org.springframework.batch.item.file.transform.FieldSet)
		 */
		@Override
		public Store mapFieldSet(FieldSet fields) throws BindException {

			// Point location = new Point(fields.readDouble("Longitude"), fields.readDouble("Latitude"));
			Address address = new Address(fields.readString("Street Address"), fields.readString("City"),
					fields.readString("Zip"), fields.readDouble("Longitude"), fields.readDouble("Latitude"));

			return new Store(fields.readString("Name"), address);
		}
	}
}
