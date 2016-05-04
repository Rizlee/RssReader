package evgen.by.test_task;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import evgen.by.test_task.database.DatabaseHelper;
import evgen.by.test_task.entity.RssItem;

public class StartAcrivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);

        if(isOnline()) {
            this.deleteDatabase("rss.db");
            new RetrieveRssItems().execute(this);
        }else{
            Toast.makeText(getApplicationContext(),"Потеряно интернет соединение, вывод предыдущей ленты",Toast.LENGTH_SHORT).show();
            runListActivity();
        }
    }


    public class RetrieveRssItems extends AsyncTask<Context,Integer,Void> {

        DatabaseHelper dbHelper;

        @Override
        protected Void doInBackground(Context... context) {

            ArrayList<RssItem> rssItems = new ArrayList<RssItem>();
            try {
                //open an URL connection make GET to the server and
                //take xml RSS data
                URL url = new URL("https://tech.onliner.by/feed");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();

                    //DocumentBuilderFactory, DocumentBuilder are used for
                    //xml parsing
                    ///////////////////////////////////////////////////////////////////////////////////
                    DocumentBuilderFactory dbf = DocumentBuilderFactory
                            .newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();

                    //using db (Document Builder) parse xml data and assign
                    //it to Element
                    Document document = db.parse(is);
                    Element element = document.getDocumentElement();

                    //take rss nodes to NodeList
                    NodeList nodeList = element.getElementsByTagName("item");



                    if (nodeList.getLength() > 0) {



                        for (int i = 0; i < nodeList.getLength(); i++) {

                            //take each entry (corresponds to <item></item> tags in
                            //xml data

                            Element entry = (Element) nodeList.item(i);
                            Element _titleE = (Element) entry.getElementsByTagName(
                                    "title").item(0);
                            Element _descriptionE = (Element) entry
                                    .getElementsByTagName("description").item(0);
                            Element _pubDateE = (Element) entry
                                    .getElementsByTagName("pubDate").item(0);
                            Element _linkE = (Element) entry.getElementsByTagName(
                                    "link").item(0);

                            String _title = _titleE.getFirstChild().getNodeValue();
                            String _description = _descriptionE.getFirstChild().getNodeValue();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                            String _date = sdf.format(new Date(_pubDateE.getFirstChild().getNodeValue()));
                            String _link = _linkE.getFirstChild().getNodeValue();

                            //create RssItemObject and add it to the ArrayList
                            RssItem rssItem = new RssItem(_title, _description,
                                    _date, _link);

                            rssItems.add(rssItem);


                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            dbHelper = OpenHelperManager.getHelper(context[0],DatabaseHelper.class);


            RuntimeExceptionDao<RssItem, Integer> itemDao = dbHelper.getItemRuntimeExceptionDao();

            setMaxProgressBar(rssItems.size());

            for (int i = 0;i<rssItems.size();i++) {
                itemDao.create(new RssItem(rssItems.get(i).getTitle(), rssItems.get(i).getDescription(),
                        rssItems.get(i).getDate(), rssItems.get(i).getLink()));

                publishProgress(i+1);
            }

            OpenHelperManager.releaseHelper();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            runListActivity();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }
    }

    private boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void  runListActivity(){
        Intent intent = new Intent(StartAcrivity.this, ListOfNewsActivity.class);
        startActivity(intent);
    }

    private void setMaxProgressBar(Integer maxValue){
        progressBar.setMax(maxValue);
    }
}
