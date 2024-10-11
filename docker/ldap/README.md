# Docker OpenLDAP
This docker image is based on Bitnami OpenLDAP git repository: [https://github.com/bitnami/bitnami-docker-openldap.git](https://github.com/bitnami/bitnami-docker-openldap.git)

Two patches have been added (and accepted as pull request) to be compatible with European Commission LDAP.




## Usage
### 1.Image Configuration
The OpenLDAP image can be configured in ["docker-compose.yml"](./docker-compose.yml) file :
```
services:
  openldap:
    image: docker.io/bitnami/openldap:2.6
    container_name: openldap
    privileged: true
    ports:
      - '389:1389'
      - '1636:1636'
    environment:
      - LDAP_ADMIN_USERNAME=admin
      - LDAP_ADMIN_PASSWORD=adminpassword
      - LDAP_ROOT=dc=cec,dc=eu,dc=int
     
    volumes:
      - 'openldap_data:/bitnami/openldap'
      - ./imports/ldifs:/ldifs
      - ./imports/schema/cudperson.ldif:/schema/custom.ldif
volumes:
  openldap_data:
    driver: local
```
In this example :
- The LDAP root is : `dc=cec,dc=eu,dc=int` 
- The admin dn is : `cn=admin,dc=cec,dc=eu,dc=int`
- The admin password is : `adminpassword`
- The imported data are in ldif files located in : `./imports/ldifs` directory
- The custom EC LDAP schema is located at `./imports/schema/cudperson.ldif`
- The container is exposed on port 389 (and on port 1389 inside Docker network)
- The container stores data in a Docker `openldap_data` volume


Please refer to ["bitnami README.md"](https://github.com/bitnami/containers/blob/main/bitnami/openldap/README.md) for other LDAP configuration options.  

### 2.Launching a docker instance.

Execute the command `docker-compose up` to launch the docker container.
  
The OpenLDAP container automatically imports schemas and sample data at startup.

You can use an LDAP tool like Apache Directory Studio to create/update/delete manually entries or to import whole ldif files.

## Configurating your application
Basic LDAP configuration that targets LDAP should be defined in one of your properties config file (spring.properties, alfresco.properties ...).

Here are some sample values to use the OpenLDAP container :
```
#LDAP Configuration
context.provider_url_ecas = ldap://ec2_openldap_hostname:389/ou=AuthenticationDomains,dc=cec,dc=eu,dc=int
context.provider_url = ldap://ec2_openldap_hostname:389/ou=People,dc=cec,dc=eu,dc=int
context.security_authentication=simple
context.security_principal=cn=admin,dc=cec,dc=eu,dc=int
context.security_credentials=adminpassword
```
.


## Differences bewteen OpenLDAP and SUN Iplanet LDAP
There are some differences between OpenLDAP and SUN Iplanet.

Basically the rootDN, the DN of organizational unit (ou) as well as the syntax of user defined schemas (custom schema or objectClass) are a bit different.

### 1/The rootDN syntax
- SUN LDAP: o=cec.eu.int
- OpenLDAP: dc=cec, dc=eu, dc=int

### 2/Organizational Unit
When we specify the parent node, we have to take account the change in the rootDN syntax

- SUN: 
    - dn: ou=AuthenticationDomains,o=cec.eu.int
    - dn: ou=People,o=cec.eu.int

- OpenLDAP:
    - dn: ou=AuthenticationDomains,dc=cec,dc=eu,dc=int
    - dn: ou=People,dc=cec,dc=eu,dc=int

### 3/User defined schemas
Circabc application needs to access 2 types of organizational units: People and AuthenticationDomains.

People contains the actual users of the LDAP and AuthenticationDomains is the list of domains for which we allow access to the application (eg: ec.europa.eu).

- Users of People use a custom schema called cudperson that is already converted and available in the schema directory.

The attribute *owner* is not recognized in OpenLDAP and should be removed.

- Instances of AuthenticationDomains use a custom schema called ecAuthenticationDomain that is not available for OpenLDAP.

For OpenLDAP, we will replace the definition of objectClass from
```
objectClass: ecAuthenticationDomain
objectClass: organization
objectClass: top 
```
to
```
objectClass: cudperson
objectClass: inetorgperson
objectClass: organizationalperson
objectClass: person
objectClass: top
```

We will also have to add attribute *sn* (same value as CN and o) and remove attribute *teletexTerminalIdentifier*

In import directory, you will find 2 files:
- `People.ldif` contains the 11 members of circabc team exported from CEDLdap and converted for OpenLDAP format
- `AuthenticationDomains.ldif` contains all of the 61 AuthenticationDomains exported from CEDLdap and converted for OpenLDAP format
