## MysQL

### 配置环境变量

将`C:\Program Files\MySQL\MySQL Server 8.0\bin`（如果安装到了其他目录，填写相应目录的地址即可）添加到环境变量`PATH`中，这样就可以在任意目录的终端中执行`mysql`命令了。

### MySQL服务

（默认开机自动启动，如果想手动操作，可以参考如下命令）

- 关闭：`net stop mysql80`
- 启动：`net start mysql80`

### MySQL常用操作

连接用户名为`root`，密码为`123456`的数据库服务：`mysql -uroot -p123456`

- `show databases;`：列出所有数据库
- `create database kob`;：创建数据库
- `drop database kob;`：删除数据库
- `use kob;`：使用数据库kob
- `show tables;`：列出当前数据库的所有表
- `create table user(id int, username varchar(100))`：创建名称为user的表，表中包含id和username两个属性。
- `drop table user;`：删除表
- `insert into user values(1, 'hong');`：在表中插入数据
- `select * from user;`：查询表中所有数据
- `delete from user where id = 2;`：删除某行数据

### IDEA关联

<img src="配置MySQL与注册登录模块.assets/image-20220721093920837-16591874391831.png" />

<img src="配置MySQL与注册登录模块.assets/image-20220721095410330-16591874391842.png" />

此时MySQL连接成功，并可以在IDEA中通过图形界面修改

![image-20220721095859613](配置MySQL与注册登录模块.assets/image-20220721095859613-16591874391843.png)

## 配置SpringBoot

### 添加依赖

