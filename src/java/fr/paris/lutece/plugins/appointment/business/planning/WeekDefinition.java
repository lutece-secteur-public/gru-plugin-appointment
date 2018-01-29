package fr.paris.lutece.plugins.appointment.business.planning;

import java.io.Serializable;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.AbstractDateConversion;

/**
 * Business class of the definition week
 * 
 * @author Laurent Payen
 *
 */
public final class WeekDefinition extends AbstractDateConversion implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4292654762871322318L;

    /**
     * Id of the week definition
     */
    private int _nIdWeekDefinition;

    /**
     * Id of the form the week definition belongs to
     */
    private int _nIdForm;

    /**
     * List of the working days that define the week definition
     */
    private List<WorkingDay> _listWorkingDays;

    /**
     * Get the id of the week definition
     * 
     * @return the id of the week definition
     */
    public int getIdWeekDefinition( )
    {
        return _nIdWeekDefinition;
    }

    /**
     * Set the id of the week definition
     * 
     * @param _nIdWeekDefinition
     *            the id to set
     */
    public void setIdWeekDefinition( int nIdWeekDefinition )
    {
        this._nIdWeekDefinition = nIdWeekDefinition;
    }

    /**
     * Get the form id the week definition belongs to
     * 
     * @return the form id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the form id the week definition belongs to
     * 
     * @param nIdForm
     *            the form id to set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

    /**
     * Get the list of the working days of the week
     * 
     * @return the list of the working days for the week
     */
    public List<WorkingDay> getListWorkingDay( )
    {
        return _listWorkingDays;
    }

    /**
     * Set the working days for the week
     * 
     * @param _listWorkingDays
     *            the list o f working days to set
     */
    public void setListWorkingDay( List<WorkingDay> listWorkingDays )
    {
        this._listWorkingDays = listWorkingDays;
    }

}
