package org.qtum.wallet.ui.fragment.transaction_fragment.transaction_detail_fragment;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.qtum.wallet.R;
import org.qtum.wallet.model.gson.history.TransactionInfo;
import org.qtum.wallet.utils.ClipboardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

/**
 * Created by kirillvolkov on 11.07.17.
 */

public class TransactionDetailHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.tv_single_string)
    TextView mTextViewAddress;
    @BindView(R.id.tv_value)
    TextView mTextViewValue;

    public TransactionDetailHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardUtils.copyToClipBoard(itemView.getContext(), mTextViewAddress.getText().toString(), new ClipboardUtils.CopyCallback() {
                    @Override
                    public void onCopyToClipBoard() {
                        Toast.makeText(itemView.getContext(),itemView.getContext().getString(R.string.copied),Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });
    }

    void bindTransactionDetail(TransactionInfo transactionInfo){
        mTextViewAddress.setText(transactionInfo.getAddress());
        if (transactionInfo.getValue() != null)
            mTextViewValue.setText(getSpannedBalance(String.format("%s HTML",transactionInfo.getValue().stripTrailingZeros().toPlainString())));
    }

    private SpannableString getSpannedBalance(String balance){

        SpannableString span =  new SpannableString(balance);
        if(balance.length() > 4) {
            span.setSpan(new RelativeSizeSpan(.6f), balance.length() - 4, balance.length(), 0);
        }
        return span;
    }
}