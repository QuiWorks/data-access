package com.biw.asg.da.sp;

import com.biw.asg.da.OracleType;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * A {@link ResultSetExtractor} for mapping SQL reference cursors.
 */
public class MetaResultSetExtractor implements ResultSetExtractor<List<Map<String, Object>>>
{
    @Override
    public List<Map<String, Object>> extractData( ResultSet rs ) throws SQLException, DataAccessException
    {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        while ( rs.next() )
        {
            Map<String, Object> record = new HashMap<>();
            for ( int i = 1; i < metaData.getColumnCount() + 1; i++ )
            {
                OracleType type = OracleType.of( metaData.getColumnTypeName( i ) ).orElse( OracleType.OBJECT );
                record.put( metaData.getColumnName( i ), type.getAccessor().apply( rs, i ) );
            }
            list.add( record );
        }
        return list;
    }
}
