Spring Boot Application Properties

In order to externalize these from the jar file we don't want to store this in the src/main/resources folder,
so that means we need to have them somewhere not on the classpath, and use the following jvm parameter when starting the application

--spring.config.location=${project_loc}/config/


Authenticate and acquire token
curl -X POST -H "Content-Type: application/json" -d "{\"username\" : \"CXA1\", \"password\" : \"admin\" }" localhost:9002/crm/authenticate
curl -X POST -H "Content-Type: application/json" -d "{\"username\" : \"SXA2\", \"password\" : \"admin\" }" localhost:9002/crm/authenticate

example
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTU4Njk4NDkxNiwiaWF0IjoxNTg2OTY2OTE2fQ.ZhW1GKhk0jTHVOjOjJdBxlsVrrSq7QNf1-3abDCCRqJPw9ALCDMhvAuKboPV1VHnN5zA47YIxMP39nteP_Ex9Q

To shutdown the application we use the actuator end point with a POST call
curl -X POST -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTWEEyIiwiZXhwIjoxNTg3MDAyNTgwLCJpYXQiOjE1ODY5ODQ1ODB9.-r2P97l_MwA1NKodBGl8PMrDYl8E0vcDCqL-6AmCCyTPF-3XLhlnLevQ_mZ8JeMIGKyinjDpUPZ507POM-YnNg" localhost:9003/actuator/shutdown



Run server from windows command
switch to power shell

Start-Job -ScriptBlock {
  & java -jar crm-graphql-spring-boot-jwt-server-1.0.0-SNAPSHOT-bootable.jar >logs\node1.out 2>logs\node1.err
}

redirect stdout, stderr to a file 
1> file.log 2>&1

tail file in windows power shell
Get-Content <file> -Wait

