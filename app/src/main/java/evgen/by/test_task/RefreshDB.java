package evgen.by.test_task;

import android.content.Context;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import evgen.by.test_task.database.DatabaseHelper;
import evgen.by.test_task.entity.RssItem;

/**
 * Created by evgen on 05.05.2016.
 */
public class RefreshDB {
    public void refresh(Context context,String urlString, int mode){
        final Pattern IMG_PATTERN = Pattern.compile("(?<=(img src=\"))[^\"]*(?=\")");
        final Pattern DESCRIPTION_PATTERN = Pattern.compile("(?<=(/>)).*(?=<br)");

        DatabaseHelper dbHelper;

        Matcher matcher;

        ArrayList<RssItem> rssItems = new ArrayList<RssItem>();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();

                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                Document document = db.parse(is);
                Element element = document.getDocumentElement();

                NodeList nodeList = element.getElementsByTagName("item");

                if (nodeList.getLength() > 0) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Element entry = (Element) nodeList.item(i);
                        Element titleElem = (Element) entry.getElementsByTagName(
                                "title").item(0);
                        Element descriptionElem = (Element) entry
                                .getElementsByTagName("description").item(0);
                        Element pubDateElem = (Element) entry
                                .getElementsByTagName("pubDate").item(0);
                        Element linkElem = (Element) entry.getElementsByTagName(
                                "link").item(0);
                        Element imageLargeElem = (Element) entry.getElementsByTagName(
                                "media:content").item(0);
                        Element categoryElem = (Element) entry.getElementsByTagName(
                                "category").item(0);

                        String title = titleElem.getFirstChild().getNodeValue();
                        String description = descriptionElem.getFirstChild().getNodeValue();

                        matcher = IMG_PATTERN.matcher(description);
                        matcher.find();
                        String imageSmall = description.substring(matcher.start(), matcher.end());

                        matcher = DESCRIPTION_PATTERN.matcher(description);
                        matcher.find();
                        description = description.substring(matcher.start(), matcher.end());

                        String imageLarge = imageLargeElem.getAttribute("url");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                        String date = sdf.format(new Date(pubDateElem.getFirstChild().getNodeValue()));

                        String link = linkElem.getFirstChild().getNodeValue();

                        String category = categoryElem.getFirstChild().getNodeValue();

                        RssItem rssItem = new RssItem(title, description,
                                date, link, imageSmall, imageLarge, category);

                        rssItems.add(rssItem);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);

        RuntimeExceptionDao<RssItem, Integer> itemDao = dbHelper.getItemRuntimeExceptionDao();

        if (mode == 1) {
            StartAcrivity.setMaxProgressBar(rssItems.size());
        }
        if (mode ==2){
            ListOfNewsActivity.setMaxProgressBar(rssItems.size());
        }

        for (int i = 0; i < rssItems.size(); i++) {
            itemDao.create(new RssItem(rssItems.get(i).getTitle(),
                    rssItems.get(i).getDescription(),
                    rssItems.get(i).getDate(),
                    rssItems.get(i).getLink(),
                    rssItems.get(i).getImageSmall(),
                    rssItems.get(i).getImageLarge(),
                    rssItems.get(i).getCategory()));
            if (mode == 1) {
                StartAcrivity.setProgressBar(i + 1);
            }
            if (mode ==2){
                ListOfNewsActivity.setProgressBar(i+1);
            }
        }
        List<RssItem> items = itemDao.queryForAll();
        OpenHelperManager.releaseHelper();

    }
}
