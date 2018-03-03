## Android MVP设计模式应用



首先，我们以这篇文章作为基础:http://kaedea.com/2015/10/11/android-mvp-pattern/





### 个人的理解

MVP设计模式相当于  model，view， presenter 三者。 相较于MVC模式，MVP模式 利用 presenter将 model和view完完全全地分开来，降低代码耦合度。



其次，MVP设计模式 将 model，view，presenter接口化，互相之间利用接口来进行交流，所以，Activity的代码会变得异常简洁，除了 init（），setListener（），并利用 IPresenter的方法之外，就没有其他的代码了。    当然，我们要实现 IView 的方法。



而在  Presenter中，Presenter要持有 IView的对象和 IModel的对象，Presenter负责中介，负责与IModel交流并且将结果 利用 IView呈现出去。



![6](E:\Blog\6.png)



这幅图就很好地说明了 MVC三者之间的接口关系。



这样，需要添加扩展就变得很诱人了，我们只需要  不断添加  IPresenter 并且 在 view中调用 IPresenter的方法，就可以顺利地添加各种功能了。



当然，缺点也比较明显，一个小小的应用程序如果要用MVP设计模式，就要将很多不必要的东西 接口化，就变得十分繁琐了。  但是，面对 Activity 清晰的条理，多几行代码又算得了什么。





###小小的示例



首先 ， 我们想要写  登录 和 注册功能的





![7](E:\Blog\7.png)





这样一来，结构就变得十分清晰。

思路大概就变成了，  当在 MainActivity 按下 确定时，MainActivity手下的 ILoginPresenter 开始工作，调用 IUser_model进行工作，IUser_model返回结果后，ILoginPresenter根据结果利用手下的 IView  进行界面反馈。





ILoginPresenter

```java
public interface ILoginPresenter {
    public void startLogin(String name,String password);
}
```



ISignupPresenter

```java
public interface ISignupPresenter {
    public void startSignup(String name,String password);
}
```



IUser_model

```java
public interface IUser_model {
    public boolean isSignupSuccess(String name,String password);
    public boolean isLoginSuccess(String name,String password);
}
```



IView

```java
public interface IView {
    public void successLogin();
    public void failLogin();
    public void successSignup();
    public void failSignup();
    public Context getContext();
}
```





LoginPresenter

```java
public class LoginPresenter implements ILoginPresenter {
    private IView view;
    private IUser_model user;

    public LoginPresenter(IView view) {
        this.view=view;
        user=new User_model(view.getContext());
    }

    @Override
    public void startLogin(String name, String password) {
        if(user.isLoginSuccess(name,password))
            successLogin();
        else
            failLogin();
    }

    private void successLogin() {
        view.successLogin();
    }

    private void failLogin() {
        view.failLogin();
    }
}
```





SignupPresenter

```java
public class SignupPresenter implements ISignupPresenter {
    private IView view;
    private IUser_model user;

    public SignupPresenter(IView view){
        this.view=view;
        user=new User_model(view.getContext());
    }


    @Override
    public void startSignup(String name, String password) {
        if(user.isSignupSuccess(name,password)) {
            successSignup();
        }
        else{
            failSignup();
        }
    }

    private void successSignup() {
        view.successSignup();
    }

    private void failSignup() {
        view.failSignup();
    }
}
```



MainActivity

```java
public class MainActivity extends AppCompatActivity implements IView,View.OnClickListener{

    private ILoginPresenter loginPresenter;
    private ISignupPresenter signupPresenter;

    private EditText inputname;
    private EditText inputpassword;
    private Button login_button;
    private Button signup_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        init();
    }

    private void init(){
        loginPresenter=new LoginPresenter(this);
        signupPresenter=new SignupPresenter(this);
        inputname=(EditText)findViewById(R.id.input_name);
        inputpassword=(EditText)findViewById(R.id.input_password);
        login_button=(Button)findViewById(R.id.login_button);
        signup_button=(Button)findViewById(R.id.signup_button);

        login_button.setOnClickListener(this);
        signup_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name=inputname.getText()+"";
        String password=inputpassword.getText()+"";

        switch(v.getId()){
            case R.id.login_button:
                loginPresenter.startLogin(name,password);
                break;
            case R.id.signup_button:
                signupPresenter.startSignup(name,password);
                break;
        }
    }


	//一下的方法都是 实现 IView的方法
    //所以上面的 是真正Activity的代码，十分简洁
    
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void successLogin() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void failLogin() {
        Toast.makeText(this, "登录失败，请核对账号密码", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void successSignup() {
        Toast.makeText(this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
        inputname.setText(null);
        inputpassword.setText(null);
    }

    @Override
    public void failSignup(){
        Toast.makeText(this, "注册失败，账号已注册或账号密码为空", Toast.LENGTH_SHORT).show();
    }
}

```





















