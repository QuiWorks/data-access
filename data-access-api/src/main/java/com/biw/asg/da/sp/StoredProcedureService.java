package com.biw.asg.da.sp;

/**
 * A Service for compiling and running SQL stored procedures.
 */
public interface StoredProcedureService
{
    /**
     * Compiles a stored procedure (SP).
     * @param packageName The name of the package where the SP is located.
     * @param procedureName The name of the SP
     * @return A {@link StoredProcedureCall}
     */
    StoredProcedureCall compileStoredProcedure( String packageName, String procedureName );

}
