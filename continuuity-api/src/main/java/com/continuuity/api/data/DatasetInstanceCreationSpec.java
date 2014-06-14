package com.continuuity.api.data;

import com.continuuity.api.dataset.DatasetProperties;

/**
 * Information for creating dataset instance.
 */
public final class DatasetInstanceCreationSpec {
  private final String instanceName;
  private final String typeName;
  private final DatasetProperties props;

  public DatasetInstanceCreationSpec(String instanceName, String typeName, DatasetProperties props) {
    this.instanceName = instanceName;
    this.typeName = typeName;
    this.props = props;
  }

  public String getInstanceName() {
    return instanceName;
  }

  public String getTypeName() {
    return typeName;
  }

  public DatasetProperties getProperties() {
    return props;
  }
}
