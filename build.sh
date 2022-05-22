#!/bin/sh
./mvnw clean package && docker build -t mangila/spring-security-restful . && docker push mangila/spring-security-restful