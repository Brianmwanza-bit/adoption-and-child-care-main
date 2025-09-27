#!/bin/bash
set -e

# Load environment variables from .env
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
fi

# Build Docker image
docker build -t adoption-backend .

# Stop and remove existing container if running
if [ $(docker ps -aq -f name=adoption-backend) ]; then
  docker stop adoption-backend || true
  docker rm adoption-backend || true
fi

# Run new container

docker run -d \
  --name adoption-backend \
  -p 50000:50000 \
  -e DB_HOST=$DB_HOST \
  -e DB_USER=$DB_USER \
  -e DB_PASS=$DB_PASS \
  -e DB_NAME=$DB_NAME \
  -e JWT_SECRET=$JWT_SECRET \
  -e PORT=50000 \
  adoption-backend

echo "Backend deployed and running on port 50000." 