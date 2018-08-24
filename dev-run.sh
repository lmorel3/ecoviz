#!/bin/bash

################################################################################
# Copyright (C) 2018 Eclipse Foundation
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
################################################################################

if [ $# -eq 0 ]; then
	echo "Usage: $0 back  [debug]"
	echo "          front"
	exit 1;
fi

if [ $1 = "back" ]; then
	# Downnload payara-micro if needed
	wget -nc https://repo.maven.apache.org/maven2/fish/payara/extras/payara-micro/5.182/payara-micro-5.182.jar
	
	cd ./ecoviz-service
	
	# Compile the app
	rm -f ./target/extracted-payaramicro/MICRO-INF/deploy/*
	mvn clean install
	
	cd ..

	# Copy the war archive
	rm -f ROOT.war
	yes | cp -f ./ecoviz-service/target/extracted-payaramicro/MICRO-INF/deploy/*.war ROOT.war
	
	if [ ! -f ROOT.war ]; then
		echo "Unable to find ROOT.war"
		exit 1;
	fi

	# Launch the service
	if [ $# -eq 2 ]; then
		java -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y -jar payara-micro-5.182.jar ROOT.war --port 8083 
	else
		java -jar payara-micro-5.182.jar ROOT.war --port 8083
	fi

elif [ $1 = "front" ]; then
	cd ./ecoviz-front
	npm run server:dev:hmr
else
	exit 1;
fi
