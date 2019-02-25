package cbcgroup.cbc.dbLocal.Tablas;

public class dbTecSinInternet

{

    public static final String CREATE_TABLE="CREATE TABLE [sinInternetTec] (\n" +
            "[id_tec] TEXT  NOT NULL,\n" +
            "[serie] TEXT  NOT NULL,\n" +
            "[id_parte] TEXT  NOT NULL,\n" +
            "[mensaje] TEXT  NOT NULL,\n" +
            "[copias] TEXT  NOT NULL,\n" +
            "[copiasColor] TEXT  NOT NULL,\n" +
            "[viaje] TEXT  NOT NULL,\n" +
            "[cierre] TEXT  NOT NULL,\n" +
            "[foto] TEXT  NULL,\n" +
            "[Espera] TEXT  NOT NULL\n" +
            ")";

    public static final String TABLE="sinInternetTec";
    public static final String CAMPO_IDTEC="id_tec";
    public static final String CAMPO_SERIE="serie";
    public static final String CAMPO_IDPARTE="id_parte";
    public static final String CAMPO_MENSAJE="mensaje";
    public static final String CAMPO_COPIAS="copias";
    public static final String CAMPO_COPIASCOLOR="copiasColor";
    public static final String CAMPO_TVIAJE="viaje";
    public static final String CAMPO_FOTO="foto";
    public static final String CAMPO_ESPERA="Espera";
    public static final String CAMPO_CIERRE="cierre";

}
