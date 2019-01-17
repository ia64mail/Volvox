# Volvox
This repo contains test project to learn Akka micro-services and Kubernetes visualization platform.

## Local setup notes
Install Java10, Docker, VirtualBox, Kubernetes CLI and Minikube.

Hints:
 - [Manage Docker as a non-root user](https://docs.docker.com/install/linux/linux-postinstall/)
 - Use following command to `eval $(minikube docker-env)` to reuse Munikube’s built-in Docker daemon; 
   see more details at [Reusing the Docker daemon](https://kubernetes.io/docs/setup/minikube/#reusing-the-docker-daemon)
 - Access to Minikube Dashboard using `minikube dashboard`
 - [Configure IDE](https://doc.akka.io/docs/akka/2.5/additional/ide.html) to avoid ambiguous Java/Scala imports  
 
## Build notes

Local build requires `lighthouse` project as an entry point ot the cluster with fixed IP address - `127.0.8.1`. 

Other systems in the cluster are assigned randomly to IP in the range `127.0.8.64/26` with a standard port `25520`.

Run build with following command 
```cmd
./gradlew build
```

##### Build docker image

Kubernetes requires pre-constructed docker image for deployment. 
Moreover, cluster discovery mechanism is based on the Kubernetes API and implemented with 
[Akka Cluster Bootstrap](https://developer.lightbend.com/docs/akka-management/current/bootstrap/index.html).   
    
Run build with following command 
```cmd
./gradlew lighthouse:dockerBuildImage
```
Resulted image should appears in the Munikube’s built-in Docker repository
```txt
> docker images
REPOSITORY                                 TAG                 IMAGE ID            CREATED             SIZE
volvox/stub-actor                          0.0.0.0             ed79c9dbaf3f        5 seconds ago       526MB
volvox/lighthouse                          0.0.0.0             e26af549b4fd        17 seconds ago      526MB
...
```

##### Manage build versions

To issue a new SVN tag and push it into repo use following command

```cmd
./gradlew build reckonTagPush -Preckon.scope=<major|minor|patch> -Preckon.stage=<alpha|beta|rc|final>
```

See more details at [How Reckon Works](https://github.com/ajoberstar/reckon/blob/master/docs/index.md).

## Deployment notes

TBD with custom Grunt task!
 
Hints:
 - Create new deployment
   ```cmd
   kubectl apply -f ./kubernetes/controllers/XXX-deployment.yaml
   ``` 
 - Delete existing deployment
   ```cmd
   kubectl delete deployment/XXX-deployment
   ```
 - Get stdout logs from the specified XXX pod
   ```cmd
   kubectl logs XXX -f
   ```       
 - Login to bash onto the specified XXX pod
   ```cmd
   kubectl exec -it XXX -- /bin/bash
   ```       
 - Login onto minikube bash
   ```cmd
   minikube ssh
   ```       
 
See more details at [Kubernetes Deployments](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/). 
 