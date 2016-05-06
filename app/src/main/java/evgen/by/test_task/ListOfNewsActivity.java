package evgen.by.test_task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import evgen.by.test_task.adapter.DrawableArrayAdapter;
import evgen.by.test_task.adapter.ListArrayAdapter;
import evgen.by.test_task.database.DatabaseHelper;
import evgen.by.test_task.entity.RssItem;

public class ListOfNewsActivity extends AppCompatActivity {
    private String currentUrl;
    private boolean refreshPossibility = true;
    private DatabaseHelper dbHelper;
    private ListView lvOfNews;
    private Toolbar toolbar;
    private static ProgressBar progressBar;
    private ListArrayAdapter adapter;
    private String[] rssNames;
    private String[] rssUrl;
    private ListView drawerListView;
    private DrawableArrayAdapter adapterD;
    private ActionBarDrawerToggle drawerListener;
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_news_activity);

        currentUrl = getResources().getString(R.string.start_page);

        rssNames = getResources().getStringArray(R.array.rss_names);
        rssUrl = getResources().getStringArray(R.array.rss);


        progressBar = (ProgressBar) findViewById(R.id.progressBar3);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.news));

        downloadNewsFromDB();
        processingDrawer();
    }


    private void downloadNewsFromDB() {
        dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        RuntimeExceptionDao<RssItem, Integer> itemDao = dbHelper.getItemRuntimeExceptionDao();
        List<RssItem> items = itemDao.queryForAll();
        OpenHelperManager.releaseHelper();

        lvOfNews = (ListView) findViewById(R.id.listView);
        adapter = new ListArrayAdapter(this, parseNewsHeader(items), parseNewsDate(items), parseNewsImage(items));

        lvOfNews.setDivider(getResources().getDrawable(android.R.color.transparent));
        lvOfNews.setAdapter(adapter);

        lvOfNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (refreshPossibility) {
                    Intent intent = new Intent(ListOfNewsActivity.this, DescriptionNewsActivity.class);
                    intent.putExtra("data", Integer.toString(position));
                    startActivityForResult(intent, 1);
                }else{
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources()
                            .getString(R.string.user_must_wait), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void processingDrawer(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.left_drawer);
        drawerListener = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.news, R.string.news ){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerListView.setDivider(getResources().getDrawable(android.R.color.transparent));

        adapterD = new DrawableArrayAdapter(getApplicationContext(),rssNames);
        drawerListView.setAdapter(adapterD);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isOnline()) {
                    if (refreshPossibility) {
                        refreshPossibility = false;
                        currentUrl = rssUrl[position];
                        getApplicationContext().deleteDatabase(getResources().getString(R.string.db_name));
                        new Refresh().execute(getApplicationContext());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String[] parseNewsHeader(List<RssItem> rssItems) {
        String[] listView = new String[rssItems.size()];

        for (int i = 0; i < rssItems.size(); i++) {
            listView[i] = rssItems.get(i).getTitle();
        }
        return listView;
    }

    private String[] parseNewsDate(List<RssItem> rssItems) {
        String[] listView = new String[rssItems.size()];

        for (int i = 0; i < rssItems.size(); i++) {
            listView[i] = rssItems.get(i).getDate();
        }
        return listView;
    }

    private String[] parseNewsImage(List<RssItem> rssItems) {
        String[] listView = new String[rssItems.size()];

        for (int i = 0; i < rssItems.size(); i++) {
            listView[i] = rssItems.get(i).getImageSmall();
        }
        return listView;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ListOfNewsActivity.this);

        dialogBuilder.setTitle(getResources().getString(R.string.exit));

        dialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
            }
        });

        dialogBuilder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dialogBuilder.show();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public class Refresh extends AsyncTask<Context, Integer, Void> {

        @Override
        protected Void doInBackground(Context... context) {
            new RefreshDB().refresh(context[0], currentUrl, 2);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setProgressBar(0);
            downloadNewsFromDB();
            refreshPossibility = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_start_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.action_refresh:{
               if (isOnline()) {
                   if (refreshPossibility) {
                       refreshPossibility = false;
                       this.deleteDatabase(getResources().getString(R.string.db_name));
                       new Refresh().execute(this);
                   }
               } else {
                   Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
               }
               break;
           }
       }
        return false;
    }

    public static void setMaxProgressBar(Integer maxValue) {
        progressBar.setMax(maxValue);
    }

    public static void setProgressBar(Integer value){
        progressBar.setProgress(value);
    }
}

