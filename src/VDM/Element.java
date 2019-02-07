package VDM;

import java.net.URL;

public class Element {

    private String channel_idN, tvg_id , tvg_logo , channel_id , group_title;
    URL url ;

    Element(String channel_idN, String tvg_id, String tvg_logo, String channel_id, String group_title) {
        this.channel_idN = channel_idN;
        this.tvg_id = tvg_id;
        this.tvg_logo = tvg_logo;
        this.channel_id = channel_id;
        this.group_title = group_title;
    }

    public String getChannel_idN() {
        return channel_idN;
    }

    public String getTvg_id() {
        return tvg_id;
    }



    public String getTvg_logo() {
        return tvg_logo;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public String getGroup_title() {
        return group_title;
    }

    public URL getUrl() {
        return url;
    }

    void setUrl(URL url) {
        this.url = url;
    }
}