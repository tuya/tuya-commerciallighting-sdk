This topic describes how to integrate the Commercial Lighting App SDK for Android into your project.

## Integrate with the SDK

- Before you start, make sure that you have performed the steps in [Preparation](https://developer.tuya.com/en/docs/app-development/saas-commercial-lighting-preparation?id=Kaq9azrzvdjpt).
- If you have not installed Android Studio, visit the [Android Studio official website](https://developer.android.com/studio) to download Android Studio.

### Step 1: Create an Android project

Create a project in Android Studio.

### Step 2: Configure `build.gradle`

Add `dependencies` downloaded in the [preparation](https://developer.tuya.com/en/docs/app-development/saas-commercial-lighting-preparation?id=Kaq9azrzvdjpt) steps to the file `build.gradle` of the Android project.

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

Add the Tuya IoT Maven repository URL to the `build.gradle` file in the root directory.

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

### Step 3: Integrate with security component

1. Go to [SDK Development](https://iot.tuya.com/oem/sdkList) on the Tuya IoT Development Platform and open the target app. Go to the **Get SDK** tab, select one or more required SDKs or BizBundles, and then download the App SDK for iOS or Android.

    <img src="https://images.tuyacn.com/content-platform/hestia/171144303891967b63255.png" width="">

2. Extract the downloaded package, put `security-algorithm.aar` in the `libs` directory of the project, and then make sure `dependencies` in `build.gradle` of your project include: `implementation fileTree(include: ['*.aar'], dir: 'libs')`.

    <img src="https://images.tuyacn.com/content-platform/hestia/17064929382466ec5314e.png" width="">

<a id="keysetting"></a>

### Step 4: Configure AppKey, AppSecret, and certificate signature

1. Go to [SDK Development](https://iot.tuya.com/oem/sdkList) on the Tuya IoT Development Platform and open the target app.
2. In the **Get Key** tab, find `AppKey` and `AppSecret` and specify them in the `AndroidManifest.xml` file.

   <img src="https://airtake-public-data-1254153901.cos.ap-shanghai.myqcloud.com/content-platform/hestia/162935428707a87f44ab0.png" width="">

   ```xml
   <meta-data
       android:name="THING_SMART_APPKEY"
       android:value="Appkey" />

   <meta-data
       android:name="THING_SMART_SECRET"
       android:value="AppSecret" />
   ```

3. Configure the app certificate.

    1. Generate an SHA-256 hash value. For more information, see [Android documentation](https://developer.android.com/studio/publish/app-signing#generate-key) and [How to Get SHA-1 and SHA-256 Keys](https://developer.tuya.com/en/docs/app-development/iot_app_sdk_core_sha1?id=Kao7c7b139vrh).
    2. Enter the SHA-256 key in the **certificate**.

### Step 5: Obfuscate the code

Configure obfuscation in `proguard-rules.pro`.

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

### Step 6: Initialize the SDK

Initialize the SDK in the main thread of the `Application`. Make sure that all processes are initialized.

**Example**:

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
Configure `appKey` and `appSecret` in `AndroidManifest.xml`, or set them during initialization.

```java
ThingSdk.init(Application application, String appkey, String appSerect)
```
:::

### Step 7: Enable or disable logging

* In **debug** mode, you can enable SDK logging to facilitate troubleshooting.
* We recommend that you disable logging in **release** mode.

   ```java
   ThingSdk.setDebugMode(true);
   ```

## Run the demo app

:::important
After you integrate with the SDK, you can get the `AppKey` and `AppSecret`. Make sure the `AppKey` and `AppSecret` are consistent with those used on the Tuya IoT Development Platform. Any mismatch will cause the SDK not to work properly. See [Step 4: Configure AppKey, AppSecret, and certificate signature](#keysetting) for details.
:::

In the following example, a demo app is used to describe the process of developing with the SDK. Before the development of your app, we recommend that you run the demo app.

### Feature overview

The demo app supports the following features:

- Project management
- Area management
- Group management
- Account management
- Device pairing
- Device control
- Outdoor project

**Demo app**:

<img src="https://images.tuyacn.com/content-platform/hestia/171144264036f269154b1.png" style="zoom: 50%;" />

For more information, go to the [GitHub repository](https://github.com/tuya/tuya-commerciallighting-sdk).

### Run the demo

1. Choose `app` > `build.gradle` and change the value of `applicationId` to your app package name.

    <img src="https://airtake-public-data-1254153901.cos.ap-shanghai.myqcloud.com/content-platform/hestia/1624355273d62a193f8b9.png" width="">

2. Make sure that you have completed [Step 3: Integrate with security component](#securitycomponent) and [Step 4: Configure AppKey, AppSecret, and certificate signature](#keysetting).

3. Run the code.

### Error: Permission Verification Failed

* **Problem**: The following error message is thrown when you run the demo.

    ```json
    {
        "success": false,
        "errorCode" : "SING_VALIDATE_FALED",
        "status" : "error",
        "errorMsg" : "Permission Verification Failed",
        "t" : 1583208740059
    }
    ```

* **Solution**:

    * Check whether the AppKey and AppSecret are correctly configured.