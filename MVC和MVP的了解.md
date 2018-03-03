​	MVC模式：view是可以直接访问model，也就是说，view内要包含model的信息，不可避免地要包含一些业务逻辑， 所以，在MVC中，model是可以重用的，因为它不依赖于view。而view是依赖于model的，所以总是在view的事件响应函数中调用model的方法。所以view经常与controller混在一起，代码长，逻辑混乱；而view又与model藕断丝连

​	MVP模式：MVP模式似乎解决了这一问题，它主要的程序逻辑在Presenter里实现。而且，Presenter与具体的view没有关系，只是对view的接口进行引用和交互。从而Presenter可以在view不断变更时重用。view绝不允许直接访问model。  

​	当然日里万机操心多了就会让自己要做的事越来越多，最终它面临的就是该层代码日益庞杂，且书写起来不太方便，必定就连事件绑定这类鸡毛算皮的事都要归它管。最终我们看到MVP中的View就真的代码轻闲了不少（国企职工嘛），难怪说View只要从相应的[IVIEW]接口下实现相应的属性和一些简 单方法就完事了，而最终调用[IVIEW]接口下的那个视图实例则完全交给了Presenter。

​	MVP的问题在于，由于我们使用了接口的方式去连接view层和presenter层，这样就导致了一个问题，如果你有一个逻辑很复杂的页面，你的接口会有很多，十几二十个都不足为奇。想象一个app中有很多个这样复杂的页面，维护接口的成本就会非常的大。

>在MVP中，Activity的代码不臃肿；
>
>在MVP中，Model(IUserModel的实现类)的改动不会影响Activity(View)，两者也互不干涉，而在MVC中会；
>
>在MVP中，IUserView这个接口可以实现方便地对Presenter的测试；
>
>在MVP中，UserPresenter可以用于多个视图，但是在MVC中的Activity就不行。



如：孩子生病去病房买药，把病房选药看作是model的操作，孩子生病的病种是view，MVC模式是孩子直接去病房选药（孩子中的方法，直接从model获取数据）

而在MVP中，孩子有一个妈妈（presenter），由妈妈（presenter）去买药，并由妈妈喂药（presenter中有view和model的成员。所以孩子生病，由孩子（view）调用她的妈妈（presenter）的方法，presenter调用它的model，获取数据后再叫孩子（presenter有view的引用）吃药。

* presenter，view，model都是利用接口来交互，所以可重用性高
* presenter中有view和model的引用，方便传递数据
* view也拥有presenter的引用。
* 互相调用时都是用接口的函数，所以要想清楚接口的方法。

