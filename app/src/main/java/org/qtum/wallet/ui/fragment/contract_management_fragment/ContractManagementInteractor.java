package org.qtum.wallet.ui.fragment.contract_management_fragment;


import org.qtum.wallet.model.contract.Contract;
import org.qtum.wallet.model.contract.ContractMethod;
import org.qtum.wallet.model.gson.SmartContractInfo;

import java.util.List;

import rx.Observable;

public interface ContractManagementInteractor {
    List<ContractMethod> getContractListByUiid(String uiid);
    List<ContractMethod> getContractListByAbi(String abi);
    Contract getContractByAddress(String contractAddress);
    Observable<SmartContractInfo> getContractInfo(final String address);
}
