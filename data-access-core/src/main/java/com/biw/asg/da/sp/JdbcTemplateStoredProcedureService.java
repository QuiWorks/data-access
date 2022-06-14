package com.biw.asg.da.sp;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.object.StoredProcedure;

import javax.sql.DataSource;
import java.util.*;

/**
 * An implementation of {@link StoredProcedureService} that uses a JDBC template to get the
 * stored procedures meta data.
 */
@Slf4j
public class JdbcTemplateStoredProcedureService implements StoredProcedureService
{

    private static final String PARAMETER_QUERY =
        "select ARGUMENT_NAME, data_type, sequence, in_out "
            + "from USER_ARGUMENTS a "
            + "where a.PACKAGE_NAME = :PACKAGE_NAME "
            + "and a.OBJECT_NAME = :OBJECT_NAME "
            + "order by sequence";
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public JdbcTemplateStoredProcedureService( NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource )
    {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @Override
    public StoredProcedureCall compileStoredProcedure( String packageName, String procedureName )
    {
        log.debug( "Compiling stored procedure: {}.{}", packageName, procedureName  );
        SqlParameterSource queryParameters = new MapSqlParameterSource()
            .addValue( "PACKAGE_NAME", packageName )
            .addValue( "OBJECT_NAME", procedureName );
        List<StoredProcedureParameter> parameters = jdbcTemplate.query( PARAMETER_QUERY,
                                                                        queryParameters,
                                                                        BeanPropertyRowMapper.newInstance( StoredProcedureParameter.class ) );
        parameters.forEach( parameter -> log.debug( "Parameter for {}.{}: {}", packageName, procedureName, parameter.toString() ) );
        StoredProcedureCallImpl storedProcedureCall = new StoredProcedureCallImpl( dataSource, packageName + "." + procedureName, parameters );
        log.debug( "Successfully compiled stored procedure: {}.{}", packageName, procedureName  );
        return storedProcedureCall;
    }

    public static class StoredProcedureCallImpl extends StoredProcedure  implements StoredProcedureCall {

        private final Set<StoredProcedureParameter> inputParameters = new HashSet<>();
        private final ObjectMapper objectMapper;
        private final String name;

        public StoredProcedureCallImpl( DataSource ds, String name, List<StoredProcedureParameter> parameters )
        {
            super( ds, name );
            this.name = name;

            objectMapper = JsonMapper.builder()
                .configure( MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true )
                .build();

            parameters.stream()
                .filter( StoredProcedureParameter::isInput )
                .peek( inputParameters::add )
                .map( input -> new SqlParameter( input.getArgumentName(), input.getDataTypeAsInteger() ) )
                .forEach( this::declareParameter );

            parameters.stream()
                .filter( StoredProcedureParameter::isOutput )
                .map( output -> new SqlOutParameter( output.getArgumentName(), output.getDataTypeAsInteger(), new MetaResultSetExtractor() ) )
                .forEach( this::declareParameter );

            compile();
        }

        @Override
        public Map<String, Object> call( Map<String, Object> input )
        {
            log.debug( "Calling {}", getSql() );

            Map<String, Object> inputs = new HashMap<>();
            inputParameters.stream().sorted( Comparator.comparingInt( StoredProcedureParameter::getSequence ) )
                .forEach( parameter -> inputs.put( parameter.getArgumentName(), input.getOrDefault( parameter.getArgumentName(), null ) ) );

            inputs.forEach( ( key, value ) -> log.debug( "Input for {}: key={} value={}", name, key, value) );

            Map<String, Object> execute = execute( inputs );
            log.debug( "Successfully called {}", name );
            return execute;
        }

        @Override
        public <T> T call( Map<String, Object> input, Class<T> type )
        {
            Map<String, Object> call = call( input );
            log.debug( "Converting results of {} to {}", name, type.getName() );
            return objectMapper.convertValue( call, type );
        }
    }


}
