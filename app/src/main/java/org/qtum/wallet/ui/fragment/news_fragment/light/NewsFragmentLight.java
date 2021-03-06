package org.qtum.wallet.ui.fragment.news_fragment.light;

import org.qtum.wallet.R;
import org.qtum.wallet.model.news.News;
import org.qtum.wallet.ui.activity.main_activity.MainActivity;
import org.qtum.wallet.ui.fragment.news_fragment.NewsFragment;

import java.util.List;

/**
 * Created by kirillvolkov on 06.07.17.
 */

public class NewsFragmentLight extends NewsFragment {
    @Override
    protected int getLayout() {
        return org.qtum.wallet.R.layout.fragment_news_light;
    }

    @Override
    public void initializeViews() {
        ((MainActivity)getActivity()).showBottomNavigationView(org.qtum.wallet.R.color.title_color_light);
        super.initializeViews();
    }

    @Override
    public void updateNews(List<News> newses) {
        mNewsAdapter = new NewsAdapter(newses, R.layout.item_news_light);
        mRecyclerView.setAdapter(mNewsAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
