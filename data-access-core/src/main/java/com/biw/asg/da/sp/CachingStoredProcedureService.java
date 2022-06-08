package com.biw.asg.da.sp;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CachingStoredProcedureService extends JdbcTemplateStoredProcedureService
{

    private final LoadingCache<String, StoredProcedureCall> loadingCache;

    @Autowired
    public CachingStoredProcedureService( NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource )
    {
        super( jdbcTemplate, dataSource );

        loadingCache = CacheBuilder.newBuilder()
            .expireAfterAccess( 12, TimeUnit.HOURS)
            .build(new CacheLoader<String, StoredProcedureCall>() {
                @Override
                public StoredProcedureCall load(final String packageAndName) {
                    String[] split = packageAndName.split( "\\." );
                    if(2 != split.length) {
                        String message = "Invalid stored procedure package and procedure name " + packageAndName;
                        log.error( message );
                        throw new RuntimeException( message );
                    }
                    StoredProcedureCall storedProcedureCall = CachingStoredProcedureService.super.compileStoredProcedure( split[0], split[1] );
                    log.debug( "loading {} into stored procedure cache.", packageAndName );
                    return storedProcedureCall;
                }
            });
    }

    @Override
    public StoredProcedureCall compileStoredProcedure( String packageName, String procedureName )
    {
        final String concatName = packageName+"."+procedureName;
        try
        {
            log.debug( "Checking stored procedure cache for {}", concatName );
            return loadingCache.get( concatName );
        }
        catch( ExecutionException e )
        {
            log.error( "Unable to load stored procedure {}.{} into cache: {}", packageName, procedureName, e.getMessage() );
            throw new RuntimeException("Unable to load stored procedure "+concatName+"into cache:", e);
        }
    }
}
