package org.qtum.wallet.ui.fragment.addresses_fragment.dark;

import android.support.v4.content.ContextCompat;
import android.view.View;
import org.qtum.wallet.R;
import org.qtum.wallet.datastorage.KeyStorage;
import org.qtum.wallet.ui.fragment.addresses_fragment.AddressHolder;
import org.qtum.wallet.ui.fragment.addresses_fragment.OnAddressClickListener;

/**
 * Created by kirillvolkov on 06.07.17.
 */

public class AddressHolderDark extends AddressHolder {

    AddressHolderDark(View itemView, OnAddressClickListener listener) {
        super(itemView, listener);

        defaultTextColor = ContextCompat.getColor(mTextViewAddress.getContext(), R.color.white);
        selectedTextColor = ContextCompat.getColor(mTextViewAddress.getContext(), R.color.white);
    }

   public void bindAddress(String address, int position) {
        if (position == KeyStorage.getInstance().getCurrentKeyPosition()) {
            mImageViewCheckIndicator.setVisibility(View.VISIBLE);
            mTextViewAddress.setTextColor(selectedTextColor);
            mLinearLayoutAddress.setBackgroundColor(ContextCompat.getColor(mLinearLayoutAddress.getContext(), R.color.primary_text_color));
        } else {
            mImageViewCheckIndicator.setVisibility(View.GONE);
            mTextViewAddress.setTextColor(defaultTextColor);
            mLinearLayoutAddress.setBackgroundColor(ContextCompat.getColor(mLinearLayoutAddress.getContext(),android.R.color.transparent));
        }
        mTextViewAddress.setText(address);
    }
}
