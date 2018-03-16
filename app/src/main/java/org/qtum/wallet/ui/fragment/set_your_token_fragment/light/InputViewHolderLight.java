package org.qtum.wallet.ui.fragment.set_your_token_fragment.light;

import android.view.View;

import org.qtum.wallet.R;
import org.qtum.wallet.ui.fragment.set_your_token_fragment.InputViewHolder;
import org.qtum.wallet.ui.fragment.set_your_token_fragment.OnValidateParamsListener;
import org.qtum.wallet.utils.FontManager;

/**
 * Created by kirillvolkov on 25.07.17.
 */

public class InputViewHolderLight extends InputViewHolder {

    public InputViewHolderLight(View itemView, OnValidateParamsListener listener) {
        super(itemView, listener);
        tilParam.setTypeface(FontManager.getInstance().getFont(tilParam.getContext().getString(R.string.fontRegular)));
        etParam.setTypeface(FontManager.getInstance().getFont(etParam.getContext().getString(R.string.fontBold)));
    }
}
