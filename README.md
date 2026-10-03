# 通讯录 Web 版（contacts-web）

一个多用户通讯录：注册 / 登录 / 登出，每个用户只能增删改查**自己的**联系人。

## 技术栈

| 层 | 用了什么 |
|---|---|
| 框架 | Spring Boot 4.1.1（内嵌 Tomcat，无需另装） |
| 持久层 | MyBatis（`mybatis-spring-boot-starter` 4.1.0）+ MySQL 8 |
| 密码 | Spring Security 的 `BCryptPasswordEncoder`（只引 `spring-security-crypto`，不引整个 security starter，避免它的默认过滤器与手写登录态打架） |
| 登录态 | 手写 `Session` + `jakarta.servlet.Filter` |
| 前端 | 原生 HTML / CSS / JavaScript + `fetch`（无前端框架、无 CDN，离线可用） |

## 环境要求

- **JDK 25**（`pom.xml` 里 `<source>` / `<target>` = 25）
- **MySQL 8**
- **Maven**（或用仓库自带的 `mvnw`，无需另装）

## 快速开始

### 1. 建库建表

```sql
CREATE DATABASE tongxunlu DEFAULT CHARSET utf8mb4;

USE tongxunlu;

CREATE TABLE `users` (
  `id`         int NOT NULL AUTO_INCREMENT,
  `username`   varchar(50) NOT NULL,
  `password`   varchar(100) NOT NULL,          -- BCrypt 哈希（定长 60 字符）
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `contacts` (
  `id`         int NOT NULL AUTO_INCREMENT,
  `user_id`    int NOT NULL,
  `name`       varchar(20) NOT NULL,
  `phone`      char(11) NOT NULL,
  `landline`   varchar(20) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contacts_user_phone` (`user_id`,`phone`),
  CONSTRAINT `fk_contacts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

> **不用手动插账号**：项目自带注册页，启动后注册一个即可（见第 4 步）。

### 2. 配置数据库密码（不要写进代码）

`src/main/resources/application-dev.yml` 里用的是环境变量占位符：

```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
```

所以要在运行环境里先设这个变量：

- **命令行（PowerShell）**：`$env:DB_PASSWORD = '你的数据库密码'`（在启动项目的**同一个窗口**里设置）
- **IDEA**：Run → Edit Configurations → Environment variables 里加一条 `DB_PASSWORD=你的数据库密码`

> ⚠️ **没设会怎样**：项目**照样能启动**（数据源是懒连接，启动时不校验），但**一登录就失败**。
> 所以"能起来"不等于"连得上库"。

### 3. 启动

```bash
# 方式一：已装 Maven
mvn spring-boot:run

# 方式二：用仓库自带 wrapper（首次运行需联网下载 Maven）
mvnw.cmd spring-boot:run        # Windows
./mvnw spring-boot:run          # macOS / Linux
```

### 4. 访问

浏览器打开 **http://localhost:9090/api/register.html**

注册一个账号 → 直接进入联系人页（注册成功即自动登录）→ 可以增 / 删 / 改 / 查自己的联系人。

> 端口 `9090`、context-path `/api` 都在 `application-dev.yml` 里。

## 接口一览

| 方法 | 路径 | 作用 | 需要登录态 |
|---|---|---|---|
| POST | `/api/register` | 注册（成功后自动登录，下发 `JSESSIONID`） | 否 |
| POST | `/api/login` | 登录 | 否 |
| POST | `/api/logout` | 登出（使 session 失效） | 是 |
| GET | `/api/contacts` | 当前用户的全部联系人 | 是 |
| GET | `/api/contacts/{id}` | 单个联系人 | 是 |
| POST | `/api/contacts` | 新增联系人 | 是 |
| PUT | `/api/contacts/{id}` | 修改联系人（全量覆盖） | 是 |
| DELETE | `/api/contacts/{id}` | 删除联系人 | 是 |

**状态码约定**

- 未登录 → `401`（由 Filter 拦下，不进入 Controller）
- 单条接口「id 不存在」或「不属于当前用户」→ `404`（对外表现一致，不泄露数据是否存在）
- 业务失败但请求合法 → `200` + `{"status":"fail","cause":"..."}`

## 两个设计要点

1. **身份只从 session 取，绝不接受前端传的 `user_id`**
   `POST` / `PUT` 时服务端拿到对象后，**无条件覆盖** `userId`（`contact.setUserId(currentUserId)`）。
   原因：`@RequestBody` 反序列化出来的字段**完全由客户端决定**，前端不传 ≠ 攻击者不传（攻击者可以直接 curl 一个 JSON）。任何"看前端脸色再决定填不填"的条件写法都会开一个越权口子。

2. **归属校验靠 SQL 兜底**
   带 `{id}` 的接口，`where` 子句里都带 `user_id`（如 `where id=#{id} and user_id=#{userId}`），查不到就 `404` ——「不存在」与「不是你的」对外**看起来完全一样**。

## 目录结构

```
src/main/java/org/example/springboot/
├── controller/   LoginController / RegisterController / LogoutController / ContactController
├── service/      UserService / ContactService（归属校验收口在这里）
├── dao/          FromUsers（users 线的 Dao 层）
├── mapper/       UserMapper / ContactMapper（MyBatis 接口）
├── entity/       User / Contact / SessionKeys
├── filter/       LoginFilter（拦 /contacts/*）
└── config/       EncoderConfig / FilterConfig

src/main/resources/
├── static/       前端页面：login.html / register.html / contacts.html + js/ + css/
├── org/example/springboot/mapper/*.xml   MyBatis SQL 映射
└── application*.yml
```

## 已知限制

- 单机部署、无 HTTPS、无验证码、无登录失败次数限制
- 一个联系人只支持「手机 + 座机」两个号码，不支持多号码（那时才需要拆表）
- 无单元测试覆盖（目前只有一个 Spring 上下文启动测试）
- `spring.profiles.active` 默认 `dev`；`application-prod.yml` 目前只有端口与 context-path
