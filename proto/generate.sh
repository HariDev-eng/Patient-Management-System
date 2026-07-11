#!/bin/bash

set -e

echo "Generating Java classes..."
mvn clean install -DskipTests

echo "Generating Go protobufs..."
protoc \
  -I=src/main/proto \
  --go_out=gen \
  --go-grpc_out=gen \
  --go_opt=paths=source_relative \
  --go-grpc_opt=paths=source_relative \
  src/main/proto/events/*.proto

echo "Done!"