## HttpURLConnection

* httpURLConnection 就是把你的request发到网络上，然后接收response的一个东西，WebView实际上也是封装了类似的一大堆东西。
* 然后，就是 设置请求，发送请求，接收response，然后就可以得到response的内容



1. 得到URL
2. 通过URL得到  HttpURLConnection
3. 通过 HttpURLConnection 得到response 的输出流
4. 然后可以 通过 BufferedReader从输出流中读取内容



```java
    /**
     * 用最普通的httpUrlConnection方法去获取
     * 通过 connection.getInputStream获得响应的 输出流
     * 然后 用 bufferedReader读出来
     * 再用主线程显示出来即可
     */
    private void useHttpUrlConnection() {
        Toast.makeText(this, "启动HttpUrlConnection", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://www.youtube.com/?hl=zh-cn");
                    conn1= (HttpURLConnection)url.openConnection();
                    conn1.setRequestMethod("GET");
                    conn1.setConnectTimeout(15000);
                    conn1.setReadTimeout(15000);

                    InputStream in=conn1.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    String content="";
                    String line="";
                    while((line=reader.readLine())!=null){
                        content=content+line;
                    }
                    reader.close();
                    showTheContent(content);
                }
                catch (Exception e){e.printStackTrace();}
                finally {
                    conn1.disconnect();
                }
            }
        }).start();



    }
```

* HttpURLConnection的setRequestMethod是get还是post

  HttpURLConnection的 setConnectTimeout是设置 超过多少时间才算是 连接超时

  HttpURLConnection的setReadTimeOut也是跟上面差不多的意思

  HttpURLConnection.getInputStream() 相当于就是  接收到了response了

* 获取网络信息这种耗时操作需要在子线程中展开

* 子线程  就是  new一个Thread，而Thread的构造函数可以是一个 Runnable接口，于是 new一个Runnable接口，在里面实现 你需要做的耗时工作即可。





OkHttp

* 这是一个 开源库的东西

* 首先要 在 app.gradle中添加依赖    com.squareup.okhttp3:3.10.0

* okHttp的包创建了一些类，简化了操作

  分别 是  OkHttpClient  , Request , Response

* 这几个类就可以很好地解决问题了



```java
    /**
     * 用开源库的 OKhttp
     * 具体就是  包里面有  request response对象，通过这些类和对象可以得到很多东西
     */
    private void useOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder()
                        .url("https://www.google.com.hk")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String content = response.body().string();
                    showTheContent(content);
                }
                catch (Exception e){e.printStackTrace();}
            }
        }).start();
    }
```

* 得到Request，然后就直接得到 Response， 而 Response.body().string()就是响应回来的信息。














## 网络的回调函数

* 上面那些，其实就是重复代码，或者说是不应该出现在 Activity里的代码。所以可以将它们放入一个类，然后把东西变成静态方法。
* 当然这些东西都是在子线程中进行的。
* 而子线程 操作的结果  却是不能够返回的。
* 那只能作为参数来 利用函数来进行。
* 所以    上面所讲的静态方法就要多加一个   Listener的接口，通过这个listener来作为回调函数
* 如果你用的是  HttpURLConnection自然就要自己写啦。
* 而如果你用的是   OkHttp，自然有人帮忙





* OkHttp包中有一个  Callback接口，这个接口就是回调函数，  当你传入参数时   ，你就把这个  Callback接口传进去，它自动就会帮你回调函数的了。
* 这个接口需要实现两个方法，分别是   onResponse和onFailure分别是成功后要回调的和失败后要回调的。
* 当然，这些运行都是在子线程中，所以 OkHttp 已经帮你封装好，它的操作就是在子线程中，而 接口由于你是在  主线程中new出来的，所以它的方法是在 主线程中，可以直接更新UI













## JSON

* 为什幺要使用 JSON格式来传输数据。 当网络上互相交替信息时，没人知道你发送的这些信息是什么来的，所以也就不知道如何解析你的数据
* 所以要传输数据，需要按照一定格式来传输。
* 这种格式就是  XML和json
* 因为  XML比较繁琐和笨重，我们这里就不用他了



* 而解析JSON格式的东西也有很多，android就自带了  JSONObject这个类，而这个类的使用其实也很简单

  JSONObject 表示的是 JSON对象，可以通过  jsonobject.getString(xxxx)得到json呈现的数据

  JSONArray 表示的是JSON数组，可以通过  遍历来遍历数组， 数组中的对象就是  JSONObject





但是还是有开源库的存在，所以，我们用  google的  GSON

* 首先要添加依赖  compile 'com.google.code.gson:gson:2.8.2'

* GSON有点像是  ORM，只要你建立了 一个 跟  json 里面的数据名字一样的 model类，就会自动赋值。

* 假设是 Perison类，如果是 单个对象

  ```java
  Gson gson=new Gson();
  Person person=gson.fromJson(jsonString,Person.class);
  ```

* 如果是 一个数组

  ```java
  List<Person> people=gson.fromJson(jsonString,new TypeToken<List<Person>>(){}.getType());
  ```

* 爽到爆炸
















































