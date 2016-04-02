### 前言

这段时间由于竞赛需要学习了JPBC库的使用， 不得不说，虽然此库极其强大，然而由于毕竟这块没有像那些大规模商用的东西那样流行，所以网上无论是英文还是中文的资料都极为稀少，官方文档也语焉不详。折腾了快半个月才把它大致搞懂，所以想把过程写下来，方便他人学习使用，如有错误，望指出。


### 背景知识:

#### 双线性群初始化:

- 质数阶双线性群（Prime-Order Bilinear Groups）；
- 合数阶双线性群（Composite-Order Bilinear Groups）；

#### 双线性群运算 :

- 指数群 Zr的加法和乘法
- 双线性群$G_1$,$G_2$的乘法和指数幂
- 目标群$G_T$乘法和指数幂
- 双线性群$G_1$,$G_2$映射到目标群$G_T$的对（Pairing）运算；


#### 质数阶双线性群（Prime-Order Bilinear Groups）

给定一个大质数p，$G_1$,$G_2$,$G_T$ 均是阶为p的乘法循环群，e 为双线性映射 e : $G_1$ x $G_2$ -> $G_T$

对称双线性映射: $G_1$ = $G_2$ 即 e : $G_1$ x $G_1$ -> $G_T$

更多知识，请查阅相关文献和维基百科。

#### JPBC中的双线性
在JPBC中， G1,G2是乘法循环群。

且G1,G2的元素都是一个个点坐标(基于椭圆曲线)，而$G_T$是$Z_p$中的一个数。

### JPBC 使用

#### 初始化

