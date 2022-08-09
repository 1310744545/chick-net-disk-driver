
# Turn a cloud disk into a resource server，Support unlimited binding
# 让你的网盘秒变资源服务器，支持无限量绑定
[![GitHub release](https://img.shields.io/github/v/release/1310744545/chick-net-disk-driver.svg)](https://github.com/1310744545/chick-net-disk-driver/releases)

说明：[1.0.0版本](https://github.com/1310744545/chick-net-disk-driver/releases/tag/1.0.0)支持阿里云盘，实现最基本的资源访问功能

- [chick-net-disk-driver](#chick-net-disk-driver)
- [如何使用](#如何使用)
  - [Jar包运行](#jar包运行)
- [参数说明](#参数说明)
- [QQ群](#qq群)
- [新手教程](#新手教程)
  - [Windows10](#windows10)
  - [Linux](#linux)
  - [Mac](#mac)
- [浏览器获取refreshToken方式](#浏览器获取refreshtoken方式)
- [功能说明](#功能说明)
  - [支持的功能](#支持的功能)
  - [暂不支持的功能](#暂不支持的功能)
  - [已知问题](#已知问题)
  - [TODO](#todo)
- [免责声明](#免责声明)

# chick-net-disk-driver
本项目实现了只需要简单的配置一下，就可以让云盘变身为文件服务器。

# 如何使用
支持refreshToken登录方式，具体看参数说明
## Jar包运行
[点击下载Jar包](https://github.com/1310744545/chick-net-disk-driver/releases)
> 建议自己下载源码编译，以获得最新代码
```bash
java -jar chick-net-disk-driver-1.0.0-SNAPSHOT.jar --system.username="your username" --system.password="your password"
```

# 参数说明
```bash
--system.username
   非必填，系统登录的用户名、默认admin
--system.password
   非必填，系统登录的密码、默认admin
--server.port
   非必填，服务器端口号，默认为10086
```
# QQ群
> 群号：594142389

# 新手教程

## Windows10
TODO

## Linux
TODO

## Mac
TODO

# 浏览器获取refreshToken方式
1. 先通过浏览器（建议chrome）打开阿里云盘官网并登录：https://www.aliyundrive.com/drive/
2. 登录成功后，按F12打开开发者工具，点击Application，点击Local Storage，点击 Local Storage下的 [https://www.aliyundrive.com/](https://www.aliyundrive.com/)，点击右边的token，此时可以看到里面的数据，其中就有refresh_token，把其值复制出来即可。（格式为小写字母和数字，不要复制双引号。例子：ca6bf2175d73as2188efg81f87e55f11）
3. 第二步有点繁琐，大家结合下面的截图就看懂了
 ![image](https://user-images.githubusercontent.com/32785355/119246278-e6760880-bbb2-11eb-877c-aca16cf75d89.png)

# 功能说明
## 支持的功能
1. 查看文件夹、查看文件
2. 文件下载

## 暂不支持的功能
1. 移动文件到其他目录的同时，修改文件名。比如 /a.zip 移动到 /b/a1.zip，是不支持的
2. 文件重命名
3. 文件删除
4. 文件移动目录
5. 文件上传（支持大文件自动分批上传）
6. 支持超大文件上传（官方限制30G）
7. 文件上传断点续传
8. 文件下载断点续传
9. 流媒体播放等功能
10. 部分客户端兼容性不好
## 已知问题
1. 暂无

## TODO
1. 支持更多登录方式（验证码、账号密码等）
2. 不支持的功能实现


# 免责声明
1. 本软件为免费开源项目，无任何形式的盈利行为。
2. 本软件服务于阿里云盘等网盘，旨在让阿里云盘功能更强大。如有侵权，请与我联系，会及时处理。
3. 本软件皆调用官方接口实现，无任何“Hack”行为，无破坏官方接口行为。
5. 本软件仅做流量转发，不拦截、存储、篡改任何用户数据。
6. 严禁使用本软件进行盈利、损坏官方、散落任何违法信息等行为。
7. 本软件不作任何稳定性的承诺，如因使用本软件导致的文件丢失、文件破坏等意外情况，均与本软件无关。
