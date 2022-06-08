package com.biw.asg.da.sp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.jdbc.object.StoredProcedure;

import java.sql.Types;

/**
 * Represents a single stored procedure parameter.
 */
@Data
@EqualsAndHashCode
public class StoredProcedureParameter
{
    private String argumentName;

    private String dataType;

    private int sequence;

    private String inOut;

    /**
     * @return True if this an input parameter.
     */
    public boolean isInput()
    {
        return "IN".equals( getInOut() );
    }

    /**
     * @return True if this an output parameter.
     */
    public boolean isOutput()
    {
        return "OUT".equals( getInOut() );
    }

    /**
     * Converts a type label provided by the DB
     * to an integer which is needed by {@link StoredProcedure}
     * @return an integer representing the parameter's data type.
     */
    public int getDataTypeAsInteger()
    {
        switch ( this.dataType )
        {
            case "NUMBER":
                return Types.NUMERIC;
            case "REF CURSOR":
                return -10;
            case "VARCHAR2":
            default:
                return Types.VARCHAR;
        }
    }

}
