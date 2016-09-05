/**
 *    Copyright 2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.caches.redis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * Cache adapter for Redis.
 *
 * @author Eduardo Macarron
 * @modify david208
 */
public final class RedisCache implements Cache {

	private final ReadWriteLock readWriteLock = new DummyReadWriteLock();

	private String id;

	private JedisCluster cluster;

	public RedisCache(final String id) {
		if (id == null) {
			throw new IllegalArgumentException("Cache instances require an ID");
		}
		RedisConfig redisConfig = RedisConfigurationBuilder.getInstance().parseConfiguration();
		this.id = (null == redisConfig.getClientName()) ? id : redisConfig.getClientName() + "_" + id;
		cluster = JedisClusterFactory.getInstance(parseJedisClusterNodes(redisConfig.getHost(), ",", ":"),
				redisConfig.getTimeout());
	}

	private Object execute(RedisCallback callback) {
		return callback.doWithRedis(cluster);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public int getSize() {
		return (Integer) execute(new RedisCallback() {
			@Override
			public Object doWithRedis(JedisCluster cluster) {
				Map<byte[], byte[]> result = cluster.hgetAll(id.toString().getBytes());
				return result.size();
			}
		});
	}

	@Override
	public void putObject(final Object key, final Object value) {
		execute(new RedisCallback() {
			@Override
			public Object doWithRedis(JedisCluster cluster) {
				cluster.hset(id.toString().getBytes(), key.toString().getBytes(), SerializeUtil.serialize(value));
				return null;
			}
		});
	}

	@Override
	public Object getObject(final Object key) {
		return execute(new RedisCallback() {
			@Override
			public Object doWithRedis(JedisCluster cluster) {
				return SerializeUtil.unserialize(cluster.hget(id.toString().getBytes(), key.toString().getBytes()));
			}
		});
	}

	@Override
	public Object removeObject(final Object key) {
		return execute(new RedisCallback() {
			@Override
			public Object doWithRedis(JedisCluster cluster) {
				return cluster.hdel(id.toString(), key.toString());
			}
		});
	}

	@Override
	public void clear() {
		execute(new RedisCallback() {
			@Override
			public Object doWithRedis(JedisCluster cluster) {
				cluster.del(id.toString());
				return null;
			}
		});

	}

	@Override
	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	@Override
	public String toString() {
		return "Redis {" + id + "}";
	}

	private static Set<HostAndPort> parseJedisClusterNodes(String jedisClusterNodes, String delim, String flag) {
		if (null == jedisClusterNodes || jedisClusterNodes.length() == 0)
			return null;
		Set<HostAndPort> jedisClusterNodeSet = new HashSet<HostAndPort>();
		String[] clusterNodeArray = jedisClusterNodes.split(delim);
		for (int i = 0; i < clusterNodeArray.length; i++) {
			String clusterNode = clusterNodeArray[i];
			if (clusterNode.contains(":")) {
				String[] nodeInfo = clusterNode.split(flag);
				jedisClusterNodeSet.add(new HostAndPort(nodeInfo[0], Integer.valueOf(nodeInfo[1])));
			} else {
				jedisClusterNodeSet.add(new HostAndPort(clusterNode, 6379));
			}
		}
		return jedisClusterNodeSet;
	}
}
