## RecyclerView



RecyclerView是什么。

其实RecyclerView就是ListView的替代品，因为ListView的优化不太好，所以现在  尽量都用  RecyclerView来代替 ListView。   实测效果其实还是可以的。 代码的整洁度，理解度要比 LIstView要高。



例子 ： 是一个 消息的对话界面。

我们先从 主布局开始说。

主布局应该就是一个  RecyclerView 然后  下面一个 LinerLayout，LinerLayout里面有一个 Edit Text和一个Button作为发送信息。



####chatui_layout

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="#cccccc">
    </android.support.v7.widget.RecyclerView>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <EditText
            android:id="@+id/inputEditText"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="10"/>

        <Button
            android:id="@+id/sentButton"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="3"
            android:text="sent"/>

    </LinearLayout>
</LinearLayout>
```

可以关注的一点是   下面发送信息的  layout一定要是  wrap_content，不然你编辑框编辑的时候会变形。

但是这个wrap_content不会与上面 RecyclerView 的 weight=1矛盾，我也不知道为什么。

这里 recyclerview的颜色需要和   recyclerview内容的布局的颜色一样，因为如果  recyclerview很短，那么recyclerview和下面的layout之间就会用空白的地方出来。



对了，RecyclerView是需要添加依赖的，需要在 app的gradle文件中

```xml
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    compile 'com.android.support:appcompat-v7:25.3.1'
    compile 'com.android.support.constraint:constraint-layout:1.0.0-alpha7'
    compile 'com.android.support:recyclerview-v7:25.3.1'
    testCompile 'junit:junit:4.12'
}
```

com.android.support:recyclerview-v7:25.3.1   是在  SDK25下的  recyclerview。





然后我们再讨论  recyclerview下的内容是什么

我们需要   一个回合的 消息， 所以我们需要两个  LinerLayout ，每个layout里面各包含一个 TextView。

以这样的东西作为 recyclerview 的内容呢。  因为我们可以根据的 send和receive 设置  另外一个布局的  visibility

为 gone，这样 另一个布局就会消失不占空间 ，这样就做到了。



为了美观，我们把 整个layout的背景设为 灰色，  receive 的textview设为白色， send的textview设为绿色。

并且  send_layout和receive_layout之间有一个view作为分隔，receive_layout下面也有一个灰色的view作为分隔。



#### receiveandsendmessage_item

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:background="#cccccc">

    <LinearLayout
        android:id="@+id/receive_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_gravity="left">

        <TextView
            android:layout_margin="10dp"
            android:id="@+id/receive_TextView"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:textSize="22sp"
            android:gravity="left|center_vertical"
            android:text="5555555"
            android:background="#ffffff"/>
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="5dp"></LinearLayout>
    <LinearLayout
        android:id="@+id/sent_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="right">

        <TextView
            android:layout_margin="10dp"
            android:id="@+id/sent_TextView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="22sp"
            android:gravity="left|center_vertical"

            android:text="5555555"
            android:layout_gravity="right"
            android:background="#00ff00"/>
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="5dp"></LinearLayout>

</LinearLayout>

```





然后就是真正的主角了， Adapter的各种函数的解释和 套路。

首先来个  model来热身， Message

```java
public class Message {
    public static final int Is_Sent=0;
    public static final int Is_Receive=1;


    private String content;
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Message(String content, int state){
        this.content=content;
        this.state=state;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
```

就是根据 state来判断究竟是 send和receive



MessageAdapter

```java
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    /**
     * recyclerView 内容是什么，它的变量就有什么，构造函数 就要把它的变量全部赋值掉。
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout Receive_layout;
        private TextView Receive_textview;
        private LinearLayout Sent_layout;
        private TextView Sent_textview;
        public ViewHolder(View itemView) {
            super(itemView);

            Receive_layout=(LinearLayout)itemView.findViewById(R.id.receive_layout);
            Receive_textview=(TextView)itemView.findViewById(R.id.receive_TextView);
            Sent_layout=(LinearLayout)itemView.findViewById(R.id.sent_layout);
            Sent_textview=(TextView)itemView.findViewById(R.id.sent_TextView);

        }
    }
}

```

我们一步步来

首先MessageAdapter的父类都是规定的。

MessageAdapter要有一个内部类，这个内部类 就是ViewHolder，需要继承RecyclerView.ViewHolder。

一语中的：

作用：recyclerView的item是什么，ViewHolder的变量就有什么，构造函数 就要把它的变量全部赋值掉。





然后就到了 MessageAdapter 的各个 抽象方法了

```java
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> ListOfMessage;

    public MessageAdapter(List<Message> list){
        ListOfMessage=list;
    }
}

```

构造函数没疑问，就是  来一个  model的List，然后赋值给自己的 私有变量。



```java
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    /**
     * 要根据view 返回一个viewHolder，如果要设置事件响应，就在这里响应。
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.receiveandsentmessage_item,
                parent,false);

        ViewHolder viewHolder=new ViewHolder(view);
        final TextView text1=(TextView)view.findViewById(R.id.receive_TextView);
        final TextView text2=(TextView)view.findViewById(R.id.sent_TextView);

        
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), text1.getText()+"", Toast.LENGTH_SHORT).show();
            }
        });

        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), text2.getText()+"", Toast.LENGTH_SHORT).show();
            }
        });


        return viewHolder;
    }


    /**
     * 根据 position 取得 Model的对象，根据对象的属性，对 holder的UI控件赋上响应的属性，如 setText，setImageResource等，
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message=ListOfMessage.get(position);

        if(message.getState()==Message.Is_Receive){
            holder.Receive_layout.setVisibility(View.VISIBLE);
            holder.Sent_layout.setVisibility(View.GONE);
            holder.Receive_textview.setText(message.getContent());
        }
        else{
            holder.Sent_layout.setVisibility(View.VISIBLE);
            holder.Receive_layout.setVisibility(View.GONE);
            holder.Sent_textview.setText(message.getContent());
        }


    }



}

```

这两个函数是最重要的，onBindViewHolder和onCreateViewHolder

onBindViewHolder：根据 position 取得 Model的对象，根据对象的属性，对 参数holder的UI控件赋上响应的属性，如 setText，setImageResource等。



onCreateViewHolder：要根据参数view 返回一个viewHolder，如果要设置事件响应，就在这里响应。可以对view的分个控件进行事件响应，也可对单一的整个view进行响应。