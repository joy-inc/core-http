### core-http

**网络请求库**

### 外部引用

```
compile 'com.joy.support:core-http:0.2.9'
```

### 请求方式：
- `JoyHttp.getLauncher().launchRefreshOnly();` // 获取网络，响应；
- `JoyHttp.getLauncher().launchCacheOrRefresh();` // 无缓存时获取网络，响应；有缓存时获取缓存，响应；
- `JoyHttp.getLauncher().launchRefreshAndCache();` // 获取网络，更新缓存，响应；
- `JoyHttp.getLauncher().launchCacheAndRefresh();` // 获取缓存，响应，然后获取网络，更新缓存，响应；

**注意：每种请求方式的响应次数！**

### 回调方式：
- `RX订阅`
- `Listener callback`

### 版本历史

- `0.2.9` 升级fastjson到1.1.63.2.android，修正部分字段赋值失败的情况；

- `0.2.6` 更新fastjson过滤String为null的情况；fix细节bugs；

- `0.2.5` Request中增加setPriority方法，用来设置请求优先级；支持core-http-compiler;

- `0.2.2` 修正接收文本数据时的编码格式；完善测试log，正式环境下不输出；删除无用的代码和文件；

- `0.2.0` 史上最大更新。优化了大文件的下载；添加ImageRequest用来下载图片；下载支持显示进度；优化了内存占用；

- `0.1.2` 大改版前的最后一版，网络请求的基础版本，支持get/post请求方式；

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

### Joy-Library中的引用体系

![](core-http.png)
