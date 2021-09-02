# aforget
​        该类库是 [AForge.NET](https://github.com/andrewkirillov/AForge.NET) 的Java版本的移植。

​		非官方移植，为个人兴趣。

------

#### 关于AForge.NET

 AForge.NET 是一个C#语言的人工智能类库，提供包含机器学习，神经网络，图像处理等代码实现。更详细的内容可查看[官网](www.aforgenet.com/framework/)



#### 移植

某日想使用Bp算法进行玩耍，但是在Github搜了半天，也没有找到一个合适的Java类库进行使用。突发奇想，曾经的毕设使用AForge.NET的神经网络，实现清晰完善，使用简单。脑袋一拍，决定做Java版本的移植。

SO，由于目的原因，目前仅做AForge.NET类库中，Neuro相关部分移植，相关代码合并为一个项目。后续是否会将其他库移植再看，已留下可扩展的空间。

###### 代码在移植过程中，由于语言的关系会进行一些调整。

- 帕斯卡转驼峰式命名（如Run -> run)
- GetSet访问器改为JavaBean方式实现，因此 Class.Count 会调整为 Class.getCount() 等等
- 注释更改为Javadoc，尽力保持原注释风格
- 包命名规则
  - 前缀 com.github.terralian.aforge 作为该项目唯一标识
  - 前缀后面为原项目的命名空间，如neuro, 再后为命名空间下的包
- C#个性语言全部调整为Java相似实现，若无则手动实现一个类似的方法。
- 原作者实现的ThreadSafeRandom类 调整为Java已有的 ThreadLocalRandom，~~不知道会不会出问题~~

###### 移植基于当前最新的2.2版本。

- 所以目前的项目的maven版本号标识为2.2.0-Simple, 后续可调整为2.2.0。

当前注释也是全移植原项目的英文版，所以对我这样的不会很友好。

- 后续可能会切个分支进行汉化

#### 使用

- 使用Jdk1.8+
- 使用maven进行依赖管理，~~暂未发布maven库，可手动下载编译~~

```
git clone https://github.com/TerraLian/terra-commons.git
```



#### 进度

- Neuro相关代码移植完毕，**仅对部分类进行了简单的测试**，建议当前只用于学习。



#### 其他

后续有想法继续完善，但是出于个人摸鱼时间所限及还有其他想要玩耍的内容，~~这个后续可能会有点长~~。

若你对该项目有兴趣，可继承咱的遗志，将该项目完善下去，~~那就不需要浪费我的青春了~~

