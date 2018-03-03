## ListView的简单使用

1. 摆出一个ListView
2. 需要一个数据适配器： ArrayAdapter或者SimpleAdapter
3. 初始化 ArrayAdapter或者SimpleAdapter。
4. LIstView加载适配器

```java
//ArrayAdapter使用于纯文字的列表
//其中ArrayAdapter的构造函数
/*
 *ArrayAdapter(上下文，当前ListView加载的每一项所需要对应的布局文件，数据源（数组）)
 */
String[]arr={"one","two","three","four","five"};
ArrayAdapter<T> arr_adapter=new ArrayAdapter<>(this,R.layout.simple_list_item1,arr);
listview.setAdapter(arr_adapter);



//SimpleAdapter适用于比较复杂的ListView格式，类似于商品的图片＋文字之类的。
//其中SimipleAdapter的构造函数
/*
 *SimpleAdapter(context,data,resource,from,to)
 *context:上下文
 *data:数据源，是List<? extends Map<String,?>>,data中的每个map，都需要有一个
 	  以from中所有字符串为key，to中所有Object为value的 pair<String from from,Object from 	     to>.
 *resource:是布局文件，代表ListView中的每一项所遵循的布局，可以是系统的，也可以是你自己写的。
 *from：字符串数组
 *to:int数组，绑定数据视图中的id，人话：你的布局文件中所有元素的id所构成的int数组
 *所以from数组和to数组是一一对应，相当于映射
 *所以data中的map元素，只需要跟from数组的对应即可，因为from与to一一对应。
 *data中的map元素的value相当于资源，放什么文字，放什么图片，都是它控制的。
 */

//比如现在，我规定ListView中每一个都有一个ImageView和一个TextView
String[] from={"picture","text"};
int[] to={R.id.iv1,R.id.tv1};
List < Map<String,Object> > data=ArrayList<>();

for(int i=0;i<20;i++){
  Map<String,Object> map=new Map<>();
  map.put("picture",图片所在位置);
  map.put("text","this is "+i);
  data.add(map);
}

SimpleAdapter sim_adapter=new SimpleAdapter(this,data,布局文件,from,to);
listview.setAdapter(sim_adapter);


//而写商品列表，可以用RelativeLayout尝试一下



```



5. 对ListView的每一项的点击事件设置监听器，和对ListView的滚动状态设置监听器。

   ```java
   //对于ListView，单个条目的点击事件对应的监听器是 OnItemClickListener
   //对于LIstView，滚动状态的检测对应的监听器是     OnScrollListener

   /*
    *点击事件的监听器
    *需要实现 */
   public void onItemClick(AdapterView<?> parent,View view,int position,long ,id) {
    	 //position是你点击的条目的位置信息
    	 //可以通过ListView的getItemOnPosition（position）获取对应的条目信息
    	 //如果ListView接收的适配器是SimpleAdapter，那getItemOnPosition返回的是一个			 //Map《String，Object》，也就是你怎么赋值，他就给回一个map你。
    	 Map<String,Object> obj= (Map<String, Object>) listview.getItemAtPosition(position);
    	 String tv1= (String) obj.get("tv1");
    	 Toast.makeText(MainActivity.this, tv1, Toast.LENGTH_SHORT).show();
    	 
    	 //这样就返回其中一个文本框的值
    }
   ```


   


```java
//实现对屏幕滚动的监控的监听器
listview.setOnScrollListener(new AbsListView.OnScrollListener() {
   @Override
   public void onScrollStateChanged(AbsListView view, int scrollState) {
    switch(scrollState){
         //用力滑了一下，然后手指离开，而屏幕仍然在走
           //并且有一个说不过去的状态，就是当在最底部时继续滑动，会新增选项，这个也算是手指离开，而屏幕仍在走
    case SCROLL_STATE_FLING:
    /**
     * 此时要试图增加条目
     */
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("pic",R.mipmap.ic_launcher);
         map.put("tv1","新增项");
         map.put("tv2","新增项");
         map.put("tv3","新增项");
         data.add(map);
         //一定要有这一句话，不然不行
        //记住这个就好，当data有更更新，要调用   sim_adapter的notifyDataSetChanged（）;
         sim_adapter.notifyDataSetChanged();
         break;
     //滑动停止的一瞬间，就是这个状态
     case SCROLL_STATE_IDLE:
         break;
      //你的手指在屏幕上，并且在滑动
     case SCROLL_STATE_TOUCH_SCROLL:
         break;
        }
  }

@Override
public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
       });
```
   		

6. 如果途中，simpleAdapter的数据源有变，则要调用   notifyDataSetChanged()方法来更新，不然会崩毁。

7. 如果ListView接收的适配器是SimpleAdapter，那getItemOnPosition返回的是一个Map《String，Object》，也就是你怎么赋值，他就给回一个map你。











## GridView的简单使用

1. 摆出一个GridView

2. 需要一个数据适配器：SimpleAdapter

3. SimpleAdapter的初始化，老规矩，同ListView一样。

4. GridView加载适配器

5. 对GridView的属性调整

   >numColumn可以决定每一行有多少个图片
   >
   >horizontalSpacing可以决定列与列之间的间隔
   >
   >verticalSpacing可以决定行与行之间的间隔

   ​











## 实现Activity的页面跳转

1. Activity，要搞一个Activity，就是要继承AppCompatActivity，重写onCreate方法，然后在AndroidManifest中注册Activity，注册完即可。

