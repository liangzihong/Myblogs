## Rxjava 2.x.y的使用



###基本使用

* RxJava其实就像是一个  asyncTask和handler机制，但是，随着代码量和逻辑不断变高，asyncTask和handler的代码量会随即变得繁琐。
* 所以此时 RxJava就变得实用，逻辑也十分得清晰，因为RxJava都是以代码链的形式写出来的，所以逻辑十分清晰。



* 普遍的使用 是RxJava+Retrofit，所以现在还不能做什么，只能做一些小函数。
* 支撑着RxJava是  Observable 和 Observer，它们之间使用 subscribe去交流，交流的媒介就是 Observable所决定的东西。



* Observable的建立

  ```java
  Observable.create(new ObservableOnSubscribe<Integer>() { // 第一步：初始化Observable
              @Override
              public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception 			{
                  Log.e(TAG, "Observable emit 1" + "\n");
                  e.onNext(1);
                  Log.e(TAG, "Observable emit 2" + "\n");
                  e.onNext(2);
                  Log.e(TAG, "Observable emit 3" + "\n");
                  e.onNext(3);
                  e.onComplete();
                  Log.e(TAG, "Observable emit 4" + "\n" );
                  e.onNext(4);
              }
  }
  ```



* e表示observer，利用这个observer的onNext就相当于调用  Observer的 onNext函数，就做到了观察者与被观察者的信息交互。
* new ObservableOnSubscribe<T> 这个T相当于 observable和observer的沟通介质。





* Observer的建立

  ```java
  Observer<String> observer=new Observer<String> {
      @Override
              public void onSubscribe(@NonNull Disposable d) {
                  
              }

              @Override
              public void onNext(@NonNull String s) {
  				log.i(TAG,"onNext:" +s);
              }

              @Override
              public void onError(@NonNull Throwable e) {

              }

              @Override
              public void onComplete() {

              }
  }
  ```

*  onSubscribe表示 一开始的操作， onNext是 被观察者 在创建的时候的 e.next的时候就调用，onError就是出现错误的时候调用，onComplete是用来 断绝 被观察者和观察者的联系。    

* 断绝之后，尽管发射者仍然会发送信息，但是 接收者就不会再调用 onNext的了。





* observable 和 observer的搭上联系

  ```java
  observable.subscribe(observer);
  ```

  ​





### 更深一点的使用

* Rxjava最简洁优雅的方式就是它的链式结构

  ```java
  Observable.create(new ObservableOnSubscribe<Response>() {
        @Override
       public void subscribe(@NonNull ObservableEmitter<Response> emitter) throws 	Exception {
              }
          )
          .subscribe(new Consumer<String>() {
                      @Override
                      public void accept(String s) throws Exception {
                          textView.setText(s);
                          Log.i(TAG, "accept: 接收器： accept");
                      }
             });
  ```

  这样就相当于 前面那个，定义了 Observable和 Observer，并且把他们两个联系在一起

  Consumer<T> 相当于简化版的 Observer，它accept函数相当于 observer的onNext函数

 

* 在RxJava中，还可以随意切换进程
*  .subscribeOn(线程) 相当于 被观察者的动作在哪个线程发生，.observeOn(线程)相当于 观察者接收到信息时，它的动作在哪个线程使用。通常有  以下四个供我们选择参考



- `Schedulers.io()` 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作；
- `Schedulers.computation()` 代表CPU计算密集型的操作, 例如需要大量计算的操作；
- `Schedulers.newThread()` 代表一个常规的新线程；
- `AndroidSchedulers.mainThread()` 代表Android的主线程



* 示例

  ```java
  Observable.create(new ObservableOnSubscribe<Integer>() {
              @Override
              public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                  Log.e(TAG, "Observable thread is : " + Thread.currentThread().getName());
                  e.onNext(1);
                  e.onComplete();
              }
          }).subscribeOn(Schedulers.newThread())
                  .subscribeOn(Schedulers.io())

                  .observeOn(Schedulers.io())
                  .subscribe(new Consumer<Integer>() {
                      @Override
                      public void accept(@NonNull Integer integer) throws Exception {
                          Log.e(TAG, "After observeOn(io)，Current thread is " + Thread.currentThread().getName());
                      }
                  });
  ```

  这个相当于 obsevrable在 一个 io线程中使用，而 Observer在主线程中使用



* 还有一个 map方法 和 doOnNext（）方法

* map方法表示可以 转变类型，可以从 Observable中的Response参数，变成 Observer的String参数。

  相当于映射而已

* 而 doOnNext方法 会在 调用了 map方法之后调用，在 accept之前调用



* 综合例子

  ```java
      /**
       * 调用顺序为 create--->map--->doOnNext--->accept
       */
      private void Test2() {
          Observable.create(new ObservableOnSubscribe<Response>() {
              @Override
              public void subscribe(@NonNull ObservableEmitter<Response> emitter) throws Exception {

                  OkHttpClient client=new OkHttpClient();
                  Request request=new Request.Builder()
                          .url("https://www.baidu.com")
                          .build();
                  Response response=client.newCall(request).execute();
                  Log.i(TAG, "subscribe: 发射器 create");
                  emitter.onNext(response);

              }
          }).subscribeOn(Schedulers.io()    )
            .map(new Function<Response, String>() {
                @Override
                public String apply(@NonNull Response response) throws Exception {
                    ResponseBody body=response.body();
                    Log.i(TAG, "apply: 发射器 map");
                    if(body!=null)
                        return body.string();
                    else return null;
                }
            })
                  //.observeOn(AndroidSchedulers.mainThread())
                  .doOnNext(new Consumer<String>() {
                      @Override
                      public void accept(String s) throws Exception {
                          Log.i(TAG, "accept: 接收器 doonnext");
                      }
                  })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Consumer<String>() {
                      @Override
                      public void accept(String s) throws Exception {
                          textView.setText(s);
                          Log.i(TAG, "accept: 接收器： accept");
                      }
                  });
      }
  ```

* 在create进行耗时操作，因为线程 为 io线程，然后进行map函数，把response的内容变成 string函数，然后string去到 doOnNext方法，最后在 accept方法中修改UI，因为 observeOn在android的主线程中运行。









