#!/bin/sh

################################################################################
# Copyright (C) 2018 Eclipse Foundation
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
################################################################################

cd /usr/share/nginx/html

main=$(ls | grep main.*.bundle.js)

echo $ECOVIZ_API_URL

# Replace API endpoint by the one provided
sed -e  "s|ECOVIZ_API_URL|$ECOVIZ_API_URL|" -i $main

# Runs Nginx
exec nginx -g 'daemon off;' "$@"
