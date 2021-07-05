package com.blockchain.wallet.ethereum;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.5.
 */
@SuppressWarnings("rawtypes")
public class Ownbit_sol_OwnbitMultiSig extends Contract {
    private static final String BINARY = "608060405260006003553480156200001657600080fd5b50604051620012fb380380620012fb8339810160405280516020820151910180519091906000908260098211801590620000505750818111155b80156200005e575060018110155b15156200006a57600080fd5b600092505b84518310156200013457600080600087868151811015156200008d57fe5b90602001906020020151600160a060020a0316600160a060020a03168152602001908152602001600020541180620000e757508451600090869085908110620000d257fe5b90602001906020020151600160a060020a0316145b15620000f257600080fd5b4360008087868151811015156200010557fe5b6020908102909101810151600160a060020a03168252810191909152604001600020556001909201916200006f565b8451620001499060019060208801906200015b565b50505060029190915550620001ef9050565b828054828255906000526020600020908101928215620001b3579160200282015b82811115620001b35782518254600160a060020a031916600160a060020a039091161782556020909201916001909101906200017c565b50620001c1929150620001c5565b5090565b620001ec91905b80821115620001c1578054600160a060020a0319168155600101620001cc565b90565b6110fc80620001ff6000396000f3006080604052600436106100ae5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630117367281146100f457806302fb0c5e146101455780631398a5f61461014d5780635f43e63f146101745780636ad688261461018957806385b2566a146101aa578063a0e67e2b146101f3578063b7d5e56414610258578063c6a2a9f1146102ad578063d74f8edd146102c2578063f3acb258146102d7575b60003411156100f2576040805133815234602082015281517f5af8184bef8e4b45eb9f6ed7734d04da38ced226495548f46e0c8ff8d7d9a524929181900390910190a15b005b34801561010057600080fd5b506100f2600160a060020a036004803582169160248035909116916044359160643580820192908101359160843580820192908101359160a4359081019101356102ec565b6100f261056c565b34801561015957600080fd5b506101626105e5565b60408051918252519081900360200190f35b34801561018057600080fd5b506101626105eb565b34801561019557600080fd5b50610162600160a060020a03600435166105f2565b3480156101b657600080fd5b506100f260048035600160a060020a031690602480359160443580830192908201359160643580830192908201359160843591820191013561060d565b3480156101ff57600080fd5b50610208610849565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561024457818101518382015260200161022c565b505050509050019250505060405180910390f35b34801561026457600080fd5b506100f260048035600160a060020a031690602480359160443580830192908201359160643580830192908201359160843580830192908201359160a4359182019101356108ab565b3480156102b957600080fd5b50610162610a8f565b3480156102ce57600080fd5b50610162610a95565b3480156102e357600080fd5b50610162610a9a565b600160a060020a03891630141561034d576040805160e560020a62461bcd02815260206004820152601d60248201527f4e6f7420616c6c6f772073656e64696e6720746f20796f757273656c66000000604482015290519081900360640190fd5b600087116103a5576040805160e560020a62461bcd02815260206004820152601960248201527f4572633230207370656e642076616c756520696e76616c696400000000000000604482015290519081900360640190fd5b610434888a898989808060200260200160405190810160405280939291908181526020018383602002808284375050604080516020808f0282810182019093528e82529095508e94508d93508392508501908490808284375050604080516020808e0282810182019093528d82529095508d94508c935083925085019084908082843750610b27945050505050565b151561048a576040805160e560020a62461bcd02815260206004820152601260248201527f696e76616c6964207369676e6174757265730000000000000000000000000000604482015290519081900360640190fd5b600380546001019055604080517fa9059cbb000000000000000000000000000000000000000000000000000000008152600160a060020a038b81166004830152602482018a90529151918a169163a9059cbb9160448082019260009290919082900301818387803b1580156104fe57600080fd5b505af1158015610512573d6000803e3d6000fd5b505060408051600160a060020a03808d1682528d1660208201528082018b905290517f3d1915a2cdcecdfffc5eb2a7994c069bad5d4aa96aca85667dedbe60bb80491c9350908190036060019150a1505050505050505050565b33600090815260208190526040812054116105d1576040805160e560020a62461bcd02815260206004820152600c60248201527f4e6f7420616e206f776e65720000000000000000000000000000000000000000604482015290519081900360640190fd5b336000908152602081905260409020439055565b60025490565b622dc6c081565b600160a060020a031660009081526020819052604090205490565b600160a060020a03881630141561066e576040805160e560020a62461bcd02815260206004820152601d60248201527f4e6f7420616c6c6f772073656e64696e6720746f20796f757273656c66000000604482015290519081900360640190fd5b3031871180159061067f5750600087115b15156106d5576040805160e560020a62461bcd02815260206004820152601e60248201527f62616c616e6365206f72207370656e642076616c756520696e76616c69640000604482015290519081900360640190fd5b610765600089898989808060200260200160405190810160405280939291908181526020018383602002808284375050604080516020808f0282810182019093528e82529095508e94508d93508392508501908490808284375050604080516020808e0282810182019093528d82529095508d94508c935083925085019084908082843750610b27945050505050565b15156107bb576040805160e560020a62461bcd02815260206004820152601260248201527f696e76616c6964207369676e6174757265730000000000000000000000000000604482015290519081900360640190fd5b600380546001019055604051600160a060020a0389169088156108fc029089906000818181858888f193505050501580156107fa573d6000803e3d6000fd5b5060408051600160a060020a038a1681526020810189905281517fd3eec71143c45f28685b24760ea218d476917aa0ac0392a55e5304cef40bd2b6929181900390910190a15050505050505050565b606060018054806020026020016040519081016040528092919081815260200182805480156108a157602002820191906000526020600020905b8154600160a060020a03168152600190910190602001808311610883575b5050505050905090565b600160a060020a038a1630141561090c576040805160e560020a62461bcd02815260206004820152601d60248201527f4e6f7420616c6c6f772073656e64696e6720746f20796f757273656c66000000604482015290519081900360640190fd5b6109a660098b8b8b8b808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050508a8a8080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505089898080602002602001604051908101604052809392919081815260200183836020028082843750610b27945050505050565b15156109fc576040805160e560020a62461bcd02815260206004820152601260248201527f696e76616c6964207369676e6174757265730000000000000000000000000000604482015290519081900360640190fd5b60035460010160038190555089600160a060020a03168983836040518083838082843782019150509250505060006040518083038185875af19250505015610a835760408051600160a060020a038c168152602081018b905281517f62ee6f1a2424e70e5cff9d61a0d928aa101e198f192d726c651f1bdad1cd40d9929181900390910190a15b50505050505050505050565b60035490565b600981565b600080805b600154811015610afa5743622dc6c0600080600185815481101515610ac057fe5b6000918252602080832090910154600160a060020a031683528201929092526040019020540110610af2576001909101905b600101610a9f565b6002548210610b0d576002549250610b22565b60018210610b1d57819250610b22565b600192505b505090565b6000806060600085518751141515610b3e57600080fd5b8451865114610b4c57600080fd5b60015487511115610b5c57600080fd5b610b64610a9a565b87511015610b7157600080fd5b610b7c8a8a8a610d6f565b92508651604051908082528060200260200182016040528015610ba9578160200160208202803883390190505b509150600090505b8651811015610c96576001838883815181101515610bcb57fe5b90602001906020020151601b018884815181101515610be657fe5b906020019060200201518885815181101515610bfe57fe5b60209081029091018101516040805160008082528185018084529790975260ff9095168582015260608501939093526080840152905160a0808401949293601f19830193908390039091019190865af1158015610c5f573d6000803e3d6000fd5b505050602060405103518282815181101515610c7757fe5b600160a060020a03909216602092830290910190910152600101610bb1565b610c9f82610e83565b1515610caa57600080fd5b610cb382610f76565b610cbb610a9a565b87511015610d5f576040805160e560020a62461bcd02815260206004820152605060248201527f416374697665206f776e6572732075706461746564206166746572207468652060448201527f63616c6c2c20706c656173652063616c6c206163746976652829206265666f7260648201527f652063616c6c696e67207370656e642e00000000000000000000000000000000608482015290519081900360a40190fd5b5060019998505050505050505050565b6000806060610d7f868686611008565b91506040805190810160405280601c81526020017f19457468657265756d205369676e6564204d6573736167653a0a333200000000815250905080826040516020018083805190602001908083835b60208310610ded5780518252601f199092019160209182019101610dce565b51815160209384036101000a600019018019909216911617905292019384525060408051808503815293820190819052835193945092839250908401908083835b60208310610e4d5780518252601f199092019160209182019101610e2e565b5181516020939093036101000a600019018019909116921691909117905260405192018290039091209998505050505050505050565b600080600060018054905084511115610e9f5760009250610f6f565b600091505b8351821015610f6a576000808584815181101515610ebe57fe5b90602001906020020151600160a060020a0316600160a060020a031681526020019081526020016000205460001415610efa5760009250610f6f565b5060005b81811015610f5f578381815181101515610f1457fe5b90602001906020020151600160a060020a03168483815181101515610f3557fe5b90602001906020020151600160a060020a03161415610f575760009250610f6f565b600101610efe565b600190910190610ea4565b600192505b5050919050565b60005b81518110156110045760008060008484815181101515610f9557fe5b90602001906020020151600160a060020a0316600160a060020a03168152602001908152602001600020541115610ffc57436000808484815181101515610fd857fe5b6020908102909101810151600160a060020a03168252810191909152604001600020555b600101610f79565b5050565b600354604080516c01000000000000000000000000308102602080840191909152600160a060020a038089168302603485015287169091026048830152605c8201859052607c8083019490945282518083039094018452609c90910191829052825160009384939092909182918401908083835b6020831061109b5780518252601f19909201916020918201910161107c565b5181516020939093036101000a60001901801990911692169190911790526040519201829003909120989750505050505050505600a165627a7a72305820352db31f6b792fd7993eff9409bee4eda9311fc544b000a88a24c4fef18702a90029";

