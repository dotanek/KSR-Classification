package krs.mkpr.Extraction;

import krs.mkpr.Preparation.Article;

import java.util.List;

public class ArticleProperties {
    private Article article;
    private List<Object> properties;

    public ArticleProperties(Article article, List<Object> properties) {
       this.article = article;
       this.properties = properties;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public List<Object> getProperties() {
        return properties;
    }

    public void setProperties(List<Object> properties) {
        this.properties = properties;
    }
}
