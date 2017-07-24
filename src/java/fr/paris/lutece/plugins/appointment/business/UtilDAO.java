package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class UtilDAO 
{

	public static int getNewPrimaryKey( String query, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        int nKey = 1;
        try
        {
            daoUtil = new DAOUtil( query, plugin );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                nKey = daoUtil.getInt( 1 ) + 1;
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return nKey;
    }
    
}
