package com.continuuity.gateway.util;

import com.continuuity.common.conf.CConfiguration;
import com.continuuity.gateway.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @todo write javadoc
public class HttpConfig {

	private static final Logger LOG = LoggerFactory
			.getLogger(HttpConfig.class);

	public static final int DefaultPort = 8080;
	public static String DefaultPrefix = "";
	public static String DefaultPath = "/";
	public static boolean DefaultChunking = true;
	public static int DefaultMaxContentSize = 1024 * 1024;
	public static boolean DefaultSsl = false;

	private String name = "<unknown>";
	private int port = DefaultPort;
	private String prefix = DefaultPrefix;
	private String path = DefaultPath;
	private int maxContentSize = DefaultMaxContentSize;
	private boolean chunk = true;
	private boolean ssl = false;

	private HttpConfig() { };
	public HttpConfig(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	public int getPort() {
		return this.port;
	}
	public String getPathPrefix() {
		return this.prefix;
	}
	public String getPathMiddle() {
		return this.path;
	}
	public boolean isChunking() {
		return this.chunk;
	}
	public boolean isSsl() {
		return this.ssl;
	}
	public int getMaxContentSize() {
		return this.maxContentSize;
	}

	public HttpConfig setPort(int port) {
		this.port = port;
		return this;
	}
	public HttpConfig setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}
	public HttpConfig setPath(String path) {
		this.path = path;
		return this;
	}

	public static HttpConfig configure(String name,
																		 CConfiguration configuration,
																		 HttpConfig defaults) throws Exception {
		HttpConfig config = new HttpConfig(name);
		config.port = configuration.getInt(Constants.buildConnectorPropertyName(
				name, Constants.CONFIG_PORT), defaults.getPort());
		config.chunk = configuration.getBoolean(Constants.buildConnectorPropertyName(
				name, Constants.CONFIG_CHUNKING), defaults.isChunking());
		config.ssl = configuration.getBoolean(Constants.buildConnectorPropertyName(
				name, Constants.CONFIG_SSL), defaults.isSsl());
		config.prefix = configuration.get(Constants.buildConnectorPropertyName(
				name, Constants.CONFIG_PATH_PREFIX), defaults.getPathPrefix());
		config.path = configuration.get(Constants.buildConnectorPropertyName(
				name, Constants.CONFIG_PATH_MIDDLE), defaults.getPathMiddle());
		config.maxContentSize = configuration.getInt(Constants.buildConnectorPropertyName(
				name, Constants.CONFIG_MAX_SIZE), defaults.getMaxContentSize());

		if (config.ssl) {
			LOG.warn("SSL is not implemented yet. Ignoring configuration for connector '" + name + "'.");
			config.ssl = false;
		}

		return config;
	}

	/**
	 * Get the base URL that this HttpConfig describes
	 * @return the base URL
	 */
	public String getBaseUrl() {
		return (this.isSsl() ? "https" : "http") + "://localhost:"
				+ this.getPort() + this.getPathPrefix() + this.getPathMiddle();
	}
}
