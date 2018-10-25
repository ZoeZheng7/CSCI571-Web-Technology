package com.csci571.hw9.hw9;

public class ReviewClass{

    private String pic;
    private String name;
    private int rating;
    private String time;
    private String content;
    private String url;

    public ReviewClass(String pic, String name, int rating, String time, String content, String url) {
        this.pic = pic;
        this.name = name;
        this.rating = rating;
        this.time = time;
        this.content = content;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public String getPic() {
        return pic;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

}
