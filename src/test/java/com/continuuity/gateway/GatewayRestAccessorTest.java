package com.continuuity.gateway;

import com.continuuity.common.conf.CConfiguration;
import com.continuuity.data.operation.executor.OperationExecutor;
import com.continuuity.data.runtime.DataFabricInMemoryModule;
import com.continuuity.gateway.accessor.RestAccessor;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test configures a gateway with a test accessor as the single connector
 * and verifies that key/values that have been persisted to the in-memory data
 * fabric can be retrieved vie HTTP get requests.
 */
public class GatewayRestAccessorTest {

	// Our logger object
	private static final Logger LOG = LoggerFactory
			.getLogger(GatewayRestAccessorTest.class);

	// A set of constants we'll use in these tests
	static  String name = "access.rest";
	static final String prefix = "/continuuity";
	static final String path = "/table/";
	static final String table = "default";
	static final int valuesToGet = 10;
	static int port = 10000;

	// This is the Gateway object we'll use for these tests
	private Gateway theGateway = null;

	private OperationExecutor executor;

	/**
	 * Create a new Gateway instance to use in these set of tests. This method
	 * is called before any of the test methods.
	 *
	 * @throws Exception If the Gateway can not be created.
	 */
	@Before
	public void setupGateway() throws Exception {

		// Set up our Guice injections
		Injector injector = Guice.createInjector(
				new DataFabricInMemoryModule());
		this.executor = injector.getInstance(OperationExecutor.class);

		// Look for a free port
		port = Util.findFreePort();

		// Create and populate a new config object
		CConfiguration configuration = new CConfiguration();

		configuration.set(Constants.CONFIG_CONNECTORS, name);
		configuration.set(Constants.buildConnectorPropertyName(name,
				Constants.CONFIG_CLASSNAME), RestAccessor.class.getCanonicalName());
		configuration.setInt(Constants.buildConnectorPropertyName(name,
				Constants.CONFIG_PORT),port);
		configuration.set(Constants.buildConnectorPropertyName(name,
				Constants.CONFIG_PATH_PREFIX), prefix);
		configuration.set(Constants.buildConnectorPropertyName(name,
				Constants.CONFIG_PATH_MIDDLE), path);

		// Now create our Gateway
		theGateway = new Gateway();
		theGateway.setExecutor(this.executor);
		theGateway.setConsumer(new Util.NoopConsumer());
		theGateway.configure(configuration);

	} // end of setupGateway


	/**
	 * Test that we can send simulated REST events to a Queue
	 *
	 * @throws Exception If any exceptions happen during the test
	 */
	@Test
	public void testReadFromGateway() throws Exception {

		// Start the Gateway
		theGateway.start();

		// Send some REST events
		for (int i = 0; i < valuesToGet; i++) {
			Util.testKeyValue(this.executor,
					"http://localhost:" + port + prefix + path + "default/",
					"key" + i, "value" + i);
		}

		// Stop the Gateway
		theGateway.stop();
	}


}
