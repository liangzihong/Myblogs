## Fragment的简单使用



### 静态加载

1. 如果要弄一个微信对话框，则可以分成标题栏和对话栏两个Fragment

2. 所以staticfragment_layout有两个<fragment>

3. 每个<fragment>都需要有一个name属性，这个属性值为一个   Fragment类或者它的子类

4. 在这个子类中，需要重写 ==onCreateView== 方法，这个方法的任务就是把  这个fragment对应的布局文件变成view返回回去。

   ```java
   //将布局文件转化为view
   View view=inflater.inflate(R.layout.simplefragment2_layout,container,false);
   return view;
   ```

5. 要注意的是，因为主布局文件拥有fragment，所以其实主布局文件也拥有fragment的控件，并且可以直接findviewbyid，而在fragment类中就要view.findviewbyid

6. 所以Fragment静态加载就是加布局文件中有fragment，然后每个fragment有对应的id和name，然后每个fragment类又重写onCreateView然后返回view。











### 动态加载

1. 动态加载其实就是不在布局文件中用<fragment> 节点来布置，而是通过代码。

2. 动态加载要点就是把fragment类去代替原有布局中的一些layout，或者是替换原本的<fragment>。可以增删改查，所以灵活性更强。

3. 代码如下：

   ```java
   SimpleFragment1 sf1=new SimpleFragment1();  //声明一个fragment类
   FragmentManager manager=getFragmentManager(); //创建一个fragment管理者
   FragmentTransaction beginTransaction=manager.beginTransaction();  //创建一个fragment事务
   beginTransaction.add(R.id.frame,sf1);     //事务可以通过增删改查来代替不同layout或    fragment,add是增加，意思是在frame剩余的地方加入
   beginTransaction.addToBackStack(null);    //设置按返回键就可以退回刚才的操作
   beginTransaction.commit();   //事务只有commit提交了，才能是真正进行操作
   //然后操作就搞定了，然后会将fragment去填充layout或者fragment
   ```











### Activity动态加载Fragment的同时，向Fragment发送信息

1. 信息传递的载体是   Bundle， 可以通过Bundle的put方法传递一些基本类型，也可以传递  Serizable接口

   ```java
   Stringi info="zst";
   Bundel bundle=new Bundle();
   bundle.putString("name",info);
   ```

   ​

2. 而fragment类是有一个  fragment.setArgument(Bundle) 的参数，然后把bundle传递进去即可。

3. 然后就像动态加载那样

   ```java
   SimpleFragment2 sf2=new SimpleFragment2();
   sf2.setArguments(bundle);
   //这样fragment就带有了bundle的数据
   //所以要在simpleFragment2那里处理消息。
   FragmentManager manager=getFragmentManager();
   FragmentTransaction beginTransaction=manager.beginTransaction();
   beginTransaction.add(R.id.astif_layout,sf2,"SimpleFragment2");  //这样为fragment添加tag，那又有什么用？
   beginTransaction.addToBackStack(null);
   beginTransaction.commit();
   ```


4. 同时，在Fragment类中，也要处理接收信息。

   ```java
   Bundle bundle=getArguments();   //fragment自带getArgument函数
   if(bundle!=null) {			//如果fragment带有参数
      String InfoFromActivity = (String) bundle.get("name");
      textView = (TextView) view.findViewById(R.id.sf2_textview);
       textView.setText("我的名字是：" + InfoFromActivity);
   }

   ```







### Activity静态加载Fragment并传递信息

1. 静态加载传递信息就比较简单了，我们可以直接把  fragment 实例化拿出来，直接操作即可

   ```java
   FragmentManager manager=getFragmentManager();
   Fragment fragment=manager.findFragmentById(R.id.sf1);
   SimpleFragment sf1=(SimpleFragment)fragment;
   //然后就把 SimpleFragment拿到手，想怎样传递数据都行。（通过setter，getter）
   ```

   ​



### Fragment向Activity传递信息

1. 这个所谓的传递信息，也就是把在Fragment类中，把所在的Activity拿到手，然后对Activity进行接口化，就可以实现多态了。

