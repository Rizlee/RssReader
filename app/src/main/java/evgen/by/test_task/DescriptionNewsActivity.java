package evgen.by.test_task;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import evgen.by.test_task.database.DatabaseHelper;
import evgen.by.test_task.entity.RssItem;

public class DescriptionNewsActivity extends AppCompatActivity {
    TextView headerTextView;
    TextView descriptionTextView;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description_news_acrtivity);
        headerTextView = (TextView)findViewById(R.id.headerTextView);
        descriptionTextView = (TextView)findViewById(R.id.descriptionTextView);

        dbHelper = OpenHelperManager.getHelper(this,DatabaseHelper.class);
        RuntimeExceptionDao<RssItem, Integer> itemDao = dbHelper.getItemRuntimeExceptionDao();
        List<RssItem> items = itemDao.queryForAll();
        OpenHelperManager.releaseHelper();

        headerTextView.setText(items.get(Integer.parseInt(getIntent().getStringExtra("data"))).getTitle());
        descriptionTextView.setText(items.get(Integer.parseInt(getIntent().getStringExtra("data"))).getDescription());

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
