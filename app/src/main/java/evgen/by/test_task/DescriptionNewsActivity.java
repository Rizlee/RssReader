package evgen.by.test_task;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.squareup.picasso.Picasso;

import java.util.List;

import evgen.by.test_task.database.DatabaseHelper;
import evgen.by.test_task.entity.RssItem;

public class DescriptionNewsActivity extends AppCompatActivity {
    TextView headerTextView;
    TextView descriptionTextView;
    TextView dateTextView;
    ImageView imageView;
    private Toolbar toolbar;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description_news_activity);

        int position = Integer.parseInt(getIntent().getStringExtra("data"));

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        headerTextView = (TextView)findViewById(R.id.headerTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        imageView = (ImageView) findViewById(R.id.imageView);

        dbHelper = OpenHelperManager.getHelper(this,DatabaseHelper.class);
        RuntimeExceptionDao<RssItem, Integer> itemDao = dbHelper.getItemRuntimeExceptionDao();
        List<RssItem> items = itemDao.queryForAll();
        OpenHelperManager.releaseHelper();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(items.get(position).getCategory());

        headerTextView.setText(items.get(position).getTitle());
        dateTextView.setText(items.get(position).getDate());
        Picasso.with(getApplicationContext())
                .load(items.get(position).getImageLarge())
                .error(R.drawable.no_image_icon).into(imageView);
        descriptionTextView.setText(items.get(position).getDescription());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
