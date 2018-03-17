## 下载Demo的笔记



* 首先 弄清楚下载东西的原理是什么。

* 下载东西 其实就是  给一个URL给你，你获得requese，然后又获得response，从response中我们可以得到很多东西。  例如  response.body().byteStream()得到响应的输出流，response.body().contentLength()可以得到你下载文件的大小。

* 所以，我们只要把输出流的  字节流打入到文件中，就会得到下载的文件。

* 那，也可以 断点续传，断点续传就是  你已经 下载了多少放在文件中，下次可以 得知这个文件的大小，然后从request中添加首部，说明从 哪个字节开始下载，同样可以完成续传。

  但是这里的文件输入流需要用的是  RamdonAccessFile ，这个类有输入输出功能，并且可以通过 seek（文件的大小），就能够从 文件最后开始写数据，这样就达到 断点续传的功能。

* 于是我们可以得到这个demo中最核心的代码

  ```java
      /**
       * 从url就可以得到要 下载的文件的大小了。
       * 通过  response.body().contentLength()就可以得到了
       * @param url
       * @return
       */
      private long getContentLength(String url)
      {
          long contentlength=0;
          OkHttpClient client=new OkHttpClient();
          Request request=new Request.Builder()
                  .url(url)
                  .build();
          try {
              Response response = client.newCall(request).execute();
              if(response!=null&&response.isSuccessful()){
                  contentlength=response.body().contentLength();
                  response.body().close();
              }
          }
          catch(Exception e){e.printStackTrace();}
          return contentlength;
      }
  ```

   写文件

  因为都是核心代码，所以只看关键的即可

  ```java
         String url=params[0];


          //从观察可以得出，下载文件的文件名都是最后 /后面的东西
          String fileName=url.substring(url.lastIndexOf("/"));
          String parentName= Environment.getExternalStoragePublicDirectory
                  (Environment.DIRECTORY_DOWNLOADS).getPath();
          String fullName=parentName+fileName;
          file=new File(fullName);

  		//得到 要下载东西的大小
          ContentLength=getContentLength(url);

          //通过判断  下载文件大小以及  文件的大小就可以得出  成功与否的结论了
          //并且 如果文件不存在，那就创造；如果存在，就记录文件的大小，方便 断点续传
          try{
              if(file.exists()) {
                  FileLength = file.length();
                  if(FileLength==ContentLength)
                      return TaskState.SUCCESS;
              }
              else if(ContentLength==0) {
                  onFail();
                  return TaskState.FAIL;
              }
              else
                  file.createNewFile();

          }catch(Exception e){e.printStackTrace();}


          //开始下载
          OkHttpClient client=new OkHttpClient();
          Request request=new Request.Builder()
                  //添加首部，只下载没下载的
                  .addHeader("RANGE","bytes="+FileLength+"-")
                  .url(url)
                  .build();
          try {
              Response response = client.newCall(request).execute();

              if(response==null)
                  return TaskState.FAIL;

              InputStream in=response.body().byteStream();

              //建立RandomAccessFile可以随机访问文件的位置
              RandomAccessFile ras=new RandomAccessFile(file,"rw");
              ras.seek(FileLength);
              byte[] arr=new byte[1024];
              int maxLength=1024;
              int len=0;

              while((len=in.read(arr,0,maxLength))!=-1){

                  if(taskState==TaskState.CANCEL){
                      file.delete();
                      return TaskState.CANCEL;
                  }
                  else if(taskState==TaskState.PAUSE)
                      return TaskState.PAUSE;
                  else {

                      ras.write(arr,0,len);
                      FileLength+=len;
                      publishProgress((int)FileLength);
                  }
              }
              in.close();
              ras.close();
              response.body().close();
          }catch(Exception e) {
              e.printStackTrace();
          }
  ```





* 弄明白下载文件和断点续传的原理，下面就讲一下这个项目的架构

* 其实它的思路很简单，下载由 一个 服务在后台运行，activity只纯粹负责界面。  

  所以大体关系是：  用户调用activity，activity通过 service 掌管 binder

* 然后，下载这种耗时操作必须是由 异步消息处理机制完成，这里我们采用  AsyncTask

  这个AsyncTask<Params,Progress,Result>是抽象类

  要实现 onPreExecute()  ,  doInBackground( Params ....)   ,   onProgressUpdate(Progress ...), onPostExecute(Result .....)

