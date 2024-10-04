# EU Login Mock Server Setup

## Overview

This configuration sets up the EU Login 9.10.6 Mock Server using Docker. This mock server can be useful for testing authentication systems against a simulated EU Login environment.

## Prerequisites

- Docker and Docker Compose installed on your machine.
- Access to the GitHub Docker repository, requiring a personal access token for authentication.

## Configuration Steps

### 0. Prepare Host Directories

Before starting the Docker Compose, create the necessary directories on your host to ensure proper volume mapping:

```bash
mkdir -p /path/on/host/tmp
mkdir -p /path/on/host/files
mkdir -p /path/on/host/surveys
mkdir -p /path/on/host/users
mkdir -p /path/on/host/archive
sudo chmod -R 777 /path/on/host
```

### 1. Authenticate to GitHub Docker Repository

Before pulling the Docker image, authenticate to the GitHub Docker repository using your personal access token that has "Package Write" access rights. Run the following command in your terminal:

    docker login ghcr.io -u USERNAME

Replace `USERNAME` with your GitHub username and provide your Personal Access Token as password.

### 2. Configure Hosts File

To ensure the Docker container is accessible via the hostname `eulogin`, add the following entry to your hosts file:

    127.0.0.1 eulogin

### 3. Configure Mock Server Users

If you need to add users to the mock server, update the configuration files located in:

    docker/eulogin/eulogin-mockup-config

### 4. Running the Server

Navigate to the directory containing your `docker-compose.yml` file, 
update the location of the eulogin config (`<path_to_eulogin_config>`)

```yaml
version: '3.8'

services:
  eulogin-mockserver:
    image: eulogin/mockserver:9.10.6
    container_name: eulogin-mockserver
    hostname: eulogin
    extra_hosts:
      - "eulogin:127.0.0.1"
      - "host.docker.internal:host-gateway"
    ports:
      - "7001:7001"
      - "7002:7002"
      - "7003:7003"
    volumes:
      - /<path_to_eulogin_config>/eulogin-mockup-config:/data/ecas-mock-server-shared
    stdin_open: true
    tty: true

```

and start the service:

    docker-compose up



### 5. Accessing the EU Login Mock Server

After the server starts, you can access the EU Login Mock Server at:

    https://eulogin:7002

Admin user is :
```
username : bournja 
password : Admin123
```

Some regular users are :
```
| Username   | Password            |
|------------|----------------------|
| chucknorris| Qwerty098            |
| lsalander  | dragon_tattoo        |
| jb007      | shaken_not_stirred   |
| jackbauer  | !CTU4Ev3r$#@         |

```
## Ports Used

- `7001`: Port 7001
- `7002`: Port 7002 (main access port)
- `7003`: Port 7003
