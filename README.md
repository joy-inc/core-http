### JoyHttp

Android网络请求库

### Add JoyHttp to your project

Gradle:

```
compile 'com.joy.http:JoyHttp:0.1.2'
```

Maven:

```
<dependency>
  <groupId>com.joy.http</groupId>
  <artifactId>JoyHttp</artifactId>
  <version>0.1.2</version>
  <type>pom</type>
</dependency>
```

 Ivy:

 ```
 <dependency org='com.joy.http' name='JoyHttp' rev='0.1.2'>
   <artifact name='$AID' ext='pom'></artifact>
 </dependency>
 ```

### 请求方式：  
- `REFRESH_ONLY` 获取网络，响应；
- `CACHE_ONLY` 获取缓存，响应；
- `REFRESH_AND_CACHE` 缓存过期了，获取网络，更新缓存，响应；
- `CACHE_AND_REFRESH` 获取缓存，响应，获取网络，更新缓存，响应；

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
public class JoyApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        JoyHttp.initialize(this, BuildConfig.DEBUG);
    }
}
```

##### 测试模式, 发送请求

- 设置测试bean

```
ObjectRequest<User> objReq = ReqFactory.newGet("www.qyer.com", User.class);

User user = new User(1, "Kevin");
objReq.setTestData(user);// for test

objReq.setResponseListener(new ObjectResponse<User>() {
    @Override
    public void onSuccess(Object tag, User user) {
    }
});
JoyHttp.getLauncher().launchRefreshOnly(objReq);
```

- 设置测试json

```
ObjectRequest<User> objReq = ReqFactory.newGet("www.qyer.com", User.class);

String json = "{
               "id": 1,
               "name": "Kevin"
               }";
objReq.setTestData(json);// for test

objReq.setResponseListener(new ObjectResponse<User>() {
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
objReq.setResponseListener(new ObjectResponse<User>() {
    @Override
    public void onSuccess(Object tag, User user) {
    }

    @Override
    public void onError(Object tag, String msg) {
    }
});
JoyHttp.getLauncher().launchRefreshOnly(objReq);
```

GET请求, 提供了基本的URL和参数列表。代码如下:

```
Map<String, String> params = new HashMap<>();
params.put("page", "1");
params.put("count", "20");

ObjectRequest<User> objReq = ReqFactory.newGet("http://open.qyer.com", User.class, params);
objReq.setResponseListener(new ObjectResponse<User>() {
    @Override
    public void onSuccess(Object tag, User user) {
    }
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
objReq.setResponseListener(new ObjectResponse<User>() {
    @Override
    public void onSuccess(Object tag, User user) {
    }
});
JoyHttp.getLauncher().launchRefreshOnly(objReq);
```

- Rx订阅方式

```
ObjectRequest<User> objReq = ReqFactory.newGet("www.qyer.com", User.class);
JoyHttp.getLauncher().launchRefreshOnly(objReq)
        .filter(new Func1<User, Boolean>() {
            @Override
            public Boolean call(User user) {
                return user != null;
            }
        })
        .subscribe(new Action1<User>() {
            @Override
            public void call(User user) {// success
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {// error
            }
        }, new Action0() {
            @Override
            public void call() {// complete
            }
        });
```

##### 销毁

```
JoyHttp.shutDown();
```