2. 实现Activity的跳转有两种方法(两种方法都是要通过   Intent   来实现的 )


   ```java


    /**
     * 第一种方法，创立Intent，然后直接用startActivity(Intent)进行跳转
     */

   //构造函数：  Intent(当前Activity，另一个Activity的class)
   Intent intent=new Intent(fActivity.this,SActivity.class);
   startActivity(intent);
   //成功跳转



   /*
    *第二种方法：利用startActivityForResult(Intent,requestcode)跳转
    *           另一个页面用   setResult(resultcode,Intent)  返回来
    *			然后第一个页面又重写  onActivityResult(requestcode,resultcode,Intent)接收
    *这种方法的好处是
    *既可以通过requestcode和resultcode来识别究竟是哪个跳转和返回。
    *也可以从第二个页面返回信息第一个页面。
    */

   //第一个页面
   Intent intent=new Intent(FActivity.this,SActivity.class);
   startActivityForResult(intent,FTSCode);

   //第二个页面
   Intent intent=new Intent();
   intent.putExtra("data","you can add any information");  //通过intent添加信息
   setResult(STFCode,intent);
   finish();   //用来结束这个Activity

   //第一个页面
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
       if(requestCode==FTSCode&&resultCode==2){
          String text=data.getStringExtra("data");
          tv1.setText(text);
        }
   }




   ```





## Spinner下拉列表的实现

1. 有以上基础，看这些就很简单了

2. 下拉列表如无特殊情况都是文字，如果是图片+文字也可以，Adapter换一个就是了。

3. 第一步：设置数据源

4. 第二步：初始化Adapter

5. 第三步：adapter设置一个下拉样式

6. 第四步：spinner加载适配器。

   ```java
   //设置数据源
   List<String> data=new ArrayList<>();
   data.add("xxx");
   data.add("yyy");

   //适配器  ArrayAdapter
   ArrayAdapter<String> adapter=new ArrayAdapter<>(this,Android.R.layout.simple_list1,data);

   //adapter设置一个下拉样式
   adapter.setDropDownViewResource(Android.R.layout.simple_spinner);

   //spinner添加适配器
   spinner.setAdapter(adapter);

   spinner.setOnItemSelectedListener(new OnItemSelectedListener{
       @Override
      public void onItemSelected(AdapterView<?>parent, View view,int position,long id){
        //position为第几项
      }
   })
   //当然，如果你想用到  SimpleAdapter，只需要重新写一个布局文件，然后在下拉样式和适配器中的layout改成你写的即可，没有太大难度。
   ```




​     











## WebView的简单使用

1. WebView其实也是一个控件，只不过它能呈现网页效果，下面说一下怎样使用

2. 首先，要在manifest文件中，加上网络权限，<uses-permission android:name="android.permission.INTERNET"/>

3. 然后就当它是一个其他控件一样，初始化。

4. 要在WebView中加载页面，使用webview.loadUrl(url);

5. 如果要把自己的app当作浏览器，就要用函数webview.setWebViewClient( WebViewClient )

   而这个WebViewClient需要重写一个shouldOverrideUrlLoading函数。

   ```java
   webView.setWebViewClient(new WebViewClient(){
   @Override
   public boolean shouldOverrideUrlLoading(WebView view, String url) {
   // TODO Auto-generated method stub
   //返回值是true的时候控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器去打开
        view.loadUrl(url);
        return true;
    }
   //WebViewClient帮助WebView去处理一些页面控制和请求通知
   });
   ```

6. 然后如果你访问的网页是javaScript写的，则需要启动JavaScript

   ```java
   WebSettings setting=webView.getSettings();
   settings.setJavaScriptEnabled(true);
   //上面就启动了JavaScript

   //而如果你想在打开网页的过程中出现进度条
   webview.setWebChromeClient(new WebChromeClient() {
     @Override
     public void onProgressChanged(WebView view,int newProgress) {
       //参数newProgress就是打开的进度，1-100
       //所以这里可以根据newProgress的数值大小初始化一个  进度条对话框  ，创建或消失，然后就可以显示了。
       //具体如何创建进度条对话框就不写了
     }
   });
   ```

7. 同时，WebView会自动帮我们保存上一个页面是什么。所以如果我们想返回上一个页面，有两种方法：重写物理按键返回， 自己创建一个Button返回。

   ```java
   //在Activity中重写物理按键的返回
   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event){
     if(keyCode==KeyEvent.KEYCODE_BACK){
       if(webView.canGoBack()){
         webView.goBack();//返回上一页面
   	  return true;
       }
       else {
         System.exit(0);//退出程序
       }
     }
     return super.onKeyDown(keyCode, event);
   }
   ```










## 控件的一些属性

1. layout_gravity：相对于父布局的位置摆放
2. gravity：文字或图片的内容相对于自己控件的位置摆放
3. drawableTop：控件内部上方的图片显示，也就是按钮上有个图片之类的
4. ​







## 第一行代码笔记

1. 在drawable文件夹新建drawable-hdpi,drawable-xhdpi,drawable-xxhdpi,  如果只有一张图片，就放在drawable-xxhdpi文件夹上。
2. 应用的图标名称修改在  manifest文件上。
3. logt+tab自动生成TAG，logi+tab自动生成Log.i();
4. crtl+o选择重写哪个方法
5. 书p38-p39说明如何创建菜单以及对应的事件响应
6. 可以通过设定Activity中的  action与category，然后在intent也构造出有相同action与category的intent，那么startActivity(intent)就能调用这个activity。  并且，还可以调用其他程序，如调用浏览器，Intent intent=new Intent(Intent.ACTION_VIEW);  intent.setData(URI);  startActivity(intent);  这样就能调用默认浏览器了。
7. 在Activity重写onBackPressed也算是退出