2. ```java
   //首先要将Activity接口化
   public interface ConverInfo{
     public void getInfo(String info);
   }

   //在Fragment类中
   @Override
   public void onAttach(Activity activity){
     ConverInfo listener=(ConverInfo)activity;
     super.onAttach(activity);
   }

   //这样就算传递信息了... 因为在Activity中已经有这个字符串了
   ConverInfo.getInfo("I love zst");

   //在Activity中
   @Override
   public void getInfo(String info){
     Toast.makeText(this,"I have get the infomation: "+info,Toast.xxx).show();
   }

   ```


​    

3. 如果是静态的也差不多，因为静态的话，Activity简直就有Fragment的实例，所以直接获取即可。










## ViewPager的简单应用





### ViewPager的添加

1. ViewPager其实就是一个控件，所以用法如其他的控件用法差不多。

2. 首先是要在xml文件中添加

   ```xml
       <android.support.v4.view.ViewPager
           android:id="@+id/viewpager"
           android:layout_height="500dp"
           android:layout_width="wrap_content">
       </android.support.v4.view.ViewPager>
   ```

   viewPager是在android.support.v4.view里面中，要设置viewPager的id和高宽，高宽决定这你滑动时所改变的范围大小。

3. 这个控件就像下拉列表或者是其他需要适配器的东西，它也需要适配器连接数据与viewPager，而这里的数据可以是View和Fragment， 所以按照以往的套路，现在的目标很明确，就是  将view 或者Fragment 加入到适配器中，然后将适配器导入即可。





### ViewPager的适配器



#### 一、ViewPager的内容是View

1. 当ViewPager的内容是View，我们任务就是要将你想也通过滑动的页面的xml文件转变为view对象，然后加入适配器，然后导入即可

2. 将xml文件转变为view，之前我们也接触过：

   ```java
           viewList=new ArrayList<>();

           /**
            * 从layout文件中变为view
            */
           View view1=View.inflate(this,R.layout.view1,null);
           View view2=View.inflate(this,R.layout.view2,null);
           View view3=View.inflate(this,R.layout.view3,null);
           View view4=View.inflate(this,R.layout.view4,null);

   ```

   直接调用View类的inflate函数，参数为xml文件和null，得到的就是view。

3. MyViewPagerAdapter

   这个Adapter是需要自己写的

   ```java
   //这个适配器需要  extends  PagerAdapter
   //然后需要 添加一个数据源List<View>,重写四个方法以及构造函数
   //这些方法的内容都是差不多的，你记住就可以了
   //做完这些，直接用即可

   public class MyViewPagerAdapter extends PagerAdapter   {
       private List<View> viewList;
       public MyViewPagerAdapter(List<View> viewList){
           this.viewList =viewList;
       }

       /**
        * 返回页面的个数
        * */
       @Override
       public int getCount() {
           return viewList.size();
       } 
       /**
       *view是否来自于对象，官方推荐是返回view==object
       * */
      @Override
      public boolean isViewFromObject(View view, Object object) {
          return view==object;
      }

      /**
       * 实例化一个页卡
       * */
      @Override
      public Object instantiateItem(ViewGroup container, int position) {
          container.addView(viewList.get(position));
          return viewList.get(position);
      }

      /**
       * 销毁一个页面
       * */
      @Override
      public void destroyItem(ViewGroup container, int position, Object object) {
          container.removeView(viewList.get(position));
      }
    }
   ```

4. 控件set适配器

      ```java
      viewPager.setAdapter(myviewpageradapter);
      ```

5. 完成。





#### ViewPager的内容是Fragment

1. 同样，viewPager的内容是fragment，意思是fragment类，所以就是通过 Fragment 把layout文件变为 view对象，并且通过 fragment 还可以对 view 的控件进行更多的操作，当它是一个Activity来写。

2. 将layout布局文件变为view：

   ```java
   // 继承Fragment就是  fragment类
   //但是从这里开始我们有点变化，我们继承的Fragment不再是 Android.app.Fragment
   //而是android.support.v4.app.Fragment
   //就是可以更好地兼容旧版本安卓，所以也没什么大变动
   public class Fragment2 extends Fragment {
       @Override
       public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
           return inflater.inflate(R.layout.view2,container,false);
       }
   }
   //这个老规矩，View view=inflater.inflate（布局文件，container，false)返回对应的view
   //然后可以再通过   view.findViewById(xx)找到对应的控件进行操作。
   ```

