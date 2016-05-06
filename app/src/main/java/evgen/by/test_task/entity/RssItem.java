package evgen.by.test_task.entity;

import com.j256.ormlite.field.DatabaseField;

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

    @DatabaseField
    private String imageSmall;

    @DatabaseField
    private String imageLarge;

    @DatabaseField
    private String category;

    public RssItem(String title, String description, String date, String link, String imageSmall, String imageLarge, String category){
        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
        this.imageSmall = imageSmall;
        this.imageLarge = imageLarge;
        this.category = category;
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

    public String getImageSmall(){
        return this.imageSmall;
    }

    public String getImageLarge(){
        return this.imageLarge;
    }

    public String getCategory(){
        return this.category;
    }

}
