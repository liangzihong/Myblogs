package downloadthread;


import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import callbacklisteners.OperationListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Liang Zihong on 2018/3/16.
 */

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



