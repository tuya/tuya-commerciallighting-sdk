## Mesh(SIG) 设备控制

## 1. 设备

ITuyaBlueMeshDevice 类封装了对指定 Mesh 内所有设备的操作

### 1.1  获取设备实例

```java
ITuyaBlueMeshDevice  mTuyaBlueMeshDevice = TuyaCommercialLightingSdk.newSigMeshDeviceInstance(meshId);
```

### 1.2 SIG Mesh 本地连接和网关连接

**示例代码**

```java
DeviceBean deviceBean=TuyaCommercialLightingSdk.getDeviceManager().getDeviceBean(mDevId)
DeviceBean gwBean=TuyaCommercialLightingSdk.getDeviceManager().getDeviceBean(deviceBean.getParentId());

//综合在线状态  (包括本地在线和网关在线)
boolean online=deviceBean.getIsOnline()
//设备本地蓝牙在线状态
boolean localOnline=deviceBean.getIsLocalOnline()
//设备网关在线状态  (需要网关在线且子设备在线 才认为网关真实在线)
boolean wifiOnline=deviceBean.isCloudOnline() && gwBean.getIsOnline()
```

### 1.3 SIG Mesh 设备和网关判断方法

**示例代码**

```java
DeviceBean deviceBean=TuyaCommercialLightingSdk.getDeviceManager().getDeviceBean(mDevId);
// 判读是否是 sigmesh 设备 （子设备+网关）
if(deviceBean.isSigMesh()){
    L.d(TAG, "This device is sigmesh device");
}

// 判读是否是 sigmesh 网关设备
if(deviceBean.isSigMeshWifi()){
    L.d(TAG, "This device is sigmesh wifi device");
}
```

### 1.4 SIG Mesh 子设备重命名

**接口说明**

```java
void renameMeshSubDev(String devId, String name, IResultCallback callback);
```

**参数说明**

| 参数     | 说明       |
| -------- | ---------- |
| devId    | 设备 Id    |
| name     | 重命名名称 |
| callback | 回调       |

**示例代码**

```java
mTuyaBlueMesh.renameMeshSubDev(devBean.getDevId(),"设备名称", new IResultCallback() {
     @Override
     public void onError(String code, String errorMsg) {
     }

     @Override
     public void onSuccess() {
     }
});
```

### 1.5 SIG Mesh 子设备状态查询

云端获取到的 dp 点数据可能不是当前设备实时的数据，可以通过该命令去查询设备的当前数据值，结果通过 `IMeshDevListener` 的 `onDpUpdate` 方法返回

**接口说明**

```java
void querySubDevStatusByLocal(String pcc, final String nodeId, final IResultCallback callback);
```

**参数说明**

| 参数     | 说明        |
| -------- | ----------- |
| pcc      | 设备大小类  |
| nodeId   | 设备 nodeId |
| callback | 回调        |

**示例代码**

```java
mTuyaBlueMeshDevice.querySubDevStatusByLocal(devBean.getCategory(), devBean.getNodeId(), new IResultCallback() {
            @Override
            public void onError(String code, String errorMsg) {
            }
            @Override
            public void onSuccess() {
            }
        });
```

### 1.6 SIG Mesh 子设备移除

**接口说明**

```java
void removeMeshSubDev(String devId, IResultCallback callback);
```

**参数说明**

| 参数     | 说明       |
| -------- | ---------- |
| devId    | 设备 Id    |
| pcc      | 设备大小类 |
| callback | 回调       |

**示例代码**

```java
mTuyaBlueMesh.removeMeshSubDev(devBean.getDevId(),devBean.getCategory(), new IResultCallback() {
            @Override
            public void onError(String code, String errorMsg) {
            }
            @Override
            public void onSuccess() {
            }
        });
```



## 2. 控制

`ITuyaBlueMeshDevice` 类提供了对 Mesh 设备的操作

### 2.1 指令下发-控制设备

**描述**

发送控制指令按照以下格式：

```java
{
  "(dpId)":"(dpValue)"
}
```

**接口说明**

```java
void publishDps(String nodeId, String pcc, String dps, IResultCallback callback);
```

**参数说明**

| 参数     | 说明           |
| -------- | -------------- |
| nodeId   | 子设备本地编号 |
| pcc      | 设备产品大小类 |
| dps      | dps            |
| callback | 回调           |

**代码示例**

```java
String dps = {"1":false};
ITuyaBlueMeshDevice mTuyaSigMeshDevice=TuyaCommercialLightingSdk.newSigMeshDeviceInstance("meshId");
mTuyaSigMeshDevice.publishDps(devBean.getNodeId(), devBean.getCategory(), dps, new IResultCallback() {
            @Override
            public void onError(String s, String s1) {
            }

            @Override
            public void onSuccess() {
            }
        });
```





### 2.2 指令下发-控制群组

**接口说明**

```java
void multicastDps(String localId, String pcc, String dps, IResultCallback callback)
```

**参数说明**

| 参数     | 说明           |
| -------- | -------------- |
| localId  | 群组本地编号   |
| pcc      | 设备产品大小类 |
| dps      | dps            |
| callback | 回调           |

**代码示例**

```java
String dps = {"1":false};
ITuyaBlueMeshDevice mTuyaSigMeshDevice= TuyaCommercialLightingSdk.newSigMeshDeviceInstance("meshId");
mTuyaSigMeshDevice.multicastDps(groupBean.getLocalId(), devBean.getCategory(), dps, new IResultCallback() {
            @Override
            public void onError(String errorCode, String errorMsg) {
            }

            @Override
            public void onSuccess() {
            }
        });
```

### 2.3 数据监听

**描述**

Mesh 网内相关信息（ dp 数据、状态变更、设备名称、设备移除）会实时同步到 `IMeshDevListener`

**代码示例**

```java
mTuyaSigMeshDevice.registerMeshDevListener(new IMeshDevListener() {
 /**
  * 数据更新
  * @param nodeId    更新设备的nodeId
  * @param dps       dp数据
  * @param isFromLocal   数据来源 true表示从本地蓝牙  false表示从云端
  */
    @Override
    public void onDpUpdate(String nodeId, String dps,boolean isFromLocal) {
      //可以通过node来找到相对应的DeviceBean
        DeviceBean deviceBean = mTuyaBlueMeshDevice.getMeshSubDevBeanByNodeId(nodeId);
     }
  /**
   * 设备状态的上报
   * @param online    在线设备列表
   * @param offline   离线设备列表
   * @param gwId      状态的来源 gwId不为空表示来自云端（gwId是上报数据的网关Id）为空则表示来自本地蓝牙
   */
  @Override
  public void onStatusChanged(List<String> online, List<String> offline,String gwId) {
  }

  /**
   * 网络状态变化
   * @param devId
   * @param status
   */
  @Override
  public void onNetworkStatusChanged(String devId, boolean status) {

  }        
  /**
   * raw类型数据上报
   * @param bytes
   */
  @Override
  public void onRawDataUpdate(byte[] bytes) {

  }
   /**
    * 设备信息变更（名称等）
    * @param bytes
    */            
  @Override
  public void onDevInfoUpdate(String devId) {
  }  
  /**
   * 设备移除
   * @param devId
   */
  @Override
  public void onRemoved(String devId) {
  }
});
```