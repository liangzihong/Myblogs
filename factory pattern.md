## 初识工厂设计模式

这篇文章是我拜读完许多高手博客之后所记录的笔记，意义在于自己总结，想了解更多可以点击[这里][id]

[id]: http://design-patterns.readthedocs.io/zh_CN/latest/creational_patterns/factory_method.html





### 1、什么是工厂设计模式

​	根据搜索引擎，在面向对象程序设计中，工厂通常是一个用来创建其他对象的对象。工厂是构造方法的抽象，用来实现不同的分配方案。工厂对象通常包含一个或多个方法，用来创建这个工厂所能创建的各种类型的对象。这些方法可能接收参数，用来指定对象创建的方式，最后返回创建的对象。

### 2、工厂设计模式的实质

​	相当于把对象的创建的过程和细节交给工厂来决定，由工厂选择生成哪个子对象实例，把new一个对象和使用一个对象分割开来。



### 3、工厂设计模式的三个实现

#### 简单工厂模式

* 将你要生成子对象的细节告诉工厂，由工厂来实现

  ```java
  public interface Product{}
  public class Washer implements Product{
    //body
  }
  public class Tv implements Product {
    //body
  }

  public class Factory {
    public static Product getProduct(String name){
      if (name == null) return null;
      else{
        switch (name){
          case "Washer":
            return new Washer();
          case "Tv":
            return new Tv();
          default:return null;
        }
      }
    }
  }
  ```

  >简单工厂模式的优缺点很明显
  >
  >理解:    简单工厂相当于是一个世界工厂,所有产品都是由这件工厂生产,一旦出问题,权限崩溃.
  >
  >优点：简洁明了
  >
  >缺点:  当你需要不断添加新产品或者产品数量很多时, 代码量和后序维护变得非常大.






#### 工厂方法模式

* 为了解决这个方式,可以将一间工厂变成多间工厂,共同生产产品

  ```java
  public interface Product{}
  public class Washer implements Product{
    //body
  }
  public class Tv implements Product {
    //body
  }

  public interface FactoryMethod{
    public Product getProduct(){}
  }

  public class WasherFactory implements FactoryMethod{
    public Product getProduct(){
      return new Washer();
    }
  }

  public class TvFactory implements FactoryMethod{
    public Product getProduct(){
      return new Tv();
    }
  }

  ```

  > 理解:工厂方法设计模式相当于发牌照或者开分店给若干工厂,让他们具有生成产品的资格
  >
  > 优点: 工厂方法把不同的产品放到了不同的工厂,如此一来只需要知道具体的工厂名就能够返回所需要的产   品，并且在增加新的产品时，只需要添加上新产品的具体类和对应具体工厂的实现即可，很好地解决了简单工厂模式的缺点
  >
  > 缺点：每增加一个新的产品，新的类就成对出现，一定程度上增加了系统的复杂度






####抽象工厂模式

* 一般情况下，一个具体工厂中只有一个工厂方法或者一组重载的工厂方法。但是有时候我们需要一个工厂可以提供多个产品对象，而不是单一的产品对象。

* 由此催生出抽象工厂模式，目的是==提供一个创建一系列相关或相互依赖对象的接口，而无须指定它们具体的类== 

* 当我们有多个  **产品族** 的时候，抽象工厂模式变得重要。

  > 产品族：在抽象工厂模式中，产品族是指由同一个工厂生产的，位于不同产品等级结构中的一组产品，如海尔电器工厂生产的海尔电视机、海尔电冰箱，海尔电视机位于电视机产品等级结构中，海尔电冰箱位于电冰箱产品等级结构中。

* 当我们 Washer  有Washer1和Washer2两种，   而Tv也有Tv1和Tv2两种。我们可以把Washer1和Tv1当作是由一间工厂所生出的两种产品，但是属于不同产品等级结构。  Washer2和Tv2同理。

* 于是有

  ```java
  public interface Washer{}
  public interface Tv{}

  public class Washer1 implements Washer{
    //body
  }

  public class Washer2 implements Washer{
    //body
  }

  public class Tv1 implements Tv{
    //body
  }

  public class Tv1 implements Tv{
    //body
  }

  public interface Factory(){
    public Washer getWasher(){}
    public Tv getTv(){}
  }

  public class Factory1 implements Factory{
    public Washer getWasher(){return new Washer1();}
    public Tv getTv(){return new Tv1();}
  }

  public class Factory2 implements Factory{
    public Washer getWasher(){return new Washer2();}
    public Tv getTv(){return new Tv2();}
  }
  ```

  >理解：把抽象工厂类理解为电器厂家标准，各个具体工厂类就是个个电器厂家，他们有各自不同的产品组
  >
  > 优点：增加新的具体工厂和产品族很方便，无须修改已有系统
  >
  >缺点： 在添加新的产品对象时，需要在抽象工厂类（接口）中添加相应的方法，但是之前所定义的具体工厂类都没有新的产品方法，要修改需要巨大的工作。

  ​

  #### 因为还是初学者，所以当我有更深刻理解时，再来更新

  参考并感谢博客：http://design-patterns.readthedocs.io/zh_CN/latest/creational_patterns/simple_factory.html

  https://www.cnblogs.com/zhouqiang/archive/2012/07/20/2601365.html

  ​






























