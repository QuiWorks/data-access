package com.biw.asg.da.sp;

import java.util.Map;

/**
 * Represents a compiles SQL stored procedure.
 */
public interface StoredProcedureCall
{
    /**
     * Executes a stored procedure call.
     *
     * @param input A map of input key value pairs.
     * @return A map of the resulting data.
     */
    Map<String, Object> call( Map<String, Object> input );

    /**
     * Executes a store procedure call.
     *
     * @param input A map of input key value pairs.
     * @param type  The class representing the resulting data.
     * @param <T>   The type of the resulting data object.
     * @return An instance of {@code T} which contains the resulting data.
     */
    <T> T call( Map<String, Object> input, Class<T> type );
}
