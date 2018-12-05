package cbcgroup.cbc.dbLocal.Tablas;

public class dbTecnicos
{
    public static String CREATE_TABLE="CREATE TABLE [Tecnicos] (\n"+
        "[nParte] TEXT  UNIQUE NOT NULL ,\n"+
            "[idTec] TEXT NOT NULL ,\n"+
        "[Cliente] TEXT  NOT NULL,\n"+
        "[nSerie] TEXT  NOT NULL,\n"+
        "[Sector] TEXT  NOT NULL,\n"+
            "[Categoria] TEXT  NOT NULL,\n"+
        "[FechaVence] TEXT  NOT NULL,\n"+
        "[Fecha] TEXT  NOT NULL,\n"+
            "[horaVence] TEXT   NULL,\n"+
            "[Modelo] TEXT  NOT NULL,\n"+
            "[Ingreso] TEXT   NULL,\n"+
        "[Inconveniente] TEXT  NOT NULL\n"+
        ")";

    public static String TABLE="Tecnicos";
    public static String CAMPO_NPARTE="nParte"; //
    public static String CAMPO_CLIENTE="Cliente";//
    public static String CAMPO_NSERIE="nSerie";//
    public static String CAMPO_SECTOR="Sector";//
    public static String CAMPO_FECHAVENCE="FechaVence";//
    public static String CAMPO_FECHA="Fecha";//
    public static String CAMPO_INCONVENIENTE="Inconveniente";
    public static String CAMPO_MODELO="Modelo";
    public static String CAMPO_INGRESO="Ingreso";
    public static String CAMPO_IDTEC="idTec";
    public static String CAMPO_CATEGORIA="Categoria";
    public static String CAMPO_HORAVENCE="horaVence";
}
