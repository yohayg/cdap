/*
 * Copyright © 2018 Cask Data, Inc.
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

import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import BtnWithLoading from 'components/BtnWithLoading';
import T from 'i18n-react';

const PREFIX = 'features.PipelineConfigurations.ActionButtons';

const mapStateToProps = (state, ownProps) => {
  return {
    saveLoading: ownProps.saveLoading,
    saveConfig: ownProps.saveConfig
  };
};

const ConfigModelessSaveBtn = ({saveLoading, saveConfig}) => {
  return (
    <BtnWithLoading
      loading={saveLoading}
      className="btn btn-primary"
      onClick={saveConfig}
      label={T.translate(`${PREFIX}.save`)}
      disabled={saveLoading}
    />
  );
};

ConfigModelessSaveBtn.propTypes = {
  saveLoading: PropTypes.bool,
  saveConfig: PropTypes.func
};

const ConnectedConfigModelessSaveBtn = connect(mapStateToProps)(ConfigModelessSaveBtn);
export default ConnectedConfigModelessSaveBtn;
