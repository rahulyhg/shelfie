package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import net.peterkuterna.android.apps.swipeytabs.SwipeyTabs;
import nl.shelfiesupport.shelfie.faye.WebSocketService;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements
        Responder, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener,
        ShelfieFragmentListener {

    public static final String GROCERY_LIST_ACTION = "GROC";
    private Receiver grocerySyncReceiver;

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int connectStatus = intent.getIntExtra("CONNECTED", -1);
            if(connectStatus > -1) {
                onFayeStatusChanged(connectStatus);
                return;
            }

            try {
                JSONObject message = new JSONObject(intent.getStringExtra("MSG"));
                Toast.makeText(MainActivity.this, "MSG :" + message, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Log.e(Tag.SHELFIE, "Invalid JSON received by Receiver");
            }
        }
    }

    private void onFayeStatusChanged(int connectStatus) {
        ImageButton syncButt = (ImageButton) findViewById(R.id.sync_groceries);

        if(connectStatus == 1) {
            Inventory.connectedToFaye = true;
            if(syncButt == null) {

            } else {
                syncButt.setImageDrawable(getResources().getDrawable(R.drawable.grocery_sync_up));
            }
        } else {
            Inventory.connectedToFaye = false;
            if(syncButt == null) {

            } else {
                syncButt.setImageDrawable(getResources().getDrawable(R.drawable.grocery_sync));
            }
            Toast.makeText(this, getString(R.string.no_faye_connect), Toast.LENGTH_LONG).show();
        }
    }

    private MainPagerAdapter pageAdapter;
    private static final List<Integer> fragIds = new ArrayList<Integer>();
    static {
        fragIds.add(R.layout.welcome);
        fragIds.add(R.layout.edit_shelf);
        fragIds.add(R.layout.grocery_list);
    }
    private ViewPager viewPager;
    protected ArrayAdapter<String> currentShelfAdapter;
    private Shelf shelf;
    private GroceryList groceryList;
    private boolean isWide = false;
    private Spinner currentShelfSpinner;
    private ListView currentShelfListView;

    protected void initShelfPicker() {
        currentShelfSpinner = (Spinner) findViewById(R.id.currentShelfSpinner);
        currentShelfListView = (ListView) findViewById(R.id.currentShelfListView);
        List<String> shelfNames = Inventory.getShelfNames(this);

        if(currentShelfSpinner != null) {
            currentShelfAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, shelfNames);

            currentShelfSpinner.setAdapter(currentShelfAdapter);
            currentShelfAdapter.notifyDataSetChanged();
            currentShelfSpinner.setSelection(Inventory.getSelectedShelfIndex(), false);
            currentShelfSpinner.setOnItemSelectedListener(this);
        } else if(currentShelfListView != null) {
            currentShelfAdapter = new ArrayAdapter<String>(this, R.layout.list_select_row, shelfNames);

            currentShelfListView.setAdapter(currentShelfAdapter);
            currentShelfAdapter.notifyDataSetChanged();
            currentShelfListView.setItemChecked(Inventory.getSelectedShelfIndex(), true);
            currentShelfListView.setOnItemClickListener(this);

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(isWide) {
            inflater.inflate(R.menu.main_menu_wide, menu);
        } else {
            inflater.inflate(R.menu.main_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void deleteCurrentShelf(View view) {
        final Context context = this;

        if(Inventory.getShelfNames(this).size() < 2) {
            Toast.makeText(context,
                    context.getString(R.string.uneedaleastoneshelf), Toast.LENGTH_LONG).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_shelf) + ": " + Shelf.getInstance(context).getName())
                    .setMessage(getString(R.string.delete_shelf_confirm))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Inventory.deleteCurrentShelf(context);
                            recreate();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
    }

    public void deleteGroceries(View view) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_groceries))
                .setMessage(getString(R.string.delete_groceries_confirm))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        groceryList.clear();
                        reinitGroceryListFragment();
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shelf = Shelf.getInstance(this);

        if(getResources().getBoolean(R.bool.isWide)) {
            initWideLayout();
        } else {
            initViewPager();
        }
        if(Inventory.connectedToFaye) { onFayeStatusChanged(1); }
    }
    @Override
    public void onResume() {
        super.onResume();
        shelf = Shelf.getInstance(this);
        groceryList = GroceryList.getInstance(this);
        initShelfPicker();
    }

    @Override
    public void onPause() {
        super.onPause();
        shelf = Shelf.getInstance(this);
        groceryList = GroceryList.getInstance(this);
        if(shelf != null) { shelf.save(this); }
        if(groceryList != null) { groceryList.save(this); }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.add_something:
                if(isWide) {
                    showAddShelfPrompt();
                } else {
                    switch (fragIds.get(viewPager.getCurrentItem())) {
                        case R.layout.grocery_list:
                            showAddGroceryPrompt(null);
                            break;
                        case R.layout.edit_shelf:
                        default:
                            showAddShelfPrompt();
                    }
                }
                break;
            case R.id.main_menu_delete:
                if(isWide) {
                    deleteCurrentShelf(null);
                } else {
                    switch (fragIds.get(viewPager.getCurrentItem())) {
                        case R.layout.grocery_list:
                            deleteGroceries(null);
                            break;
                        case R.layout.edit_shelf:
                        default:
                            deleteCurrentShelf(null);
                    }
                }
                break;
            case R.id.sync_groceries:
                toggleGrocerySync(null);
                break;
            case R.id.email_menu_button:
                emailGroceries(null);
                break;

            case R.id.share_menu_button:
                shareShelf(null);
                break;

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleGrocerySync(View view) {

        Intent intent = new Intent(this, WebSocketService.class);
        if(Inventory.connectedToFaye) {
            intent.putExtra("disconnect", true);
            startService(intent);
        } else {
            intent.putExtra("channel", "/testing");
            startService(intent);
        }
    }

    public void shareShelf(View view) {
        if (shelf == null) {
            shelf = Shelf.getInstance(this);
        }
        Toast.makeText(this, getString(R.string.exporting), Toast.LENGTH_LONG).show();
        new ExportTask(this, shelf).execute("");
    }

    public void emailGroceries(View view) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("message/rfc822");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_groceries_subject));
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, groceryList.asText());
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_groceries_title)));
    }

    public void showAddGroceryPrompt(View view) {
        final EditText input = new EditText(this);
        input.setHint(getString(R.string.grocery_hint));
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_grocery))
                .setView(input)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().trim().length() == 0) {
                            showAddGroceryPrompt(null);
                        } else {
                            ShelfItem grocery = new ShelfItem(input.getText().toString(), 1);
                            if (groceryList != null) {
                                groceryList.addGrocery(grocery, 1);
                                reinitGroceryListFragment();
                            }

                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    public void addShelf(View view) {
        showAddShelfPrompt();
    }

    private void showAddShelfPrompt() {
        final EditText input = new EditText(this);
        final Context self = this;
        input.setHint(getString(R.string.shelf_name));
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_shelf))
                .setView(input)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().trim().length() == 0) {
                            showAddShelfPrompt();
                        } else {
                            String newShelfStr = input.getText().toString().trim();
                            Inventory.createNewShelf(self, newShelfStr);
                            Inventory.setSelectedShelfByIndex(self, Inventory.getShelfNames(self).size() - 1);

                            recreate();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();

    }



    private void initViewPager() {
        isWide = false;
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) { actionBar.show(); }

        setContentView(R.layout.main);


        viewPager = (ViewPager) findViewById(R.id.mainPager);
        final SwipeyTabs swipeyTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);
        pageAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragIds, this, viewPager);
        viewPager.setAdapter(pageAdapter);
        swipeyTabs.setAdapter(pageAdapter);
        viewPager.setOnPageChangeListener(swipeyTabs);
        viewPager.setCurrentItem(1);
        pageAdapter.notifyDataSetChanged();

    }

    private void initWideLayout() {

        isWide = true;
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) { actionBar.hide(); }

        setContentView(R.layout.main_wide);
    }
    private void selectShelf(int position) {

        if(Inventory.getSelectedShelfIndex() != position) {
            if(currentShelfSpinner != null) {
                currentShelfSpinner.setSelection(position);
            } else if(currentShelfListView != null) {
                currentShelfListView.setSelection(position);
            }
            Inventory.setSelectedShelfByIndex(this, position);
            reinitFragments();
        }
    }
    protected void parseAndShareExport(String jsonStr) {
        try {
            String id = ((JSONObject) new JSONObject(jsonStr).getJSONArray("added").get(0)).getString("_id");
            shelf.setExportId(id);
            shelf.save(this);
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_subject) + ": " +
                    shelf.getName() + " " +
                    getString(R.string.via) + ": http://getshelfie.herokuapp.com/" + id);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
        } catch (JSONException e) {
            Log.w(Tag.SHELFIE, "failed to parse response: " + jsonStr);
            Toast.makeText(this, getString(R.string.export_failed), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void respondWith(String response) {
        if(response != null) {
            parseAndShareExport(response);
        } else {
            Toast.makeText(this, getString(R.string.export_failed), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectShelf(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        selectShelf(position);
    }

    @Override
    public void reinitGroceryListFragment() {
        GroceryListFragment fragment;
        if(isWide) {
            fragment = (GroceryListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_grocery_list);
        } else {
            ViewGroup container = (ViewGroup) findViewById(R.id.mainPager);
            fragment = (GroceryListFragment) pageAdapter.instantiateItem(container, fragIds.indexOf(R.layout.grocery_list));
        }
        fragment.reinit();
    }

    @Override
    public void reinitEditShelfFragment() {
        EditShelfFragment fragment;
        if(isWide) {
            fragment = (EditShelfFragment) getSupportFragmentManager().findFragmentById(R.id.frag_edit_shelf);
        } else {
            ViewGroup container = (ViewGroup) findViewById(R.id.mainPager);
            fragment = (EditShelfFragment) pageAdapter.instantiateItem(container, fragIds.indexOf(R.layout.edit_shelf));
        }
        fragment.reinit();
    }

    @Override
    public void reinitFragments() {
        reinitEditShelfFragment();
        reinitGroceryListFragment();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(grocerySyncReceiver);
        super.onStop();
    }

    @Override
    protected void onStart() {
        grocerySyncReceiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.GROCERY_LIST_ACTION);
        registerReceiver(grocerySyncReceiver, intentFilter);
        super.onStart();
    }
}
