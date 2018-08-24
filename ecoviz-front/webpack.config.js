/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

/**
 * Look in ./config folder for webpack.dev.js
 */
switch (process.env.NODE_ENV) {
  case 'prod':
  case 'production':
    module.exports = require('./config/webpack.prod')({env: 'production'});
    break;
  case 'test':
  case 'testing':
    module.exports = require('./config/webpack.test')({env: 'test'});
    break;
  case 'dev':
  case 'development':
  default:
    module.exports = require('./config/webpack.dev')({env: 'development'});
}
