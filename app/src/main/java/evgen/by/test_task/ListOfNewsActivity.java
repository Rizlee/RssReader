package evgen.by.test_task;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import evgen.by.test_task.database.DatabaseHelper;
import evgen.by.test_task.entity.RssItem;

public class ListOfNewsActivity extends AppCompatActivity {

    private boolean refreshPossibility = true;
    private DatabaseHelper dbHelper;
    private ListView lvOfNews;
    private Toolbar toolbar;
    private static ProgressBar progressBar;
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_news_activity);

        progressBar = (ProgressBar) findViewById(R.id.progressBar3);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.news));

        downloadNewsFromDB();
    }

    private void downloadNewsFromDB() {
        dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        RuntimeExceptionDao<RssItem, Integer> itemDao = dbHelper.getItemRuntimeExceptionDao();
        List<RssItem> items = itemDao.queryForAll();
        OpenHelperManager.releaseHelper();

        lvOfNews = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter(this, parseNewsHeader(items), parseNewsDate(items), parseNewsImage(items));

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

        dialogBuilder.setTitle("Выйти?");

        dialogBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
            }
        });

        dialogBuilder.setNegativeButton("Нет, я останусь", new DialogInterface.OnClickListener() {
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
            new RefreshDB().refresh(context[0],2);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
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

