## 对话框的简单使用



1. 对话框前提的建立

   ```java
   //对话框建立首先需要一个  builder来设置对话框的属性
   //之后用   builder.create（）就可以直接生成一个对话框

   //builder的声明定义
   private android.app.AlertDialog.Builder builder;
   builder=new AlertDialog.Builder(this);

   //对话框的标题，对话框的图标
   builder.setTitle("确认对话框");
   builder.setIcon(R.mipmap.ic_launcher);

   xxxx //然后是一些各自不同的设置

   setBuilder(builder);

   //设置对话框的确定和取消的作用，直接setPositiveBUtton和setNegativeButton即可。
       private void setBuilder(AlertDialog.Builder builder){
           //对话框的事件响应,按确认和取消都是直接消失
           builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
               }
           });
           builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
               }
           });
       }

   //以上四个就是对话框的基本设置,每个对话框基本都要写的
   ```

    

2. 对话框有五种类型

   ```java
   //确认对话框，最简单的对话框
   //框中只有文字
   builder.setMessage("I love zst");
   ```

    

   ```java
   //单选对话框，对话框中选择其中一个选项
   //单选的选项需要给一个数据源，String[]
   //直接builder.setSingleChoiceItems(string[],0,OnClickListener)  表示就是单选对话框
   String[]SingleChoice={"CEO","CTO","COO"};
   builder.setSingleChoiceItems(SingleChoice, 0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
                 String choose=SingleChoice[which];
          }
   });
   ```

    

   ```java
   //多选对话框
   //选择的项也需要一个数据源，String[]
   //直接setMultiChoiceItems 搞定
   builder.setMultiChoiceItems(MultiplyChoice, null, new DialogInterface.OnMultiChoiceClickListener() {
       //ischecked代表当你取消或选择某一项时，就会调用，此时which就是你所操作的，所以只有你
       //点中或者取消才会调用这个函数
       @Override
       public void onClick(DialogInterface dialog, int which, boolean isChecked) {
               if(isChecked) {
                    Toast.makeText(MainActivity.this, "你选择了"+MultiplyChoice[which], Toast.LENGTH_SHORT).show();}
                else{Toast.makeText(MainActivity.this, "你取消了"+MultiplyChoice[which], Toast.LENGTH_SHORT).show(); }
         }
   });
   ```

    

   ```java
   //列表对话框
   //其实也就是单选对话框没了单选的圆圈，你点中就是选中
   //同样选择的项也是String[]
   builder.setItems(ItemList, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
              xxx
         }
   });
   ```

    

   ```java
   //自定义对话框
   //就是自定义一个对话框
   //其实就是builder调用一个setView（View）,将一个view丢进去即可。。。
   //比如你想  对话框中是  一个图片+EditText，那你的布局文件就是imageView+EditTExt即可
   View view=View.inflate(this,R.layout.mydialog_layout,null);
   builder.setView(view);

   //如果要设置里面的监听事件，可以用   view.findViewById(xx)  得到对应的控件
   ```

3. 召唤出Dialog

   ```java
   Dialog dialog=builder.create();
   dialog.show();
   ```

   ​

