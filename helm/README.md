### EUSurvey Helm Chart

This Helm chart facilitates the deployment of the EUSurvey application on Kubernetes, running a Tomcat server and MySQL database. The Tomcat server uses an image specifically customized for the EUSurvey server (see [README in the Docker folder](../docker/README.md)). MySQL is used for the database.

#### Sub-Charts:
- **Tomcat (Server)**:
  - Based on the [Bitnami Tomcat Helm chart](https://github.com/bitnami/charts/tree/master/bitnami/tomcat).
  - As the Bitnami Tomcat Helm chart mounts the Tomcat's folder at startup (and thus overwrites the webapp folder of the EUSurvey image), I had to find a way to deploy the `EUSurvey.war` that is in the custom EUSurvey custom image; an Init Container and a Custom lifecycle hook are used to deploy the WAR file:
    - **Init Container**: Used to prepare the environment before the main container starts. Specifically, it moves the EUSurvey WAR to a temporary `/usr/local/share` location mounted by a shared volume.
    - **Lifecycle Hooks**: Use a post-start hook to move the WAR from the shared volume mounted at `/usr/local/share` into the `webapps` directory of Tomcat, ensuring the application starts serving immediately after deployment.
- **MySQL (Database)**: Utilizes the [Bitnami MySQL Helm chart](https://github.com/bitnami/charts/tree/master/bitnami/mysql). Preconfigured to support the requirements of EUSurvey, including MySQL configuration and SQL initialization setup.

#### Installation:
To install this chart into your Kubernetes cluster, follow these steps:

1. Add the Helm repository for Bitnami if not already added:
   ```bash
   helm repo add bitnami https://charts.bitnami.com/bitnami
   helm repo update
   ```

2. Install the chart with:
   ```bash
   helm dependency update
   helm install eusurvey ./eusurvey
   ```

#### Accessing the Application:
If you install the application locally on Minikube, the server can be accessed through an Ingress at `http://eusurvey/eusurvey`. Before accessing the URL, update your `/etc/hosts` file to point `eusurvey` to your Minikube IP.

Example entry for `/etc/hosts`:
```
192.168.49.1 eusurvey
```


#### Link to EULogin / OpenLDAP service
The installed server can be used with optional EULogin CAS Authentication and OpenLDAP mockup services. See [README in the Docker folder](../docker/README.md).

#### TODO
This is an alpha version of the chart, and some optimizations need to be made:
- Optimize the size of the initContainer with a lighter custom image.
- HTTP Sessions: if we kill a container, we lose the session. An implementation with shared Http Session in a remote distributed location is needed (Redis, Database ...).
- MySQL version 8.0.37 requires some configurations to remain compatible, but if we used the new standards from 8.4+, it would be more secure (caching_sha2_password + RSA instead of mysql_native_password without SSL).
- EUSurvey seems to use system fonts for captcha, which are not present on the Kubernetes node. Therefore, I have added them to the Docker image.
- Test file system persistence.
- Test DB persistence.
- Test Tomcat replication.
- Test database replication with master/slave.
