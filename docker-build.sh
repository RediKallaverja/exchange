#!/bin/bash

# Set the name for your Docker image
IMAGE_NAME="exchange-server:latest"

# build image
docker build --tag $IMAGE_NAME .

if [ $? -eq 0 ]; then
  echo "Docker image build successful. Image name: $IMAGE_NAME"
else
  echo "Error: Docker image build failed."
fi

# run docker and expose port 8080
docker run -p8080:8080 $IMAGE_NAME