package callbacklisteners;

/**
 * Created by Liang Zihong on 2018/3/16.
 */

public interface OperationListener {

    public void pause();
    public void start();
    public void cancel();
    public void fail();
    public void success();
    public void progress(int level);
}
