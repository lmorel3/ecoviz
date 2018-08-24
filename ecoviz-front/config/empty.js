/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
module.exports = {
  hmrModule: function(ngmodule) {
    return ngmodule;
  },
  NgProbeToken: {},
  HmrState: function() {},
  _createConditionalRootRenderer: function(rootRenderer, extraTokens, coreTokens) {
    return rootRenderer;
  },
  __platform_browser_private__: {}
};
