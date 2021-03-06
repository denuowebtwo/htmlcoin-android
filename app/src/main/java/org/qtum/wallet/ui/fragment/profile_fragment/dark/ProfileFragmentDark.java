package org.qtum.wallet.ui.fragment.profile_fragment.dark;

import org.qtum.wallet.R;
import org.qtum.wallet.ui.fragment.profile_fragment.DividerItemDecoration;
import org.qtum.wallet.ui.fragment.profile_fragment.light.PrefAdapterLight;
import org.qtum.wallet.ui.fragment.profile_fragment.ProfileFragment;

/**
 * Created by kirillvolkov on 05.07.17.
 */

public class ProfileFragmentDark extends ProfileFragment {

    private PrefAdapterLight adapter;

    @Override
    protected int getLayout() {
        return R.layout.lyt_profile_preference;
    }

    @Override
    public void initializeViews() {
        super.initializeViews();
        dividerItemDecoration = new DividerItemDecoration(getContext(), R.drawable.color_primary_divider, R.drawable.section_setting_divider, getPresenter().getSettingsData());
        showBottomNavView(R.color.color_custom_dark);
        adapter = new PrefAdapterLight(getPresenter().getSettingsData(), this, R.layout.lyt_profile_pref_list_item);
        prefList.addItemDecoration(dividerItemDecoration);
        prefList.setAdapter(adapter);
    }

    @Override
    public void resetText() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        //prefList.removeItemDecoration(dividerItemDecoration);
        super.onDestroyView();
    }
}
