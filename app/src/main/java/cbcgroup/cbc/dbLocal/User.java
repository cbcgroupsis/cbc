package cbcgroup.cbc.dbLocal;

public class User
{
    private Integer id;
    private String nombre;
    private String email;
    private String sector;
    private String iduser;
    private String token;
    public User(Integer id, String nombre, String email, String sector, String iduser, String token) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.sector = sector;
        this.iduser = iduser;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
