package evgen.by.test_task.entity;

import android.media.Image;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by evgen on 03.05.2016.
 */
public class RssItem {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String title;
    @DatabaseField
    private String description;
    @DatabaseField
    private String date;
    @DatabaseField
    private String link;

    public RssItem(String title, String description, String date, String link){
        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
    }

    public RssItem(){
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getDate()
    {
        return this.date;
    }

    public String getLink()
    {
        return this.link;
    }

}
