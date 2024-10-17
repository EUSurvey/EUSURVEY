

### EUSurvey Helm Chart

This Helm chart facilitates the deployment of the EUSurvey application on Kubernetes, running multiple Tomcat servers, a MySQL database, and a Redis service for HTTP sessions management. The chart uses PVCs for file persistence and K8s secrets for configuration.

#### Sub-Charts:

- **Tomcat Servers**:
  - The EUSurvey application consists of several Tomcat instances : 
     - There is one main instance (deployed as a StatefulSet) serving user requests, 
     - and three additional instances: **task**, **taskldap**, and **tasktodo**, each with specific properties. These task servers are configured via separate Helm charts (using `tomcat-task`, `tomcat-ldap-task`, and `tomcat-todo-task`), and they start after the main EUSurvey instance has successfully started.
   - Each instance requires unique properties, which are managed and injected during deployment (see the lifecycle hooks and Init Containers section below).

  - **WAR Deployment**:
    - The WAR file deployment is handled by Init Containers and post-start lifecycle hooks. This ensures the WAR is properly extracted, and configuration files (such as `spring.properties` and `ehcache.xml`) are injected into the web application during startup.
  
- **MySQL (Database)**:
  - Based on the [Bitnami MySQL Helm chart](https://github.com/bitnami/charts/tree/master/bitnami/mysql), with a custom database and user setup for EUSurvey.

- **Redis (Session Management)**:
  - Redis is used to manage session persistence across the Tomcat instances, ensuring users' sessions are maintained even in the event of pod restarts.

#### Secrets:
- **Spring Property Files**:
  - Properties from `spring.properties` and `ehcache.xml` are injected at deployment time. These files are customized based on the deployment environment and handled via Init Containers, which copy the necessary files into the `WEB-INF` directory of the Tomcat server.
  
- **Config Injection**:
  - The Init Containers handle the correct configuration is applied to task servers specific properties like `host.executing.task`, `host.executing.ldaptask`, and `host.executing.todotask`.

#### Persistent Volume Claims (PVCs):
- PVCs are used for persistence of files, archives, surveys, and user data. They are defined in the templates directory. PVCs refer to the default Storage Class to create PVs (which can be overriden in `values.yaml`).
- Some volumes are shared between multiple pods (such as archives and surveys), while others are unique to individual pods (such as temporary files). 


#### Usage:

To install this chart into your Kubernetes cluster, run the following command, specifying the environment:

```bash
helm install eusurvey --set environment=ossK8s .
```

This will deploy the EUSurvey application along with its required components, including MySQL, Redis, and the various Tomcat instances.

#### Accessing the Application:
- If you install the application locally (e.g., on Minikube), the server can be accessed through an Ingress at `http://eusurvey/eusurvey`. Ensure your `/etc/hosts` file points `eusurvey` to your Minikube IP.

Example entry for `/etc/hosts`:
```
192.168.49.1 eusurvey
```

