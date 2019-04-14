# AppFileManager
app 文件管理工具（ios, android）

### android
  1. 在 android.gradle（project/build.gradle） 中添加

  ```gradle
      allprojects {
          repositories {
              ...
              maven { url 'https://jitpack.io' }
          }
      }
  ```

### ios 存放 ios 相关的文件
### jsb 存放 js桥接相关的部分（uniapp）