## JavaScript 快速入门



### 一、简介

javaScript是一种脚本语言，可以放在浏览器中直接运行，通常是作为 <script>的内容放在HEML代码中。

或者把 js代码变成js后缀文件，然后通过 <script src='location'> 来插入脚本。



### 二、基本类型

javaScript非常简单，大致只有几种 基本类型，  **object, number, String, Array,Map,bool，Set,** 就没了。



#### 字符串

字符串跟java，C/C++没有什么不同，都是双引号，逃逸字符之类的。

字符串对象还有很多内置的函数，如 toUpperCase,toLowerCase,SubString,indexOf之类的。

字符串对象还能用 ${}作为模板在双引号内当成字符串的内容。

字符串对象不可变， 

#### 数组

数组通常直接赋值  [] 作为开头。   如 var arr=[]

数组可以放任何对象，所以不用分什么类型的数组。

数组也是对象，js中的对象随你扩展。  所以 数组也由你扩展 。 var arr=[1,2,3];  arr[5]=6;  这样arr大小就变了。

数组的内置函数：sort( function(a,b){} ), sort(),  indexOf(a), slice(start, cnt), push(), pop(),reverse()等等。

### 对象

这个是js中比较重要的东西。

首先，一个对象用 {}表示，var a = { xxx }， 反正只要大括号扩着就是 对象。

其次，{}里面的内容代表着你的属性 ,对对象的属性，你可以 ==任意增删改查== 

```javascript
var xiaoming = {
    name: '小明',
    birth: 1990,
    score: null
};
xiaoming.name // 小明
xiaoming.sex // undefined
xiaoming.sex=male;
xiaoming.sex //male
```

还有一些内置的，比如检测 这个对象有没有某个属性，或这个属性是继承回来还是自己的，这些要用的时候再细看。

js中对象直接凭空造出来，不用new，不用定义，它的类型就是  object。

####Map和Set

Map和Set就要new了， var map=new Map();

```javascript
var map=new Map();
map.set("a",0); //添加，其中 key-value以一个数组形式存入一个数组，即二维数组
map.set("b",1); // map=[ ["a",0],["b",1]  ]; 
map.get("a"); //0
map.delete("a"); //删掉了

//Set同理，基本一样，只是是一维数组而已
```



### 三、函数

js中的函数以 function作为关键词，不用表明返回值 ，只需要写传入参数 的 变量名即可，参数按值传递。

```javascript
function abs(x) {
    if (x >= 0) {
        return x;
    } else {
        return -x;
    }
}

var b=abs;
b("i love china"); // i love china
```



js很自由，所以，如果你调用函数时可以任意传入n个参数进去，在函数内再仔细判别.

arguments是函数内部内置的变量，它代表实际传入的参数的数组

rest参数也类似，代表你用不到的参数的 数组。

```javascript
function abs(x) {
    for(let ch in arguments)
        console.log(ch)
    if(arguments.length>1)
        return;
    if (x >= 0) {
        return x;
    } else {
        return -x;
    }
}
abs(10); //ok,10
abs(5,6,7); //5,6,7 return,because arguments=[5,6,7]

```

```javascript
function abs(x...rest) {
    for(let ch in rest)
        console.log(ch)
    if (x >= 0) {
        return x;
    } else {
        return -x;
    }
}
abs(10); //ok,10
abs(5,6,7); //6,7,reuturn 5 , rest=[6,7];	

```

函数更多高级用法迟些再更新。



### 四、面向对象

javaScript的面向对象其实是面向原型，即 javaScript不存在自定义的类，只有对象，所以如果某两个对象是继承关系，那么 我们说 **子对象的原型是父对象** ，与java，C++的区别是 **以对象为单位去继承** 。

原型和构造函数之间的东西比较复杂和混乱，现在由于时间紧急先不写，所以先写个平时用的。

在新版本的javaScript中创建了class关键字，这个关键字省略了很多 原理上的东西。

所以现在只需像  java那样 更快捷地了解。

```javascript
class Student {
    //构造函数，这里用  this.xxxx去定义属性
    constructor(name) {
        this.name = name;
    }

    //相当于静态方法，但this却指向调用的对象。这里需要好好探究一下。
    hello() {
        alert('Hello, ' + this.name + '!');
    }
}
```

对象的继承

```javascript
class PrimaryStudent extends Student {
    constructor(name, grade) {
        super(name); // 记得用super调用父类的构造方法!
        this.grade = grade;
    }

    myGrade() {
        alert('I am at grade ' + this.grade);
    }
}
```



