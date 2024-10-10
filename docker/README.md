# EUSurvey Docker Project

This project provides a Docker-based setup for running the EUSurvey application, along with optional containers for EULogin and OpenLDAP. 

## Prerequisites

- Docker and Docker Compose installed on your machine.
- Access to the GitHub CIRCABC repository (required for EULogin setup).

## First-Time Setup

### 1. Build the Server Archive

Before running the Docker containers, you need to build the server archive:

1. Navigate to the EUSurvey root folder.

2. Run the Maven build command:
   ```bash
   mvn clean install -Denvironment=ossdocker
   ```

3. Copy the generated WAR file to the Docker folder:
   ```bash
   mkdir -p docker/server/dist
   cp target/eusurvey.war docker/server/dist/eusurvey.war
   ```

### 2. Build and Run the Docker Images

1. Build the Docker images:
   From the `docker` folder, run:
   ```bash
   docker-compose build
   ```
2. Create a shared folder for the EUSurvey files:
   ```
   mkdir ~/eusurveytemp
   chmod 777 ~/eusurveytemp
   ```

3. Run the Docker containers:
   ```bash
   docker-compose up -d
   ```
   That will start Tomcat server container running EUSurvey, a MySQL database for persistence and a Dev Email server.

### 3. Access the Application
You can connect to your local EUSurvey instance at [http://localhost:8080/eusurvey](http://localhost:8080/eusurvey/)
- The default admin system account credentials are (you have to click the link "*Log in with a system account*" in the login page):
  - Username: `admin`
  - Password: `adminpw`


## Optional: Run EULogin and OpenLDAP Containers

You can optionally run the EULogin and OpenLDAP containers to support authentication with pre-defined users.

### 1. Run the EULogin Container

1. Log in to Docker with access to the Circabc GitHub repository:
   ```bash
   docker login ghcr.io
   ```
2. Pull the EULogin mockup image:
     ```bash
    docker pull ghcr.io/circabc/circabc_rest/eulogin/mockserver:9.10.6
   ```
  

3. Run the eulogin container:
   ```bash
   cd eulogin
   docker-compose up -d
   ```

4. Access eulogin at [https://eulogin:7002](https://eulogin:7002):
   - First, map `eulogin` to your local IP in your host file (`/etc/hosts` on Linux/Mac or `C:\Windows\System32\drivers\etc\hosts` on Windows).
   - Login with admin credentials:
     - Username: `bournja`
     - Password: `Admin123`

5. Connect to EUSurvey with users defined in your EULogin configuration:
   - Example users:
     - bournja/Admin123
     - chucknorris/Qwerty098

### 2. Run the OpenLDAP Container

1.  Run the OpenLDAP container:
   ```bash
   cd openldap
   docker-compose up -d
   ```

2. Check OpenLDAP with [Apache Directory Studio](https://directory.apache.org/studio/):
   - Connection details:
     - Bind DN or user: `cn=admin,dc=cec,dc=eu,dc=int`
     - Password: `EC1234`


Happy Surveying!
