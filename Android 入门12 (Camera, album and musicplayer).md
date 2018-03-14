## 使用相机



* 首先可以这样理解，从你自己的activity到照相机或者是相册，都算是一个 activity跳转的过程，所以大体上可以去这样理解 整个框架。
* 而当你调用 摄像头，你拍摄下来的照片储存在哪里，就要用 Uri来决定，根据uri指向的位置，摄像头这个activity会帮你把照片放到哪里。
* 同样，对于  SD卡上 照片，音乐资源，通过  字符串形式的路径，或者 uri形式的路径，都可以把这个资源调用上来，变成一个  位图 Bitmap，从而能够把图片用于  UI操作。



创建  照片存储位置的URI

```java
    /**
     * 拍照 启动函数
     */
    private void goTakingPhoto(){
        String name=editText.getText()+".jpg";
        if(".jpg".equals(name)){
            Toast.makeText(this, "名字不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //创建  图片将要存放的位置
        file=new File("/sdcard/Pictures/",name);
        try{

            //存在则不行，不存在则可以，并新建这个文件
            if(file.exists()){
                Toast.makeText(this, "该名字已经存在", Toast.LENGTH_SHORT).show();
                return;
            }
            file.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }


        if(Build.VERSION.SDK_INT>=24)
            imageUri= FileProvider.getUriForFile(this,"com.example.Use_Camera.fileprovider",file);
        else
            imageUri=Uri.fromFile(file);

        //启动相机
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHOTO);
        return;
    }
```



* **file=new File("/sdcard/Pictures/",name);   /sdcard/Pictures/是我手机放图片的位置，name是文件名，这样建立一个 File对象。** 

* file.createNewFile();   创建这个文件出来

* **imageUri= FileProvider.getUriForFile(this,"com.example.Use_Camera.fileprovider",file);** 

  **如果SDK>24，就要这样创建 uri，通过 fileprovider这个特殊的 contentprovider 来对文件参数作出URI** 

  ​

  并且这样要在 配置文件中  声明 provider，authority为 getUriForFile第二个参数

  ```xml
          <provider
              android:authorities="com.example.Use_Camera.fileprovider"
              android:name="android.support.v4.content.FileProvider"
              android:exported="false"
              android:grantUriPermissions="true">
              <meta-data
                  android:name="android.support.FILE_PROVIDER_PATHS"
                  android:resource="@xml/file_paths">
              </meta-data>
          </provider>
  ```

  同时还要创建一个 xml文件夹，下面有一个file_paths的xml文件。 第一个个内容随便填，第二个为null

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <path xmlns:android="http://schemas.android.com/apk/res/android">
      <external-path name="my_images" path=""></external-path>
  </path>
  ```

* 如果sdk版本小于24，直接  Uri.fromFile(file)即可得到uri

* 启动 摄像头的 intent设置很简单

  **Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");  说明 仅摄像头接收的action** 

  **intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);   设置照片 应该存放的位置** 

  **startActivityForResult(intent,TAKE_PHOTO);  然后启动摄像头，并且等待回传过来的 结果** 





接收并处理 摄像头activity完成后的东西

```java
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
                    //将拍摄的照片显示出来
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    }
                    catch (Exception e){e.printStackTrace();}
     			}
                break;
            default:
                break;
        }