在所有操作之前， 都必须有对双线性群进行初始化。[官方文档](http://gas.dia.unisa.it/projects/jpbc/docs/pairing.html#.Vv8hsi594p8)对各个参数进行了详细说明。这里我使用A群， 建立 jpbc.properties 文件， 内容如下:

	type=a
	q 8780710799663312522437781984754049815806883199414208211028653399266475630880222957078625179422662221423155858769582317459277713367317481324925129998224791
	h 12016012264891146079388821366740534204802954401251311822919615131047207289359704531102844802183906537786776
	r 730750818665451621361119245571504901405976559617
	exp2 159
	exp1 107
	sign1 1
	sign0 1
	
参数说明:
	- type : 指定椭圆曲线的种类
	- q : G群的阶数的比特长度

	
#### 产生随机数:

``` java
	//随机产生一个Z_p群的元素
	Element Z_p = pairing.getZr().newRandomElement().getImmutable();
	//随机产生一个G_1群的元素
	Element G_1 = pairing.getG1().newRandomElement().getImmutable();
	//随机产生一个G_2群的元素
	Element G_2 = pairing.getG2().newRandomElement().getImmutable();
	//随机产生一个G_T群的元素
	Element G_T = pairing.getGT().newRandomElement().getImmutable();
	
```

#### 将指定的元素哈希到双线性群中 :

``` java
//将byte[] byteArray_Z_p哈希到Z_p群
Element hash_Z_p = pairing.getZr().newElement().setFromHash(byteArray_Z_p, 0, byteArray_Z_p.length);
//将byte[] byteArray_G_1哈希到G_1群
Element hash_G_1 = pairing.getG1().newElement().setFromHash(byteArray_G_1, 0, byteArray_G_1.length);
//将byte[] byteArray_G_2哈希到G_2群
Element hash_G_2 = pairing.getG2().newElement().setFromHash(byteArray_G_2, 0, byteArray_G_2.length);
//将byte[] byteArray_G_T哈希到G_T群
Element hash_G_T = pairing.getGT().newElement().setFromHash(byteArray_G_T, 0, byteArray_G_T.length);
```

#### 双线性群的运算

``` java
//初始化相关参数
Element G_1 = pairing.getG1().newRandomElement().getImmutable();
Element G_2 = pairing.getG2().newRandomElement().getImmutable();
Element Z = pairing.getZr().newRandomElement().getImmutable();
Element G_T = pairing.getGT().newRandomElement().getImmutable();
	
Element G_1_p = pairing.getG1().newRandomElement().getImmutable();
Element G_2_p = pairing.getG2().newRandomElement().getImmutable();
Element Z_p = pairing.getZr().newRandomElement().getImmutable();
Element G_T_p = pairing.getGT().newRandomElement().getImmutable();
	
//G_1的相关运算
//G_1 multiply G_1
Element G_1_m_G_1 = G_1.mul(G_1_p);
//G_1 power Z
Element G_1_e_Z = G_1.powZn(Z);
	
//G_2的相关运算
//G_2 multiply G_2
Element G_2_m_G_2 = G_2.mul(G_2_p);
//G_2 power Z
Element G_2_e_Z = G_2.powZn(Z);
	
//G_T的相关运算
//G_T multiply G_T
Element G_T_m_G_T = G_T.mul(G_T_p);
//G_T power Z
Element G_T_e_Z = G_T.powZn(Z);
	
//Z的相关运算
//Z add Z
Element Z_a_Z = Z.add(Z_p);
//Z multiply Z
Element Z_m_Z = Z.mul(Z_p);
	
//Pairing运算
Element G_p_G = pairing.pairing(G_1, G_2); 
```

### 使用JPBC中，可能会需要用到的其它函数:

由于JPBC并不是一个完整的密码学库，仅仅实现了双线性映射相关的部分。在实现一些密码学算法的时候，需要用到一些其它函数，而这些函数的参数与JPBC的基本参数类型Element又不是匹配的，一开始并不知道直接调用toBytes()就行了，所以纠结了好久。

我用一个Utils类封装了一些常用函数，以便后续调用:

``` java
public class Utils {
	
    /**
     * 标准sha256加密
     *
     * @param data
     * @return hash
     */
    public static byte[] sha256(byte[] data) {
        //Create a sha 256 of the message
        SHA256Digest dgst = new SHA256Digest();
        dgst.reset();
        dgst.update(data, 0, data.length);
        int digestSize = dgst.getDigestSize();
        byte[] hash = new byte[digestSize];
        dgst.doFinal(hash, 0);
        return hash;
    }
	
	
    public static String bytesToHex(byte[] bytes) {
        return org.apache.commons.codec.binary.Hex.encodeHexString(bytes);
    }
	
    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
	
    public static byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[Math.min(a.length, b.length)];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (((int) a[i]) ^ ((int) b[i]));
        }
        return result;
    }
	
    // 把一个byte[] 分拆成每段256字节的数组List
    public static ArrayList<byte[]> slice(byte[] msg) {
	
        ArrayList<byte[]> list = new ArrayList<byte[]>();
	
        // 待优化
        // boxCount 表示分组数目
        int boxCount = ((msg.length % SystemParameter.SIZE) == 0)
                ? (msg.length / SystemParameter.SIZE) :
                ((msg.length / SystemParameter.SIZE) + 1);
	
        for (int i = 0; i < boxCount - 1; ++i) {
            list.add(Arrays.copyOfRange(msg,
                    i * SystemParameter.SIZE, (i + 1) * SystemParameter.SIZE));
        }
        list.add(Arrays.copyOfRange(msg,
                (boxCount - 1) * SystemParameter.SIZE, msg.length));
        return list;
    }
	
    // 数组list组装成单个的byte[]
    public static byte[] splice(ArrayList<byte[]> byteMessage) {
	
        int boxCount = byteMessage.size();
	
        // byteSum 表示总字节数大小
        int byteSum = (SystemParameter.SIZE * (boxCount - 1)) +
                byteMessage.get(boxCount - 1).length;
        byte[] temp = new byte[byteSum];
	
        for (int i = 0; i < boxCount - 1; ++i) {
            for (int t = 0; t < SystemParameter.SIZE; ++t) {
                temp[i * SystemParameter.SIZE + t] = byteMessage.get(i)[t];
            }
        }
        for (int i = 0; i < byteMessage.get(boxCount - 1).length; ++i) {
            temp[SystemParameter.SIZE * (boxCount - 1) + i] =
                    byteMessage.get(boxCount - 1)[i];
        }
        return temp;
    }
	
	
    public static String toString(ArrayList<byte[]> byteMessage) {
        return new String(splice(byteMessage));
    }
	
}

```

上面包含了哈希和异或函数，以及一个把明文密文分拆给一个List以及组装回去的函数。

### 注意点

1. Java的运算结果都是产生一个新的Element来存储，所以我们需要把运算结果赋值给一个新的Element；
2. Java在进行相关运算时，参与运算的Element值可能会改变。所以，如果需要在运算过程中保留参与运算的Element值，
   在存储的时候一定要调用getImmutable()
3. 为了保险起见，防止Element在运算的过程中修改了Element原本的数值，可以使用Element.duplicate()方法。
   这个方法将返回一个与Element数值完全一样的Element，但是是个新的Element对象。



### 参考资料

[Java密码学原型算法实现——第三部分：双线性对](http://blog.csdn.net/liuweiran900217/article/details/45080653)

[JPBC DOC](http://gas.dia.unisa.it/projects/jpbc/)
