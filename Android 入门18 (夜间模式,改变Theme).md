### 夜间模式的使用

1. 首先，夜间模式其实就是变换他们的背景，这是原理。

2. ==以下例子对我的一个计算器作为例子讲解。== 

3. 那么怎么对控件中处理呢?

   ```
   首先需要在 values文件夹中创建一个  attr.xml 文件
   里面可以是一个 主题。例如  panel1，panel2，以后我的panel1控件部分的颜色就根据这个panel1来安排
       <attr name="panel1" format="color"></attr>
       <attr name="panel2" format="color"></attr>
       <attr name="show_view" format="color"></attr>
   ----------------------------------------------------------------- 
   然后在 styles.xml进行修改
   <style name="AppTheme.Light" parent="Theme.AppCompat.Light.NoActionBar">
   	<item name="panel1">@color/panel1_light</item>
   	<item name="panel2">@color/panel2_light</item>
   </style>

   <style name="AppTheme.Dark" parent="Theme.AppCompat.Light.NoActionBar">
       <item name="panel1">@color/panel1_dark</item>
       <item name="panel2">@color/panel2_dark</item>
   </style>
   这里就是需要用到的Theme， 可以看到 我们给 panel1和panel2设置不同的颜色。
   ----------------------------------------------------------------------

   然后我们如果要改变颜色，要怎么写

   <style name="AppTheme.Light.Cyan" parent="AppTheme.Light">
   	<item name="colorPrimary">@color/cyan</item>
   	<item name="colorPrimaryDark">@color/cyan_dark</item>
   </style>

   <style name="AppTheme.Dark.Cyan" parent="AppTheme.Dark">
   	<item name="colorPrimary">@color/cyan</item>
   	<item name="colorPrimaryDark">@color/cyan_dark</item>
   </style>
   因为这两个style的parent都是上面两个，所以parent有的它也有。
   所以这里修改的是 colorPrimary和colorPrimary的颜色。
   -------------------------------------------------------------------------
   所以我们只要对控件的backround进行设定就好了
   有点特殊的是   
   控件的 backround需要赋值    为  ?attr/panel1或  ?attr/panel2, ?attr/colorPrimary这类

   ```

   以上就是在xml文档中需要编写的代码。

4. 但是，修改theme不是那么随便

   可以参考网站https://blog.csdn.net/wsscy2004/article/details/7562909

   其意思是在activity重新创建的时候再改变Theme

   而因为在计算器中，从设置页的activity到主页面的activity，主页面的activity会调用  onStart函数，所以我们在onStart函数中调用Util类的函数帮助即可。

   ​