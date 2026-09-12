#!/bin/bash
# deploy.sh — build, tag with git SHA, push, then deploy to k8s.
# Run from the REPO ROOT (the folder that contains analytics-service/,
# api-gateway/, auth-service/, ..., proto/, and k8s/).
#
# Usage:
#   ./scripts/deploy.sh            # build all services + deploy
#   ./scripts/deploy.sh --no-build # skip docker build/push, just re-apply k8s
set -euo pipefail

DOCKER_USER="haripradap09"
K8S_DIR="k8s"
NAMESPACE="patient-management"

# service (k8s Deployment name) -> Dockerfile path, relative to repo root
declare -A SERVICES=(
  [analytics-service]="analytics-service/Dockerfile"
  [api-gateway]="api-gateway/Dockerfile"
  [appointment-service]="appointment-service/Dockerfile"
  [auth-service]="auth-service/Dockerfile"
  [billing-service]="billing-service/Dockerfile"
  [diagnosis-service]="diagnosis-svc/Dockerfile"
  [doctor-svc]="doctor-svc/Dockerfile"
  [inventory-service]="inventory-service/Dockerfile"
  [nurse-service]="nurse-service/Dockerfile"
  [patient-service]="patient-service/Dockerfile"
  [prescription-service]="prescription-svc/Dockerfile"
)

# --- 1. figure out the tag ---
GIT_SHA=$(git rev-parse --short HEAD)
if [ -n "$(git status --porcelain)" ]; then
  echo "WARNING: you have uncommitted changes. Tagging as ${GIT_SHA}-dirty"
  echo "         (commit first if you want a tag that maps to a real commit)"
  GIT_SHA="${GIT_SHA}-dirty"
fi
echo "==> Using tag: ${GIT_SHA}"

# --- 2. build + push (skippable with --no-build) ---
if [[ "${1:-}" != "--no-build" ]]; then
  for name in "${!SERVICES[@]}"; do
    dockerfile="${SERVICES[$name]}"
    image="${DOCKER_USER}/${name}:${GIT_SHA}"
    echo "==> Building ${image}  (dockerfile: ${dockerfile})"
    docker build -f "${dockerfile}" -t "${image}" .
    echo "==> Pushing ${image}"
    docker push "${image}"
  done
else
  echo "==> --no-build passed, skipping docker build/push"
fi

# --- 3. point every deployment at the new tag, in one place ---
echo "==> Updating ${K8S_DIR}/kustomization.yaml to tag ${GIT_SHA}"
sed -i.bak "s/newTag: .*/newTag: ${GIT_SHA}/" "${K8S_DIR}/kustomization.yaml"
rm -f "${K8S_DIR}/kustomization.yaml.bak"

# --- 4. apply ---
echo "==> Applying manifests"
kubectl apply -k "${K8S_DIR}/"

# --- 5. wait for rollout, fail loudly if something doesn't come up ---
echo "==> Waiting for rollouts"
for name in "${!SERVICES[@]}"; do
  kubectl rollout status "deployment/${name}" -n "${NAMESPACE}" --timeout=180s
done
kubectl rollout status deployment/postgres -n "${NAMESPACE}" --timeout=180s
kubectl rollout status deployment/kafka -n "${NAMESPACE}" --timeout=180s

echo "==> Done. Deployed tag: ${GIT_SHA}"
kubectl get pods -n "${NAMESPACE}"