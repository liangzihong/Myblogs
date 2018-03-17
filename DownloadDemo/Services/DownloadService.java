package Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.widget.Toast;

import callbacklisteners.DownloadUIListener;
import downloadthread.DownloadTask;

/**
 * Created by Liang Zihong on 2018/3/16.
 */



public class DownloadService extends Service {

    private DownloadTask Task;
    enum State{
        NOTHING,PAUSE,  CANCEL, WORK;
    }
    private State state=State.NOTHING;


    private String urlForCancel;
    @Override
    public IBinder onBind(Intent intent) {

        return new MyBinder();
    }


    /**
     * 这是mybinder
     * 也是我们在activity中控制的东西，相当于controller
     * 通过它  我们持有一个 DownloadTask实例
     * 通过 mybinder的函数操作，控制  downloadtask的暂停 开始 取消 失败等操作
     */
    public class MyBinder extends Binder{
        public void startDownload(String url) {

            if(Task==null){
                if(state==State.NOTHING)
                    Toast.makeText(DownloadService.this, "现在开始下载", Toast.LENGTH_SHORT).show();
                else if(state==State.PAUSE)
                    Toast.makeText(DownloadService.this, "现在继续下载", Toast.LENGTH_SHORT).show();
                    urlForCancel=url;
                    state=State.WORK;
                    Task=new DownloadTask(new DownloadUIListener((NotificationManager)getSystemService(NOTIFICATION_SERVICE),
                            DownloadService.this));
                    Task.execute(url);

            }
            else
                Toast.makeText(DownloadService.this, "当前有下载中的任务", Toast.LENGTH_SHORT).show();
        }

        public void cancleDownload() {
            if(state==State.NOTHING)
                Toast.makeText(DownloadService.this, "目前没任何下载任务", Toast.LENGTH_SHORT).show();
            else {
                //这是一种 如果暂停了取消，就失去了 Task的联系，所以按照之前留下的url，重新开一个
                //再从task中把文件删除掉
                if(state==State.PAUSE) {
                    state = State.CANCEL;
                    startDownload(urlForCancel);
                }
                Task.onCancel();
                Toast.makeText(DownloadService.this, "你取消下载", Toast.LENGTH_SHORT).show();
                state=State.CANCEL;
                Task=null;
            }
        }

        public void pauseDownload(){
            if(state==State.NOTHING)
                Toast.makeText(DownloadService.this, "目前没任何下载任务", Toast.LENGTH_SHORT).show();
            else {
                Task.onPause();
                Toast.makeText(DownloadService.this, "你暂停了下载", Toast.LENGTH_SHORT).show();
                state=State.PAUSE;
                Task=null;
            }
        }
        
        


    }
}
