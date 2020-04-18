#!/bin/sh

mvn spring-boot:run -Dmain.class=org.elephant.timeserver.TimeServer.GrpcTimeServer
