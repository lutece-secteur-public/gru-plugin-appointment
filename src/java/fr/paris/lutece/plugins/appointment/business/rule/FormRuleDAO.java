package fr.paris.lutece.plugins.appointment.business.rule;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form Rule objects
 * 
 * @author Laurent Payen
 *
 */
public class FormRuleDAO implements IFormRuleDAO
{

    private static final String SQL_QUERY_NEW_PK = "SELECT max(id_form_rule) FROM appointment_form_rule";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_form_rule (id_form_rule, is_captcha_enabled, is_mandatory_email_enabled, is_active_authentication, nb_days_before_new_appointment, min_time_before_appointment, nb_max_appointments_per_user, nb_days_for_max_appointments_per_user, id_form) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_form_rule SET is_captcha_enabled = ?, is_mandatory_email_enabled = ?, is_active_authentication = ?, nb_days_before_new_appointment = ?, min_time_before_appointment = ?, nb_max_appointments_per_user = ?, nb_days_for_max_appointments_per_user = ?, id_form = ? WHERE id_form_rule = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_form_rule WHERE id_form_rule = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_form_rule, is_captcha_enabled, is_mandatory_email_enabled, is_active_authentication, nb_days_before_new_appointment, min_time_before_appointment, nb_max_appointments_per_user, nb_days_for_max_appointments_per_user, id_form FROM appointment_form_rule";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form_rule = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";

    @Override
    public int getNewPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = null;
        int nKey = 1;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
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

    @Override
    public synchronized void insert( FormRule formRule, Plugin plugin )
    {
        formRule.setIdFormRule( getNewPrimaryKey( plugin ) );
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, formRule, plugin, true );
        executeUpdate( daoUtil );
    }

    @Override
    public void update( FormRule formRule, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, formRule, plugin, false );
        executeUpdate( daoUtil );
    }

    @Override
    public void delete( int nIdFormRule, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdFormRule );
        executeUpdate( daoUtil );
    }

    @Override
    public FormRule select( int nIdFormRule, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        FormRule formRule = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
            daoUtil.setInt( 1, nIdFormRule );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                formRule = buildFormRule( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return formRule;
    }

    @Override
    public FormRule findByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        FormRule formRule = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                formRule = buildFormRule( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return formRule;
    }

    /**
     * Build a Form rule business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new formRule with all its attributes assigned
     */
    private FormRule buildFormRule( DAOUtil daoUtil )
    {
        int nIndex = 1;
        FormRule formRule = new FormRule( );
        formRule.setIdFormRule( daoUtil.getInt( nIndex++ ) );
        formRule.setIsCaptchaEnabled( daoUtil.getBoolean( nIndex++ ) );
        formRule.setIsMandatoryEmailEnabled( daoUtil.getBoolean( nIndex++ ) );
        formRule.setIsActiveAuthentication( daoUtil.getBoolean( nIndex++ ) );
        formRule.setNbDaysBeforeNewAppointment( daoUtil.getInt( nIndex++ ) );
        formRule.setMinTimeBeforeAppointment( daoUtil.getInt( nIndex++ ) );
        formRule.setNbMaxAppointmentsPerUser( daoUtil.getInt( nIndex++ ) );
        formRule.setNbDaysForMaxAppointmentsPerUser( daoUtil.getInt( nIndex++ ) );
        formRule.setIdForm( daoUtil.getInt( nIndex ) );
        return formRule;
    }

    /**
     * Build a daoUtil object with the FormRule business object
     * 
     * @param query
     *            the query
     * @param formRule
     *            the FormRule
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, FormRule formRule, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( query, plugin );
        if ( isInsert )
        {
            daoUtil.setInt( nIndex++, formRule.getIdFormRule( ) );
        }
        daoUtil.setBoolean( nIndex++, formRule.isCaptchaEnabled( ) );
        daoUtil.setBoolean( nIndex++, formRule.isMandatoryEmailEnabled( ) );
        daoUtil.setBoolean( nIndex++, formRule.isActiveAuthentication( ) );
        daoUtil.setInt( nIndex++, formRule.getNbDaysBeforeNewAppointment( ) );
        daoUtil.setInt( nIndex++, formRule.getMinTimeBeforeAppointment( ) );
        daoUtil.setInt( nIndex++, formRule.getNbMaxAppointmentsPerUser( ) );
        daoUtil.setInt( nIndex++, formRule.getNbDaysForMaxAppointmentsPerUser( ) );
        daoUtil.setInt( nIndex++, formRule.getIdForm( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, formRule.getIdFormRule( ) );
        }
        return daoUtil;
    }

    /**
     * Execute a safe update (Free the connection in case of error when execute the query)
     * 
     * @param daoUtil
     *            the daoUtil
     */
    private void executeUpdate( DAOUtil daoUtil )
    {
        try
        {
            daoUtil.executeUpdate( );
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
    }

}
