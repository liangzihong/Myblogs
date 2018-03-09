## Broadcast 和 BroadcastReceiver的使用



首先， 广播的作用是 在 应用程序间传递信息，或者是 系统的一些反应来传递信息给其他 app。

这种跨应用的传递就是通过 Broadcast 和 BroadcastReceiver来传播和接收。



然后，广播的内容 其实就是  Intent 。 广播 比较普遍的有 两种 广播，分别是 标准广播（无序广播）和 有序广播。       



无序广播可以说是  传遍天下无敌手，只要是广播接收器的过滤器有与  一个无序广播有对应的 权限 ，就能够传遍接收者，不能被其中一个接收者所打断。



而有序广播就不同了，它可以被 先传到的 广播接收者所截断或者是对这条广播进行 加工。

 



发送广播

```java
Intent intent=new Intent();
intent.putExtra("name","ali");
intent.setAction("This is my action");  //这条语句是设置 intent的权限，只有有这个权限的过滤器的接收器才能收到
//发送无序广播
sendBroadcast(intent);
//发送有序广播
sendOrderedBroadcast(intent);
```



然后轮到广播接收者

想成为 广播接收者，只需要 extends BroadcastReceiver。 并且要实现 onReceive这个回调函数，这个回调函数是只要接收到对应的广播就能播放的。

```java
public class BroadcastReceiver_1 extends BroadcastReceiver {
    private static final String TAG = "BcR_1's TAG ";

    /**
     * 广播接收者 要重写 OnReceive函数
     * 这个函数 context就是发送广播的上下文，intent就是广播中的内容
     * 而intent又可以传递信息。
     *
     * 当这个Receiver接收到信息，就会回掉这个 onReceive
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String name=intent.getStringExtra("name");
        Log.i(TAG, "onReceive: I am BroadcastReceiver_1, your name is "+name);

        //传进则是  setResultExtras(bundle);
        Bundle bundle=getResultExtras(true);
        String name2=bundle.getString("name");
        if(name2!=null){
            Log.i(TAG, "onReceive: I am 1, and I receive the extra info from 2");
        }
    }
}

```





权限文件

因为 BroadcastReceiver是四大组件之一，所以在配置文件中也需要配置的。  

在清单文件中配置，相当于是静态加载 BroadcastReceiver

```xml

        <!-- 接着要在清单文件中，添加两个 Receiver和他们所对应的 intent-filter-->

        <receiver android:name=".BroadcastReceiver_1">
            <intent-filter android:priority="100">

                <action android:name="MyAction"> </action>
            </intent-filter>
        </receiver>

        <receiver android:name=".BroadcastReceiver_2">
            <intent-filter android:priority="200">

                <action android:name="MyAction"> </action>
            </intent-filter>
        </receiver>


```

receiver也要写上receive的类名 和  intent-filter ，意图拦截器。



intent-filter表明这是一个 广播内容 的过滤器， 要在这里  设置  antion 的名字，才表明可以收到哪些广播。

除了自己写的action之外，android还定义了多个 action，如：手机电量变了，手机网络连接情况如何等。







### 动态注册和静态注册



静态注册：静态注册相当与 应用没打开，都能够接收消息并且 做出 回调函数的反馈。比如：可以设置成 开机就有一个Toast出来。



动态注册：动态注册相当于你的应用开了之后，才能够注册并且接收信息的。  如果你不需要 应用没打开就接收信息，建议使用动态注册，因为系统的有一些 在配置文件中是没有的。  比如：感受手机有没网络。



感受手机有没网络的程序

```java
public class NetworkBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=manager.getActiveNetworkInfo();
        if(info!=null&&info.isAvailable()){
           Toast.makeText(context, "Network is available", Toast.LENGTH_SHORT).show();
        }
        else{
           Toast.makeText(context, "Network is unavailable", Toast.LENGTH_SHORT).show();
        }
    }
}
```

ConnectivityManager 是一个 感受 连接服务的一个 管理者。

NetworkInfo是一个 感受 有没网 的一个信息。

如果有网，info ！=null &&info.isAvailable()==true

如果没网，info==null.





动态注册

```java
public class LookNetworkActivity extends AppCompatActivity {
    NetworkBroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter=new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        receiver=new NetworkBroadcastReceiver();
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
```

动态注册 需要一个  IntentFilter和一个BroadcastReceiver

然后  registerBroadcastReceiver(receiver,filter)。注册完毕。

动态注册 一定要  unRegisterBroadcastReceiver(receiver)。 取消注册。





静态注册实现 开机有toast

* BroadcastReceiver不多说，就是实现 继承方法，Toast即可
* 在配置文件中，需要 注册 BroadcastReceiver， 然后添加  intent-filter，在intent-filter下添加  action android:name="android.intent.action.BOOT_COMPLETED".
* 然后你开机就能启动了。







###使用本地广播

如果使用本地广播，则 这个机制发出的广播只能够在应用程序的内部进行传递，并且广播接收器也只能接收来自本应用程序发出的广播，这样所有的安全性问题就不存在了。



使用本地广播其实只需要做一点小变动。

```java
public class LocalBroadcastActivity extends AppCompatActivity {

    private LocalBroadcastManager manager;
    private BroadcastReceiver_2 receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager=LocalBroadcastManager.getInstance(this);
        IntentFilter filter=new IntentFilter();
        filter.addAction("MyAction");
        receiver=new BroadcastReceiver_2();
        manager.registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregisterReceiver(receiver);

    }
}
```

就是多了一个 LocalBroadcastManager, 通过它来  注册和 撤销注册而已。



广播 的实践， 在我不是微信中 实现 强制下线功能。





### 广播的作用

通过广播，我们可以配合  BaseActivity，在 我不是微信中实现  强制下线的功能。



