# Kubernetes manifests

This folder contains Kubernetes manifests for running the IMDG chatbot stack in a local development environment. 
- The primary purpose of these resources is to provide a simple baseline deployment for Minikube
- This can be used for Helm packaging and be reused in other kubernetes distribution on different cloud platform, with minimal changes

## Purpose

These manifests are intended to help you:

- deploy the UI and server services locally,
- provision supporting infrastructure such as Milvus, monitoring, and certificate management,
- validate the application topology before moving to a more advanced or cloud-based environment.

## Folder overview

- imdg-server/: deployment, service, configmap, and RBAC resources for the backend service
- imdg-chatbot-ui/: deployment, service, configmap, and RBAC resources for the Streamlit UI
- milvus/: basic Milvus deployment and supporting resources
- argocd/: Argo CD manifests and values
- cert-manager/: certificate manager manifests and values
- grafana/: Grafana deployment and related configuration
- prometheus/: Prometheus configuration and related resources
- namespace.yaml: namespace definitions for the stack
- command.txt: example Helm and kubectl commands used during setup

## Usage

A typical local workflow is:

```bash
minikube start --cpus 4 --memory 8192
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/imdg-server/
kubectl apply -f k8s/imdg-chatbot-ui/
kubectl apply -f k8s/milvus/
```

You can then inspect the resources with:

```bash
kubectl get pods -A
kubectl get svc -A
```
