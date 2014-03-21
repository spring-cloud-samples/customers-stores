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
package example.customers;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author Oliver Gierke
 */
@Value
@Embeddable
@AllArgsConstructor
public class Location {

	private final double latitude, longitude;

	protected Location() {
		this.latitude = 0.0;
		this.longitude = 0.0;
	}
}
