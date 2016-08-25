package cn.refactor.ultraindicator;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.refactor.ultraindicator.lib.UltraIndicatorView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
    }

    private void setupViews() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        assert viewPager != null;
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 6;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = View.inflate(container.getContext(), R.layout.layout_viewpager_item, null);
                TextView textTv = (TextView) view.findViewById(R.id.tv_text);
                textTv.setText(String.valueOf(position));
                container.addView(view);
                return view;
            }
        });

        UltraIndicatorView ultraIndicatorView = (UltraIndicatorView) findViewById(R.id.ultra_indicator_view);
        ultraIndicatorView.setupWithViewPager(viewPager);
    }
}
