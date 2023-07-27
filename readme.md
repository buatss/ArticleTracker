# READ ME
---

## Run project steps
- download and install *JDK 17*
- _(optional)_ download and install *Apache Maven 3.9.1*
- download and install geckondriver with mozilla firefox and create enviromental variable **geckodriver** and set value to path to **geckodriver.exe** 
- set up your local MySQL database according to _src/main/resources/application.properties_
- execute command - `./mvnw spring-boot:run` from repository root

## Scripts description
- `./start_db.sh -v` starts mysql db **mysql-volume**, without optional argument `-v` volume will be disposable with container 

Note: Maven commands are valid for Unix system, to execute this on Windows system replace `./mvnw` by `mvnw.cmd` in each command.