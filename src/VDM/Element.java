package VDM;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Element {

    private String channel_idN, tvg_id, channel_id, group_title;
    private URL url, tvg_logo;

    Element(String channel_idN, String tvg_id, String tvg_logo, String channel_id, String group_title) {
        this.channel_idN = channel_idN;
        this.tvg_id = tvg_id;
        try {
            this.tvg_logo = new URL(tvg_logo);
        } catch (MalformedURLException e) { }
        this.channel_id = channel_id;
        this.group_title = group_title;
    }

    public String getChannel_idN() {
        return channel_idN;
    }

    public String getTvg_id() {
        return tvg_id;
    }


    URL getTvg_logo() {
        return tvg_logo;
    }

    String getChannel_id() {
        return channel_id;
    }

    String getGroup_title() {
        return group_title;
    }

    URL getUrl() {
        return url;
    }

    void setUrl(URL url) {
        this.url = url;
    }
}

class Serie {

    private String titulo;
    private ArrayList<Temporada> temporadas;

    Serie(String titulo) {
        this.titulo = titulo;
        this.temporadas = new ArrayList<Temporada>();
    }

    String getTitulo() {
        return titulo;
    }

    public void setTemporadas(ArrayList<Temporada> temporadas) {
        for (Temporada temporada : temporadas) {
            temporada.setSerie(this);
        }
        this.temporadas = temporadas;
    }

    ArrayList<Temporada> getTemporadas() {
        return temporadas;
    }
}

class Temporada {

    private Serie serie;
    private int nro;
    private ArrayList<Episodio> episodios;

    Temporada(int nro) {
        this.nro = nro;
        this.episodios = new ArrayList<Episodio>();

    }

    public Serie getSerie() {
        return serie;
    }

    int getNro() {
        return nro;
    }

    void setSerie(Serie serie) {
        this.serie = serie;
    }

    ArrayList<Episodio> getEpisodios() {
        return episodios;
    }
}

class Episodio {

    private URL url;
    private URL logo;
    private Serie serie;
    private Temporada temporada;
    private String name;

    Episodio(URL url, URL logo, String name, Serie serie, Temporada temporada) {
        this.url = url;
        this.logo = logo;
        this.name = name;
        this.serie = serie;
        this.temporada = temporada;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL getLogo() {
        return logo;
    }

    public void setLogo(URL logo) {
        this.logo = logo;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public Temporada getTemporada() {
        return temporada;
    }

    public void setTemporada(Temporada temporada) {
        this.temporada = temporada;
    }

    public String getName() {
        return name;
    }
}
