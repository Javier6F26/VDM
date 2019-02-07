package VDM;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Element {

    private String channel_idN, tvg_id  , channel_id , group_title;
    URL url, tvg_logo ;

    Element(String channel_idN, String tvg_id, String tvg_logo, String channel_id, String group_title) {
        this.channel_idN = channel_idN;
        this.tvg_id = tvg_id;
        try {
            this.tvg_logo = new URL(tvg_logo);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.channel_id = channel_id;
        this.group_title = group_title;
    }

    public String getChannel_idN() {
        return channel_idN;
    }

    public String getTvg_id() {
        return tvg_id;
    }



    public URL getTvg_logo() {
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

class Serie{

    private String titulo;
    private ArrayList<Temporada> temporadas;

    public Serie(String titulo) {
        this.titulo = titulo;
        this.temporadas=new ArrayList<Temporada>();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTemporadas(ArrayList<Temporada> temporadas) {
        for(Temporada temporada :temporadas ){
            temporada.setSerie(this);
        }
        this.temporadas = temporadas;
    }

    public ArrayList<Temporada> getTemporadas() {
        return temporadas;
    }
}
class Temporada{

    private Serie serie;
    private int nro;
    ArrayList<Episodio> episodios;

    public Temporada(int nro) {
        this.nro = nro;
        this.episodios=new ArrayList<Episodio>();

    }

    public Serie getSerie() {
        return serie;
    }

    public int getNro() {
        return nro;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public ArrayList<Episodio> getEpisodios() {
        return episodios;
    }
}

class Episodio{

    private URL url;
    private URL logo;
    private Serie serie;
    private Temporada temporada;
    private String name;

    public Episodio(URL url, URL logo,String name, Serie serie, Temporada temporada) {
        this.url = url;
        this.logo = logo;
        this.name =name;
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
}
