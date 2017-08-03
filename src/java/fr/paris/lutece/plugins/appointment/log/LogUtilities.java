package fr.paris.lutece.plugins.appointment.log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.portal.business.user.AdminUser;

public final class LogUtilities
{
    private static final String DATE = "Date :";
    private static final String HOUR = "Hour :";
    private static final String ACTION = "Action :";
    private static final String RESOURCE_ID = "Resource Id :";
    private static final String BY = "By :";
    private static final String DASH = "-";
    private static final String STARS = "***********************************************************************************************";

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private LogUtilities( )
    {
    }

    public static String buildLog( String action, String idResource, AdminUser user )
    {
        StringJoiner stjLog = new StringJoiner( StringUtils.SPACE );
        stjLog.add( StringUtils.LF ).add( StringUtils.LF ).add( STARS ).add( StringUtils.LF ).add( StringUtils.LF ).add( DASH ).add( DATE )
                .add( LocalDate.now( ).toString( ) ).add( DASH ).add( HOUR ).add( LocalTime.now( ).toString( ) ).add( DASH ).add( ACTION ).add( action )
                .add( DASH ).add( RESOURCE_ID ).add( idResource );
        if ( user != null )
        {
            stjLog.add( DASH ).add( BY ).add( user.getFirstName( ) ).add( user.getLastName( ) ).add( DASH ).add( StringUtils.LF );
        }
        stjLog.add( StringUtils.LF ).add( STARS ).add( StringUtils.LF ).add( StringUtils.LF );
        return stjLog.toString( );
    }

}
