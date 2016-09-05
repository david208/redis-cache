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

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisConfig extends JedisPoolConfig {

	/**
	 * 集群集合，例子：192.168.220.161:7000,192.168.220.162:7000,192.168.220.166:7000
	 */
	private String host = Protocol.DEFAULT_HOST;
	private int timeout = Protocol.DEFAULT_TIMEOUT;
	/**
	 * 集群不支持
	 */
	private String password;
	/**
	 * 集群不支持
	 */
	private int database = Protocol.DEFAULT_DATABASE;
	/**
	 *  namespace
	 */
	private String clientName;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		if (host == null || "".equals(host)) {
			host = Protocol.DEFAULT_HOST;
		}
		this.host = host;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if ("".equals(password)) {
			password = null;
		}
		this.password = password;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		if ("".equals(clientName)) {
			clientName = null;
		}
		this.clientName = clientName;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
