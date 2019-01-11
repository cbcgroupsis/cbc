package cbcgroup.cbc.dbLocal.Tablas;

public class dbInsumos
{
    public static final String CREATE_TABLE="CREATE TABLE [Insumos] (\n" +
            "[nPedido] TEXT  NOT NULL,\n" +
            "[Cliente] TEXT  NOT NULL,\n" +
            "[Serie] TEXT   NOT NULL,\n" +
            "[Modelo] TEXT  NOT NULL,\n" +
            "[Foto] TEXT  NULL,\n" +
            "[GuardarLocal] TEXT  NULL\n" +
            ")";
    public static final String TABLE="Insumos";
    public static final String CAMPO_NPEDIDO="nPedido";
    public static final String CAMPO_Cliente="Cliente";
    public static final String CAMPO_SERIE="Serie";
    public static final String CAMPO_MODELO="Modelo";
    public static String CAMPO_FOTO="Foto";
    public static String CAMPO_GUARDARLOCAL="GuardarLocal";
}
