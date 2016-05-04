package evgen.by.test_task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import evgen.by.test_task.database.DatabaseHelper;
import evgen.by.test_task.entity.RssItem;

public class ListOfNewsActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    ListView lvOfNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_news_activity);

        downloadNewsFromDB();
    }

    private void downloadNewsFromDB(){
        dbHelper = OpenHelperManager.getHelper(this,DatabaseHelper.class);
        RuntimeExceptionDao<RssItem, Integer> itemDao = dbHelper.getItemRuntimeExceptionDao();
        List<RssItem> items = itemDao.queryForAll();
        OpenHelperManager.releaseHelper();

        lvOfNews = (ListView) findViewById(R.id.listView);
        ArrayAdapter adapter = new ArrayAdapter(this, parseNewsHeader(items),parseNewsDate(items));

        lvOfNews.setDivider(getResources().getDrawable(android.R.color.transparent));
        lvOfNews.setAdapter(adapter);

        lvOfNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(ListOfNewsActivity.this,DescriptionNewsActivity.class);
                intent.putExtra("data",Integer.toString(position));
                startActivityForResult(intent, 1);
            }
        });
    }

    private String[] parseNewsHeader(List<RssItem> rssItems){
        String[] listView = new String[rssItems.size()];

        for (int i = 0;i<rssItems.size();i++){
            listView[i]=rssItems.get(i).getTitle();
        }
        return listView;
    }

    private String[] parseNewsDate(List<RssItem> rssItems){
        String[] listView = new String[rssItems.size()];

        for (int i = 0;i<rssItems.size();i++){
            listView[i]=rssItems.get(i).getDate();
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
}
