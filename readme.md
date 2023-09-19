# READ ME
---

## Run project steps

### Using docker

- go to docker directory
- execute `docker-compose up -d`

### With local tools, production profile

- download and install *JDK 17*
- download and install Mysql server 8.0.33
- download and install *Apache Maven 3.9.1*
- download and install geckondriver with mozilla firefox and create enviromental variable **geckodriver** and set value
  to path to **geckodriver.exe**
- set up your local MySQL database according to _src/main/resources/application-prod.properties_
- execute command - `mvn spring-boot:run -Dspring-boot.run.profiles=prod` from repository root

### Using wrapper, development profile

- execute command`./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`

## Scripts description

- `./start_db.sh -v` starts mysql db with **mysql-volume** in docker, without optional argument `-v` volume will be
  disposable

Note: Maven commands are valid for Unix system, to execute this on Windows system replace `./mvnw` by `mvnw.cmd` in each
command.