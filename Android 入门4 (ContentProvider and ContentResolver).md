## ContentProvider和ContentResolver的简单使用



#### 什么是ContentProvider

1. ContentProvider相当于是对 数据增删改查 的上一层封装， 提供给别的应用对数据进行增删改查， 好处是当 数据存储方式进行变动的时候，外面的代码依然可以不动。
2. 也就是说，如果我们的app要对其他app的数据进行调用，就是要通过 其他app提供的 contentprovider进行操作。
3. Android系统中对一些数据写好了ContentProvider，我们可以直接对那些数据进行调用。
4. 有 联系人， 电话号码， 短信内容等。



#### 什么是ContentResolver

1. 我们通过ContentProvider用数据时， 我们不是直接 操作他们的ContentProvider，因为如果要处理多个app的数据，那么就有非常多的ContentProvider，所以我们就用  ContentResolver 操作 ContentProvider
2. ContentResolver的函数类似 SQLite的函数， 就是  insert，update， query，delete之类的，因为它始终都是数据管理之类的东西。
3. 然后直接就到了操代码的环节了，  因为一些理论知识，一些类的类型还没搞清楚



#### 什么是URI

1. ContentResolver操作数据时，你要告诉它是搞哪条数据，哪个数据库之类的，URI就是充当路标功能。
2. URI的获取通常是在ContentProvider的身上获取。  所以现在很明显了， ContentProvider提供URI给ContentResolver用，使其来操作ContentProvider管理的数据。





####获取联系人

```java
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
//在android 6.0以上， 一些危险权限不能仅仅是在 配置文件中 写一下就OK
//还需要申请权限，获取联系人的信息 就是危险权限
//只需要一句申请函数即可
// requestPermissions(new String[]{你需要申请的权限},一个code);

requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},0);
```





```java
/**
 *要声明的是 联系人的 电话号码  和 名字 是分开两个不同的表
 *首先是获取联系人的 _ID 和它的 DISPLAY_NAME
 
 * Contact是系统提供的ContentProvider
 */
private void readContacts(){
	ContentResolver cr=getContentResolver();
    Uri uri=Contact.CONTENT_URI;   //uri指向联系人数据库的其中一个表，里面有   _ID和display_name
    
    //就像SQLite操作，uri指向一个表，然后查询你想要的列
    Cursor cursor=cr.query(uri,new String[]{Contact._ID,Contact.DISPLAY_NAME},null,null,null)
    if(cursor！=null){
        while(cursor.moveToNext()){
            int id=cursor.getInt(cursor.getColumnIndex(Contact._ID));
            String name=cursor.getString(cursor.getColumnIndex(Contact.DISPLAY_NAME));
            
            getPhoneNumber(id);
        }    
	}
}
```





```java
/**
 *要声明的是 联系人的 电话号码  和 名字 是分开两个不同的表
 *获取到id，而id应该是外键，电话号码中也有id。
 *所以只能通过id去找电话号码
 
 *Phone是系统提供的ContentProvider
 */
private void getPhoneNumver(int id){
    ContentResolver cr=getContentResolver();
    Uri uri=Phone.CONTENT_URI;  //uri指向联系人数据库的 phone表，里面有   _ID和 NUMBER
    
    Cursor cursor=cr.query(uri,new String[]{Phone._ID,Phone.NUMBER},
                          Phone._ID+"="+id,null,null);
    if(cursor！=null){
        while(cursor.moveToNext()){
            String number=cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
        }    
	}
}

```



综上，获取数据只有新的三句话

ContentResolver cr=getContentResolver();    拿到Activity的ContentResolver

Uri uri=Phone.CONTENT_URI;			      拿到 ContentProvider 提供的指向 表 的URI

Cursor cursor=cr.query(uri,new String[] { ContentProvider提供的表的列 } , 条件句，条件补充句 ,null);  

​									     拿到对应的  Cursor







####添加联系人

```java
/**
 *添加联系人要更加麻烦些
 *当然，也是通过   ContentResolver增删改查
 */

private void addContacts() {
        ContentResolver cr=getContentResolver();
        ContentValues values=new ContentValues();

        //插入一个空的 contentvalues 可以在联系人中得到空的一行

        //RawContacts是系统给的ContentProvider，content_uri明显就是指向content表的uri
        //所以向content表中指出要一行空行，并得到指向这个空行的uri
        //然后要那一行空行的id，通过  辅助类 ContentUris得到uri后面的id
        //然后对那个空行做文章
        Uri uri=cr.insert(ContactsContract.RawContacts.CONTENT_URI,values);
        long raw_contact_id= ContentUris.parseId(uri);   //相当于得到多出来那一行的id


        //添加人名
        values.clear();

        //以下两行是固定动作
        // StructuredName估计也是ContentProvider
        values.put(StructuredName.RAW_CONTACT_ID,raw_contact_id);
        values.put(StructuredName.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE);
        values.put(StructuredName.DISPLAY_NAME,"Bosco");
        cr.insert(ContactsContract.Data.CONTENT_URI,values);

        //添加电话号码
        // Phone估计也是ContentProvider
        values.clear();

        //两行固定动作
        values.put(Phone.RAW_CONTACT_ID,raw_contact_id);
        values.put(Phone.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        values.put(Phone.NUMBER,"678910");
        cr.insert(ContactsContract.Data.CONTENT_URI,values);
    }

}
```



综上，增加数据操作比较麻烦

用到的系统的ContentProvider有三个   RawContacts，Phone，StructuredName

* RawContacts.CONTENT_URI估计是新联系人的表

