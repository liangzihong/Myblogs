#Activity



###Activity的生命周期

Activity的生命周期有7个过程，除了一个 onRestart之外，其他六个分成三对，成对出现和消失



完整生存期： onCreate and onDestroy，只要 activity每消失，就在这两个函数之间。

可见生存期：onStart and onStop，可见，但是焦点不在这个activity中。

前台生存期：onResume and onPause，可见，并且焦点就是这个activity，可以操纵。



activity可以以对话框的形式存在，尽管它还是 activity。    只需要在 配置文件中 activity的属性中加上  android:theme ="@style/Theme.Appcompat.Dialog"。





### 如何知道目前进行的是哪一个Activity

这个功能或许现在也用不到，但是对调试程序绝对有大帮助。可以帮你快速找到  每一个界面 分别是哪一个 antivity。



首先我们要知道一点知识：

* java的反射机制，使得 父类 可以获取子类的信息，意思是，在子类实现父类的方法中，父类可以获取子类的信息，包括子类的类名等等之类的东西。      如  当实现  一个普通类的  getClass().getName()时，实际上是父类Object.getClass().getName()，但是返回的仍然是子类的类名。



所以我们要自己写一个类 BaseActivity

```java
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Toast.makeText(this, this.getClass().getName()+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

```

然后使每一个 activity都继承 BaseActivity，这样，当 每一个activity onCreate时，就会 Toast打出activity的类名，方便在大项目中 快速改变UI。





### 如何一键消除所有Activity,随时随地退出程序

因为每个activity你想要退出，你就要按下一堆 物理返回 按键，逐个逐个activity回退。

而如果你想要一个按键就 消除所有activity，则可以需要 BaseActivity的配合



我们的思想是  弄一个  ActivityController类出来，里面来一个 List存储所有的activity，然后当一键消除时，就可以在 List中 把他们都 finish()掉。

```java
public class ActivityController {

    private static List<Activity> ListOfActivity=new ArrayList<>();

    public static void addActivity(Activity activity){
        ListOfActivity.add(activity);
    }

    public static void removeActivity(Activity activity){
        ListOfActivity.remove(activity);
    }

    public static void removeAll(){
        for(Activity activity: ListOfActivity){
            ListOfActivity.remove(activity);
            activity.finish();
        }
    }
}
```



我们还要BaseActivity的配合

```java
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Toast.makeText(this, this.getClass().getName()+"", Toast.LENGTH_SHORT).show();
        ActivityController.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}

```







### 启动Activity的最好方法

假如当你 做页面跳转时，要传递参数，那么你要知道 另一个 activity 要的是什么参数。

而如果在大的项目中，就要看 另一个activity的源码或者是问别人。 

那有什么方法可以一目了然了。

这里介绍一个新的方法。



```java
    public static void startAction(Context context,String param1){
        Intent intent=new Intent(context,LoginActivity.class);
        intent.putExtra("param1",param1);
        context.startActivity(intent);
    }

    public static void startAction(Context context){
        Intent intent=new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }
    
```



在需要跳转的 Activity中加一个静态函数，在里面新建 Intent来传入参数，利用 context来startActivity，如此一来，别人做跳转页面时，一看就知道需要传递什么参数进去。







### Activity的启动模式

其实，Activity有四种启动模式。

每个app都有一个 存储你打开的activity的 栈。

当你在 a Activity跳转到 b activity时，在 栈中，b activity就会变成栈顶元素。

这是前提。

启动模式 在 activity的配置文件中更改。



standard模式

这个模式意思就是，如果我们在 activity a中跳转到 activity a，程序就会再新建一个 activity a，两个activity a是不同的实例。这个模式也是默认的模式



singleTop模式

改变模式 首先 要在 activity的配置文件中修改属性， android:launchMode="singleTop".

这个模式表示 ，在栈顶 的activity不会再重复建立，即activity a中跳转到 activity a，程序不会再建立一个activity a实例。



singleTask模式

在栈中的所有activity都不会重复建立，如果栈顶activity跳转到  栈中其中一个activity，这个activity就会调到栈顶的activity位置上。



singleInstance模式

这个模式听说是可以 让不同app之间共享  activity，如果跳转到 这个模式的activity，那么这个activity将在另一个独立的栈中。





### Activity临时数据的保存

当你在输入文字时，另一个activity启动了，你输入文字的activity因为内存不足被回收，当你退回那个activity时，你的文字全没了。如何防止这种情况发生。



其实 onCreate的参数就是一个 Bundle，就是让你保存临时数据的。

只需要在activity中重写 onSaveInstanceState(Bundle outstate)，把东西放入 outstate即可。

那么当 activity调用onCreate时，你看看 savedInstanceState是否为null即可。











## Intent

intent显示的页面跳转以前就会，所以就不写了。

今天写个新的作用，隐式跳转。

 

```java
    private void init(){
        button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:10086"));
                startActivity(intent);
            }
        });
    }
```

这段代码能把我们带到 拨号 的界面。

因为 Intent.ACTION_DIAL是系统的内置动作，而只有拨号 这个app是有 这个ACTION的，所以就找到它。

而 setData()， data部分就指定了 协议 是tel协议，号码是10086.



由此可得，我们可以通过这一功能跳到不同 接收 系统各种内置动作的应用。 如：打开浏览器，打开拨号等。









## Menu的使用

这个就不想写了，网上就有。





















