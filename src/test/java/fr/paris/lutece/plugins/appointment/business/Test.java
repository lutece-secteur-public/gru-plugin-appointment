package fr.paris.lutece.plugins.appointment.business;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.sf.json.JSONObject;

public final class Test
{
    @JsonFormat( pattern = "dd::MM::yyyy" )
    static LocalDate date = LocalDate.now( );

    public static LocalDate getDate( )
    {
        return date;
    }

    public void setDate( LocalDate date )
    {
        Test.date = date;
    }

    public static void main( String [ ] args ) throws JsonProcessingException
    {

        ObjectMapper mapper = new ObjectMapper( );
        mapper.registerModule( new JavaTimeModule( ) );
        String strDate = mapper.writeValueAsString( date );
        System.out.println( strDate );

    }
}
