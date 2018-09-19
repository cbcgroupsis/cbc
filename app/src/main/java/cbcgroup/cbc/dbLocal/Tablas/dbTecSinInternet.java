package cbcgroup.cbc.dbLocal.Tablas;

public class dbTecSinInternet

{

    public static String CREATE_TABLE="CREATE TABLE [sinInternetTec] (\n" +
            "[id_tec] TEXT  NOT NULL,\n" +
            "[serie] TEXT  NOT NULL,\n" +
            "[id_parte] TEXT  NOT NULL,\n" +
            "[mensaje] TEXT  NOT NULL,\n" +
            "[copias] TEXT  NOT NULL,\n" +
            "[copiasColor] TEXT  NOT NULL,\n" +
            "[viaje] TEXT  NOT NULL,\n" +
            "[foto] TEXT  NULL,\n" +
            "[Espera] TEXT  NOT NULL\n" +
            ")";

    public static String TABLE="sinInternetTec";
    public static String CAMPO_IDTEC="id_tec";
    public static String CAMPO_SERIE="serie";
    public static String CAMPO_IDPARTE="id_parte";
    public static String CAMPO_MENSAJE="mensaje";
    public static String CAMPO_COPIAS="copias";
    public static String CAMPO_COPIASCOLOR="copiasColor";
    public static String CAMPO_TVIAJE="viaje";
    public static String CAMPO_FOTO="foto";
    public static String CAMPO_ESPERA="Espera";

}