在[Maven仓库地址](https://mvnrepository.com/)中搜索相关依赖

然后在`pom.xml`文件中添加依赖：

- `Spring Boot Starter JDBC`

- `Project Lombok`

  自动资源管理、自动生成 getter、setter、equals、hashCode 和 toString 等

- `MySQL Connector/J`

- `mybatis-plus-boot-starter`

- `mybatis-plus-generator`

  [Mybatis-Plus官网](https://baomidou.com/)

- `spring-boot-starter-security`（暂时不装）

- `jjwt-api`（暂时不装）

以为例`Spring Boot Starter JDBC`为例，复制依赖代码

![image-20220721100258315](配置MySQL与注册登录模块.assets/image-20220721100258315-16591874391844.png)

```xml
<!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-jdbc -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
    <version>2.7.1</version>
</dependency>
```

复制到`pom.xml`中的`dependencies`模块下：

![image-20220721100509553](配置MySQL与注册登录模块.assets/image-20220721100509553-16591874391845.png)

最终添加的`dependency`如下

```xml
<!--Spring Boot Starter JDBC-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
    <version>2.7.1</version>
</dependency>
<!--Project Lombok-->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.24</version>
    <scope>provided</scope>
</dependency>
<!--MySQL Connector/J-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.29</version>
</dependency>
<!--mybatis-plus-boot-starter-->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.2</version>
</dependency>
<!--mybatis-plus-generator-->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-generator</artifactId>
    <version>3.5.3</version>
</dependency>
```

重新加载所有`maven`项目

![image-20220721101330393](配置MySQL与注册登录模块.assets/image-20220721101330393-16591874391846.png)

加载完成之后，就能看到依赖项

![image-20220721101645294](配置MySQL与注册登录模块.assets/image-20220721101645294-16591874391847.png)

### 数据库配置

在`application.properties`中添加数据库配置：

```
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.url=jdbc:mysql://localhost:3306/kob?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

![image-20220721102022625](配置MySQL与注册登录模块.assets/image-20220721102022625-16591874391848.png)

此时运行项目即可

### SpringBoot中的常用模块

- `pojo`层：将数据库中的表对应成`Java`中的`Class`

- `mapper`层（也叫`Dao`层）：将`pojo`层的`class`中的操作（CRUD），映射成`sql`语句

  Create => insert

  Retrieve=> select

- `service`层：写具体的业务逻辑，组合使用`mapper`中的操作

- `controller`层：负责请求转发，接受前端页面过来的参数，传给相应`Service`处理，接到返回值，再传给页面

依次实现这些模块

#### pojo层

首先创建`com/kob/backend/pojo`包，然后在其中创建`User.java`

负责实现与`User`表想对应的`User`类

![image-20220721104508277](配置MySQL与注册登录模块.assets/image-20220721104508277-16591874391849.png)

这里的关键是三个注解

从编译后的`class`文件中，也能证明这一点。

添加注解之前：

![image-20220721104817267](配置MySQL与注册登录模块.assets/image-20220721104817267-165918743918410.png)

添加注解之后

![image-20220721104700989](配置MySQL与注册登录模块.assets/image-20220721104700989-165918743918411.png)

#### mapper层

首先创建`com/kob/backend/mapper`包，然后在其中创建`UserMapper.java`

添加`@Mapper`注解并且继承`mybatisplus`中的`BaseMapper`，传入`<User>`

目的是将`pojo`层的`User`中的操作（CRUD），映射成`sql`语句

![image-20220721105538329](配置MySQL与注册登录模块.assets/image-20220721105538329-165918743918412.png)

#### controller层

为方便调试，在当前阶段，先讲`service`与`controller`写在一块（后期具体业务需要分开，`controller`调用`sevice`中的接口）

创建`com/kob/backend/controller`包

然后针对`User`表创建`/user/UserController.java`

添加`@RestController`注解

```java
@RestController
```

在这里我们可以实现与`User`表相关的业务逻辑（正常应该在`service`层 这里为了方便调试 暂时写在一块了）

`@RequestMapping `将所有请求类型全部接收过来

- 如果只处理`post`类型的请求`@PostMapping`
- 如果只处理`get`类型的请求`@GetMapping`

1）实现查询当前所有用户

在`controller`中如何调用数据库的接口

 首先引入刚刚定义的UserMapper接口

```java
@Autowired
UserMapper userMapper;
```

UserMapper接口由mybatisplus来实现

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

继承了mybatisplus中的BaseMapper

![image-20220721140045933](配置MySQL与注册登录模块.assets/image-20220721140045933-165918743918413.png)

可以通过[Mybatis-Plus官网](https://baomidou.com/)来查看所有API的具体用法

![image-20220721140317544](配置MySQL与注册登录模块.assets/image-20220721140317544-165918743918414.png)

如果我们希望查询所有用户，就要借助`selectList`这个api

```java
@RestController
public class UserController {
    @Autowired
    UserMapper userMapper;

    @GetMapping("/user/all/")
    public List<User> getAll(){
        return userMapper.selectList(null);
    }
}
```

结果运行如下：

![image-20220721142053795](配置MySQL与注册登录模块.assets/image-20220721142053795-165918743918415.png)

与实际数据表中结果一致

![image-20220721142552136](配置MySQL与注册登录模块.assets/image-20220721142552136-165918743918416.png)

2）指定ID查询用户

使用`selectById`api根据ID来查询用户

```java
@GetMapping("/user/{userId}")
public User getUser(@PathVariable int userId){
    return userMapper.selectById(userId);
}
```

结果如下

![image-20220721142514277](配置MySQL与注册登录模块.assets/image-20220721142514277-165918743918418.png)

同时也可以借助`Mybatis-Plus`中的条件构造器，来构造一些自定义的条件，通过对条件进行筛选的方式来过滤出结果。

![image-20220721142953598](配置MySQL与注册登录模块.assets/image-20220721142953598-165918743918417.png)

```java
@GetMapping("/user/{userId}")
public User getUser(@PathVariable int userId){
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", userId);//构造条件
    return userMapper.selectOne(queryWrapper);//过滤
}
```

结果与`selectById`一致。

这里代码中的返回值中的`User`就是所得到的符合条件`pojo`中`User`类的对象。

![image-20220722104627447](配置MySQL与注册登录模块.assets/image-20220722104627447-165918743918419.png)

实际上就是返回数据表中的一行数据。

3）区间查询

同样可以通过`ge`和`le`来进行区间查询

```java
@GetMapping("/user/{userId}/to/{userId2}")
public List<User> getUser(@PathVariable int userId, @PathVariable int userId2){
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.ge("id", userId).le("id",userId2);//构造条件
    return userMapper.selectList(queryWrapper);//过滤
}
```

> 可以无限追加条件，通过`.condition`的方式

例如查询`id>=1`&& `id <=4`的用户

![image-20220721145736086](配置MySQL与注册登录模块.assets/image-20220721145736086-165918743918420.png)

4）插入数据

```java
@GetMapping("/user/add/{userId}/{username}/{password}")
public String addUser (
        @PathVariable int userId,
        @PathVariable String username,
        @PathVariable String password){

    User user = new User(userId, username, password);
    userMapper.insert(user);
    return "Add User Successfully";
}
```

![image-20220722105747351](配置MySQL与注册登录模块.assets/image-20220722105747351-165918743918421.png)

![image-20220722105800471](配置MySQL与注册登录模块.assets/image-20220722105800471-165918743918422.png)

5）删除记录

```java
@GetMapping("/user/delete/{userId}")
public String deleteUser(@PathVariable int userId){
    userMapper.deleteById(userId);
    return "Delete User Successfully";
}
```

![image-20220722110331397](配置MySQL与注册登录模块.assets/image-20220722110331397-165918743918423.png)

![image-20220722110341718](配置MySQL与注册登录模块.assets/image-20220722110341718-165918743918424.png)

## 集成Spring Security

借助`Spring Security`来实现登录认证，再没有判断登录认证的情况下，访问任意界面，均无法访问，并弹出登录界面

在`pom.xml`文件中添加依赖：

- `spring-boot-starter-security`

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
      <version>2.7.1</version>
  </dependency>
  ```

<img src="配置MySQL与注册登录模块.assets/image-20220722111559228-165918743918425.png" />

此时再进行访问任意界面，均无法访问，并弹出登录界面

![image-20220722111838408](配置MySQL与注册登录模块.assets/image-20220722111838408-165918743918426.png)

该页面是`Spring Security`是自己实现的。

### login

默认用户名为`user`

动态生成密码如下：

![image-20220722112149800](配置MySQL与注册登录模块.assets/image-20220722112149800-165918743918427.png)

输入之后就能成功访问

![image-20220722112255525](配置MySQL与注册登录模块.assets/image-20220722112255525-165918743918428.png)

并且在之后的访问过程中，均不需要重新登录。

### 授权验证原理

这涉及到授权验证方式：`session`（之后会使用`jjwt`，`session`是传统的授权与验证方式）

![image-20220722113233626](配置MySQL与注册登录模块.assets/image-20220722113233626-165918743918429.png)

1）登录阶段

登录成功以后，后端生成`SessionID`，将其同时保存在后端数据库与浏览器的`Cookie`中

![image-20220722114408783](配置MySQL与注册登录模块.assets/image-20220722114408783-165918743918430.png)

2）在每次向后端`Springboot`发送请求的同时，会将`SessionID`从`Cookie`中取出同样传送给后端`Springboot`。然后`Springboot`通过向数据库查询判断当前`SessionID`是否存在以及是否过期，如果存在，将有关`SessionID`的信息（包括对应的用户名、过期时间）从数据库中取出，判断是否过期，如果当前`SessionID`没有过期，表示登录成功。如果发现`SessionID`过期或者根本不存在，则返回给用户登录页重新登录。

例如：登录成功后会在`Cookie`中存入`Session`信息

![image-20220722115108943](配置MySQL与注册登录模块.assets/image-20220722115108943-165918743918431.png)

每次向后端请求时，都会取出

![image-20220722115250826](配置MySQL与注册登录模块.assets/image-20220722115250826-165918743918432.png)

如果对Session信息进行篡改或者删除

![image-20220722115330665](配置MySQL与注册登录模块.assets/image-20220722115330665-165918743918434.png)

![image-20220722121136508](配置MySQL与注册登录模块.assets/image-20220722121136508-165918743918433.png)

再次请求时，由于后端在数据库中找不到对应的`SessionID`，返回登录页面

![image-20220722121223519](配置MySQL与注册登录模块.assets/image-20220722121223519-165918743918435.png)

注意：`SessionID`相当于给浏览器颁发的一张临时身份证，之后浏览器在执行业务操作的时候，都要随身携带这个身份证。

### logout

此外，`Spring Security`还自己实现了`logout`界面

> 可以理解为自己是新的controller

![image-20220722112438434](配置MySQL与注册登录模块.assets/image-20220722112438434-165918743918436.png)

退出之后继续回到了最初的界面

![image-20220722112507976](配置MySQL与注册登录模块.assets/image-20220722112507976-165918743918437.png)

### 修改Spring Security

此时登录还只是通过Spring Security提供的默认用户名和随机生成密码，如何通过数据库判断一个其中存储的用户是否登录成功呢？需要修改Spring Security

#### UserDetailsServiceImpl

实现`service.impl.UserDetailsServiceImpl`类，继承自`UserDetailsService`接口，用来接入数据库信息

![image-20220722145332746](配置MySQL与注册登录模块.assets/image-20220722145332746-165918743918438.png)

该`UserDetailsService`api接收一个username，通过该username返回包含用户的用户名和密码的UserDetails接口的实现类的对象（简称UserDetails的实现对象）

首先考虑根据username去数据库中查询对应的user

![image-20220722151902338](配置MySQL与注册登录模块.assets/image-20220722151902338-165918743918439.png)

在处理User是否存在之前，先创建一个UserDetails的实现类：

#### UserDetailsImpl

* 创建`service.impl.utils.UserDetailsImpl`

![image-20220722150951906](配置MySQL与注册登录模块.assets/image-20220722150951906-165918743918540.png)

实现如下：

```java
public class UserDetailsImpl implements UserDetails {

    private User user;
    
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {//是否帐户未过期
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {//是否未被锁定
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {//凭证是否未被过期
        return true;
    }

    @Override
    public boolean isEnabled() {//是否被启用
        return true;
    }
}
```

如果不加显示构造函数，也可以下面这样，自动添加相关的属性和构造函数。

![image-20220722152132786](配置MySQL与注册登录模块.assets/image-20220722152132786-165918743918541.png)

现在继续回到`service.impl.UserDetailsServiceImpl`，填上最后这段代码

![image-20220722152326201](配置MySQL与注册登录模块.assets/image-20220722152326201-165918743918542.png)

此时就可以实现根据数据库中User信息来进行登录，即根据用户的用户名，去查询用户信息，再根据输入的密码判断是否匹配，而不是使用默认的用户名和密码。

-----

不过此时登录会报错

![image-20220722153046526](配置MySQL与注册登录模块.assets/image-20220722153046526-165918743918543.png)

如果直接用明文密码来存储，需要在数据库中加上{noop}标记，代表不需要加密直接判断，就不用到PasswordEncoder

![image-20220722153312703](配置MySQL与注册登录模块.assets/image-20220722153312703-165918743918544.png)

此时再来登录

![image-20220722153427283](配置MySQL与注册登录模块.assets/image-20220722153427283-165918743918545.png)

就成功了 

<img src="配置MySQL与注册登录模块.assets/image-20220722154604750-165918743918546.png" alt="image-20220722154604750" style="zoom: 67%;" />

并且可以访问所有的API

-----

#### 密码的加密存储

上述可以看出，如果密码使用明文，必须声明！

如果我们需要对密码进行加密，实现`config.SecurityConfig`类，用来实现用户密码的加密存储

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

BCryptPasswordEncoder的测试

![image-20220722155757004](配置MySQL与注册登录模块.assets/image-20220722155757004-165918743918547.png)

![image-20220722160024903](配置MySQL与注册登录模块.assets/image-20220722160024903-165918743918548.png)

现在需要将密码改成对应的加密形式，否则的话没法验证通过

![image-20220722161616372](配置MySQL与注册登录模块.assets/image-20220722161616372-165918743918549.png)

可以借助`BCryptPasswordEncoder`的`encode()`方法更新之前数据库中的密码

![image-20220722162142668](配置MySQL与注册登录模块.assets/image-20220722162142668-165918743918550.png)

此时再次登录，就可以成功。	

同时，要在添加阶段就直接存储加密阶段的代码

![image-20220722163222639](配置MySQL与注册登录模块.assets/image-20220722163222639-165918743918551.png)

![image-20220722163442482](配置MySQL与注册登录模块.assets/image-20220722163442482-165918743918552.png)

结果如下：

![image-20220722163801468](配置MySQL与注册登录模块.assets/image-20220722163801468-165918743918553.png)

## 集成jwt验证

### 原理

默认情况下，使用session进行身份验证。但对于前后端分离的情况，可能会出现跨域问题，使用session会变得不方便，用jwt验证会更加容易。

对于给定的url，可以分为两大类：

* 公开可以访问
  * login页面
* 需要授权才能访问

先来看下传统使用session进行身份验证的方式：

<img src="配置MySQL与注册登录模块.assets/image-20220727102042730-165918743918554.png" alt="image-20220727102042730" style="zoom:80%;" />

（1）用户进行登录时，登录成功以后，后端生成`SessionID`，将其同时保存在后端数据库或者内存，和浏览器的`Cookie`中。（后端同样保存了`SessionID`与用户信息`userInfo`的映射关系）

（2）在每次向后端`Springboot`发送请求的同时，会将`SessionID`从`Cookie`中取出同样传送给后端。

（3）对于需要授权访问的url，`Springboot`通过向数据库or内存查询判断当前`SessionID`是否存在以及是否过期，如果存在，将有关`SessionID`的信息（包括对应的用户名、过期时间）从数据库中取出，判断是否过期，如果发现`SessionID`过期或者根本不存在，则返回给用户登录页重新登录。

（4）若当前`SessionID`没有过期，则通过`SessionID`与用户信息`userInfo`的映射关系，将对应的`User`提取到上下文中（在Contoller中就可以通过一些API来拿到`User`)，成功进行授权页面的访问。

为了解决跨域的情景，使用Jwt验证。

![image-20220727145258762](配置MySQL与注册登录模块.assets/image-20220727145258762-165918743918555.png)

优势如下：

* 容易实现跨域

* 不需要在服务器端存储

  对于有多个服务器的情况，就可以实现用一个令牌来登录多个服务器

### 配置

1）在pom.xml文件配置相关依赖

- `jjwt-api`
- `jjwt-impl`
- `jjwt-jackson`

2）添加相关类

- 实现`utils.JwtUtil`类，为`jwt`工具类，用来创建、解析`jwt token`

  ```java
  import io.jsonwebtoken.Claims;
  import io.jsonwebtoken.JwtBuilder;
  import io.jsonwebtoken.Jwts;
  import io.jsonwebtoken.SignatureAlgorithm;
  import org.springframework.stereotype.Component;
  
  import javax.crypto.SecretKey;
  import javax.crypto.spec.SecretKeySpec;
  import java.util.Base64;
  import java.util.Date;
  import java.util.UUID;
  
  @Component
  public class JwtUtil {
      // 有效期14天
      public static final long JWT_TTL = 60 * 60 * 1000L * 24 * 14;
      //秘钥
      public static final String JWT_KEY = "SDFGjhdsfalshdfHFdsjkdsfds121232131afasdfac";
  
      public static String getUUID() {
          return UUID.randomUUID().toString().replaceAll("-", "");
      }
  
      public static String createJWT(String subject) {
          JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
          return builder.compact();
      }
  
      private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
          SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
          SecretKey secretKey = generalKey();
          long nowMillis = System.currentTimeMillis();
          Date now = new Date(nowMillis);
          if (ttlMillis == null) {
              ttlMillis = JwtUtil.JWT_TTL;
          }
  
          long expMillis = nowMillis + ttlMillis;
          Date expDate = new Date(expMillis);
          return Jwts.builder()
                  .setId(uuid)
                  .setSubject(subject)
                  .setIssuer("sg")
                  .setIssuedAt(now)
                  .signWith(signatureAlgorithm, secretKey)
                  .setExpiration(expDate);
      }
  
      public static SecretKey generalKey() {
          byte[] encodeKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
          return new SecretKeySpec(encodeKey, 0, encodeKey.length, "HmacSHA256");
      }
  
      public static Claims parseJWT(String jwt) throws Exception {
          SecretKey secretKey = generalKey();
          return Jwts.parserBuilder()
                  .setSigningKey(secretKey)
                  .build()
                  .parseClaimsJws(jwt)
                  .getBody();
      }
  }
  ```

  

- 实现`config.filter.JwtAuthenticationTokenFilter`类，用来验证`jwt token`是否合法有效，如果验证成功，则将`User`信息注入上下文中

  ```java
  import com.kob.backend.mapper.UserMapper;
  import com.kob.backend.pojo.User;
  import com.kob.backend.service.impl.utils.UserDetailsImpl;
  import com.kob.backend.utils.JwtUtil;
  import io.jsonwebtoken.Claims;
  import org.jetbrains.annotations.NotNull;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
  import org.springframework.security.core.context.SecurityContextHolder;
  import org.springframework.stereotype.Component;
  import org.springframework.util.StringUtils;
  import org.springframework.web.filter.OncePerRequestFilter;
  
  import javax.servlet.FilterChain;
  import javax.servlet.ServletException;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import java.io.IOException;
  
  @Component
  public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
      @Autowired
      private UserMapper userMapper;
  
      @Override
      protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
          String token = request.getHeader("Authorization");
  
          if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
              filterChain.doFilter(request, response);
              return;
          }
  
          token = token.substring(7);
  
          String userid;
          try {
              Claims claims = JwtUtil.parseJWT(token);
              userid = claims.getSubject();
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
  
          User user = userMapper.selectById(Integer.parseInt(userid));
  
          if (user == null) {
              throw new RuntimeException("用户名未登录");
          }
  
          UserDetailsImpl loginUser = new UserDetailsImpl(user);
          UsernamePasswordAuthenticationToken authenticationToken =
                  new UsernamePasswordAuthenticationToken(loginUser, null, null);
  
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
  
          filterChain.doFilter(request, response);
      }
  }
  ```

- 配置`config.SecurityConfig`类，放行登录、注册等接口

  ```java
  import com.kob.backend.config.filter.JwtAuthenticationTokenFilter;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.http.HttpMethod;
  import org.springframework.security.authentication.AuthenticationManager;
  import org.springframework.security.config.annotation.web.builders.HttpSecurity;
  import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
  import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
  import org.springframework.security.config.http.SessionCreationPolicy;
  import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
  import org.springframework.security.crypto.password.PasswordEncoder;
  import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
  
  @Configuration
  @EnableWebSecurity
  public class SecurityConfig extends WebSecurityConfigurerAdapter {
      @Autowired
      private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
  
      @Bean
      public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder();
      }
  
      @Bean
      @Override
      public AuthenticationManager authenticationManagerBean() throws Exception {
          return super.authenticationManagerBean();
      }
  
      @Override
      protected void configure(HttpSecurity http) throws Exception {
          http.csrf().disable()
                  .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                  .and()
                  .authorizeRequests()
                  .antMatchers("/user/account/token/", "/user/account/register/").permitAll()
                  .antMatchers(HttpMethod.OPTIONS).permitAll()
                  .anyRequest().authenticated();
  
          http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
      }
  }
  ```

  也就是对于登录和注册的URL，变为公开可以访问：（未来需要放行其他URL，也在这个地方继续添加即可）

  ![image-20220727151311053](配置MySQL与注册登录模块.assets/image-20220727151311053-165918743918556.png)

## 后端API实现

在具体的API实现之前，先来更新下数据库。将数据库中的id域变为自增

- 在数据库中将id列变为自增

  <img src="配置MySQL与注册登录模块.assets/image-20220727151452235-165918743918557.png" alt="image-20220727151452235" style="zoom:80%;" />

- 在`pojo.User`类中添加注解：`@TableId(type = IdType.AUTO)`

  <img src="配置MySQL与注册登录模块.assets/image-20220727152146421-165918743918558.png" alt="image-20220727152146421" style="zoom:80%;" />

下面是具体的API编写。在Springboot中实现API一共需要实现三个地方：

* `controller`
* `service`
* `service.impl`

![image-20220727153732362](配置MySQL与注册登录模块.assets/image-20220727153732362-165918743918559.png)

### `sevice`

在`sevice`中写接口

![image-20220727153812288](配置MySQL与注册登录模块.assets/image-20220727153812288-165918743918560.png)

`RegisterService`

![image-20220727154116631](配置MySQL与注册登录模块.assets/image-20220727154116631-165918743918561.png)

`LoginService`

![image-20220727160515837](配置MySQL与注册登录模块.assets/image-20220727160515837-165918743918562.png)

`InfoService`

![image-20220727154316612](配置MySQL与注册登录模块.assets/image-20220727154316612-165918743918563.png)

### `service.impl`

在`service.impl`实现接口

#### `LoginServiceImpl`

![image-20220727155907368](配置MySQL与注册登录模块.assets/image-20220727155907368-165918743918564.png)

```java
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Map<String, String> getToken(String username, String password) {
        //封装用户名和密码 存放的是加密之后的密码
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        //验证是否可以正常登录
        //登录失败会自动处理
        Authentication authenticate =
                authenticationManager.authenticate(authenticationToken);
        //取出用户
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        User user = loginUser.getUser();

        //将用户的user id封装成jwt-token
        String jwt = JwtUtil.createJWT(user.getId().toString());

        //定义返回结果格式
        Map<String, String> map = new HashMap<>();
        map.put("error_message","success");
        map.put("token",jwt);
        return map;
    }
}
```

#### `InfoServiceImpl`

```java
@Service
public class InfoServiceImpl implements InfoService {
    @Override
    public Map<String, String> getinfo() {
        //如果授权成功 则从上下文中将User信息提取出来
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();

        User user = loginUser.getUser();
        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("id", user.getId().toString());
        map.put("username", user.getUsername());
        map.put("photo", user.getPhoto());
        return map;
    }
}
```

#### `RegisterServiceImpl`

主要是加入了一些规则判断，若不符合规则，返回相应的错误信息，若符合规则，则在数据库中添加一个新的User并且将成功的信息返回。

```java
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        if(username == null){
            map.put("error_message", "用户名不能为空");
            return map;
        }
        if(password == null || confirmedPassword == null){
            map.put("error_message","密码不能为空");
            return map;
        }
        username = username.trim();//trim() 方法用于删除字符串的头尾空白符
        if(username.length() == 0){
            map.put("error_message","用户名不能为空");
            return map;
        }
        if(password.length() == 0 || confirmedPassword.length() == 0){
            map.put("error_message","密码不能为空");
            return map;
        }
        if(username.length() > 100){
            map.put("error_message","用户名长度过长");
            return map;
        }
        if(password.length() > 100 || confirmedPassword.length() > 100){
            map.put("error_message","密码长度过长");
            return map;
        }
        if(!password.equals(confirmedPassword)){
            map.put("error_message","两次输入的密码不一致");
            return map;
        }

        //查询用户是否已经存在 如果存在则不允许注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        List<User> users = userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            map.put("error_message","用户名已存在");
            return map;
        }

        //满足以上的条件 添加新用户
        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/118942_lg_ff1a85241e.jpg";
        User user = new User(null, username, encodedPassword, photo);
        userMapper.insert(user);
        map.put("error_message","success");
        return map;
    }
}
```

### `controller`

![image-20220728093037903](配置MySQL与注册登录模块.assets/image-20220728093037903-165918743918565.png)

#### `LoginController`

```java
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/user/account/token/")
    public Map<String, String> getToken(@RequestParam Map<String,String> map){
        String username = map.get("username");
        String password = map.get("password");
        return loginService.getToken(username, password);
    }
}
```

注意，对于登录而言，一般是`post`请求，如果是`get`请求，会将用户名和密码参数放在`url`链接中，明文传输，而`post`请求看不到明文，所以使用`@PostMapping`注解；

将`post`请求中的参数，放在`Map`中，需要用到注解`@RequestParam`

如何调试这段代码的功能呢？

由于是Post请求，所以没法从浏览器输入URL的方式进行访问，因为浏览器中对应的Get请求，不能在浏览器中调试。有两种调试方法

1）前端框架中调试

![image-20220728094451442](配置MySQL与注册登录模块.assets/image-20220728094451442-165918743918566.png)

![image-20220728094508871](配置MySQL与注册登录模块.assets/image-20220728094508871-165918743918567.png)

2）使用postman（更方便 推荐）

![image-20220728094618658](配置MySQL与注册登录模块.assets/image-20220728094618658-165918743918569.png)

对于返回的token

```
"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5OWE5ZThjZDNlZDI0OTI3YTZiMWMzNDk5MDU1ZDljMyIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTY1ODk3MjYzMCwiZXhwIjoxNjYwMTgyMjMwfQ.iioSQLuAyzpYLPzTgGuhs1ODb6mYIzpqnz6K8VQqbWc"
```

使用https://jwt.io/中提供的工具进行解析，可以看出对应的userID

![image-20220728094756371](配置MySQL与注册登录模块.assets/image-20220728094756371-165918743918568.png)

#### `InfoController`

```java
@RestController
public class InfoController {
    @Autowired
    private InfoService infoService;

    @GetMapping("/user/account/info/")
    public Map<String, String> getinfo(){
        return infoService.getinfo();
    }
}
```

一般而言，获取信息对应的get，修改，删除和添加对应的是post

调试：

![image-20220728101432315](配置MySQL与注册登录模块.assets/image-20220728101432315-165918743918570.png)

![image-20220728101243101](配置MySQL与注册登录模块.assets/image-20220728101243101-165918743918571.png)

使用哪个用户的token，就可以生成哪个用户的信息。至此，我们实现了用户的登录和授权认证。

#### `RegisterController`

```java
@RestController
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @PostMapping("/user/account/register/")
    public Map<String, String> register(@RequestParam Map <String,String> map){
        String username = map.get("username");
        String password = map.get("password");
        String confirmedPassword = map.get("confirmedPassword");
        return registerService.register(username,password,confirmedPassword);
    }
}
```

调试如下：

1）失败案例

![image-20220728150902475](配置MySQL与注册登录模块.assets/image-20220728150902475-165918743918572.png)

2）成功案例

![image-20220728151143060](配置MySQL与注册登录模块.assets/image-20220728151143060-165918743918573.png)

![image-20220728151201956](配置MySQL与注册登录模块.assets/image-20220728151201956-165918743918574.png)

-----

到此为止，后端登录和注册模块的API就全部实现。

## 登录界面

### 配置路由

新增两个页面：

- `src\views\user\account\UserAccountLoginView.vue`
- `src\views\user\account\UserAccountRegisterView.vue`

![image-20220728152601740](配置MySQL与注册登录模块.assets/image-20220728152601740-165918743918575.png)

并在`src\router\index.js`中为其注册路由

![image-20220728152816374](配置MySQL与注册登录模块.assets/image-20220728152816374-165918743918576.png)

验证：

![image-20220728153110597](配置MySQL与注册登录模块.assets/image-20220728153110597-165918743918677.png)

![image-20220728153118764](配置MySQL与注册登录模块.assets/image-20220728153118764-165918743918678.png)

### 登录基本样式

样式改造：

借助`bootstrap`中的`Grid system`，一个用户布局的工具。

![image-20220728153406815](配置MySQL与注册登录模块.assets/image-20220728153406815-165918743918679.png)

`Grid`将每行分为 12 个模板列，允许您创建跨越任意数量列的不同元素组合。列类指示要跨越的模板列的数量（例如，`col-4`跨越四个）我们的登录窗口设置为跨越三个`col-3`，并设置为居中。

![image-20220728153904506](配置MySQL与注册登录模块.assets/image-20220728153904506-165918743918680.png)

效果如下：

![image-20220728193431171](配置MySQL与注册登录模块.assets/image-20220728193431171-165918743918681.png)

借助`bootstrap`中的`Form controls`，负责处理表单样式。

![image-20220728153926183](配置MySQL与注册登录模块.assets/image-20220728153926183-165918743918682.png)



此时还缺少一个登录按钮，用到`bootstrap`提供的`Buttons`

![image-20220728193905219](配置MySQL与注册登录模块.assets/image-20220728193905219-165918743918683.png)

 ```vue
 <template>
     <ContentFieldVue>
         <div class="row  justify-content-md-center">
             <div class="col-3">
                 <form>
                     <div class="mb-3">
                         <label for="username" class="form-label">用户名</label>
                         <input type="text" class="form-control" id="username" placeholder="请输入用户名">
                     </div>
                     <div class="mb-3">
                         <label for="password" class="form-label">密码</label>
                         <input type="password" class="form-control" id="password" placeholder="请输入密码">
                     </div>
                     <div class="error-message"></div>
                     <button type="submit" class="btn btn-primary">登录</button>
                 </form>
             </div>
         </div>
 
     </ContentFieldVue>
 </template>
 ```

![image-20220728194554177](配置MySQL与注册登录模块.assets/image-20220728194554177-165918743918684.png)

### 全局信息vuex

对于每个页面而言，都需要存储当前登录的用户信息，也就是需要将用户信息设置为全局存储。

需要用到`vue`的其中一个特性`vuex`

创建`src\store\user.js`

将用户信息`（id，username，is_login)`以及负责授权用的`jwt-token`保存在该文件中

<img src="配置MySQL与注册登录模块.assets/image-20220728195310670-165918743918685.png" alt="image-20220728195310670" style="zoom:67%;" />

并导入到全局`module`中

![image-20220728225232906](配置MySQL与注册登录模块.assets/image-20220728225232906-165918743918686.png)

然后在`action`中编写辅助函数，现在需要发生`login`请求并且获取token

<img src="配置MySQL与注册登录模块.assets/image-20220728204314494-165918743918687.png" alt="image-20220728204314494" style="zoom:80%;" />

### 登录实现

现在在登录的主页面`views\user\account\UserAccountLoginView.vue`负责实现登录功能。

`UserAccountLoginView.vue`

`script`部分：

![image-20220728221012255](配置MySQL与注册登录模块.assets/image-20220728221012255-165918743918688.png)

其中：

* 借助`ref`定义变量
* `router.push({name:'home'})`表示如果登录成功 跳转到name为home的页面中

`template`部分

![image-20220728221040989](配置MySQL与注册登录模块.assets/image-20220728221040989-165918743918689.png)

其中

* `@submit.prevent="login"`表示`submit`时触发`login`函数 并阻止默认行为

* `v-model`将输入的值，与`script`部分使用`ref`定义的变量绑定
* `{{ error_message }}`表示直接取出变量`error_message `的值

此时，实现了成功登录

如果输出错误：![image-20220728221459186](配置MySQL与注册登录模块.assets/image-20220728221459186-165918743918690.png)

如果输出正确：

![image-20220728221623853](配置MySQL与注册登录模块.assets/image-20220728221623853-165918743918691.png)

### 动态显示信息

在登录完成之后，我们希望在前端页面中，动态显示出用户信息（也就是用户名、头像、ID）。因此需要在登录成功之后，再次向后端发送请求来获取当前用户的用户信息。

因此需要在`src\store\user.js`中增加辅助函数

<img src="配置MySQL与注册登录模块.assets/image-20220728225440474-165918743918692.png" alt="image-20220728225440474" style="zoom:80%;" />

并更新`UserAccountLoginView.vue`如下：

![image-20220728225540736](配置MySQL与注册登录模块.assets/image-20220728225540736-165918743918693.png)

也就是登录成功之后，进行获取信息，如果获取成功，就在控制台输出相应的用户信息。

![image-20220728225643894](配置MySQL与注册登录模块.assets/image-20220728225643894-165918743918694.png)

 现在如何显示到导航栏上去呢？

![image-20220728225808191](配置MySQL与注册登录模块.assets/image-20220728225808191-165918743918695.png)



需要在`components\NavBar.vue`中修改下面代码：

![image-20220728234304235](配置MySQL与注册登录模块.assets/image-20220728234304235-165918743918696.png)

此时登录成功：

![image-20220728234319299](配置MySQL与注册登录模块.assets/image-20220728234319299-165918743918697.png)

> 这里有一个bug，登录成功之后刷新页面变为未登录。此时的Jwt-token存放在浏览器的内存中，会因刷新而清空，需要将Jwt-token存放在浏览器的local Storage中，即使用户关闭或者刷新浏览器，都不会退出登录状态。
>
> 这个后面会给出解决方法，暂时按下不表。

未登录：

![image-20220728234334252](配置MySQL与注册登录模块.assets/image-20220728234334252-165918743918698.png)

### 退出logout

用户登录之后如何退出呢？

对于整个认证机制，Jwt-token完全存在于用户本地。

![image-20220728235323138](配置MySQL与注册登录模块.assets/image-20220728235323138-165918743918699.png)

Jwt-token中除了存放user Id之外，还存放一个过期时间，服务器验证的时候可以判断是否过期。

所以用户退出的逻辑很简单，那就是用户自己删掉Jwt-token，这件事前端就可以完成。

同样是在`src\store\user.js`中写入相关辅助函数

![image-20220729000147004](配置MySQL与注册登录模块.assets/image-20220729000147004-1659187439186100.png)

然后在`src\components\NavBar.vue`中，添加一个退出的事件。

<img src="配置MySQL与注册登录模块.assets/image-20220729001035097-1659187439186101.png" alt="image-20220729001035097" style="zoom:80%;" />

在点击"退出"时触发`logout`函数，同时跳转到登录页面即可。

![image-20220729001925441](配置MySQL与注册登录模块.assets/image-20220729001925441-1659187439186102.png)

## 前端页面授权

实现前端页面授权，也就是判断jwt-token不合法的时候，自动退出到登录界面。

可以在`src\router\index.js`实现

在`routes`中，为某一个`route`新增一个判断是否需要授权的信息，`true`表示需要授权才能访问。

![image-20220729105021822](配置MySQL与注册登录模块.assets/image-20220729105021822-1659187439186103.png)

引入`store`，来判断用户是否登录

```js
import store from "../store/index"
```

同时增加`beforeEach`函数，则进入某个页面之前，执行该函数。

![image-20220729105529808](配置MySQL与注册登录模块.assets/image-20220729105529808-1659187439186104.png)

这样就可以实现，在未登录时，访问`to.meta.requestAuth`为`true`的页面，会自动重定向到登录页面。登录之后，才能正常访问。

## 注册页面

`views\user\account\UserAccountRegisterView.vue`，实现的逻辑与登录页面一致。

<img src="配置MySQL与注册登录模块.assets/image-20220729145932746-1659187439186105.png" alt="image-20220729145932746" style="zoom:80%;" />

<img src="配置MySQL与注册登录模块.assets/image-20220729145950110-1659187439186106.png" alt="image-20220729145950110" style="zoom:80%;" />

注意：

注册阶段的`ajax`请求直接放在了`UserAccountRegisterView.vue`

而登录阶段的`ajax`请求则是：

![image-20220729150937884](配置MySQL与注册登录模块.assets/image-20220729150937884-1659187439186107.png)

之所以会将操作放到`user.js`中，原因是需要修改`store.state`值

![image-20220729151138473](配置MySQL与注册登录模块.assets/image-20220729151138473-1659187439186108.png)

区分一个概念：`store.state`和`store.state.user`

![image-20220729151958681](配置MySQL与注册登录模块.assets/image-20220729151958681-1659187439186109.png)

打印`store.state`

```json
{
    "user": {
        "id": "7",
        "username": "zhou",
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzZDVlM2RmY2FmMWY0ZDJmYmNjZmYyZjc4MTI4ZjhmMSIsInN1YiI6IjciLCJpc3MiOiJzZyIsImlhdCI6MTY1OTA3ODc2NywiZXhwIjoxNjYwMjg4MzY3fQ.3xPS9Ahls8VBNIYb_FhD7Gn_mDCTOWTK7y-8DwKmEXA",
        "is_login": true,
        "photo": "https://cdn.acwing.com/media/user/profile/photo/118942_lg_ff1a85241e.jpg"
    }
}
```

打印`store.state.user`

```json
{
    "id": "7",
    "username": "zhou",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzZDVlM2RmY2FmMWY0ZDJmYmNjZmYyZjc4MTI4ZjhmMSIsInN1YiI6IjciLCJpc3MiOiJzZyIsImlhdCI6MTY1OTA3ODc2NywiZXhwIjoxNjYwMjg4MzY3fQ.3xPS9Ahls8VBNIYb_FhD7Gn_mDCTOWTK7y-8DwKmEXA",
    "is_login": true,
    "photo": "https://cdn.acwing.com/media/user/profile/photo/118942_lg_ff1a85241e.jpg"
}
```

## 登录状态持久化

对于前面提到的bug，也就是登录成功之后刷新页面变为未登录。此时的Jwt-token存放在浏览器的内存中，具体来说是存储在`store.state.user`中的`token`变量中，会因刷新而清空，需要将Jwt-token存放在浏览器的`local Storage`中，即使用户关闭或者刷新浏览器，都不会退出登录状态。

1、在登录成功时，存储到`local Storage`中，在退出时，从`local Storage`中删除

`store\user.js`

<img src="配置MySQL与注册登录模块.assets/image-20220729153126905-1659187439186110.png" alt="image-20220729153126905" style="zoom:70%;" />

![image-20220729153138844](配置MySQL与注册登录模块.assets/image-20220729153138844-1659187439186111.png)

![image-20220729153305180](配置MySQL与注册登录模块.assets/image-20220729153305180-1659187439186112.png)

2、每次刷新页面时，变为未登录状态，经过`router\index.js`写入的逻辑，会重定向到登录页面

![image-20220729155615022](配置MySQL与注册登录模块.assets/image-20220729155615022-1659187439186113.png)

然后我们添加相关的判断逻辑，在每次刷新页面时进入到登录页面之后，先判断`local Storage`是否有`jwt-token`，如果存在，将`jwt-token`取出验证是否有效，如果有效，则不需要重新登录，跳转到首页（home）即可

`views\user\account\UserAccountLoginView.vue`

![image-20220729160513255](配置MySQL与注册登录模块.assets/image-20220729160513255-1659187439186114.png)

此时可以初步实现效果。只不过还有瑕疵。也就是刷新之后：由于经过----->登录页面----->首页。因此，登录页面会一闪而过，有一种"白影"效果。可以先让登录页面默认不展示，在判断结束之后再展示。

3、处理“白影”

新增一个变量`pulling_info`表示当前是否正在从服务器获取信息中，如果正在拉取信息，则不展示登录页面

![image-20220729163532450](配置MySQL与注册登录模块.assets/image-20220729163532450-1659187439186115.png)

`pulling_info`为`ture`表示正在拉取信息，为`false`表示已经拉取完毕

当拉取信息结束之后，再显示对应的页面

![image-20220729163206283](配置MySQL与注册登录模块.assets/image-20220729163206283-1659187439186116.png)

![image-20220729163221461](配置MySQL与注册登录模块.assets/image-20220729163221461-1659187439186117.png)

当判断完`jwt-token`是否存在和有效后，更新`pulling_info`为`false`，表示拉取结束。

注意：当验证有效时，先进行的跳转页面，再进行的更新`pulling_info`，所以看不到“白影”

![image-20220729163707686](配置MySQL与注册登录模块.assets/image-20220729163707686-1659187439186118.png)

至此，登录和注册模块完成！
