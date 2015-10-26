#!/bin/sh

mvn -U -pl api-image clean install
mvn -U -pl service-image clean package
