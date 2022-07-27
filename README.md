# 📎 LinkLip-Server
> 어떻게 하면 다양한 형태의 순간적인 정보를 간편하게 저장하고 체계적으로 관리할 수 있을까? <br>
> 여러분들의 효율적인 정보 관리를 위한 최고의 수단, 링클립입니다.

## 🧑🏻‍💻 Contributor

| 김영권 | 권준우 |
| :---: | :---: |
| <img src="https://avatars.githubusercontent.com/u/39653584?v=4" width="200px" height="200px"> | <img src="https://avatars.githubusercontent.com/u/39055981?v=4" width="200px" height="200px"> |
| [youngkwon02](https://github.com/youngkwon02) | [top7578](https://github.com/top7578) |

---

## ✡️ Pre-Install
- Open JDK 11 </br>
- Maria Database 10.8.3

## 💥 How to install

### 1. Install Maria DB
#### - Mac OS (OS X)
```shell
brew install mariadb
brew services start mariadb
```

#### - Windows (download from below link)
```text
https://mariadb.org/download/?t=mariadb&o=true&p=mariadb&r=10.8.3
```

### 2. Create database user
``` shell
sudo mariadb-secure-installation
```
```text
❗️ 로컬 Database 계정 정보
- ID: root
- PW: 1234
```

### 3. Clone this project
```shell
git clone https://github.com/SoftwareMaestro-LinkLip/LinkLip-Server.git
```

### 4. Run server
```shell
# Base URL: http://127.0.0.1:8080
./gradlew bootRun
```
---



## 📝 Dependency

 ```java
 dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
	implementation group: 'io.springfox', name: 'springfox-swagger-ui', version: '2.9.2'
	implementation group: 'io.springfox', name: 'springfox-swagger2', version: '2.9.2'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
 ```

## 💠 ERD
<img width="1508" alt="Screen Shot 2022-07-27 at 4 02 22 PM" src="https://user-images.githubusercontent.com/39653584/181182856-31c8952f-cccd-4ab1-9800-cd753cc70a93.png">

