#!/bin/bash
# Rebuild the streaming application image from source and restart its container.
# Docker compiles the jar inside the image (see docker/StreamingAppDockerfile),
# so no local JDK or Maven is required. Run this from the repository root.
set -e

docker compose -f docker/ignite-streaming-app.yaml down
docker compose -f docker/ignite-streaming-app.yaml up -d --build
