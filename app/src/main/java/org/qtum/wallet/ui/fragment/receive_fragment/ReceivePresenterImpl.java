package org.qtum.wallet.ui.fragment.receive_fragment;

import org.qtum.wallet.model.gson.AddressBalance;
import org.qtum.wallet.ui.base.AddressInteractor;
import org.qtum.wallet.ui.base.AddressInteractorImpl;
import org.qtum.wallet.ui.base.base_fragment.BaseFragmentPresenterImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReceivePresenterImpl extends BaseFragmentPresenterImpl implements ReceivePresenter {

    private ReceiveView mReceiveView;
    private ReceiveInteractor mReceiveInteractor;
    private String mAmount = "0.0";
    private String mTokenAddress;
    private Subscription subscription;

    private AddressInteractor mAddressInteractor;

    public ReceivePresenterImpl(ReceiveView view, ReceiveInteractor interactor) {
        mReceiveView = view;
        mReceiveInteractor = interactor;

        mAddressInteractor = new AddressInteractorImpl(view.getContext());
    }

    @Override
    public void initializeViews() {
        super.initializeViews();
        getView().setUpAddress(getInteractor().getCurrentReceiveAddress());
        changeAmount("");
    }

    @Override
    public void onBalanceChanged(BigDecimal unconfirmedBalance, BigDecimal balance) {
        String balanceString = balance.toString();
        if (balanceString != null) {
            String unconfirmedBalanceString = unconfirmedBalance.toString();
            if (getView().isUnconfirmedBalanceValid(unconfirmedBalanceString)) {
                getView().updateBalance(getInteractor().formatBalance(balanceString), getInteractor().formatUnconfirmedBalance(unconfirmedBalance));
            } else {
                getView().updateBalance(getInteractor().formatBalance(balanceString));
            }
        }
    }

    @Override
    public ReceiveView getView() {
        return mReceiveView;
    }

    private ReceiveInteractor getInteractor() {
        return mReceiveInteractor;
    }

    //qtum:qQMKtb8fs82ZTQwB1PWB8LDffoTKrNkK4z?amount=1.00000000&label=1234&message=mne&tokenAddress=....
    //qtum:QYxULw7ppJex4uhHoDQbW4jRjYP1vS2CEc?amount=2&label=1234&message=mne&tokenAddress=1e6abee8af69f098aa354802164c79333623b252

    @Override
    public void changeAmount(String s) {
        mAmount = s;
        String result = buildFullQrCodeData(getInteractor().getCurrentReceiveAddress(), mAmount, mTokenAddress);
        getView().showSpinner();
        subscription = getView().imageEncodeObservable(result);
    }

    @Override
    public void setTokenAddress(String address) {
        mTokenAddress = address;
        String result = buildFullQrCodeData(getInteractor().getCurrentReceiveAddress(), mAmount, mTokenAddress);
        getView().showSpinner();
        subscription = getView().imageEncodeObservable(result);
    }

    private String getFormattedReceiveAddr(String addr) {
        return getInteractor().formatReceiveAddress(addr);
    }

    private String getFormattedAmount(String amount) {
        if (getView().isAmountValid(amount)) {
            return getInteractor().formatAmount(amount);
        } else {
            return "";
        }
    }

    private String getadditionalInfo() {
        return "label=HTML Mobile Wallet&message=Payment Request";
    }

    private String getFormattedTokenAddr(String addr) {
        if (getView().isTokenAddressValid(addr)) {
            return getInteractor().formatTokenAddress(addr);
        } else {
            return "";
        }
    }

    private String buildFullQrCodeData(String receiveAddr, String amount, String mTokenAddress) {
        return getFormattedReceiveAddr(receiveAddr) + getFormattedAmount(amount) + getadditionalInfo() + getFormattedTokenAddr(mTokenAddress);
    }

    public void loadAndUpdateBalance() {
        final List<String> addresses = new ArrayList<>();
        addresses.add(getInteractor().getCurrentReceiveAddress());

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
                        getView().updateBalance(addressBalance.getFormattedBalance().stripTrailingZeros().toPlainString(), addressBalance.getFormattedUnconfirmedBalance().stripTrailingZeros().toPlainString());
                    }
                });
    }

    @Override
    public void changeAddress() {
        String result = buildFullQrCodeData(getInteractor().getCurrentReceiveAddress(), mAmount, mTokenAddress);
        getView().showSpinner();
        subscription = getView().imageEncodeObservable(result);
        getView().setUpAddress(getInteractor().getCurrentReceiveAddress());
    }

    private int moduleWidth = 0;
    private boolean withCrossQR = false;

    private int patternWidth = 0;
    private int topOffsetHeight = 0;
    private int leftOffcetWidth = 0;
    private boolean firstInputY, firstInputX = false;

    @Override
    public void calcModuleWidth(boolean isDataPixel, int x, int y) {
        if (isDataPixel) {
            if (!firstInputY) {
                topOffsetHeight = y;
                firstInputY = true;
            }
            if (!firstInputX) {
                leftOffcetWidth = x;
                firstInputX = true;
            }
            patternWidth++;
        } else {
            if (patternWidth > 0) {
                moduleWidth = (moduleWidth > patternWidth) ? patternWidth : moduleWidth;
                patternWidth = 0;
            }
        }
    }

    @Override
    public void setModuleWidth(int moduleWidth) {
        this.moduleWidth = moduleWidth;
    }

    @Override
    public boolean getWithCrossQr() {
        return withCrossQR;
    }

    @Override
    public int getTopOffsetHeight() {
        return topOffsetHeight;
    }

    @Override
    public int getModuleWidth() {
        return moduleWidth;
    }

    @Override
    public CharSequence getCurrentReceiveAddress() {
        return getInteractor().getCurrentReceiveAddress();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}