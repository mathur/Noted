package com.rmathur.noted.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.rmathur.noted.R;
import com.rmathur.noted.data.model.DrawerItem;
import com.rmathur.noted.ui.adapters.drawer.MainDrawerAdapter;
import com.rmathur.noted.ui.fragments.main.MainFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private ActionBarDrawerToggle drawerToggle;
    private List<DrawerItem> items;

    private int currentDrawerItem;

    @InjectView(R.id.main_drawer)       DrawerLayout drawerLayout;
    @InjectView(R.id.main_drawer_list)  ListView drawerList;
    @InjectView(R.id.main_toolbar)      Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        items = new ArrayList<>();

        items.add(new DrawerItem(R.drawable.record_icon, "Record"));

        MainDrawerAdapter adapter = new MainDrawerAdapter(this, items);
        drawerList.setAdapter(adapter);
        setSupportActionBar(toolbar);

        // If the fragment changes, currentDrawerItem changes
        currentDrawerItem = 0;
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, MainFragment.newInstance())
                .commit();
        setTitle("Record");
    }

    @OnItemClick(R.id.main_drawer_list)
    void drawerItemClick(int position) {
        Log.d(TAG, "POSITION: " + position);
        DrawerItem item = items.get(position);
        if(position != currentDrawerItem) {
            if (item.getLabel().equals("Record"))
                switchToFragment(MainFragment.newInstance());
            setTitle(item.getLabel());
            currentDrawerItem = position;
        }
        drawerLayout.closeDrawer(Gravity.START);
    }

    private void switchToFragment(Fragment fragment) {
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return drawerToggle.onOptionsItemSelected(item) ||
               super.onOptionsItemSelected(item);
    }
}
