# FilecoinJ

服务于JAVA的Filecoin客户端JSON-RPC封装sdk

本人也是区块链技术的爱好者，也从事着区块链的一些相关工作，这段时间看Filecoin上线了，初步研究了一下，由于看目前网络上还没有JAVA版的Filecoin SDK，出于受益于开源，回报于开源的心态，故此花了点时间整理了一版Filecoin JAVA SDK基础版本，
主要功能有：

* 1.创建钱包
* 2.导入私钥
* 3.获取gas
* 4.获取nonce
* 5.转账
* 6.获取余额

时间有限只整理了钱包交互的基本功能，希望可以供大家参考学习，为区块链技术增添一份力量。

使用也很简单都在 Filecoin这个类里面可以调用到
* 第一步创建Filecoin
 ``` 
 Filecoin filecin = new Filecoin("节点rpc url","节点 具有写权限的token")
```
* 第二步就可以使用该对象调用相应的方法实现相应的功能了
```
1.创建钱包
filecoin.createWallet();
2.导入私钥
filecoin.importWallet("私钥");
```
需要注意的是：
ove.blake2b
这个包可能下不下来
这里附上github的链接：https://github.com/alphazero/Blake2b
大家可以自行下载打包到本地
        
详细的大家可以参考代码，还有测试方法，代码都比较简单，有需要的码友可以去不断的优化
有时间我会更新整理一下
