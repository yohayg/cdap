/*
 * Copyright © 2015 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package co.cask.cdap.client;

import co.cask.cdap.client.app.FakeApp;
import co.cask.cdap.client.common.ClientTestBase;
import co.cask.cdap.common.conf.Constants;
import co.cask.cdap.test.XSlowTests;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Test for {@link PreferencesClient}
 */
@Category(XSlowTests.class)
public class PreferencesClientTestRun extends ClientTestBase {

  private PreferencesClient client;
  private ApplicationClient appClient;

  @Before
  public void setUp() throws Throwable {
    super.setUp();
    client = new PreferencesClient(clientConfig);
    appClient = new ApplicationClient(clientConfig);
  }

  @Test
  public void testInstancePreferences() throws Exception {
    Map<String, String> propMap = client.getInstancePreferences();
    Assert.assertEquals(ImmutableMap.<String, String>of(), propMap);
    propMap.put("k1", "instance");
    client.setInstancePreferences(propMap);
    Assert.assertEquals(propMap, client.getInstancePreferences());

    File jarFile = createAppJarFile(FakeApp.class);
    appClient.deploy(jarFile);

    propMap.put("k1", "namespace");
    client.setNamespacePreferences(Constants.DEFAULT_NAMESPACE, propMap);
    Assert.assertEquals(propMap, client.getNamespacePreferences(Constants.DEFAULT_NAMESPACE, true));
    Assert.assertEquals(propMap, client.getNamespacePreferences(Constants.DEFAULT_NAMESPACE, false));

    client.deleteNamespacePreferences(Constants.DEFAULT_NAMESPACE);
    propMap.put("k1", "instance");
    Assert.assertEquals(propMap, client.getNamespacePreferences(Constants.DEFAULT_NAMESPACE, true));
    Assert.assertEquals(ImmutableMap.<String, String>of(), client.getNamespacePreferences(Constants.DEFAULT_NAMESPACE,
                                                                                          false));
    propMap.put("k1", "namespace");
    client.setNamespacePreferences(Constants.DEFAULT_NAMESPACE, propMap);
    Assert.assertEquals(propMap, client.getNamespacePreferences(Constants.DEFAULT_NAMESPACE, true));
    Assert.assertEquals(propMap, client.getNamespacePreferences(Constants.DEFAULT_NAMESPACE, false));
    propMap.put("k1", "application");
    client.setApplicationPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, propMap);
    Assert.assertEquals(propMap, client.getApplicationPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, true));
    Assert.assertEquals(propMap, client.getApplicationPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, false));
    propMap.put("k1", "program");
    client.setProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows", FakeApp.FLOWS.get(0), propMap);
    Assert.assertEquals(propMap, client.getProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows",
                                                              FakeApp.FLOWS.get(0), true));
    Assert.assertEquals(propMap, client.getProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows",
                                                              FakeApp.FLOWS.get(0), false));
    client.deleteProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows", FakeApp.FLOWS.get(0));
    propMap.put("k1", "application");
    Assert.assertEquals(0, client.getProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows",
                                                              FakeApp.FLOWS.get(0), false).size());
    Assert.assertEquals(propMap, client.getProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows",
                                                              FakeApp.FLOWS.get(0), true));

    client.deleteApplicationPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME);

    propMap.put("k1", "namespace");
    Assert.assertEquals(0, client.getApplicationPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, false).size());
    Assert.assertEquals(propMap, client.getApplicationPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, true));
    Assert.assertEquals(propMap, client.getProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows",
                                                              FakeApp.FLOWS.get(0), true));

    client.deleteNamespacePreferences(Constants.DEFAULT_NAMESPACE);
    propMap.put("k1", "instance");
    Assert.assertEquals(0, client.getNamespacePreferences(Constants.DEFAULT_NAMESPACE, false).size());
    Assert.assertEquals(propMap, client.getNamespacePreferences(Constants.DEFAULT_NAMESPACE, true));
    Assert.assertEquals(propMap, client.getApplicationPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, true));
    Assert.assertEquals(propMap, client.getProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows",
                                                              FakeApp.FLOWS.get(0), true));

    client.deleteInstancePreferences();
    propMap.clear();
    Assert.assertEquals(propMap, client.getInstancePreferences());
    Assert.assertEquals(propMap, client.getNamespacePreferences(Constants.DEFAULT_NAMESPACE, true));
    Assert.assertEquals(propMap, client.getNamespacePreferences(Constants.DEFAULT_NAMESPACE, true));
    Assert.assertEquals(propMap, client.getApplicationPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, true));
    Assert.assertEquals(propMap, client.getProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows",
                                                              FakeApp.FLOWS.get(0), true));
    appClient.delete(FakeApp.NAME);
  }

  @Test
  public void testDeletingApp() throws Exception {
    Map<String, String> propMap = Maps.newHashMap();
    File jarFile = createAppJarFile(FakeApp.class);
    appClient.deploy(jarFile);

    propMap.put("k1", "application");
    client.setApplicationPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, propMap);
    Assert.assertEquals(propMap, client.getApplicationPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, false));

    propMap.put("k1", "program");
    client.setProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows", FakeApp.FLOWS.get(0), propMap);
    Assert.assertEquals(propMap, client.getProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows",
                                                              FakeApp.FLOWS.get(0), false));

    appClient.delete(FakeApp.NAME);
    // deleting the app should have deleted the preferences that were stored. so deploy the app and check
    // if the preferences are empty. we need to deploy the app again since getting preferences of non-existent apps
    // is not allowed by the API.
    appClient.deploy(jarFile);
    propMap.clear();
    Assert.assertEquals(propMap, client.getApplicationPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, false));
    Assert.assertEquals(propMap, client.getProgramPreferences(Constants.DEFAULT_NAMESPACE, FakeApp.NAME, "flows",
                                                              FakeApp.FLOWS.get(0), false));
  }

  @Test(expected = IOException.class)
  public void testInvalidNamespace() throws Exception {
    client.setNamespacePreferences("somespace", ImmutableMap.of("k1", "v1"));
  }

  @Test(expected = IOException.class)
  public void testInvalidApplication() throws Exception {
    client.getApplicationPreferences("somespace", "someapp", true);
  }
}