```

*  Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

  通过这一函数，直接就把  图片对应的URI变成  位图。

* 然后设置即可。



完成。

所以其实调用摄像头的大致思路很简单：

1. 得到文件
2. 得到文件的uri
3. 通过startActivityForResult启动 摄像头
4. 通过 BitmapFactory将  uri变成  bitmap







## 调用相册里面的照片

* 首先要明确一点，调用摄像头和调用相册里面的照片，都是需要  读写SD卡的权限的

  ```xml
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
  ```

* 但这样其实还不够，因为这是危险权限，需要在 代码里动态申请并且 查看反馈。

  如果不添加这样的代码，那么当你第一次用app不小心取消了权限，你就以后都没有权限，除了去设置调整。

   

  申请权限的函数

  ```java
  //申请权限的统一代码
  private void applyPermission(){
     //如果没有得到 读写sd卡的权限
     if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                  != PackageManager.PERMISSION_GRANTED)
     requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
          else
              //dosomething
      }
  ```

   

  接收反馈的函数

  下面的 case就是 你发送时的标识

  ```java
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
      switch (requestCode){
        case 100:
          //如果得到权限
         if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
             //dosomething
         else
             Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
          }
  }
  ```

  ​

* 打开相册的方式其实又像开启activity

  ```java
  private void openAlbum(){
      Intent AlbumIntent=new Intent("android.intent.action.GET_CONTENT");
      AlbumIntent.setType("image/*");
      startActivityForResult(AlbumIntent,CHOOSE_FROM_ALBUM);
  }
  ```

  这个 action有很多响应的东西，有文件相册视频之类的，然后再  set一个 image/*作为类型，然后打开startActivityForResult。

  ​

* 然后就要接收了信号了

  ```java
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch(requestCode){
          case CHOOSE_FROM_ALBUM:
              if(resultCode==RESULT_OK)
                  //判断手机的android版本，高低版本用不同的函数方法去解决
                  if(Build.VERSION.SDK_INT>=19)
                      handleImageOnKitKat(data);
                  else
                      handleImageBeforeKitKat(data);
              break;
          default:
              break;
      }
  }

  ```

   

  然后这两个方法都非常的无解，非常的烦。

  大体原因是因为 android将 选择图片 的URI进行了一大段 封装， 这里就不搞了，直接复制粘贴算了。

  ```java
      @TargetApi(19)
      public void handleImageOnKitKat(Intent data){
          String imagePath=null;
          Uri uri=data.getData();
          if(DocumentsContract.isDocumentUri(this,uri)) {
              //如果是document类型的uri，则通过document id处理
              String docId = DocumentsContract.getDocumentId(uri);
              if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                  String id = docId.split(":")[1];   //解析出数字格式的ID
                  String selection = MediaStore.Images.Media._ID + "=" + id;
                  imagePath = getImagePath
                          (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                  Toast.makeText(this, "第一个", Toast.LENGTH_SHORT).show();
              } 
              else if ("com.android.providers.downloads.documents".equals(uri.getAuthority()))
              {
                  Uri contentUri = ContentUris.withAppendedId(Uri.parse("" +
                          "content://downloads/public_downloads"), Long.valueOf(docId));
                  imagePath = getImagePath(contentUri, null);
                  Toast.makeText(this, "第二个", Toast.LENGTH_SHORT).show();
              }
          }
          else if("content".equalsIgnoreCase(uri.getScheme())) {
              imagePath = getImagePath(uri, null);
              Toast.makeText(this, "第三个", Toast.LENGTH_SHORT).show();
          }
          else if("file".equalsIgnoreCase(uri.getScheme())) {
              Toast.makeText(this, "第四个", Toast.LENGTH_SHORT).show();
              imagePath = uri.getPath();
          }
          displayImage(imagePath);
      }


      private String getImagePath(Uri uri,String selection){
          String path=null;
          //通过Uri和selection来获取真实的图片路径
          Cursor cursor=getContentResolver().query(uri,null,selection,null,null);

          if(cursor!=null){
              if(cursor.moveToFirst()) path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
              cursor.close();
          }
          return path;
      }

      /**
       * 通过 图片的位置，可以通过  BitmapFactory 解析得到 bitmap，然后imageView就可以直接			setImageBitmap
       */
      private void displayImage(String imagePath){
          if(imagePath!=null) {
              Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
              picture.setImageBitmap(bitmap);
          }
          else
              Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
      }
  ```

  事实证明，是第三种方式，直接就是通过getImagePath（uri）得到路径的String。

  **再通过  Bitmap bitmap=BitmapFactory.decodeFile(String imagePath);得到 图像** 





完成！





## 音乐播放器

* 音乐播放器其实很简单，它封装好了给你，它的类 是  MediaPlayer

* 它有几个方法，

  isPlaying()   播放器是否在播放

  start()	播放器开始播 你设置好的歌

  stop()	停止播歌，在换歌和取消时用到

  pause()	暂停播歌

  setDataSourece(String)	设置 播的歌曲的  路径（字符串形式）

  prepare()	做完上一个函数就要  prepare（）准备好

  release()     release 释放资源

* 所以其实逻辑很简单，设置几个按钮，然后按来按去就行了



难的其实是在文件中搜寻，哪个是装音乐的文件夹，当然，我自己的手机当然会偷懒，直接固定我music文件夹，不过如果要写的话，可以第一次登录把所有文件都搜一遍，然后放在sharedPreference中，不过每一次更新歌曲都要再按钮重新搜索，这样就比较麻烦了。



```java
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String > songList;
    private int PlayingIndex;
    private MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        init();
    }


    private void init(){
        PlayingIndex=-1;
        player=new MediaPlayer();
        //listview的初始化
        listView=(ListView)findViewById(R.id.listView);
        songList=Music.getSongs();
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,songList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public void onClick(View view){
        if(PlayingIndex==-1)
            return;
        switch(view.getId()){
            case R.id.display_button:
                if(player.isPlaying()==false)
                    player.start();
                break;
            case R.id.next_button:
                player.stop();
                player.reset();
                PlayingIndex=(PlayingIndex+1)%songList.size();
                startTheSong(PlayingIndex);
                break;
            case R.id.pause_button:
                if(player.isPlaying())
                    player.pause();
                break;
            case R.id.previous_button:
                player.stop();
                player.reset();
                PlayingIndex=(PlayingIndex-1)%songList.size();
                startTheSong(PlayingIndex);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(PlayingIndex==position)
                return;
            else{
                PlayingIndex=position;
                player.stop();
                player.reset();
                startTheSong(PlayingIndex);
            }
    }

    //传入第几首歌，来播放音乐
    public void startTheSong(int index){
        try{
           //设置player的文件（String），并且处于准备状态。
            player.setDataSource(Music.FOLDERNAME+songList.get(index));
            player.prepare();
            player.start();
        }
        catch(Exception e){e.printStackTrace();}
    }

    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player!=null){
            player.stop();
            player.release();
        }
    }
}
```































