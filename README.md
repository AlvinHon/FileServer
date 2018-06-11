# SimplySer
A Simple file transfer http server to provide file downloads/uploads at local filesystem. It focus on providing simpler operations to download/upload file, such as by draging file for uploading file.

Most common technology for file storage is a Samba [Samba](https://en.wikipedia.org/wiki/Samba_(software)) File Network Server. Hence for users do not setup a samba file server, this SimplySer might serve your purpose simply and quickly.


## snapshot
![alt text](https://github.com/AlvinHon/SimplySer/blob/master/preview.png "SimplySer Preview")

## Technologies

* Spring Boot
* Java 1.8
* Maven
* JQuery
* Bootstrap CSS
* React js, Babel js

## Build

Maven is used for building Spring Boot application.

```sh
maven package -Dmaven.test.skip=true
```

Output .jar file (simplyser-x.x.x-SNAPSHOT.jar) is located at target folder.

To deploy the application, below is the file structure:

```
root/
src/
  main/
    webapp/
      resources/
        (..)
simplyser-x.x.x-SNAPSHOT.jar
```

Files src/main/webapp/resources/* are required for the frontend web page. Some JS files are external libraries downloaded via CDN (Bootstrap, JQuery, ReactJS, BabelJS). Just for convenience, those files are predownloaded for running the application locally. Please also consider using cdn links (in index.html) instead of local resources.

root/ folder is your root filesystem folder for the file server. 


and run:

```
java -jar simplyser-x.x.x-SNAPSHOT.jar
```

## Configure

File src/main/resources/application.properties defined the server related information.

```
server.address=localhost
server.port=8080
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

