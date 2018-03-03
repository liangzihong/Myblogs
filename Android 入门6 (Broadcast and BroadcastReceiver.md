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



