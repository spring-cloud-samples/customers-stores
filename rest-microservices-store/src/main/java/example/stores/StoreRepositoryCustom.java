package example.stores;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * @author Spencer Gibb
 */
public interface StoreRepositoryCustom {

	@RestResource(rel = "by-location")
	List<GeoResult<RedisGeoCommands.GeoLocation<Store>>> findNear(@Param("location") Point location, @Param("distance") Distance distance);
}
