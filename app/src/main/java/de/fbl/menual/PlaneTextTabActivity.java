package de.fbl.menual;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.io.IOException;

import de.fbl.menual.Fragment.HistoryFragment;
import de.fbl.menual.Fragment.SuggestionsFragment;
import de.fbl.menual.adapters.ViewPageAdapter;
import de.fbl.menual.utils.Constants;

public class PlaneTextTabActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    SuggestionsFragment suggestionsFragment;
    HistoryFragment historyFragment;
    private int preselectedTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestions_tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        Bundle extras;
        try {
            extras = getIntent().getExtras();
            preselectedTab = (int) extras.get(Constants.HISTORY_PRESELECT);
        }catch (Exception ex){
            //Catch Exception
        }
        if(preselectedTab != 0){
            viewPager.setCurrentItem(1);
        }
        tabLayout = (TabLayout) findViewById(R.id.tablayout);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position,false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                PlaneTextTabActivity.this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager)
    {
      ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
      suggestionsFragment=new SuggestionsFragment();
      historyFragment=new HistoryFragment();
      adapter.addFragment(suggestionsFragment,"SUGGESTIONS");
      adapter.addFragment(historyFragment,"HISTORY");
      viewPager.setAdapter(adapter);
    }

}