    public static final String FUNC_SPENDERC20 = "spendERC20";

    public static final String FUNC_ACTIVE = "active";

    public static final String FUNC_GETREQUIRED = "getRequired";

    public static final String FUNC_MAX_INACTIVE_BLOCKNUMBER = "MAX_INACTIVE_BLOCKNUMBER";

    public static final String FUNC_GETOWNERBLOCK = "getOwnerBlock";

    public static final String FUNC_SPEND = "spend";

    public static final String FUNC_GETOWNERS = "getOwners";

    public static final String FUNC_SPENDANY = "spendAny";

    public static final String FUNC_GETSPENDNONCE = "getSpendNonce";

    public static final String FUNC_MAX_OWNER_COUNT = "MAX_OWNER_COUNT";

    public static final String FUNC_GETREQUIREDWITHOUTINACTIVE = "getRequiredWithoutInactive";

    public static final Event FUNDED_EVENT = new Event("Funded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SPENT_EVENT = new Event("Spent", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SPENTERC20_EVENT = new Event("SpentERC20", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SPENTANY_EVENT = new Event("SpentAny", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Ownbit_sol_OwnbitMultiSig(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Ownbit_sol_OwnbitMultiSig(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Ownbit_sol_OwnbitMultiSig(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Ownbit_sol_OwnbitMultiSig(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> spendERC20(String destination, String erc20contract, BigInteger value, List<BigInteger> vs, List<byte[]> rs, List<byte[]> ss) {
        final Function function = new Function(
                FUNC_SPENDERC20, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, destination), 
                new org.web3j.abi.datatypes.Address(160, erc20contract), 
                new org.web3j.abi.datatypes.generated.Uint256(value), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(vs, org.web3j.abi.datatypes.generated.Uint8.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(rs, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(ss, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> active(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_ACTIVE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<BigInteger> getRequired() {
        final Function function = new Function(FUNC_GETREQUIRED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> MAX_INACTIVE_BLOCKNUMBER() {
        final Function function = new Function(FUNC_MAX_INACTIVE_BLOCKNUMBER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getOwnerBlock(String addr) {
        final Function function = new Function(FUNC_GETOWNERBLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> spend(String destination, BigInteger value, List<BigInteger> vs, List<byte[]> rs, List<byte[]> ss) {
        final Function function = new Function(
                FUNC_SPEND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, destination), 
                new org.web3j.abi.datatypes.generated.Uint256(value), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(vs, org.web3j.abi.datatypes.generated.Uint8.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(rs, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(ss, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<List> getOwners() {
        final Function function = new Function(FUNC_GETOWNERS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> spendAny(String destination, BigInteger value, List<BigInteger> vs, List<byte[]> rs, List<byte[]> ss, byte[] data) {
        final Function function = new Function(
                FUNC_SPENDANY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, destination), 
                new org.web3j.abi.datatypes.generated.Uint256(value), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(vs, org.web3j.abi.datatypes.generated.Uint8.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(rs, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(ss, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getSpendNonce() {
        final Function function = new Function(FUNC_GETSPENDNONCE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> MAX_OWNER_COUNT() {
        final Function function = new Function(FUNC_MAX_OWNER_COUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getRequiredWithoutInactive() {
        final Function function = new Function(FUNC_GETREQUIREDWITHOUTINACTIVE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public List<FundedEventResponse> getFundedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(FUNDED_EVENT, transactionReceipt);
        ArrayList<FundedEventResponse> responses = new ArrayList<FundedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            FundedEventResponse typedResponse = new FundedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<FundedEventResponse> fundedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, FundedEventResponse>() {
            @Override
            public FundedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(FUNDED_EVENT, log);
                FundedEventResponse typedResponse = new FundedEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<FundedEventResponse> fundedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(FUNDED_EVENT));
        return fundedEventFlowable(filter);
    }

    public List<SpentEventResponse> getSpentEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SPENT_EVENT, transactionReceipt);
        ArrayList<SpentEventResponse> responses = new ArrayList<SpentEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SpentEventResponse typedResponse = new SpentEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.transfer = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SpentEventResponse> spentEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, SpentEventResponse>() {
            @Override
            public SpentEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SPENT_EVENT, log);
                SpentEventResponse typedResponse = new SpentEventResponse();
                typedResponse.log = log;
                typedResponse.to = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.transfer = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SpentEventResponse> spentEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SPENT_EVENT));
        return spentEventFlowable(filter);
    }

    public List<SpentERC20EventResponse> getSpentERC20Events(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SPENTERC20_EVENT, transactionReceipt);
        ArrayList<SpentERC20EventResponse> responses = new ArrayList<SpentERC20EventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SpentERC20EventResponse typedResponse = new SpentERC20EventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.erc20contract = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.transfer = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SpentERC20EventResponse> spentERC20EventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, SpentERC20EventResponse>() {
            @Override
            public SpentERC20EventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SPENTERC20_EVENT, log);
                SpentERC20EventResponse typedResponse = new SpentERC20EventResponse();
                typedResponse.log = log;
                typedResponse.erc20contract = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.transfer = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SpentERC20EventResponse> spentERC20EventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SPENTERC20_EVENT));
        return spentERC20EventFlowable(filter);
    }

    public List<SpentAnyEventResponse> getSpentAnyEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SPENTANY_EVENT, transactionReceipt);
        ArrayList<SpentAnyEventResponse> responses = new ArrayList<SpentAnyEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SpentAnyEventResponse typedResponse = new SpentAnyEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.transfer = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SpentAnyEventResponse> spentAnyEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, SpentAnyEventResponse>() {
            @Override
            public SpentAnyEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SPENTANY_EVENT, log);
                SpentAnyEventResponse typedResponse = new SpentAnyEventResponse();
                typedResponse.log = log;
                typedResponse.to = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.transfer = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SpentAnyEventResponse> spentAnyEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SPENTANY_EVENT));
        return spentAnyEventFlowable(filter);
    }

    @Deprecated
    public static Ownbit_sol_OwnbitMultiSig load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Ownbit_sol_OwnbitMultiSig(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Ownbit_sol_OwnbitMultiSig load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Ownbit_sol_OwnbitMultiSig(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Ownbit_sol_OwnbitMultiSig load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Ownbit_sol_OwnbitMultiSig(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Ownbit_sol_OwnbitMultiSig load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Ownbit_sol_OwnbitMultiSig(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Ownbit_sol_OwnbitMultiSig> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, List<String> _owners, BigInteger _required) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_owners, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(_required)));
        return deployRemoteCall(Ownbit_sol_OwnbitMultiSig.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<Ownbit_sol_OwnbitMultiSig> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, List<String> _owners, BigInteger _required) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_owners, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(_required)));
        return deployRemoteCall(Ownbit_sol_OwnbitMultiSig.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Ownbit_sol_OwnbitMultiSig> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, List<String> _owners, BigInteger _required) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_owners, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(_required)));
        return deployRemoteCall(Ownbit_sol_OwnbitMultiSig.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Ownbit_sol_OwnbitMultiSig> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, List<String> _owners, BigInteger _required) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_owners, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(_required)));
        return deployRemoteCall(Ownbit_sol_OwnbitMultiSig.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class FundedEventResponse extends BaseEventResponse {
        public String from;

        public BigInteger value;
    }

    public static class SpentEventResponse extends BaseEventResponse {
        public String to;

        public BigInteger transfer;
    }

    public static class SpentERC20EventResponse extends BaseEventResponse {
        public String erc20contract;

        public String to;

        public BigInteger transfer;
    }

    public static class SpentAnyEventResponse extends BaseEventResponse {
        public String to;

        public BigInteger transfer;
    }
}
