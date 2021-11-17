## TuyaSmart Commercial Lighting SDK

[English](./ReadMe.md)  | [中文文档](./README-zh.md) 

### What is TuyaSmart Commercial Lighting?
TuyaSmart commercial lighting solution is a complete set of IOT control system, which is suitable for the new and existing commercial lighting market, and provides a complete set of solution services from equipment end to software control end and construction end. Green building and healthy building are realized through equipment management, energy management and control, human factor lighting, etc.
TuyaSmart commercial lighting solutions provide customers with digital and visual management platform for later product maintenance and operation, help customers realize business intelligence and reduce management costs.

Six core advantages of tuyasmart commercial lighting solutions:

- Low cost out of the box solution
- Rich IOT capabilities
- Fast growing strong ecosystem
- Sustainable value added services
- Digital global project operation management
- Financial grade network data security
### What is TuyaSmart Commercial Lighting SDK?

TuyaSmart commercial lighting SDK is an Android solution for commercial lighting. Android developers can quickly develop app functions for commercial lighting and related scenes based on the SDK, and realize the management and control of projects, space and devices.

> Note：From the 1.9.7 version,Commercial Lighting SDK has done the security checksum。You need to get SHA256 in[Tuya IoT platform](https://developer.tuya.com/en/docs/app-development/iot_app_sdk_core_sha1?id=Kao7c7b139vrh),then bind your SHA256,otherwise it will report an illegal client error. If you need a local dubug to run Sample, you need to configure your signature information in the app module under build.gradle, android closures at：
```groovy
signingConfigs {
        debug {
            storeFile file('../xxx.jks')
            storePassword 'xxx'
            keyAlias 'xxx'
            keyPassword 'xxx'
        }
    }
```

### Technical support
- Tuya IOT Developer Platform: https://developer.tuya.com/en/
- Tuya developer help center: https://support.tuya.com/en/help
- Tuya work order system: https://service.console.tuya.com/
