# 初识单例设计模式

**这篇文章是我拜读完许多高手博客之后所记录的笔记，意义在于自己总结，想了解更多可以点击[这里][id]**

[id]:<http://blog.csdn.net/dmk877/article/details/50311791>

## 1、什么是设计模式

​	根据百度百科通俗易懂的解释，设计模式（Design Pattern）是一套被反复使用、多数人知晓的、经过分类的、代码设计经验的总结。  使用设计模式的目的：为了代码可重用性、让代码更容易被他人理解、保证代码可靠性。 设计模式使代码编写真正工程化；设计模式是软件工程的基石脉络，如同大厦的结构一样。        ==人话：设计模式就是前人所总结出来的写代码的模式和经验==



## 2、单例设计模式

​	顾名思义，单例设计模式旨在只能建立该类的一个唯一对象，因为只是初学者，所以暂时不知道单例设计模式可以用在什么地方，



## 3、单例设计模式设计思想

 	所谓单例，指的是单个实例，所以我们只能创造该类的一个实例，并且这个实例拥有这个类的唯一资源。 要做到如此，其实只需要简单三个步骤：

1. 把类的构造器设为私有
2. 设置一个静态的指针创建唯一实例
3. 通过公有方法把指针送出



### 4、三种常用安全的单例模式代码

1. **lazy initialization double check**

   ```java
   public class Singleton{
   	/*
   	自定义的数据，函数
   	*/
     private static Singleton instance = null;
     private Singleton(){} //构造函数 	
     
     public static Singleton getInstance() {
       if(instance == null){
         synchronized(Singleton.class){   //保证线程安全
           if(instance == null)
             instance = new Singleton();
         }
       }
       return instance;
     }
     
   }
   ```

2. **Inner class**

   ```java
   public class Singleton {
     	/*
   	自定义的数据，函数
   	*/
     private class InnerClass {
       private static Singleton instance = new Singleton();
     }
     
     private Singleton(){}
     public static Singleton getInstance() {
       return InnerClass.instance;
     }
   }
   ```

   因为使用普通饿汉式单例模式时，instance在第一次类加载的时候就会被建立，造成一定的空间浪费。 所以采用了内部类的方式，当Singleton类加载时，InnerClass类并没有并加载，当要使用instance时，instance才真正创建。   同时，因为采用了**类装载的机制** ,所以保证初始化实例时只有一个进程，保证线程安全。

3. **enum** 

   ```java
   public enum Singleton{
     instance;
     	/*
   	自定义的数据，函数
   	*/
     	private Singleton(){}
   }
   ```

   枚举类简洁明了。

   这里推荐[基本了解java枚举类][enum]

   [enum]: http://www.jianshu.com/p/46dbd930f6a2

#### 因为还是初学者，所以当我有更深刻理解时，再来更新

参考博客：http://blog.csdn.net/dmk877/article/details/50311791

























