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

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

/**
 * Entity to represent a {@link Store}.
 * 
 * @author Oliver Gierke
 */
@Data
@RedisHash("stores")
public class Store implements Serializable {

	private final @Id String id;
	private final String name;
	private final Address address;

	public Store(String name, Address address) {

		this.name = name;
		this.address = address;
		this.id = null;
	}

	protected Store() {

		this.id = null;
		this.name = null;
		this.address = null;
	}
}
