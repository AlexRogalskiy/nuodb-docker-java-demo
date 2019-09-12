# NuoDB Docker Java Demo

Java demo for using ArgoCD with NuoDB and Kubernetes.

To deploy:

```
argocd app create nuodb-docker-java-demo \
  --repo https://github.com/nuodb/nuodb-docker-java-demo.git \
  --path argocd \
  --dest-server https://kubernetes.default.svc \
  --dest-namespace nuodb
```
