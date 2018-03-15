package org.qtum.wallet.ui.fragment.wallet_fragment.dark;

import android.support.design.widget.AppBarLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.qtum.wallet.R;
import org.qtum.wallet.model.gson.history.History;
import org.qtum.wallet.ui.fragment.wallet_fragment.WalletFragment;
import org.qtum.wallet.ui.wave_visualizer.WaveHelper;
import org.qtum.wallet.ui.wave_visualizer.WaveView;
import org.qtum.wallet.utils.LogUtils;
import org.qtum.wallet.utils.ResizeWidthAnimation;

import java.util.List;
import butterknife.BindView;

/**
 * Created by kirillvolkov on 05.07.17.
 */

public class WalletFragmentDark extends WalletFragment {

    @BindView(R.id.app_bar_placeholder) View appbarPlaceholder;
    @BindView(R.id.not_confirmed_balance_view) View notConfirmedBalancePlaceholder;
    @BindView(R.id.tv_placeholder_balance_value) TextView placeHolderBalance;
    @BindView(R.id.tv_placeholder_not_confirmed_balance_value) TextView placeHolderBalanceNotConfirmed;
    @BindView(R.id.balance_view) FrameLayout waveContainer;

    @BindView(R.id.iv_choose_address) ImageView mIvChooseAddr;

    float headerPAdding = 0;
    float prevPercents = 1;

    @BindView(R.id.page_indicator)
    public View pagerIndicator;

    @Override
    protected int getLayout() {
        return R.layout.fragment_wallet;
    }

    final DisplayMetrics dm = new DisplayMetrics();

    boolean expanded = false;


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void initializeViews() {
        super.initializeViews();

        showBottomNavView(R.color.color_custom_dark);

        headerPAdding = convertDpToPixel(16,getContext());

        uncomfirmedBalanceValue.setVisibility(View.GONE);
        uncomfirmedBalanceTitle.setVisibility(View.GONE);

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mAppBarLayout != null) {
                    if (mSwipeRefreshLayout != null) {
                        if (!mSwipeRefreshLayout.isActivated()) {
                            if (verticalOffset == 0) {
                                mSwipeRefreshLayout.setEnabled(true);
                            } else {
                                mSwipeRefreshLayout.setEnabled(false);
                            }
                        }
                    }

                    percents = (((getTotalRange() - Math.abs(verticalOffset)) * 1.0f) / getTotalRange());

                    float testPercents = percents - (1 - percents);
                    float testP2 = (percents >= .8f) ? 0 : (1 - percents) - percents;

                    balanceView.setAlpha(testPercents);
                    mButtonQrCode.setAlpha(testPercents);
                    mTextViewWalletName.setAlpha(testPercents);
                    mIvChooseAddr.setAlpha(testPercents);
                    appbarPlaceholder.setAlpha(testP2);
                }
            }
        });

        appbarPlaceholder.setVisibility(View.VISIBLE);

    }

    public int getTotalRange() {
        return mAppBarLayout.getTotalScrollRange();
    }


    @Override
    public void updateHistory(List<History> historyList) {
        super.updateHistory(new TransactionAdapterDark(historyList,getAdapterListener()));
    }

    @Override
    public void updateBalance(String balance, String unconfirmedBalance) {
        try {
            balanceValue.setText(String.format("%s HTML",balance));
            placeHolderBalance.setText(String.format("%s HTML",balance));
            if(unconfirmedBalance != null) {
                notConfirmedBalancePlaceholder.setVisibility(View.VISIBLE);
                uncomfirmedBalanceValue.setVisibility(View.VISIBLE);
                uncomfirmedBalanceTitle.setVisibility(View.VISIBLE);
                uncomfirmedBalanceValue.setText(String.format("%s HTML", unconfirmedBalance));
                placeHolderBalanceNotConfirmed.setText(String.format("%s HTML", unconfirmedBalance));
            } else {
                notConfirmedBalancePlaceholder.setVisibility(View.GONE);
                uncomfirmedBalanceValue.setVisibility(View.GONE);
                uncomfirmedBalanceTitle.setVisibility(View.GONE);
            }
        } catch (NullPointerException e){
            LogUtils.debug("WalletFragmentDark", "updateBalance: " + e.getMessage());
        }
    }
}
