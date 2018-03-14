## 使用通知  Notification



创建通知其实很简单，首先需要一个  NotificationManager来对通知进行管理

所以改用 support-v4库中的 NotificationCompat类来创建

代码很简单



Notification的创建

```java
    public void onClick(View view){
        switch(view.getId())
        {
            case R.id.startNotification:
                NotificationManager manager=(NotificationManager)getSystemService
                        (NOTIFICATION_SERVICE);
                Notification notification=new 			               Notification.Builder(this).setContentTitle("Hello")
                        .setContentText("这是一条通知，详情请点开")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .build();
                manager.notify(1,notification);
                Toast.makeText(this, "通知显示成功", Toast.LENGTH_SHORT).show();
                break;
            default:
        }

    }
```



//创建  NotificationManager

NotificationManager manager=(N

otificationManager)getSystemService(NOTIFICATION_SERVICE);

//创建 Notification

Notification notification=new Notification.Builder(this).xxxx().xxxxxx().xxxx().build()

接着后面接一大堆常规后缀，都是 标题，内容，图标，时间，点完就关闭之类的，这些记住即可

最后一定要有一个   build（）表示  Notification正式成立。

然后  manager.notify(1,notification); 则通知正式上线，1为这个通知的代号。





Notification一些比较中端的操作

如果你想要 通知会有震动 并且  你点开通知会有反应，打开Activity，service或其他东西，都要进行设置

这里我们选择点开通知就会跳到 另一个 activity



```java
    public void onClick(View view){
        switch(view.getId())
        {
            case R.id.startNotification:
                Intent intent=new Intent(this,ClickNotificationActivity.class);
                PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);

                NotificationManager manager=(NotificationManager)getSystemService
                        (NOTIFICATION_SERVICE);
                Notification notification=new Notification.Builder(this).setContentTitle("Hello")
                        .setContentText("这是一条通知，详情请点开")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{0,1000,1000,1000})
                        .setLights(Color.RED,1000,1000)
                        .build();
                manager.notify(1,notification);
                Toast.makeText(this, "通知显示成功", Toast.LENGTH_SHORT).show();
                break;
            default:
        }

    }
```



//这两句话，创建一个  PendingIntent，pending为罚时，可以理解为延迟产生的intent

//当你按下通知时，才会产生的意思之类的。

Intent intent=new Intent(this,ClickNotificationActivity.class);

PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);



并且在一大堆后缀中添加一个   setContentIntent(pehndingIntent)，表示这是它的点击响应事件



setVibrate(new long[]{0,1000,1000,1000})表示震动，有多少个1000就震几次，每次1s

setLights(Color.RED,1000,1000) 表示 呼吸灯的状态

当然，震动是要在配置文件中添加权限的。

```xml
<uses-permission android:name="android.permission.VIBRATE"></uses-permission>
```





如果你想要更多功能，可以上网再搜寻。







