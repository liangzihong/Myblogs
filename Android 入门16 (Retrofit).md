首先，这是一篇别人的博客。

因为第一次使用，还没有什么经验，就把别人的博客放下来，加上自己的一点解释。

## Retrofit 是什么？

Retrofit is a type-safe HTTP client for Android and java.

互联网上的资料很多很杂，在收集资料初步了解后，我先粗糙地认为：Retrofit 适用于与 Web 服务器提供的 API 接口进行通信。

当你想要做更多的 HTTP 操作时，可以使用 OkHttp，Retrofit的底层也是由 OkHttp 网络加载库来支持的。

关于 Retrofit 的原理，有三个十分重要的概念：『注解』，『动态代理』，『反射』。将会在以后逐步进行分析。

## 初步使用 Retrofit

Retrofit 在使用上与其他网络开源库有些区别，初次使用可能会感到困惑，其使用主要有四个步骤。

在使用前，我们首先假设我们要从某个 API 接口来获取数据，这里我们使用一位博主所提供的接口。接口的 URL 地址如下：

> [https://api.github.com/users/Guolei1130](https://link.jianshu.com?t=https://api.github.com/users/Guolei1130)

### 添加依赖和权限

在 build.gradle 文件中添加依赖，在 Manifest.xml文件中添加所需的网络权限。

converter-gson是一个转换接口，只要安装上他，会自动把response的内容转换为model类

```
// build.gradle
compile 'com.squareup.retrofit2:retrofit:2.4.0'
compile 'com.squareup.retrofit2:converter-gson:2.4.0'

// AndroidManifest.xml
<uses-permission android:name="android.permission.INTERNET" />

```

在 Retrofit 2.0 中，如果要将 JSON 数据转化为 Java 实体类对象，需要自己显式指定一个 Gson Converter。

### 定义接口

在这一步，需要将我们的 API 接口地址转化成一个 Java 接口。

我们的 API 接口地址为：

> [https://api.github.com/users/Guolei1130](https://link.jianshu.com?t=https://api.github.com/users/Guolei1130)

转化写成 Java 接口为：

```
public interface APIInterface {
  @GET("/users/{user}")
  Call<TestModel> repo(@Path("user") String user);

```

在后文构造 Retrofit 对象时会添加一个 baseUrl（[https://api.github.com](https://link.jianshu.com?t=https://api.github.com)）。

在此处 GET 的意思是 发送一个 GET请求，请求的地址为：baseUrl + "/users/{user}"。

{user} 类似于占位符的作用，具体类型由 repo(@Path("user") String user) 指定，这里表示 {user} 将是一段字符串。

Call<TestModel> 是一个请求对象，<TestModel>表示返回结果是一个 TestModel 类型的实例。



* baseUrl是主机名，并不在接口中出现
* 我们使用get注解时，如果固定的字符串就直接打上去，如果是 值不同的，就用{param1}括起来，然后在参数中加上 @Path("param1") String xxx，就表示会用xxx的值代替{param1} 成为url的一部分，就像使用 渲染模板 HandleBars一样。
* 然后 Call<T>是固定要返回的东西，Call是一个相当于 发送请求的东西。



### 定义 Model

请求会将 Json 数据转化为 Java 实体类，所以我们需要自定义一个 Model：

```
public class TestModel {
  private String login;

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }
}

```



### 进行连接，请求前的准备

现在我们有了『要连接的 Http 接口』和 『要返回的数据结构』，就可以开始执行请求啦。

首先，构造一个 Retrofit 对象：

```
Retrofit retrofit= new Retrofit.Builder()
.baseUrl("https://api.github.com")
.addConverterFactory(GsonConverterFactory.create())
.build();

```

注意这里添加的 baseUrl 和 GsonConverter，前者表示要访问的网站，后者是添加了一个转换器。

接着，创建我们的 API 接口对象，这里 APIInterface 是我们创建的接口：

```
APIInterface service = retrofit.create(APIInterface.class);

```

使用 APIInterface 创建一个『请求对象』：

```
Call<TestModel> model = service.repo("Guolei1130");
```

注意这里的 .repo("Guolei1130") 取代了前面的 {user}。到这里，我们要访问的地址就成了：

> [https://api.github.com/users/Guolei1130](https://link.jianshu.com?t=https://api.github.com/users/Guolei1130)

可以看出这样的方式有利于我们使用不同参数访问同一个 Web API 接口，比如你可以随便改成 .repo("ligoudan")





###发送请求

```
model.enqueue(new Callback<TestModel>() {
  @Override
  public void onResponse(Call<TestModel> call, Response<TestModel> response) {
    // Log.e("Test", response.body().getLogin());
    System.out.print(response.body().getLogin());
  }

  @Override
  public void onFailure(Call<TestModel> call, Throwable t) {
    System.out.print(t.getMessage());
  }
  });

```

* 至此，我们就利用 Retrofit 完成了一次网络请求。
* 但是不知道是做了封装还是怎样，  在  onResponse的回调函数中，它的线程是主线程，所以可以UI操作。







### 更多更难的操作

除了get方法，当然还有  Post，Delete，Put方法，并且还有 ？对应Query，多个就对应 QueryMap，这些更难的操作就遇到时再补充。