################################################################################
# Copyright (C) 2018 Eclipse Foundation
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
################################################################################

FROM nginx:1.15.1-alpine

ENV ECOVIZ_API_URL '/api'

COPY ./config/nginx.conf /etc/nginx/conf.d/default.conf
                                  
## Remove default nginx website
RUN rm -rf /usr/share/nginx/html/*

ADD ./docker/entrypoint.sh /
COPY ./dist /usr/share/nginx/html

CMD ["/entrypoint.sh"]
