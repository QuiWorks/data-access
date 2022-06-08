package com.biw.asg.da.sp;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            for( int i = 1; i < metaData.getColumnCount() + 1; i++ )
            {
                switch ( metaData.getColumnTypeName( i ) )
                {
                    case "NUMBER":
                        record.put( metaData.getColumnName( i ), rs.getInt( i ) );
                        break;
                    case "CLOB":
                    case "VARCHAR2":
                        record.put( metaData.getColumnName( i ), rs.getString( i ) );
                        break;
                    default:
                        record.put( metaData.getColumnName( i ), rs.getObject( i ) );
                        break;
                }
            }
            list.add( record );
        }
        return list;
    }

}
