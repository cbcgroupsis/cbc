package cbcgroup.cbc.dbLocal.Tablas;

public class dbTecnicos
{
    public static final String CREATE_TABLE="CREATE TABLE [Tecnicos] (\n"+
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

    public static final String TABLE="Tecnicos";
    public static final String CAMPO_NPARTE="nParte"; //
    public static final String CAMPO_CLIENTE="Cliente";//
    public static final String CAMPO_NSERIE="nSerie";//
    public static final String CAMPO_SECTOR="Sector";//
    public static final String CAMPO_FECHAVENCE="FechaVence";//
    public static final String CAMPO_FECHA="Fecha";//
    public static final String CAMPO_INCONVENIENTE="Inconveniente";
    public static final String CAMPO_MODELO="Modelo";
    public static final String CAMPO_INGRESO="Ingreso";
    public static final String CAMPO_IDTEC="idTec";
    public static final String CAMPO_CATEGORIA="Categoria";
    public static final String CAMPO_HORAVENCE="horaVence";
}
