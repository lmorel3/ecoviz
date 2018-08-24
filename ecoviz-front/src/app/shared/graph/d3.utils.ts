/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import * as d3 from 'd3';

export function d3rebind(target, source) {
    // Method is assumed to be a standard D3 getter-setter:
    // If passed with no arguments, gets the value.
    // If passed with arguments, sets the value and returns the target.
    let d3_rebind = function (target, source, method) {
        return function() {
        var value = method.apply(source, arguments);
        return value === source ? target : value;
        };
    }

    var i = 1, n = arguments.length, method;
    while (++i < n) target[method = arguments[i]] = d3_rebind(target, source, source[method]);
    return target;
};

