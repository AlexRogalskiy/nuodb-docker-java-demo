# NuoDB Docker Java Demo

Java demo for using ArgoCD with NuoDB and Kubernetes.

To create a container:

1. In the project root run `mvn package` - this creates an executable jar in `docker/`
2. Change to that directory: `cd docker`
3. Create a container: `docker build -t nuodb-docker-java-demo:1.0 .`

To deploy:

```
argocd app create nuodb-docker-java-demo \
  --repo https://github.com/nuodb/nuodb-docker-java-demo.git \
  --path argocd \
  --dest-server https://kubernetes.default.svc \
  --dest-namespace nuodb
```

The `--path` option defines where to find the YAML files to use for the deployment - in this case in the `argocd/` of this project.
