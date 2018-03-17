package Activities;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.liangzihong.downloaddemo.R;

import Services.DownloadService;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Intent IntentToService;
    private DownloadService.MyBinder mybinder;

    //与 服务建立  connection，获取binder，于是就可以通过binder来控制service的操作了
    //通过binder的函数可以 间接 控制  downloadTask
    private ServiceConnection conn=new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            mybinder=(DownloadService.MyBinder)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        applyPermission();
        init();

    }


    private void init(){
        editText=(EditText)findViewById(R.id.editText);
        IntentToService=new Intent(this, DownloadService.class);
        bindService(IntentToService,conn, Service.BIND_AUTO_CREATE);
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.start_button:
                if(editText.getText()!=null){
                    mybinder.startDownload(editText.getText()+"");
                }
                break;
            case R.id.pause_button:
                mybinder.pauseDownload();
                break;
            case R.id.cancel_button:
                mybinder.cancleDownload();
                editText.setText(null);
                break;
        }
    }


    /**
     * 申请权限
     */
    private void applyPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }

    /**
     * 回应权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        switch (requestCode) {
            case 100:
                //如果得到权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    break;
                else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                    MainActivity.this.finish();
                }
        }
    }

}
