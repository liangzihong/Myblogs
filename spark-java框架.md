折腾了几天，终于大概弄清楚一些东西。

首先是是spark-java的配置，直接从网上得到，大概就是创建一个maven，然后在配置文件中添加一些东西。然后打代码就差不多了。

web框架，以我的理解就是封装了servlet。



### GET，POST请求方法，以及路径

*  路由：由请求方法，url，以及一个参数为（request，response），返回字符串的匿名函数

* ```java
  get("/hello",((request, response) -> {
              return "hello world";
          }));
  ```

* 很简单的一个路由，输入localhost:4567/hello即可，然后返回的东西就在网上输出

* 同样，如果你想是POST，就把get变成post即可



* 那我们可以怎样发送post方法的请求呢？  我们可以用curl这个命令，可以百度得到如何使用
* 然后在这个网站http://www.ruanyifeng.com/blog/2011/09/curl.html得到如何使用
* 就可以字形发送参数和post请求。



### 转发和重定向

* 这里貌似只有重定向

* 有两种方式，一种是 直接   

* ```
  redirect.get("/frompath","/topath");
  ```

* 意思是只要是get方法访问from path，就会帮你转到get的topath

* 也可以利用response.redirect("xxxx");



### 如何跟前端联系起来

* 首先打一句这样的代码

* ```
  staticFileLocation("/public");
  ```

* 表明public是这工程里面一个resource文件夹里面的一个文件夹，它里面的资源给我们任意调用。

* 例如我在  public下放一个  index.html，我们就可以直接 localhost:4567/index.html就可以访问



==所以只利用前三个的东西，就可以写很多东西，例如啤酒选择，然后重定向，又可以去到get这些就可以不断显示== 



### 然后就是session和cookie

* 作用也还是那些作用session在服务器端，cookie在客户端。
* session是可以任意对象的，cookie就只能是字符串



### 然后就是过滤器

* 所谓过滤器，相当于请求到达  路由  给路由处理前后  对请求或者相应进行处理。
* 例子就看官网例子就行。




### 学会使用模板

* 暂时用的是handlebars
* 模板大概就是一个MVC的View，就是把html的代码放入后缀为.hbs那里，然后==构造路由时== 又有点不同的代码。
* 然后还可以把html的格式统一化，使用一个layout.hbs，然后其它个个hbs都要遵循这个规则，如果想要 override 也可以，参见https://www.learnhowtoprogram.com/java/web-applications-with-java/using-and-fine-tuning-layouts-in-spark




* 同时在构造路由的函数中，你传进的model，是一个HashMap，它的key可以对应前端文件里面的变量，  
* 如 传进去的map有一个  key=UserName的pair，那么在.hbs中，就可以用   {{UserName}}代表map【UserName】这个变量，同时模板也很智能，假如你传入的是一个  ArrayList，它也可以有.hbs的循环，如果  你传入的ArrayList内部是一个对象，有getXXX的函数，你也可以直接{{property}}作为这个对象的参数。反正很智能
* 不过要注意的是，如果要用到模板然后又用到对象的{{property}}，要注意函数名一定要是  getContent（）之类的，getSomething，S要大写。
* HTML要有链接，链接同样和之前的规矩一样，


















## Middle Blog

1. 首先要添加依赖，MySQL的依赖，模板的依赖，sql2o的依赖，迟点还有gson的依赖。

   ```xml
   <dependencies>
           <dependency>
               <groupId>com.sparkjava</groupId>
               <artifactId>spark-core</artifactId>
               <version>2.1</version>
           </dependency>

           <dependency>
               <groupId>org.sql2o</groupId>
               <artifactId>sql2o</artifactId>
               <version>1.5.4</version>
           </dependency>

           <dependency>
               <groupId>mysql</groupId>
               <artifactId>mysql-connector-java</artifactId>
               <version>5.1.45</version>
           </dependency>

           <dependency>
               <groupId>com.sparkjava</groupId>
               <artifactId>spark-template-handlebars</artifactId>
               <version>2.7.1</version>
           </dependency>

           <dependency>
               <groupId>com.google.code.gson</groupId>
               <artifactId>gson</artifactId>
               <version>2.8.2</version>
           </dependency>
       </dependencies>
   ```

   ​


