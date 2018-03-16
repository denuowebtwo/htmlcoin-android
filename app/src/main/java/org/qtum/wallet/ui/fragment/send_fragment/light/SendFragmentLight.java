package org.qtum.wallet.ui.fragment.send_fragment.light;

import org.qtum.wallet.R;
import org.qtum.wallet.ui.activity.main_activity.MainActivity;
import org.qtum.wallet.ui.fragment.send_fragment.SendFragment;
import org.qtum.wallet.utils.FontManager;

/**
 * Created by kirillvolkov on 06.07.17.
 */

public class SendFragmentLight extends SendFragment {

    @Override
    protected int getLayout() {
        return R.layout.fragment_send_light;
    }

    @Override
    public void initializeViews() {
        super.initializeViews();

        ((MainActivity) getActivity()).showBottomNavigationView(R.color.title_color_light);

        mTextInputEditTextAddress.setTypeface(FontManager.getInstance().getFont(getString(R.string.fontBold)));
        mTextInputEditTextAmount.setTypeface(FontManager.getInstance().getFont(getString(R.string.fontBold)));
        mTextInputEditTextFee.setTypeface(FontManager.getInstance().getFont(getString(R.string.fontBold)));
        tilAdress.setTypeface(FontManager.getInstance().getFont(getString(R.string.fontRegular)));
        tilAmount.setTypeface(FontManager.getInstance().getFont(getString(R.string.fontRegular)));
        tilFee.setTypeface(FontManager.getInstance().getFont(getString(R.string.fontRegular)));

    }

}
