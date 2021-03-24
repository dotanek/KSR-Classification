package krs.mkpr.Extraction;

import krs.mkpr.Keywords;

import java.util.List;

public class Article {
    private String title;
    public String text;
    private String dateline;
    private String country;

    public Article() {
        Keywords keywords = new Keywords();
    }

    public Article(String title, String text, String dateline, String country) {
        this.title = title;
        this.text = text;
        this.dateline = dateline;
        this.country = country;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        /*return "Article{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", dateline='" + dateline + '\'' +
                ", country='" + country + '\'' +
                '}';*/
        return "{country='"+ country + "'}";
    }
}