* 使用方法具体看博客

* http://blog.csdn.net/boyupeng/article/details/49001215

* 然后，我们再建立一个  UIListener，作为通知栏  响应 的 监听器，负责更新 通知栏。

* 这样，我们思路也大体完成

  下载任务在 doInBackground中进行，在下载过程中，每1024个字节准备写入文件中时，会判断此时有没暂停或取消下载，如果没有的话，就把数据交给另一个 线程中的  publishProgress函数，让他调用 onProgressUpdate函数，在onProgressUpdate中，调用接口 的onProgress函数，就根据数据创建通知栏。

* 如果在准备写入文件的时候发现被按了暂停或取消，此时就 return 自己定义的 枚举，结果就会交给  onPostExecute函数，在函数中再根据  暂停，取消，成功，失败去调用接口对应的函数，创建对应的通知栏

* 具体代码如下

  ```java
  enum TaskState{
      SUCCESS,    PAUSE,  CANCEL,   FAIL,   WORK;
  }

  public class DownloadTask extends AsyncTask<String ,Integer,TaskState> {

      private OperationListener callbackListener;
      private TaskState taskState;
      private File file;

      private long ContentLength;
      private long FileLength;



      public DownloadTask(OperationListener listener){
          callbackListener=listener;
          taskState=TaskState.WORK;
      }



      /**
       * 执行耗时操作前用的
       */
      @Override
      protected void onPreExecute() {
          callbackListener.start();
      }




      /**
       * 执行耗时操作
       * 期间只能调用   publishProgress(Progress), 让publishProgress来调用onProgressUpdate
       */
      @Override
      protected TaskState doInBackground(String... params) {
          String url=params[0];


          //从观察可以得出，下载文件的文件名都是最后 /后面的东西
          String fileName=url.substring(url.lastIndexOf("/"));
          String parentName= Environment.getExternalStoragePublicDirectory
                  (Environment.DIRECTORY_DOWNLOADS).getPath();
          String fullName=parentName+fileName;
          file=new File(fullName);


          ContentLength=getContentLength(url);




          //通过判断  下载文件大小以及  文件的大小就可以得出  成功与否的结论了
          //并且 如果文件不存在，那就创造；如果存在，就记录文件的大小，方便 断点续传
          try{
              if(file.exists()) {
                  FileLength = file.length();
                  if(FileLength==ContentLength)
                      return TaskState.SUCCESS;
              }
              else if(ContentLength==0) {
                  onFail();
                  return TaskState.FAIL;
              }
              else
                  file.createNewFile();

          }catch(Exception e){e.printStackTrace();}


          //开始下载
          OkHttpClient client=new OkHttpClient();
          Request request=new Request.Builder()
                  //添加首部，只下载没下载的
                  .addHeader("RANGE","bytes="+FileLength+"-")
                  .url(url)
                  .build();
          try {
              Response response = client.newCall(request).execute();

              if(response==null)
                  return TaskState.FAIL;

              InputStream in=response.body().byteStream();

              //建立RandomAccessFile可以随机访问文件的位置
              RandomAccessFile ras=new RandomAccessFile(file,"rw");
              ras.seek(FileLength);
              byte[] arr=new byte[1024];
              int maxLength=1024;
              int len=0;

              while((len=in.read(arr,0,maxLength))!=-1){

                  if(taskState==TaskState.CANCEL){
                      file.delete();
                      return TaskState.CANCEL;
                  }
                  else if(taskState==TaskState.PAUSE)
                      return TaskState.PAUSE;
                  else {

                      ras.write(arr,0,len);
                      FileLength+=len;
                      publishProgress((int)FileLength);

                  }
              }

              in.close();
              ras.close();
              response.body().close();

          }catch(Exception e) {
              e.printStackTrace();
          }
          finally {
              if(taskState==TaskState.CANCEL){
                  file.delete();
                  return TaskState.CANCEL;
              }
              else if(taskState==TaskState.PAUSE)
                  return TaskState.PAUSE;
          }

          return TaskState.SUCCESS;

      }




      /**
       * 用来进行更新操作的
       * @param values
       */
      @Override
      protected void onProgressUpdate(Integer... values) {

          int progress=values[0];
          callbackListener.progress(  (int)((double)progress*100/(double)ContentLength));

      }


      /**
       * 由doInBackGround结束后返回的values来调用的
       * 用来进行 一些任务的收尾工作
       * 根据结果来对  UI  进行操作
       * @param
       */
      @Override
      protected void onPostExecute(TaskState state) {

          switch (state){
              case SUCCESS:
                  callbackListener.success();
                  break;
              case PAUSE:
                  callbackListener.pause();
                  break;
              case FAIL:
                  callbackListener.fail();
                  break;
              case CANCEL:
                  if(file.exists())
                      file.delete();
                  callbackListener.cancel();
                  break;
          }
      }


      public void onPause(){taskState=TaskState.PAUSE;}

      public void onCancel(){taskState=TaskState.CANCEL;}

      public void onFail(){taskState=TaskState.FAIL;}


      /**
       * 从url就可以得到要 下载的文件的大小了。
       * 通过  response.body().contentLength()就可以得到了
       * @param url
       * @return
       */
      private long getContentLength(String url)
      {
          long contentlength=0;
          OkHttpClient client=new OkHttpClient();
          Request request=new Request.Builder()
                  .url(url)
                  .build();
          try {
              Response response = client.newCall(request).execute();
              if(response!=null&&response.isSuccessful()){
                  contentlength=response.body().contentLength();
                  response.body().close();
              }
          }
          catch(Exception e){e.printStackTrace();}
          return contentlength;
      }


  }
  ```





