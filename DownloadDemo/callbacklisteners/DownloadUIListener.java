package callbacklisteners;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ServiceCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.liangzihong.downloaddemo.R;

import Activities.MainActivity;
import downloadthread.DownloadTask;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Liang Zihong on 2018/3/16.
 */

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
            builder.setContentText("正在下载"+progress+"%");
            builder.setProgress(100,progress,false);
        }


        return builder.build();
    }















}
