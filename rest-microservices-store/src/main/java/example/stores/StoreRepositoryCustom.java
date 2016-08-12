package example.stores;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.PagedResources;

import java.util.List;

/**
 * @author Spencer Gibb
 */
public interface StoreRepositoryCustom {

	@RestResource(rel = "by-location")
	PagedResources<Store> findAllStoreByAddress(@Param("location") Point location, @Param("distance") Distance distance);

	/**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
	Page<Store> findAll(Pageable pageable);
}
