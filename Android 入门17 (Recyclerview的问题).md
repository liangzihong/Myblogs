## Recyclerview机制的入门级理解

参考博客：https://blog.csdn.net/xyq046463/article/details/51800095

首先来一段高手的引用

对于使用ViewHolder引起的图片错乱问题，相信大部分人都有遇到过，我也一样，对于解决方法也有所了解，但一直都是知其然不知其所以然。

所以，这次直接把ViewHolder的工作原理，通过简单的demo代码来验证一次，验证后对于图片错乱和闪烁这种问题的成因就很清楚了。

下面先上一副图

![8](E:\Blog\8.png)





这幅图就比较清晰的画出了ViewHolder的工作原理。

可以看到，图中左上角item1上面有一条蓝色的线，item7下面也有一条蓝色的线，这两条线就是屏幕的上下边缘，我们在屏幕中能看到的内容就是item1~item7。

当我们控制屏幕向下滚动时，屏幕上的变化是，item1离开了屏幕，紧接着item8进入了屏幕，这是我们看到的。在item1离开，item8进入的过程中，还有一个我们看不到的过程。当item1离开屏幕时，它会进入Recycler(反复循环器)构件，然后被放到了item8的位置，成为了我们看到的item8。





所以，可以理解成 Recyclerview是一个穷学生，贷款永远是拆东墙补西墙。

那么问题来了，究竟是哪一个函数表示 有新的贷款要还呢？

答案就是 **OnBindViewHolder** 函数 ，onBindViewHolder表示绑定一个viewholder，这里就表示 必须要拿之前的钱去还贷款。



所以，我在 MyGanMeizhi中的一段函数，细看其实是一个大BUG

```java
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        MeizhiModel model=modellist.get(position);

        Glide.with(context).load(model.getUrl()).into(holder.pic);
        holder.statement.setText(model.getText());

    }
```

如果我一移动，在屏幕中重新出现的viewholder就会在不同线程 同时 利用Glide来进行图片加载，那么就会出现很多问题，比如 图片闪烁，图片不断错位，因为你不能保证哪个线程先完成。   反正就真的尴尬。





## 解决方法

其实网上说的解决方法在我这里好像都没有什么用，它还是会闪烁和错位，那我就看了下 MEIZHI的源码，发现它的图片都是 统一大小的。

那我也尝试过一下，发现每次 统一大小后闪烁几乎没发生，错位也没有发生。那我只好统一大小作为解决方法了。

那这样又有一个问题，就是图片的重点有点不同，此时就要修改一下  **ImageView的ScaleType** 属性，我把ScaleType属性变成 “centerCrop”  是按比例 缩放图片，这样就比较好了。

至此，这个BUG就完成了



### 收获

解决这一过程，得到的收获其实也有。

1. recyclerview有全局刷新 和 局部刷新这两个功能，全局刷新就是  notifydatasetchanged().

   局部刷新就是 notifyItemInserted(int position) 单个更新

   ​			notifyItemRangeInserted(int startPosition,  Collection<T>)

   局部刷新明显比 全局刷新要高效率

2. recyclerview 的瀑布流是怎样写得。

   其实就是    setLayoutManager（new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

   这样就行。

3. 如何对ScrollerView的  addOnScrollerListener进行监听，发生拉到底部的事件

   其实就是   if( newState== RV.SCOLL_STATE_IDLE)

4. refreshLayout如何把它设置成 一进画面就是刷新，并更新数据

   这是有诀窍的，这个google即可。

5. SwipeRefreshLayout里面包有  Recyclerview时

   在 swipeRefreshLayout 设置一个属性   app:layout_behavior="@string/appbar_scrolling_view_behavior"

   这个操作的话，你要 依赖两个东西   就是   recyclerview 和 Material Design 的包，并且这两个包 的版本要一样。

   通常是   25.3.7？   反正25.3.1/25.1.0一定能用。

6. 如果你要弄一个  特定的int出来，那其实就是要一个   R.ID.xxxxx

   那你可以怎么办呢？

   可以在 values下新开一个xml文件，名字随便。   在里面添加一个  item项，有 name属性和style属性，style=“id”表示就是 R.id   style=“color” 表示就是 R.color

7. 如何自定义toolbar

   https://developer.android.com/training/appbar/setting-up.html?hl=zh-cn

   这里面说的很清楚。

   而如果你要toolbar随着页面下翻而消失，就要 用   CollapsingToolbarlayout

   如果不用消失，就用FrameLayout，并且把  Toolbar这个控件放在第一即可

   Toolbar.setNavigationIcon(xxxx) 这个可以设置  标题栏返回按钮的 图标，相当于直接就有了 响应事件？

   setSupportActionBar（toolbar）完成导入

8. Recyclerview可以通过   MyViewHolder作为i父类，生成两个子类，通过子类的不同 而有不同的布局文件。

   因为 onCreateViewHolder会有一个int viewtype参数，可以通过这个布局文件 生成不同的viewHolder

   ```java
           @Override
           public int getItemViewType(int position) {
               return position == 0 || position == 2 ? 0 : 1;
           }
   ```

   只需要控制这个 父类的函数，就可以规定 哪个位置返回的 viewtype 然后在  onCreateViewHolder判断。