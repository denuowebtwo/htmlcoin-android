package org.qtum.wallet.ui.fragment.send_fragment.dark;

import org.qtum.wallet.ui.fragment.send_fragment.SendFragment;
import org.qtum.wallet.utils.FontManager;
import org.qtum.wallet.R;

/**
 * Created by kirillvolkov on 06.07.17.
 */

public class SendFragmentDark extends SendFragment {

    @Override
    protected int getLayout() {
        return org.qtum.wallet.R.layout.fragment_send;
    }

    @Override
    public void initializeViews() {
        super.initializeViews();

        showBottomNavView(R.color.color_custom_dark);
        mTextInputEditTextAddress.setTypeface(FontManager.getInstance().getFont(getString(org.qtum.wallet.R.string.fontRegular)));
        mTextInputEditTextAmount.setTypeface(FontManager.getInstance().getFont(getString(org.qtum.wallet.R.string.fontRegular)));
        mTextInputEditTextFee.setTypeface(FontManager.getInstance().getFont(getString(org.qtum.wallet.R.string.fontRegular)));
        tilAdress.setTypeface(FontManager.getInstance().getFont(getString(org.qtum.wallet.R.string.fontRegular)));
        tilAmount.setTypeface(FontManager.getInstance().getFont(getString(org.qtum.wallet.R.string.fontRegular)));
        tilFee.setTypeface(FontManager.getInstance().getFont(getString(org.qtum.wallet.R.string.fontRegular)));
    }

}