3. 写适配器，适配  List\<Fragment\> 的 MyFragmentPagerAdapter

   ```java
   //这个其实也很简单，只需要继承  FragmentPagerAdapter
   //然后添加 一个List<Fragment>的成员，重写几个方法和构造函数即可
   //需要注意的是  以后的 Fragment都是  android.support.v4.app.Fragment
   public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
       private List<Fragment> fragmentList;
       public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
           super(fm);
           this.fragmentList = fragmentList;
       }
       /**
        * 返回具体的页卡
        * @param position
        * @return
        */
       @Override
       public Fragment getItem(int position) {
           return fragmentList.get(position);
       }
       /**
        * 返回页卡的个数
        * @return
        */
       @Override
       public int getCount() {
           return fragmentList.size();
       }
   }
   ```

4. 在主Activity中的代码

   ```java
   //道理也很简答，就是创建Fragment，创建List<Fragment>，创建适配器，适配器导入，KO
   //如果想要比如实现类似微信的转页面，然后不同页面有不同效果的，就是在Fragment中进行对应的操作。
           fragmentList=new ArrayList<>();

           Fragment1 fragment1=new Fragment1();
           Fragment2 fragment2=new Fragment2();
           Fragment3 fragment3=new Fragment3();
           Fragment4 fragment4=new Fragment4();

           fragmentList.add(fragment1);
           fragmentList.add(fragment2);
           fragmentList.add(fragment3);
           fragmentList.add(fragment4);

           MyFragmentPagerAdapter adapter2=new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList);
   //getSupportFragmentManager()。
           viewPager.setAdapter(adapter2);

   //要注意的是，以往我们获取  FragmentManager都是直接  getFragmentManager，而现在我们对应的Fragment是android.support.v4.app.Fragment , 所以对应获取FragmentManager用的方法是  
   //getSupportFragmentManager()。
   ```

   ​








## Gallery的简单应用



1. Gallery的添加，就是一个普通控件，没什么影响，宽高分别是  match-parent 和 wrap-content

2. Gallery也是需要适配器的，而这里的适配器显然就是图片的资源，但是  ArrayAdapter\<String\>和SimpleAdapter都不是这样的适配器，所以我们自己写适配器

3. ImageAdapter

   ```java
   public class ImageAdapter extends BaseAdapter {
       private int[]resource;
       private Context context;

       public ImageAdapter(int []resource,Context context){
           this.resource=resource;
           this.context=context;
       }

       /**
        * 返回已定义的数据源的总数量，也可以设置无限大，可以无限循环
        * 等下也会这样做
        * @return
        */
       @Override
       public int getCount() {
           return Integer.MAX_VALUE;
       }
     
       /**
        * 告诉适配器取得目前容器中的对象
        * 固定的写法，返回数据源的目标对象
        * @param position
        * @return
        */
       @Override
       public Object getItem(int position) {
           return resource[position];
       }
   ```

   ```java
    
       /**
        * 告诉适配器取得目前容器中的ID
        * 固定的写法，返回position
        */
       @Override
       public long getItemId(int position) {
           return position;
       }
   ```


```java
   /**
    * 取得目前欲显示的图像view，传入数组ID值使之读取与成像
    * 人话，你想要呈现在屏幕上的是什么，你就返回那个的类给他
    * 如图片就是ImageView之类的
    */
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
       position=position%resource.length;
       ImageView imageView=new ImageView(context);
       imageView.setBackgroundResource(resource[position]);

       /**
        * 设置以什么样的规格出现
        * setScaleType表示它的比例是怎样的
        */
       imageView.setLayoutParams(new Gallery.LayoutParams(400,300));
       imageView.setScaleType(ImageView.ScaleType.FIT_XY);
       return imageView;
   }
}
```
  
4. Gallery对应的事件响应函数是   setOnSelectedListener(xxxx);

```java
   //我们可以从中设置选中图片就在Gallery的下方放大显示图片
   //下面有一个ImageView，然后image设置backGroundResouce，在调整比例   setScaleType   
   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           image.setBackgroundResource((Integer) adapter.getItem(position%resource.length));
           image.setScaleType(ImageView.ScaleType.FIT_CENTER);
       }
```

























