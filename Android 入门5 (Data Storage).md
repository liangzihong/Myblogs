## 文件存储



#### SharedPreferences

1. 首先要声明的是，这种信息存储存储在手机的 data/data/《包名》的某个文件下

2. SharedPreferences相当于是  一个   HashMap<String , object> 的东西， 以键-值对

   的方式存储在文件中。 ==可通过 IDE的ddms功能去查看模拟手机的文件== 

3. 使用也很简单



SharedPreferences的存

```java
//声明SharedPreferences
//声明Editor
	import android.content.SharedPreferences;
    private void userSharedPreferences(){
        SharedPreferences pref=getSharedPreferences("FirstStore",MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString(user.getText()+"",password.getText()+"");
        editor.commit();
        finish();
    }
```



SharedPreferences的取

```java
/**
 *验证账号密码是否为  FirstStore文件中存储的一样
 */

    private boolean useSharedPreferences(String inputname,String inputpassword){
        SharedPreferences pref=getSharedPreferences("FirstStore",MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        //pref.getString(name, defaultvalue)，如果 name搜索不到，则返回默认值 defaultvalue
        if(pref.getString(inputname,"").equals(inputpassword)){
            return true;
        }
        else return false;
    }
```



综上， SharedPreferences的应用很简单，其实只有简单的四步：

* 声明 SharedPreferences和对应的  文件名 和 文件类型

  SharedPreferences pref=getSharedPreferences(  文件名 , 文件类型 );

* 声明Editor

  Editor editor=pref.edit();

* 放键-值对并提交

  editor.putString(key,value);

  editor.commit()

* 通过键，取出值

  String value= pref.getString(key, defaultValue);









#### SQLite数据库

1. SQLite数据库无需安装，可在多个操作系统中运行，规模小，适合用于android
2. 每个app可以有自己的数据库，只需要一个语句就可以出来
3. 用的基本上都是  增删改查的 sql的语句





利用 sql 的语句直接执行

```java
    private void useSQLStatement() {
        //每个app都有一个自己的数据库，直接声明创建即可。不麻烦
        //第一句是如果没有数据库，就创建一个数据库叫test1.db
        //如果已经有了这个数据库，就打开
        SQLiteDatabase database = openOrCreateDatabase("test1.db", MODE_PRIVATE, null);

        //如果没有这个表，就创建，如果有，就打开
        database.execSQL("create table if not exists " + TABLENAME + " (_id integer primary            key autoincrement, " +"name text not null, sex text not null, age integer not                  null)");
        database.execSQL("insert into " + TABLENAME + "(name,sex,age) values 	                        	     ('Ben','male',20)");
        database.execSQL("insert into " + TABLENAME + "(name,sex,age) values 	                        	     ('nancy','female',36)");
        database.execSQL("insert into " + TABLENAME + "(name,sex,age) values 	                        	     ('ali','female',35)");

        Cursor cursor = database.rawQuery("select * from user where sex='female' ", null);
        while (cursor.moveToNext()) {
            Log.i(TAG, "onCreate: " + cursor.getInt(cursor.getColumnIndex("_id")));
            Log.i(TAG, "onCreate: " + cursor.getString(cursor.getColumnIndex("name")));
            Log.i(TAG, "onCreate: " + cursor.getString(cursor.getColumnIndex("sex")));
            Log.i(TAG, "onCreate: " + cursor.getInt(cursor.getColumnIndex("age")));
        }
        cursor.close();
        database.close();
    }

```



cursor相当于一个查询得到的结果，它包含查询到的行。

* cursor.moveToNext()得到下一个行 或 false
* cursor.getInt( 列名的序号 )  把这一行中 的某一个列 的数据变成int 。 同理有 getString  ，getDouble
* cursor.getColumnIndex(列名)  把列名 变成 int ，可以给getXXX使用







利用 系统 内置的 sql 函数会更加方便

```java
    private void useSQLFunction(){
        SQLiteDatabase database=openOrCreateDatabase("test1.db",MODE_PRIVATE,null);
        database.execSQL("create table if not exists " + TABLENAME + " (_id integer primary            key autoincrement, " +
         "name text not null, sex text not null, age integer not null)");

        //database.insert()插入功能
        ContentValues values=new ContentValues();
        values.put("name","Wongtsuiyu");
        values.put("age",35);
        values.put("sex","female");
        database.insert(TABLENAME,null,values);
        values.clear();  //清除

        values.put("name","Moon");
        values.put("age",28);
        values.put("sex","female");
        database.insert(TABLENAME,null,values);
        values.clear();  //清除

        values.put("name","Louis");
        values.put("age",35);
        values.put("sex","male");
        database.insert(TABLENAME,null,values);
        values.clear();  //清除

        //database.update()更新功能
        //将男的变成100岁
        values.put("age",100);
        database.update(TABLENAME,values,"sex='male' ",null);



        //database.query()  查询功能
        //查出所有性别为女的

        Cursor cursor=database.query(TABLENAME,null,"sex='female'",null,null,null,"age");
        if(cursor!=null){
            while (cursor.moveToNext()) {
                Log.i(TAG, "onCreate: " + cursor.getInt(cursor.getColumnIndex("_id")));
                Log.i(TAG, "onCreate: " + cursor.getString(cursor.getColumnIndex("name")));
                Log.i(TAG, "onCreate: " + cursor.getString(cursor.getColumnIndex("sex")));
                Log.i(TAG, "onCreate: " + cursor.getInt(cursor.getColumnIndex("age")));
            }
        }

    }

```



ContentValues相当于一个  HashMap

* 把列名 和对应的数据放入 values中
* 然后 database对values进行增删改查的操作即可。







#### 文件读写功能

1. 你在android可以控制文件的读写，具体文件的位置我不太清楚，只要你写出它文件名，就会默认把文件放啊这应该放在的地方，不用管他。
2. 都是用 FileOutputStream和FileInputStream来对文件读写操作。



写文件

```java
    /**
     * 写文件，把字符串变成字节写入文件中
     * 利用   string.getBytes()
     * @param content
     * @throws Exception
     */
    private void writeFiles(String content) throws Exception{

        //openFileOutput(文件名，模式)直接返回输入流。  直接fop.write(string.getBytes())
        FileOutputStream outputStream=openFileOutput("store.txt",MODE_PRIVATE);
        outputStream.write(content.getBytes());
        outputStream.close();
    }
```



* openFileOutput( 文件名， 文件类型) 返回一个对应这个文件的 FileOutputStream对象
* 如果文件类型不是 append ，那么会覆盖上一次的文件而不会追加， 这里要注意。
* 直接  outputStream.write(  string.getBytes());
* 这是以前没用过的方法，把string变成字节，然后写到文件中



读文件

```java
    /**
     * 读文件，把字节变为字符串
     * 利用   bytearrayoutputStream这个类
     * baos.write(byte [],start,cnt)
     * baos.toString()
     * @return
     * @throws Exception
     */
    private String readFiles() throws Exception {

        //利用到新知识  bytearrayoutputstream，可以直接 toString将字节变成String
        FileInputStream inputStream=openFileInput("store.txt");
        ByteArrayOutputStream baos=new ByteArrayOutputStream();

        byte []arr=new byte[1024];
        int len=0;
        while((len=inputStream.read(arr,0,1024))!=-1){
            baos.write(arr,0,len);
        }

        String ret= baos.toString();
        baos.close();
        inputStream.close();

        return ret;
    }

```



* 把字节写到  ByteArrayOutputStream中，可以最后toString（）把字符串变回来。



































