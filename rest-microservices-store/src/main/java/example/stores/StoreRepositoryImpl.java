package example.stores;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Spencer Gibb
 */
@Component
public class StoreRepositoryImpl implements StoreRepositoryCustom {

	public static final String STORES_KEY_PREFIX = "stores:";
	private RedisTemplate redis;
	private RedisKeyValueTemplate kv;

	public StoreRepositoryImpl(@Qualifier("redisTemplate") RedisTemplate redis, RedisKeyValueTemplate kv) {
		this.redis = redis;
		this.kv = kv;
	}

	@Override
	public List<GeoResult<RedisGeoCommands.GeoLocation<Store>>> findNear(@Param("location") Point location, @Param("distance") Distance distance) {
		GeoResults geoResults = this.redis.opsForGeo().geoRadius("stores_geo", new Circle(location, distance),
				RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
						.includeCoordinates()
						.includeDistance()
						.limit(10));
		List<GeoResult<RedisGeoCommands.GeoLocation<Store>>> results = geoResults.getContent();
		ArrayList<GeoResult<RedisGeoCommands.GeoLocation<Store>>> list = new ArrayList<>(results.size());
		for (GeoResult<RedisGeoCommands.GeoLocation<Store>> store : results) {
			list.add(store);
		}
		return list;
		// return new PageImpl<>(new ArrayList<Store>(), pageable, results.size());
	}

	/**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
	public Page<Store> findAll(Pageable pageable) {
		ScanOptions options = new ScanOptions.ScanOptionsBuilder()
				.match(STORES_KEY_PREFIX+"*")
				.count(20)
				.build();
		Cursor<byte[]> cursor = this.redis.getConnectionFactory().getConnection().scan(options);

		//TODO actually implement paging (ie skip until current page, then populate
		ArrayList<Store> stores = new ArrayList<>();
		while (cursor.hasNext()) {
			String key = new String(cursor.next()).substring(STORES_KEY_PREFIX.length());
			Store store = this.kv.findById(key, Store.class);
			stores.add(store);
			if (stores.size() >= 20) {
				try {
					cursor.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return new PageImpl<>(stores, pageable, this.kv.count(Store.class));
	}
}
