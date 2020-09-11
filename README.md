# BaseDemo
## 介绍
这是基础MVP框架以及一些APP框架搭建的基础工具，方便快速搭建APP框架

### **如何使用：**


#### 添加Jitpack仓库 Gradle依赖：
```java
implementation 'com.github.mJian:BaseDemo:1.0.0'
因为集成了ButterKnife9.0.0，需要在APP的build.gradle中添加如下代码：
compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
