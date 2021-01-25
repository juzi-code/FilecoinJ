# FilecoinJ

服务于JAVA的Filecoin客户端JSON-RPC封装sdk

主要功能有：

* 1.离线创建钱包
* 2.访问节点创建地址
* 3.导入私钥
* 4.获取gas
* 5.获取nonce
* 6.转账
* 7.获取余额
* 8.校验地址有效性
* 9.根据cid获取交易信息

#正在开发完善中... 实际功能比以上列举要多，Filecoin类中的方法是所有的功能，描述文件有时间会补全...


时间有限只整理了钱包交互的基本功能，希望可以供大家参考学习，为区块链技术增添一份力量。

在springboot中使用很简单
* 第一步配置节点信息
 ``` 
 filecoin:
   rpc-url: 节点rpc url
   rpc-token: 节点 具有写权限的token
```
* 第二步依赖注入
 ``` 
 @Autowired
private Filecoin filecin;
```
* 第三步就可以使用该对象调用相应的方法实现相应的功能了
```
1.创建钱包
filecoin.createWallet();
2.导入私钥
filecoin.importWallet("私钥");
```
* 非springboot项目创建Filecoin方法
```
Filecoin filecin = new Filecoin('节点rpc url', '节点 具有写权限的token');
```

需要注意的是：
ove.blake2b
这个包可能下不下来
这里附上github的链接：https://github.com/alphazero/Blake2b
大家可以自行下载打包到本地
        
详细的大家可以参考代码，还有测试方法，代码都比较简单，有需要的码友可以去不断的优化
有时间我会更新整理一下

# 代码是在https://github.com/lishitao1992/filecoinj 这个作者的基础之上进行修改的，感谢！
