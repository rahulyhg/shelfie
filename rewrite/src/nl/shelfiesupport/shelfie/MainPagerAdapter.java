package nl.shelfiesupport.shelfie;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 3;
    private final Context context;
    private List<Integer> fragIds = new ArrayList<Integer>();

    public MainPagerAdapter(FragmentManager fm, List<Integer> fragIds, Context context) {
        super(fm);
        this.fragIds = fragIds;
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        switch (fragIds.get(i)) {
            case R.layout.welcome:
                return new WelcomeFragment();
            case R.layout.edit_shelf:
                return new EditShelfFragment();
            case R.layout.grocery_list:
            default:
                return new GroceryListFragment();
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (fragIds.get(position)) {
            case R.layout.welcome:
                return context.getString(R.string.info);
            case R.layout.edit_shelf:
                return context.getString(R.string.edit_shelf_title);
            case R.layout.grocery_list:
            default:
                return context.getString(R.string.make_list);
        }
    }
}
