package org.qtum.wallet.ui.fragment.watch_contract_fragment.light;

import org.qtum.wallet.model.ContractTemplate;
import org.qtum.wallet.ui.fragment.watch_contract_fragment.OnTemplateClickListener;
import org.qtum.wallet.ui.fragment.watch_contract_fragment.TemplatesAdapter;
import org.qtum.wallet.ui.fragment.watch_contract_fragment.WatchContractFragment;
import org.qtum.wallet.utils.FontManager;

import java.util.List;

/**
 * Created by kirillvolkov on 25.07.17.
 */

public class WatchContractFragmentLight extends WatchContractFragment {

    @Override
    protected int getLayout() {
        return org.qtum.wallet.R.layout.fragment_watch_contract_light;
    }

    @Override
    public void initializeViews() {
        super.initializeViews();

        mTilContractName.setTypeface(FontManager.getInstance().getFont(getResources().getString(org.qtum.wallet.R.string.fontRegular)));
        mTilContractAddress.setTypeface(FontManager.getInstance().getFont(getResources().getString(org.qtum.wallet.R.string.fontRegular)));

        mEditTextContractName.setTypeface(FontManager.getInstance().getFont(getResources().getString(org.qtum.wallet.R.string.font_bottom_navigation)));
        mEditTextContractAddress.setTypeface(FontManager.getInstance().getFont(getResources().getString(org.qtum.wallet.R.string.font_bottom_navigation)));
    }

    @Override
    public void setUpTemplatesList(List<ContractTemplate> contractTemplateList, OnTemplateClickListener listener) {
        mRecyclerViewTemplates.setAdapter(new TemplatesAdapter(contractTemplateList, listener, org.qtum.wallet.R.layout.item_template_chips_light, org.qtum.wallet.R.drawable.chip_selected_corner_background));
    }

}
