## adb

使用 adb工具，可以查看手机和模拟器中 =，这个项目数据库和各种表的数据之类的。

这里就大致地说一下。



首先  adb.exe是在  sdk目录下的 platform-tools下的，如果你要在 cmd中使用，就要把 系统变量中的 path再添加一个，把 platform-tools的目录放入 path中。 然后就可以使用了。

然后，在cmd中写 adb shell。

然后 按 su进入 超级管理员权限。

然后通过  cd /data/data/包名/databases 进入 数据库。

 然后通过 ls命令得到各种数据库

然后  sqlite3+ 数据库名 进入数据库

然后 .table查看表名

接着下来的操作都是  标准sql，例如   select * from user; 查看 user表里的东西。





## LitePal

LitePal是一个 开源的第三方库的一个 ORM框架，用这个比用  前面的三种文件存储方式要好。

这里再 深入加深什么是 ORM， ORM是 对象关系映射，能够使程序以 面向对象的方式存储数据库。

它能通过  类的成员自动制造出一个表，表的列跟成员名字类型一样， 并且封装  增删改查功能。 

而sql2o则不是纯粹的 ORM，因为他不能自动制造一个表，只能由用户自己来搞。



LitePal的前提条件。

首先 ，要在 app/gradle中添加依赖， 现在 LitePal已经发展到1.6.1

```xml
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    compile 'com.android.support:appcompat-v7:25.3.1'
    compile 'com.android.support.constraint:constraint-layout:1.0.0-alpha7'
    
	compile 'org.litepal.android:core:1.6.1'
    
	testCompile 'junit:junit:4.12'
}
```



然后，要在  manifest配置文件中，添加一句话

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.liangzihong.litepal_use">

    <application
        
        android:name="org.litepal.LitePalApplication"
        
                 
        android:allowBackup="true"
```



然后，要在  main目录下 新建一个  assets 资产文件夹，并在里面新建一个  litepal.xml 文件

内容如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<litepal>
    <dbname value="user"></dbname>
    <version value="1"></version>

    <list>
        <mapping class="MODELS.User"></mapping>
    </list>
</litepal>
```

value 是数据库的名字

version是数据库的版本，如果要新加表，或者 改变表，就要改version。

而 list下面的  mapping 则是  你要创建表所根据的对象。  这个对象要是  带包名的全名。



当然，你的  model要是 有getter和setter全套功夫。

并且并且

要继承  DataSupport ！！！

然后就开始应用了。





### 具体的CRUD功能和创建数据库的功能

```java
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.createTable:

                //创建数据库，直接  LitePal.getDatabase()
                LitePal.getDatabase();
                break;
            case R.id.addUser:

                //增加数据库的数据
                //先新建一个 model
                //然后  model.save()即可保存
                User b=new User("Ali",35,"female");
                b.save();
                User c=new User("wongtsuiyu",35,"female");
                c.save();
                User d=new User("Maming",40,"male");
                d.save();
                User e=new User("Ben",35,"male");
                e.save();
                break;
            case R.id.updateUser:

                /**
                 * 更新功能，把一个你想更新的属性放在一个新的model上
                 * 然后 model.updateAll("name=?","xxx");
                 * 这几个参数是可变参数，可以有n个条件，这样就充当了  where 的角色。
                 */
                User f=new User();
                f.setName("nobody");
                f.updateAll("name=?","Maming");
                f.save();
                break;

            case R.id.deleteUser:

                /**
                 * 删除功能，DataSupport.deleteAll(Model.class,"name=?","xx")
                 * 第一个参数其实是相当于表名，因为 Model.class对应的就是表名
                 * 然后后面的可变长参数就是 相当于 where
                 */
                DataSupport.deleteAll(User.class,"name=?","nobody");
                break;
            case R.id.queryUser:

                /**
                 * 查询功能，
                 * DataSupport.findAll(Model.class)  搜出全部行，返回 List<\Model\>
                 * DataSupport.select(你想要的列名).find(Model.class)
                 * DataSupport.where(条件,"xx").find(Model.class)
                 * DataSupport.order("xx").find(Model.class)
                 *
                 * 然后可以混合在一起
                 * arr=DataSupport.where("age > ?","30")
                 * .order("age")
                 * .find(User.class);
                 * 反正最后一定要 Model.class表明是哪一个表
                 */

                List<User> arr= DataSupport.findAll(User.class);
                arr=DataSupport.where("age > ?","30")
                        .order("age")
                        .find(User.class);

                for (User user:arr){
                    Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }
```



创建：创建数据库，直接  LitePal.getDatabase()



增：增加数据库的数据
​	先新建一个 model
​	然后  model.save()即可保存



改：更新功能，把一个你想更新的属性放在一个新的model上
​	然后 model.updateAll("name=?","xxx");
​	这几个参数是可变参数，可以有n个条件，这样就充当了  where 的角色。



删：删除功能，DataSupport.deleteAll(Model.class,"name=?","xx")
​	第一个参数其实是相当于表名，因为 Model.class对应的就是表名
​	然后后面的可变长参数就是 相当于 where



查：DataSupport.findAll(Model.class)  搜出全部行，返回 List<\Model\>
​	DataSupport.select(你想要的列名).find(Model.class)
​	DataSupport.where(条件,"xx").find(Model.class)
​	DataSupport.order("xx").find(Model.class)

​	然后可以混合在一起
​	arr=DataSupport.where("age > ?","30")
​	.order("age")
​	.find(User.class);
​	反正最后一定要 Model.class表明是哪一个表



所以真的很简单，只是要熟悉罢了。  用来用去 都是  DataSupport这个类的方法。

不过用的时候最后   封装成一个  DAO ，方便操作。









































