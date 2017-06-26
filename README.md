### JoyHttp

Android网络请求库

### Add JoyHttp to your project

Gradle:

```
compile 'com.joy.http:JoyHttp:0.2.1'
```

Maven:

```
<dependency>
  <groupId>com.joy.http</groupId>
  <artifactId>JoyHttp</artifactId>
  <version>0.2.1</version>
  <type>pom</type>
</dependency>
```

 Ivy:

 ```
 <dependency org='com.joy.http' name='JoyHttp' rev='0.2.0'>
   <artifact name='$AID' ext='pom'></artifact>
 </dependency>
 ```

### 请求方式：  
- `JoyHttp.getLauncher().launchRefreshOnly();` `REFRESH_ONLY` 获取网络，响应；
- `JoyHttp.getLauncher().launchCacheOrRefresh();` `CACHE_OR_REFRESH` 无缓存时获取网络，响应；有缓存时获取缓存，响应；
- `JoyHttp.getLauncher().launchRefreshAndCache();` `REFRESH_AND_CACHE` 获取网络，更新缓存，响应；
- `JoyHttp.getLauncher().launchCacheAndRefresh();` `CACHE_AND_REFRESH` 获取缓存，响应，然后获取网络，更新缓存，响应；

**注意：每种请求方式的响应次数！**

### 回调方式：
- `RX订阅`
- `Listener callback`

### 第三方支持：
- [x] `Volley`
    - [ ] 优化下载大文件
    - [ ] 优化加载图片
- [ ] `Retrofit` `Okhttp`

### 用法示例

##### 初始化

```
JoyHttp.initialize(this, BuildConfig.DEBUG);
```

##### 测试模式, 发送请求

- 设置测试bean

```
ObjectRequest<User> objReq = ReqFactory.newGet("www.qyer.com", User.class);

User user = new User(1, "Klevin");
objReq.setTestData(user);// for test

objReq.setResponseListener(new ResponseListenerImpl<User>() {
    @Override
    public void onSuccess(Object tag, User user) {
    }
});
JoyHttp.getLauncher().launchRefreshOnly(objReq);
```

- 设置测试json串

```
ObjectRequest<User> objReq = ReqFactory.newGet("www.qyer.com", User.class);

String json = "{
               "id": 1,
               "name": "Klevin"
               }";
objReq.setTestData(json);// for test

objReq.setResponseListener(new ResponseListenerImpl<User>() {
    @Override
    public void onSuccess(Object tag, User user) {
    }
});
JoyHttp.getLauncher().launchRefreshOnly(objReq);
```

##### 正常模式, 发送请求

- 普通回调方式

GET请求, 并且提供了完整的URL。代码如下:

```
ObjectRequest<User> objReq = ReqFactory.newGet("www.qyer.com", User.class);
objReq.setResponseListener(new ResponseListenerImpl<User>() {
    @Override
    public void onSuccess(Object tag, User user) {
    }

    @Override
    public void onError(Object tag, Throwable error) {
        super.onError(tag, error);
        showErrorTipView();
    }

    @Override
    public void onError(Object tag, JoyError error) {
        showToast(error.getMessage());
    }
});
JoyHttp.getLauncher().launchRefreshOnly(objReq);
```

GET请求, 提供了完整的URL和请求头。代码如下:

```
Map<String, String> headers = new HashMap<>();
headers.put("api-auth", "AD7A1775CA0B3154F9EDD");
headers.put("user-token", "user_token");

ObjectRequest<User> objReq = ReqFactory.newGet("www.qyer.com", User.class, null, headers);
objReq.setResponseListener(new ResponseListenerImpl<User>() {
    @Override
    public void onSuccess(Object tag, User user) {
    }

    ...
});
JoyHttp.getLauncher().launchRefreshOnly(objReq);
```

GET请求, 提供了基本的URL和参数列表。代码如下:

```
Map<String, String> params = new HashMap<>();
params.put("page", "1");
params.put("count", "20");

ObjectRequest<User> objReq = ReqFactory.newGet("http://open.qyer.com", User.class, params);
objReq.setResponseListener(new ResponseListenerImpl<User>() {
    @Override
    public void onSuccess(Object tag, User user) {
    }

    ...
});
JoyHttp.getLauncher().launchRefreshOnly(objReq);
```

GET请求, 提供了基本的URL、参数列表和请求头。代码如下:

```
Map<String, String> params = new HashMap<>();
params.put("page", "1");
params.put("count", "20");

Map<String, String> headers = new HashMap<>();
headers.put("api-auth", "AD7A1775CA0B3154F9EDD");
headers.put("user-token", "user_token");

ObjectRequest<User> objReq = ReqFactory.newGet("http://open.qyer.com", User.class, params, headers);
objReq.setResponseListener(new ResponseListenerImpl<User>() {
    @Override
    public void onSuccess(Object tag, User user) {
    }

    ...
});
JoyHttp.getLauncher().launchRefreshOnly(objReq);
```

- Rx订阅方式

```
ObjectRequest<User> objReq = ReqFactory.newGet("www.qyer.com", User.class);
JoyHttp.getLauncher().launchRefreshOnly(objReq)
        .subscribe(new Action1<User>() {// success
            @Override
            public void call(User user) {
            }
        }, new JoyErrorAction() {// error
            @Override
            public void call(Throwable t) {
                super.call(t);
                showErrorTipView();
            }

            @Override
            public void call(JoyError error) {
                showToast(error.getMessage());
            }
        }, new Action0() {// complete
            @Override
            public void call() {
            }
        });
```

或者

```
ObjectRequest<User> objReq = ReqFactory.newGet("www.qyer.com", User.class);
JoyHttp.getLauncher().launchRefreshOnly(objReq)
        .subscribe(new Action1<User>() {// success
            @Override
            public void call(User user) {
            }
        }, new JoyErrorAction() {// error
            @Override
            public void call(JoyError error) {
                showToast(error.getMessage());
            }
        }, new Action0() {// complete
            @Override
            public void call() {
            }
        });
```

##### 销毁

```
JoyHttp.shutDown();
```
