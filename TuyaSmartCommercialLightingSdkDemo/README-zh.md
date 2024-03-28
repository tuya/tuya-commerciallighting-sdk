## 集成 SDK

- 开始操作前，确保您已经完成了 [准备工作](https://developer.tuya.com/cn/docs/app-development/saas-commercial-lighting-preparation?id=Kaq9azrzvdjpt)。
- 如果您还未安装 Android Studio，访问 [安卓官网](https://developer.android.com/studio) 进行下载安装。

### 第一步：创建 Android 工程

在 Android Studio 中新建工程。

### 第二步：配置 build.gradle 文件

在安卓项目的 `build.gradle` 文件里，添加集成 [准备工作](https://developer.tuya.com/cn/docs/app-development/saas-commercial-lighting-preparation?id=Kaq9azrzvdjpt) 中下载的 `dependencies` 依赖库。

```groovy
android {
    defaultConfig {
        ndk {
            abiFilters "armeabi-v7a", "arm64-v8a"
        }
    }
    packagingOptions {
        pickFirst 'lib/*/libv8android.so'
        pickFirst 'lib/*/libv8wrapper.so'
        pickFirst 'lib/*/libc++_shared.so'
    }
}

configurations.all {
    exclude group: "com.thingclips.smart" ,module: 'thingsmart-modularCampAnno'
}

dependencies {
    implementation 'com.facebook.soloader:soloader:0.10.4'
    implementation 'com.alibaba:fastjson:1.1.67.android'
    implementation 'com.squareup.okhttp3:okhttp-urlconnection:3.14.9'
    implementation 'com.thingclips.smart:thingcommerciallightingsdk:2.8.1'
}
```

在根目录的 `build.gradle` 文件中，增加涂鸦 IoT Maven 仓库地址，进行仓库配置。

```groovy
repositories {
    maven { url 'https://maven-other.tuya.com/repository/maven-releases/' }
    maven { url "https://maven-other.tuya.com/repository/maven-commercial-releases/" }
    maven { url 'https://jitpack.io' }
    google()
    mavenCentral()
    maven { url 'https://maven.aliyun.com/repository/public' }
    maven { url 'https://central.maven.org/maven2/' }
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
    maven { url 'https://developer.huawei.com/repo/' }
}

```

<a id="securitycomponent"></a>

### 第三步：集成安全组件

1. 在 [App 工作台](https://iot.tuya.com/oem/sdkList)，找到您创建的 SDK。在 **获取 SDK** 页面，勾选一款或多款您需要的 SDK 或者业务包，然后下载对应应用平台的集成资料包。

   <img alt="获取 SDK" src="https://images.tuyacn.com/content-platform/hestia/17064928636d38c39dd09.png" width="">

2. 将下载后的资料包解压，并将 `security-algorithm.aar` 放置到工程 `libs` 目录下，并确认工程 `build.gradle` 的 `dependencies` 中有以下依赖：`implementation fileTree(include: ['*.aar'], dir: 'libs')`。

   <img alt="依赖" src="https://images.tuyacn.com/content-platform/hestia/17064929382466ec5314e.png" width="">

<a id="keysetting"></a>

### 第四步：设置 Appkey、AppSecret 和证书签名

1. 在 [App 工作台](https://iot.tuya.com/oem/sdkList)，找到您创建的 SDK。
2. 在 **获取密钥** 中，获取 `AppKey` 和 `AppSecret`，并配置在 `AndroidManifest.xml`中。
   <img alt="获取密钥" src="https://images.tuyacn.com/content-platform/hestia/1710483703d4b8d803ef1.png" width="">

    ``` xml
    <meta-data
        android:name="THING_SMART_APPKEY"
        android:value="应用 Appkey" />

    <meta-data
        android:name="THING_SMART_SECRET"
        android:value="应用密钥 AppSecret" />
    ```

3. 配置应用证书。

    1. 生成一个 SHA256 密钥。关于应用证书的更多信息，参考 [Android 官方文档](https://developer.android.com/studio/publish/app-signing#generate-key) 以及 [如何获取证书 SHA256](https://developer.tuya.com/cn/docs/app-development/iot_app_sdk_core_sha1?id=Kao7c7b139vrh)。
    2. 将您的 SHA256 密钥填入到 **证书** 中。

### 第五步：混淆配置

在 `proguard-rules.pro` 文件中，配置相应混淆配置。

```bash
#fastJson
-keep class com.alibaba.fastjson.**{*;}
-dontwarn com.alibaba.fastjson.**

#mqtt
-keep class com.thingclips.smart.mqttclient.mqttv3.** { *; }
-dontwarn com.thingclips.smart.mqttclient.mqttv3.**

#OkHttp3
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-keep class okio.** { *; }
-dontwarn okio.**

-keep class com.thingclips.**{*;}
-dontwarn com.thingclips.**

# Matter SDK
-keep class chip.** { *; }
-dontwarn chip.**

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * {
    native <methods>;
}

```

### 第六步：初始化 SDK

您需要在 `Application` 的主线程中初始化 SDK，确保所有进程都能初始化。示例代码如下：

```java
public class TuyaSmartApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ThingSdk.init(this);
    }
}
```

:::info
`appKey` 和 `appSecret` 可以配置在 `AndroidManifest.xml` 文件里，也可以在初始化代码里初始化。

```java
ThingSdk.init(Application application, String appkey, String appSerect)
```
:::

### 第七步：开启或关闭日志

* 在 **debug** 模式下，您可以开启 SDK 的日志开关，查看更多的日志信息，帮助您快速定位问题。
* 在 **release** 模式下，建议关闭日志开关。

    ```java
    ThingSdk.setDebugMode(true);
    ```

## 运行 Demo App

:::important
在完成快速集成 SDK 后，您将获取到 SDK 使用的 `AppKey` 和 `AppSecret`。集成 SDK 时，确认 `AppKey` 和 `AppSecret` 是否与平台上的信息一致，任意一个不匹配会导致 SDK 无法使用。详细操作，参考 [第四步：设置 Appkey、AppSecret 和证书签名](#keysetting)。
:::

Demo App 演示了 App SDK 的开发流程。在开发 App 之前，建议您先按照以下流程完成 Demo App 的操作。

### Demo App 介绍

Demo App 主要包括：
- 项目管理功能
- 区域管理功能
- 群组管理功能
- 账号管理功能
- 设备入网功能
- 设备控制功能
- 户外项目功能

**Demo 示意图**：

<p> <img src="https://airtake-public-data-1254153901.cos.ap-shanghai.myqcloud.com/content-platform/hestia/16243554383b21d81e279.jpg" width = "200" style="vertical-align:top; display:inline;">    <img src="https://airtake-public-data-1254153901.cos.ap-shanghai.myqcloud.com/content-platform/hestia/1624355571470c1caa8cd.jpg" width = "200" style="vertical-align:top; display:inline;"> <img src="https://airtake-public-data-1254153901.cos.ap-shanghai.myqcloud.com/content-platform/hestia/1624355595647d9161359.jpg" width = "200" style="vertical-align:top; display:inline;"></p>

更多详情，参考 [Github 项目](https://github.com/tuya/tuya-commerciallighting-sdk)。

### 运行 Demo

1. 替换 `app` 目录下 `build.gradle` 文件中的 `applicationId` 为您的应用包名。
   <img alt="6769cfd5-ac6f-4a19-b5b2-0174b0ae7acb.png" src="https://airtake-public-data-1254153901.cos.ap-shanghai.myqcloud.com/content-platform/hestia/1624355273d62a193f8b9.png" width="">

2. 确认您已经完成 [第三步：集成安全组件](#securitycomponent) 以及 [第四步：设置 Appkey、AppSecret 和证书签名](#keysetting)。

3. 单击运行。

### 故障排查：API 接口请求提示签名错误

* **问题现象**：运行 Demo 时提示以下错误：

    ```json
    {
        "success": false,
        "errorCode" : "SING_VALIDATE_FALED",
        "status" : "error",
        "errorMsg" : "Permission Verification Failed",
        "t" : 1583208740059
    }
    ```

* **解决方法**：

    * 检查您的 AppKey 和 AppSecret 是否正确配置。