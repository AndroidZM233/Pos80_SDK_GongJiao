
# 公交人脸识别（恩钛~乾海）
## 五部分

* 路由器插4G卡 号段是192.168.10.1
* 摄像头
* 工控机（主板服务器，跑jar包 连摄像头，人脸识别到后  通过http发送出来，发送地址需要编辑配置信息）
* 本模块，作为httpServer 接收消息（faceid），接收到后 显示提示 组Json包发送到云服务器
* 云服务器

以上内容第四部分是我们完成的工作
## 测试步骤
### 固定设备IP
* 查看设备MAC
* 192.168.10.1 输入密码admin
* 无线->DSCP 中 设置静态地址分配，
  将设备MAC绑定IP为 192.168.10.193
  其余设置
  然后按照操作步骤.doc 配置服务器连接PDA地址




![image-20181225125025473](/Users/echo/Library/Application Support/typora-user-images/image-20181225125025473.png)




工控机调试指令 ssh远程 连接 同步人脸库  查看log等
* ssh n-tech-admin@192.168.10.129 密码n-tech123@
* cd /anytec/server/bus/ 进入到应用目录
* rm -r db 删除数据库重新同步
* curl -X POST http://192.168.10.129:10000/device/sync 同步人脸
* findface-facenapi.token  查看token
* curl -H "Authorization:Token HCyF-ZGA6" http://192.168.10.129:8000/v0/faces 查看所有录入人脸信息

* 修改配置文件需要重启 sudo supervisorctl restart all