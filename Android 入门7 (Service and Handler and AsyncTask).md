## Handler

* Handler是一个处理者，这是异步消息处理机制的重要一环，它通常负责 发送信息 和 处理信息。

  通常是  在 子线程中  发送信息，   然后会经过一系列的机制  到达  主线程中的 处理信息的函数

  此时就可以更新UI了

* 这是手办眼见功夫，就不打了。

* 具体在 第一行代码 的  p343-p344



* 同样重要的 还有  Message，what字段可以携带信息，arg1和arg2可以携带整型数据，obj可以携带 object数据。
* MessageQueue and Looper 
* 之前用的  runOnUiThread就是一个异步消息处理机制的封装函数。





## AsyncTask

* AsyncTask就是android将异步消息处理机制 封装一下而已

* 它是抽象类，有几个抽象方法

* onPreExecute()    主线程方法，用于 进行耗时操作前的准备

  doInBackground(Params..)	   子线程方法，在这里进行耗时操作

  onProgressUpdate(Progress)	主线程方法，可以进行UI操作， 需要调用publishProgress（）来调用

  onPostExecute(Result)	执行一些任务的收尾工作。

* 第一行代码的 p348-p349

































## Service



服务是在后台中运行的程序，没有UI，不可见。服务也是四大组件之一，现在学的只是基础，所以很多关于它的都不知道。



service类是抽象类，想要继承service类，必须重写  onBind 函数。

service类似Activity，是四大组建之一，不但需要在清单文件中配置，也有它类似Activity的生命周期。这个详细可以看guolin的博客。



service的内容其实也就是  Intent，intent的构造函数   第一个参数是启动源的Context，第二个参数是 service类的子类。







### 开启服务

开启服务有两种方式：

第一种是 开了之后就与 启动源 没有任何关系的，所以如果删除了启动源就再也得不到这个 service。

第二种是，通过一个接口方法，可以返回一个 IBinder接口给启动源，通过这个IBinder接口，可以得到各种数据甚至是 service 本身，这样一来，service就可以在各个Activity中自由行走并且传递数据。



####startService

MyStartService类

```java
public class MyStartService extends Service {
    @Override
    public void onCreate() {
        Toast.makeText(this, "MyStartService---onCreate", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "MyStartService---onStartCommand", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "MyStartService---onDestroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "MyStartService---onBind", Toast.LENGTH_SHORT).show();
        return null;
    }
}
```



在Activity中 ，通过 startService启动 MyStartService

```java
public class MainActivity extends AppCompatActivity {
    private Intent intent1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
    }
    public void onClick(View view){
        switch(view.getId()){
            case R.id.start:
                intent1=new Intent(MainActivity.this, MyStartService.class);
                startService(intent1);
                break;
            case R.id.stop:
                stopService(intent1);
                break;
        }
    }
}
```



直接像  startActivity那样直接  startService（intent）即可。







#### bindService

MyBindService类

```java
public class MyBindService extends Service {
    @Override
    public void onCreate() {
        Toast.makeText(this, "MyStartService---onCreate", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "MyStartService---onDestroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "MyStartService---onUnbind", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "MyStartService---onBind", Toast.LENGTH_SHORT).show();
        return new Mybinder();
    }


    public void play(){
        Toast.makeText(this, "MyStartService---play", Toast.LENGTH_SHORT).show();
    }
    public void pause(){
        Toast.makeText(this, "MyStartService---pause", Toast.LENGTH_SHORT).show();
    }
    public void next(){
        Toast.makeText(this, "MyStartService---next", Toast.LENGTH_SHORT).show();
    }
    public void previous(){
        Toast.makeText(this, "MyStartService---previous", Toast.LENGTH_SHORT).show();
    }    
    
    public class Mybinder extends Binder{
        public MyBindService getService(){
            return MyBindService.this;
        }
    }
}

```



这里重要的 是 onBind方法，它会返回一个  IBinder接口，通过接口我们可以做很多东西。

首先我们写一个  Mybinder 继承 Binder类， BInder类是android写的实现了  IBinder接口的类。

然后，Mybinder类就随我们写，那么我想通过  这个 Mybinder传回 service本身，于是我便返回了 MyBindService.this.





在Activity中 绑定服务 并且 获取 IBinder接口

```java
public class MainActivity extends AppCompatActivity {
    private Intent intent2;
    private MyBindService service;

    private ServiceConnection conn=new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service=((MyBindService.Mybinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

    }
    public void onClick(View view){
        switch(view.getId()){
            case R.id.bind:
                intent2=new Intent(MainActivity.this, MyBindService.class);
                bindService(intent2,conn,Service.BIND_AUTO_CREATE);
                break;
            case R.id.unbind:
                unbindService(conn);
                break;
            case R.id.play:
                service.play();
                break;
            case R.id.pause:
                service.pause();
                break;
            case R.id.previous:
                service.previous();
                break;
            case R.id.next:
                service.next();
                break;
        }
    }
}

```



采用 bindService方法必须 有一个   ServiceConnection的类， 于是我们写了一个 Service Connection类，这个类需要实现两个方法，第一个表示 启动源 与 service绑定后 调用的。 第二个表示 启动源 与 service 意外丢失联系时启用的，一般我们不写。



而在onServiceConnected中，第二个参数 就是  服务返回的  IBinder接口，于是我们用一个  Mybinder 向下转型，通过 getService()获取到了  MyBindService本身。  于是通过这个  MyBindService ，就可以轻松获取服务，调用服务的函数了。









## IntentService

* 比如你需要一个后台服务去 下载一个东西，那么你要一些比较麻烦的操作

  因为服务默认是在子线程中运行的，再者，服务一旦开启之后，如果要停下，就要调用  stopSelf()函数，那你是不是觉得很麻烦。

* 所以android弄了一个 IntentService类

* 目的就是要可以  简单地创建一个 异步的，会自动停止的服务。

  其实就是在封装了一个    在内部开启一个新线程的 service



* 需要继承  IntentService这个类，然后实现  onHandleIntent这个方法，这个方法就是要执行的耗时操作，是在子线程中运行的，并且它是直接运行完  后自动关闭的。

* 其他 Intent intentService=new Intent(this,MyIntentService.class);

  ​         startService(intentService);   是一样的

* 并且是要在配置文件中声明的。











































