package org.qtum.wallet.ui.fragment.watch_contract_fragment.dark;

import org.qtum.wallet.R;
import org.qtum.wallet.model.ContractTemplate;
import org.qtum.wallet.ui.fragment.watch_contract_fragment.OnTemplateClickListener;
import org.qtum.wallet.ui.fragment.watch_contract_fragment.TemplatesAdapter;
import org.qtum.wallet.ui.fragment.watch_contract_fragment.WatchContractFragment;
import org.qtum.wallet.utils.FontManager;

import java.util.List;

/**
 * Created by kirillvolkov on 25.07.17.
 */

public class WatchContractFragmentDark extends WatchContractFragment {

    @Override
    protected int getLayout() {
        return R.layout.fragment_watch_contract;
    }

    @Override
    public void initializeViews() {
        super.initializeViews();

        mTilContractName.setTypeface(FontManager.getInstance().getFont(getResources().getString(R.string.fontRegular)));
        mTilContractAddress.setTypeface(FontManager.getInstance().getFont(getResources().getString(R.string.fontRegular)));

        mEditTextContractName.setTypeface(FontManager.getInstance().getFont(getResources().getString(R.string.fontRegular)));
        mEditTextContractAddress.setTypeface(FontManager.getInstance().getFont(getResources().getString(R.string.fontRegular)));
    }

    @Override
    public void setUpTemplatesList(List<ContractTemplate> contractTemplateList, OnTemplateClickListener listener) {
        mRecyclerViewTemplates.setAdapter(new TemplatesAdapter(contractTemplateList, listener, R.layout.item_template_chips, R.color.accent_red_color));

    }

}
