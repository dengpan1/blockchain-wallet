eth多签钱包：
一个 M-N 多签的含义，以 3-5 多签为例，是指 5 个人管理资产，3 个人同意的情况下，可以花费该笔资产。在以太坊中，一个地址（私钥）代表一个人。如何表示你同意花费某笔资产？有两种方式：
	a.用你的私钥对相应的花费（金额、目标地址等等）进行签名，并给出签名结果；
	b.用你的私钥发送一笔以太坊交易，去调用某个特定接口，并给予特定参数；
	
	Ownbit 多签使用了第一种方法，而 Gnosis 多签使用了第二种方法。

1、Gnosis：
	MultiSigWalletWithDailyLimit.sol
	github：https://github.com/ConsenSysMesh/MultiSigWallet
	ropsten address：0x0fe5a721c954fc1afe279c382a8d0222f2d2e8fe		3-2

2、 Ownbit
	OwnbitMultiSigV5.sol
	github：https://github.com/bitbill/bitbill-multisig-contracts
	ropsten address：0x839B36cb7202E2b5dFb854CACC60DFE2bd171aE0		3-2