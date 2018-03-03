# eclipse oxygen 配置tomcat 9.0



###第一步 装上eclipse的EE插件

​	因为我以前学习java都是用eclipse oxygen的se版本，所以并不支持j2EE，所以第一步，就是要先把它升级为EE版本。有两种方法供我们选择。

1. 重新安装eclipse的EE版本。
2. 安装eclipse的EE插件。

这里我推荐使用第二种方法，更方便更快捷。

* 在菜单栏  ==帮助== --->  ==install new software== 

  ![1](E:\Blog\1.png)

* 在Work with中输入一下网址：==Oxygen - http://download.eclipse.org/releases/oxygen== 

  > 网址中前面与后面的Oxygen根据你eclipse的版本而变化。
  >
  > ![2](E:\Blog\2.png)

* 接着我们在出来的一堆名称中,对下面这一个选项直接打勾（其实可以不需要这么多，但避免因为了少了一些东西而要折腾很久，所以防范未然）

  >![3](E:\Blog\3.png)

* 然后等待下载和安装。当你新建文件时出现web项目选项，证明已经可以开发EE了。







### 第二步  添加tomcat服务器

在  ==首选项== ---> ==服务器==  -----> ==运行时环境==  ------>  ==添加==   ----->==Apache== ----> ==选择自己的tomcat版本== 

![4](E:\Blog\4.png)

完成即可。





###第三步 使浏览器所访问的服务器和eclipse的服务器相通

  	 安装完后你会发现，当我建立一个servlet程序时，可以在eclipse中成功运行，但是却不能在浏览器中输入同样的地址得到servlet程序。同样，在eclipse中的浏览器输入  http://localhost:8080时也不能访问到tomcat，所以要解决这个问题。

1. 在eclipse双击你刚才建立的服务器，进入  服务器概述

   > ![5](E:\Blog\5.png)

2. 此时，我们把  ==Server Locations== 里的  ==Use workspace metadata== 选项改为  ==Use Tomcat installation==  , 同时把Deploy path改成你安装的服务器的  webapps目录。  确定即可。此时就完成。

3. 如果像上图那样，信息为只读时。就需要返回，右键你所建的服务器，==添加和移除== ，把项目全都移走即可。



此时配置完成。



