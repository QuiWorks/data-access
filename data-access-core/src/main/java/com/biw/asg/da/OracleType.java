package com.biw.asg.da;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public enum OracleType implements SQLType {
    VARCHAR2("VARCHAR2", 12),
    NVARCHAR("NVARCHAR", -9),
    NUMBER("NUMBER", 2),
    FLOAT("FLOAT", 6),
    LONG("LONG", -1),
    DATE("DATE", 91),
    BINARY_FLOAT("BINARY FLOAT", 100),
    BINARY_DOUBLE("BINARY DOUBLE", 101),
    TIMESTAMP("TIMESTAMP", 93),
    TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", -101),
    TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE", -102),
    INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", -103),
    INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", -104),
    PLSQL_BOOLEAN("PLSQL_BOOLEAN", 252),
    RAW("RAW", -2),
    LONG_RAW("LONG RAW", -4),
    ROWID("ROWID", -8),
    UROWID("UROWID"),
    CHAR("CHAR", 1),
    NCHAR("NCHAR", -15),
    CLOB( "CLOB", 2005, (rs, i) -> {
        try
        {
            return rs.getString( i );
        }
        catch( SQLException e )
        {
            e.printStackTrace();
            return null;
        }
    } ),
    NCLOB("NCLOB", 2011),
    BLOB("BLOB", 2004),
    BFILE("BFILE", -13),
    JSON("JSON", 2016),
    OBJECT("OBJECT", 2002),
    REF("REF", 2006),
    VARRAY("VARRAY", 2003),
    NESTED_TABLE("NESTED_TABLE", 2003),
    ANYTYPE("ANYTYPE", 2007),
    ANYDATA("ANYDATA", 2007),
    ANYDATASET("ANYDATASET"),
    XMLTYPE("XMLTYPE", 2009),
    HTTPURITYPE("HTTPURITYPE"),
    XDBURITYPE("XDBURITYPE"),
    DBURITYPE("DBURITYPE"),
    SDO_GEOMETRY("SDO_GEOMETRY"),
    SDO_TOPO_GEOMETRY("SDO_TOPO_GEOMETRY"),
    SDO_GEORASTER("SDO_GEORASTER"),
    ORDAUDIO("ORDAUDIO"),
    ORDDICOM("ORDDICOM"),
    ORDDOC("ORDDOC"),
    ORDIMAGE("ORDIMAGE"),
    ORDVIDEO("ORDVIDEO"),
    SI_AVERAGE_COLOR("SI_AVERAGE_COLOR"),
    SI_COLOR("SI_COLOR"),
    SI_COLOR_HISTOGRAM("SI_COLOR_HISTOGRAM"),
    SI_FEATURE_LIST("SI_FEATURE_LIST"),
    SI_POSITIONAL_COLOR("SI_POSITIONAL_COLOR"),
    SI_STILL_IMAGE("SI_STILL_IMAGE"),
    SI_TEXTURE("SI_TEXTURE"),
    REF_CURSOR("REF CURSOR", -10);

    private final String typeName;
    private final int code;
    private BiFunction<ResultSet, Integer, Object> accessor;

    public static OracleType toOracleType(SQLType var0) throws SQLException {
        return var0 instanceof OracleType ? (OracleType)var0 : null;
    }

    public static OracleType toOracleType(int var0) throws SQLException {
        List var1 = Arrays.asList(values());
        OracleType var2 = null;
        Iterator var3 = var1.iterator();

        while(var3.hasNext()) {
            OracleType var4 = (OracleType)var3.next();
            if (var4.getVendorTypeNumber() == var0) {
                var2 = var4;
                break;
            }
        }

        return var2;
    }

    public static Optional<OracleType> of(String name)
    {
        return Arrays.stream( OracleType.values() )
            .filter( type -> type.getName().equals( name ) )
            .findAny();
    }

    OracleType(String var3) {
        this(var3, -2147483648);
    }

    OracleType(String var3, int var4) {
        this(var3, var4, (rs, i) -> {
            try
            {
                return rs.getObject( i );
            }
            catch( SQLException e )
            {
                e.printStackTrace();
                return null;
            }
        } );
    }


    OracleType( String typeName, int code, BiFunction<ResultSet, Integer, Object> accessor ) {
        this.typeName = typeName;
        this.code = code;
        this.accessor = accessor;
    }

    public BiFunction<ResultSet, Integer, Object> getAccessor()
    {
        return accessor;
    }

    public String getName() {
        return this.typeName;
    }

    public int getCode()
    {
        return code;
    }

    public String getVendor() {
        return "Oracle Database";
    }

    public Integer getVendorTypeNumber() {
        return this.code;
    }

}
