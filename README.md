# EUSurvey
EUSurvey is the official online survey management tool of the European Commission. Its development was started in 2013 under the supervision of [DIGIT](https://ec.europa.eu/dgs/informatics/index_en.htm) and is published as open source software under the terms of the EUPL public license. EUSurvey is a servlet based application and can be installed on any servlet container.

## Installation requirements
1. Tomcat 9
1. Java 11
1. MySQL 8.0
1. Maven

Make sure to create a JRE_HOME environment variable that points to the Java installation folder.
Make sure to create a CATALINA_HOME environment variable that points to Tomcat's installation folder.
Make sure to add Maven's bin folder to the PATH environment variable.

## Quick start
Extensive installation guidelines may be found in our [installation guide](https://joinup.ec.europa.eu/sites/default/files/document/2017-08/eusurvey_oss_installation_guide_v1_4_0_1.pdf). We give a summary of installation guidelines here.

### Database initialization
Connect to the database server using a tool like MySQL Command Line Client to execute the following commands:

1. Create eusurvey schema:
``` sql 
CREATE SCHEMA `eusurveydb` DEFAULT CHARACTER SET utf8mb4;
```

2. Create a user which will access this schema;
``` sql 
CREATE USER 'eusurveyuser'@'localhost' IDENTIFIED BY 'eusurveyuserpassword'; 
GRANT ALL PRIVILEGES ON eusurveydb.* TO 'eusurveyuser'@'localhost';
GRANT EVENT ON *.* TO 'eusurveyuser'@'localhost'; 
```

3. Set Mysql variables
``` sql 
SET GLOBAL event_scheduler = ON;
```

### Download source code
Either download EUSurvey's source code as a ZIP archive an extract its content or use git to clone the repository.


### Spring properties modification
Modify the spring properties from src/main/config file to match your requirements.
For the standard OSS version please modify src/main/config/oss.
If you want to use docker, please modify src/main/config/ossdocker.

### Run the application
Build EUSurvey's war using the following command:
``` batch
mvn clean install -Denvironment=oss
```
Copy the war file from target/eusurvey.war to your Tomcat's webapps folder and restart Tomcat to automatically deploy the application. 

## Repository conventions
### Workflow
We apply the [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).

### Commits
For each commit, we ask to add the number of the issue to which the commit is relevant. E.g.  Issue #1245 : Adding css for class .aClassName. 


### Contributing
Please follow the [Forking](https://help.github.com/en/articles/fork-a-repo) workflow.

1. __Open a feature issue for your future changes;__
Over this page, click on the "Issues" button, then click on the "New issue" button and finally create a "Feature Request". Then, detail the feature you would like to implement so that we may discuss the changes to be made in the future Pull Request.

2. __Fork the original repository;__
When ready to start implementing, click on the "Fork" button. This action opens your personnal _fork_ of this repository.

3. __Clone your fork repository to your local machine;__
From your repository page, find the "Clone or Download" button, and copy the URL. Then, launch the following commands to clone this repository to your local machine and add our original repository as upstream.
```batch
# clone your repository
git clone https://github.com/<YourRepository>/EUSURVEY.git
git remote add upstream https://github.com/EUSurvey/EUSURVEY.git
```
4. __Create a local branch for your changes;__
Please name your branch following your issue title, followed by the number of this issue. E.g. for Dockerize the application (issue number 20), name your branch "dockerizeTheApplication#20". Run the following command to create this local branch:
``` batch 
# from develop branch
git checkout -b dockerizeTheApplication#20
```

5. __Work on this branch and push your changes to your remote repository;__
Please follow the commit message convention to start the commit message with the issue number as described below. By running the following commands, you may add one or several of the files you changed, commit those, and push these changes to your remote repository, respectively.
``` batch 
# from dockerizeTheApplication#20 branch
git add <changedFile>
git commit -m "Issue #20 : Description of the changes"
git push
```

6. __Fetch the eventual changes from the original develop branch to your local repository's develop branch;__
Before submitting your changes to us, please update your repository with the latests changes performed on the original repository's develop branch. To achieve this, you may run the first command which fetches the latest upstream changes, and the second associated with the third which merge these changes in your local develop branch.
```batch
# Fetch from upstream remote
git fetch upstream

# Checkout your develop branch and merge upstream
git checkout develop
git merge upstream/develop
```

7. __Merge the changes from your develop branch to your local issue branch__
Indeed, you are able to take in account the up changes in your branch, by rebasing all the commits from your develop branch to your development branch.
```batch 
git checkout dockerizeTheApplication#20
git rebase develop
```
Then, push your changes as explained before to your fork repository to be ready for the pull request!

8. __Open a pull request from this feature branch to our repository;__
Finally, from your forked remote repository URL, https://github.com/YourRepository, select the nameOfYourIssue#1 branch and press the "New pull request button". Hence, this will create a pull request to our repository. We will then test your branch, and discuss the pull request together. When accepted, your changes will be inserted into our develop branch by merging your development branch to our develop branch.



