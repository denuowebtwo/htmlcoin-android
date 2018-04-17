package org.qtum.wallet.ui.fragment.send_fragment;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.qtum.wallet.R;
import org.qtum.wallet.datastorage.realm.RealmStorage;
import org.qtum.wallet.model.Currency;
import org.qtum.wallet.model.CurrencyToken;
import org.qtum.wallet.model.contract.Token;
import org.qtum.wallet.model.gson.AddressBalance;
import org.qtum.wallet.model.gson.UnspentOutput;
import org.qtum.wallet.model.gson.call_smart_contract_response.CallSmartContractResponse;
import org.qtum.wallet.model.gson.token_balance.Balance;
import org.qtum.wallet.model.gson.token_balance.TokenBalance;
import org.qtum.wallet.model.gson.token_balance.TokenBalanceResponse;
import org.qtum.wallet.ui.base.AddressInteractor;
import org.qtum.wallet.ui.base.AddressInteractorImpl;
import org.qtum.wallet.ui.base.base_fragment.BaseFragment;
import org.qtum.wallet.ui.base.base_fragment.BaseFragmentPresenterImpl;
import org.qtum.wallet.utils.CurrentNetParams;
import org.qtum.wallet.utils.LogUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SendPresenterImpl extends BaseFragmentPresenterImpl implements SendPresenter {

    private SendView mSendFragmentView;
    private SendInteractor mSendBaseFragmentInteractor;
    private boolean mNetworkConnectedFlag = false;
    private List<Token> mTokenList;
    private double minFee = 0.1;
    private double maxFee = 1;

    private int minGasPrice;
    private int maxGasPrice = 120;

    private int minGasLimit = 100000;
    private int maxGasLimit = 5000000;

    private AddressInteractor mAddressInteractor;
    private TokenBalanceResponse mTokenBalance;

    public SendPresenterImpl(SendView sendFragmentView, SendInteractor interactor) {
        mSendFragmentView = sendFragmentView;
        mSendBaseFragmentInteractor = interactor;

        mAddressInteractor = new AddressInteractorImpl(sendFragmentView.getContext());
    }

    @Override
    public void initializeViews() {
        super.initializeViews();
        mTokenList = new ArrayList<>();
        for (Token token : getInteractor().getTokenList()) {
            if (token.isSubscribe()) {
                mTokenList.add(token);
            }
        }
        if (!mTokenList.isEmpty()) {
            getView().setUpCurrencyField(R.string.default_currency);
        } else {
            getView().hideCurrencyField();
        }

        // temporary use 0.1
        // minFee = getInteractor().getFeePerKb().doubleValue();
        getView().updateFee(minFee, maxFee);
        minGasPrice = getInteractor().getMinGasPrice();
        getView().updateGasPrice(minGasPrice, maxGasPrice);
        getView().updateGasLimit(minGasLimit, maxGasLimit);

        loadBalance();
    }

    @Override
    public void handleBalanceChanges(final BigDecimal unconfirmedBalance, final BigDecimal balance) {
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                String balanceString = balance.toString();
                if (balanceString != null) {
                    return Observable.just(balanceString);
                } else {
                    return Observable.error(new Throwable("Balance is null"));
                }
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String balanceString) {
                        getView().handleBalanceUpdating(balanceString, unconfirmedBalance);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

    }

    @Override
    public void searchAndSetUpCurrency(String currency) {
        for (Token token : getInteractor().getTokenList()) {
            if (token.getContractAddress().equals(currency)) {
                getView().setUpCurrencyField(new CurrencyToken(token.getContractName(), token));

                loadAndUpdateTokenBalance(token);

                return;
            }
        }
        getView().setAlertDialog(R.string.token_not_found, "Ok", BaseFragment.PopUpType.error);
    }

    @Override
    public void onCurrencyChoose(Currency currency) {
        getView().setUpCurrencyField(currency);

        if (currency.getName().equals("Html " + getView().getStringValue(org.qtum.wallet.R.string.default_currency))) {
            loadAndUpdateBalance();
        } else {
            for (final Token token : mTokenList) {
                String contractAddress = token.getContractAddress();
                if (contractAddress.equals(((CurrencyToken) currency).getToken().getContractAddress())) {
                    loadAndUpdateTokenBalance(token);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getView().removePermissionResultListener();
        //TODO:unsubscribe rx
    }

    @Override
    public SendView getView() {
        return mSendFragmentView;
    }

    private SendInteractor getInteractor() {
        return mSendBaseFragmentInteractor;
    }

    @Override
    public void onResponse(String publicAddress, double amount, String tokenAddress) {
        getView().updateData(publicAddress, amount);
        if (!tokenAddress.isEmpty()) {
            searchAndSetUpCurrency(tokenAddress);
        }
    }

    @Override
    public void onResponseError() {
        getView().errorRecognition();
    }

    private String availableAddress = null;
    public String params = null;

    @Override
    public void send() {
        if (mNetworkConnectedFlag) {
            double feeDouble = minFee;
            try {
                feeDouble = Double.valueOf(getView().getFeeInput().replace(',', '.'));
            } catch (NumberFormatException ex) {
                LogUtils.error("Send", ex.getMessage(), ex);
            }

            if (feeDouble < minFee || feeDouble > maxFee) {
                getView().dismissProgressDialog();
                getView().setAlertDialog(org.qtum.wallet.R.string.error, R.string.invalid_fee, "Ok", BaseFragment.PopUpType.error);
                return;
            }

            try {
                Address.fromBase58(CurrentNetParams.getNetParams(), getView().getAddressInput());
            } catch (AddressFormatException a) {
                getView().setAlertDialog(org.qtum.wallet.R.string.error, org.qtum.wallet.R.string.invalid_qtum_address, "Ok", BaseFragment.PopUpType.error);
                return;
            }

            if (!getView().isValidAmount()) {
                return;
            }
            getView().hideKeyBoard();
            getView().showPinDialog();

        } else {
            getView().setAlertDialog(org.qtum.wallet.R.string.no_internet_connection, org.qtum.wallet.R.string.please_check_your_network_settings, "Ok", BaseFragment.PopUpType.error);
        }
    }

    public boolean isAmountValid(String amount) {
        BigDecimal bigAmount = new BigDecimal(amount);
        BigDecimal pattern = new BigDecimal("2").pow(256);

        return bigAmount.compareTo(pattern) < 0;
    }

    @Override
    public void onPinSuccess() {
        Currency currency = getView().getCurrency();
        String from = getView().getFromAddress();
        String address = getView().getAddressInput();
        String amount = getView().getAmountInput();
        final double feeDouble = Double.valueOf(getView().getFeeInput());
        final String fee = validateFee(feeDouble);
        int gasLimit = getView().getGasLimitInput();
        int gasPrice = getView().getGasPriceInput();

        if (currency.getName().equals("Html " + getView().getStringValue(org.qtum.wallet.R.string.default_currency))) {
            if(isAmountValid(amount)) {
                getInteractor().sendTx(from, address, amount, fee, getView().getSendTransactionCallback());
            } else {
                getView().setAlertDialog(getView().getContext().getString(R.string.amount_too_big), getView().getContext().getString(R.string.ok), BaseFragment.PopUpType.error);
            }
        } else {
            for (final Token token : mTokenList) {
                String contractAddress = token.getContractAddress();
                if (contractAddress.equals(((CurrencyToken) currency).getToken().getContractAddress())) {
                    String resultAmount = amount;

                    if (token.getDecimalUnits() != null) {
                        resultAmount = String.valueOf((int) (Double.valueOf(amount) * Math.pow(10, token.getDecimalUnits())));
                        resultAmount = String.valueOf(Integer.valueOf(resultAmount));
                    }

                    if(!isAmountValid(resultAmount)){
                        getView().setAlertDialog(getView().getContext().getString(R.string.amount_too_big), getView().getContext().getString(R.string.ok), BaseFragment.PopUpType.error);
                    }

//                    TokenBalance tokenBalance = getView().getTokenBalance(contractAddress);
                    if (mTokenBalance == null || mTokenBalance.getItems() == null || mTokenBalance.getItems().size() == 0) {
                        getView().setAlertDialog(org.qtum.wallet.R.string.error, "", "Ok", BaseFragment.PopUpType.error);
                        return;
                    }

                    List<Balance> balances = mTokenBalance.getItems();
                    availableAddress = null;

                    if (!from.equals("")) {
                        for (Balance balance : balances) {
                            if (balance.getAddress().equals(from)) {
                                if (balance.getBalance().floatValue() >= Float.valueOf(resultAmount)) {
                                    availableAddress = balance.getAddress();
                                    break;
                                } else {
                                    break;
                                }
                            }
                        }
                    } else {
                        for (Balance balance : balances) {
                            if (balance.getBalance().floatValue() >= Float.valueOf(resultAmount)) {
                                availableAddress = balance.getAddress();
                                break;
                            }
                        }
                    }


                    if (!getView().isValidAvailableAddress(availableAddress)) {
                        return;
                    }

                    createAbiMethodParams(address, resultAmount, token, fee, gasPrice, gasLimit);

                    break;
                }
            }
        }

    }

    private void createAbiMethodParams(String address, String resultAmount, final Token token, final String fee, final int gasPrice, final int gasLimit) {
        getInteractor().createAbiMethodParamsObservable(address, resultAmount, "transfer")
                .flatMap(new Func1<String, Observable<CallSmartContractResponse>>() {
                    @Override
                    public Observable<CallSmartContractResponse> call(String s) {
                        params = s;
                        return getInteractor().callSmartContractObservable(token, s, availableAddress);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CallSmartContractResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().dismissProgressDialog();
                        getView().setAlertDialog(org.qtum.wallet.R.string.error, e.getMessage(), "Ok", BaseFragment.PopUpType.error);
                    }

                    @Override
                    public void onNext(CallSmartContractResponse response) {
                        if (!response.getExecutionResult().getExcepted().equals("None")) {
                            getView().setAlertDialog(org.qtum.wallet.R.string.error,
                                    response.getExecutionResult().getExcepted(), "Ok", BaseFragment.PopUpType.error);
                            return;
                        }
                        createTx(params, token.getContractAddress(), availableAddress, gasLimit, gasPrice, fee);
                    }
                });

    }

    private String validateFee(Double fee) {
        return getInteractor().getValidatedFee(fee);
    }

    private void createTx(final String abiParams, final String contractAddress, String senderAddress, final int gasLimit, final int gasPrice, final String fee) {
        getInteractor().getUnspentOutputs(senderAddress, new SendInteractorImpl.GetUnspentListCallBack() {
            @Override
            public void onSuccess(List<UnspentOutput> unspentOutputs) {
                String txHex = getInteractor().createTransactionHash(abiParams, contractAddress, unspentOutputs, gasLimit, gasPrice, fee);
                getInteractor().sendTx(txHex, getView().getSendTransactionCallback());
            }

            @Override
            public void onError(String error) {
                getView().dismissProgressDialog();
                getView().setAlertDialog(org.qtum.wallet.R.string.error, error, "Ok", BaseFragment.PopUpType.error);
            }
        });
    }

    private void loadBalance() {
        if (getView().getCurrency().getName().equals("Html " + getView().getStringValue(org.qtum.wallet.R.string.default_currency))) {
            AddressBalance addressBalance = RealmStorage.getInstance(mSendFragmentView.getContext()).getAddressBalance();
            if (addressBalance != null){
                getView().updateBalance(addressBalance.getFormattedBalance().stripTrailingZeros().toPlainString(), addressBalance.getFormattedUnconfirmedBalance().stripTrailingZeros().toPlainString(), null);
            } else {
                loadAndUpdateBalance();
            }
        } else {
            for (final Token token : mTokenList) {
                String contractAddress = token.getContractAddress();
                if (contractAddress.equals(((CurrencyToken) getView().getCurrency()).getToken().getContractAddress())) {
                    loadAndUpdateTokenBalance(token);
                }
            }
        }
    }

    private void loadAndUpdateBalance() {
        final List<String> addresses = new ArrayList<>();
        if (!"".equals(getView().getFromAddress())) {
            addresses.add(getView().getFromAddress());
        } else {
            addresses.addAll(mAddressInteractor.getAddresses());
        }
        mAddressInteractor.getAddressBalance(addresses)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AddressBalance>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AddressBalance addressBalance) {
                        getView().updateBalance(addressBalance.getFormattedBalance().stripTrailingZeros().toPlainString(), addressBalance.getFormattedUnconfirmedBalance().stripTrailingZeros().toPlainString(), null);
                    }
                });
    }

    private void loadAndUpdateTokenBalance(final Token token) {
        final List<String> addresses = new ArrayList<>();
        if (!"".equals(getView().getFromAddress())) {
            addresses.add(getView().getFromAddress());
        } else {
            addresses.addAll(mAddressInteractor.getAddresses());
        }
        mAddressInteractor.getTokenBalance(token, addresses)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TokenBalanceResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TokenBalanceResponse tokenBalanceResponse) {
                        mTokenBalance = tokenBalanceResponse;
                        if (tokenBalanceResponse.getItems() != null && tokenBalanceResponse.getItems().size() > 0) {
                            BigDecimal total = BigDecimal.ZERO;
                            for (Balance balance: tokenBalanceResponse.getItems()) {
                                if (addresses.contains(balance.getAddress())) {
                                    total = total.add(balance.getBalance());
                                }
                            }

                            String resultamount = total.divide(new BigDecimal("10").pow(token.getDecimalUnits())).toPlainString();
                            getView().updateBalance(resultamount, "", token.getSymbol());
                        } else {
                            getView().updateBalance("","", token.getSymbol());
                        }
                    }
                });
    }

    @Override
    public void updateNetworkSate(boolean networkConnectedFlag) {
        mNetworkConnectedFlag = networkConnectedFlag;
    }

    /**
     * Getter for unit tests
     */
    public double getMinFee() {
        return minFee;
    }

    /**
     * Getter for unit tests
     */
    public List<Token> getTokenList() {
        return mTokenList;
    }

    /**
     * Getter for unit tests
     */
    public String getAvailableAddress() {
        return availableAddress;
    }

    /**
     * Setter for unit tests
     */
    public void setTokenList(List<Token> tokenList) {
        this.mTokenList = tokenList;
    }

    /**
     * Setter for unit tests
     */
    public void setMinFee(double minFee) {
        this.minFee = minFee;
    }

    /**
     * Setter for unit tests
     */
    public void setMaxFee(double maxFee) {
        this.maxFee = maxFee;
    }
}