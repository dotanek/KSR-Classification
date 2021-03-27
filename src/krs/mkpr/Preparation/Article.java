package krs.mkpr.Preparation;

import krs.mkpr.Keywords;

import java.util.List;

public class Article {
    private String title;
    private String text;
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

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", dateline='" + dateline + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}