* 所以Binder就要手持一个 Task的实例

* 控制它开始执行，暂停，取消

* 具体代码看项目。

* 所以项目架构

  用户控制--->activity，activity得到binder， binder手持task，根据activity的按钮 执行task的 开始，暂停，取消。

  Task 的 通知负责由UIListener实现，UIListener创建更改取消通知栏。





* 然后就剩下接口的函数了

* 这就好办，需要办的就是如何创建通知栏，更新通知栏，取消通知栏

* 创建通知栏

  ```java
      /**
       * 创建  Notification
       * 如果是progress《0，说明任务已经完成，内容就显示 下载已完成
       * 如果progress》0，就要在通知栏显示一个进度条
       * builder.setProgress(100,progress,false)即可
       * @param title
       * @param progress
       * @return
       */
      private Notification getNotification(String title,int progress){

          Intent intent=new Intent(context, MainActivity.class);
          PendingIntent pi=PendingIntent.getActivity(context,0,intent,0);

          Notification.Builder builder=new Notification.Builder(context)
                  .setContentTitle(title)
                  .setSmallIcon(R.mipmap.ic_launcher)
                  .setContentIntent(pi);
          if(progress<0){
              builder.setContentText("已完成下载");
          }else
          {
              //创建进度条的函数
              builder.setContentText("正在下载"+progress+"%");
              builder.setProgress(100,progress,false);
          }
          return builder.build();
      }
  ```

* ```java

  public class DownloadUIListener implements OperationListener {

      private NotificationManager manager;
      private Context context;
      private int nowlevel;

      public DownloadUIListener(NotificationManager manager,Context context){
          this.manager=manager;
          this.context=context;
      }


      //暂停就通知栏显示暂停就ok
      @Override
      public void pause() {
          manager.notify(1,getNotification("暂停中",nowlevel));
      }


      //开始下载，给个Toast就好
      @Override
      public void start() {
          nowlevel=0;
          ((Service)context).startForeground(1,getNotification("开始下载",0));
      }


      //如果下载是被取消了，也要取消通知栏
      @Override
      public void cancel() {
          ((Service)context).stopForeground(true);
      }

      //如果下载是失败的，则要取消通知栏
      @Override
      public void fail() {
          ((Service)context).stopForeground(true);
          Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
      }

      //如果是成功，取消前面的通知
      //展现新的通知
      @Override
      public void success() {
          manager.notify(1,getNotification("下载完成",-1));
          Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
          return;
      }


      //如果是正在下载中，告诉它 现在下载的进度。
      @Override
      public void progress(int level) {
          nowlevel=level;
          manager.notify(1,getNotification("下载ing",level));
          Log.i("UILIstener", "progress: "+level);
          return;
      }

  }
  ```

* 然后不写了，细节的话就直接看书算了。