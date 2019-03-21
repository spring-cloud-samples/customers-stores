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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

import java.io.Serializable;
// import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

/**
 * Value object to represent an {@link Address}.
 * 
 * @author Oliver Gierke
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address implements Serializable {

	private String street, city, zip;
	// private /*@GeoSpatialIndexed*/ Point location;
	private Double x, y;


	Point getLocation() {
		return new Point(x, y);
	}
}
