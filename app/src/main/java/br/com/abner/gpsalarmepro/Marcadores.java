package br.com.abner.gpsalarmepro;

/**
 * Created by AbnerAdmin on 10/09/2015.
 */
public class Marcadores {

    public Marcadores(long id, String endereco, Double latitude, Double longitude, long ativo, long distancia) {
        this.id = id;
        this.endereco = endereco;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ativo = ativo;
        this.distancia = distancia;
    }

    public Marcadores(){}

    private long id;
    private String nome;
    private String endereco;
    private Double latitude;
    private Double longitude;
    private long ativo = 0;
    private long distancia = 500;

    public long getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getEndereco() {
        return endereco;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public long getAtivo() {
        return ativo;
    }
    public long getDistancia() {
        return distancia;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public void setAtivo(long ativo) {
        this.ativo = ativo;
    }
    public void setDistancia(long distancia) {
        if( distancia < 0 ) distancia = 0;
        this.distancia = distancia;
    }

    public void toLong(boolean ativo){
        if (ativo == true) {
            setAtivo(1);
        } else if (ativo == false) {
            setAtivo(0);
        }
    }

    @Override
    public String toString() {
        return "{"+id+": "+nome+", "+endereco+", "+latitude+", "+longitude+", "+ ativo +", "+distancia+" metros}";
    }
}
