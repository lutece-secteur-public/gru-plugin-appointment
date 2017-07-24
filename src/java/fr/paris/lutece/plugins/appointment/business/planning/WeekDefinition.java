package fr.paris.lutece.plugins.appointment.business.planning;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Business class of the definition week
 * 
 * @author Laurent Payen
 *
 */
public final class WeekDefinition implements Serializable
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
     * Date from which the week definition has to be applied
     */
    private LocalDate _dateOfApply;

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
     * Get the date from which the week definition has to be applied
     * 
     * @return the date from which the week definition has to be applied
     */
    public LocalDate getDateOfApply( )
    {
        return _dateOfApply;
    }

    /**
     * Get the date from which the week definition has to be applied
     * 
     * @return the date in Sql Date format
     */
    public Date getSqlDateOfApply( )
    {
        Date date = null;
        if ( this._dateOfApply != null )
        {
            date = Date.valueOf( _dateOfApply );
        }
        return date;
    }

    /**
     * Set the date from which the week definition has to be applied
     * 
     * @param dateOfApply
     *            the date to set
     */
    public void setDateOfApply( LocalDate dateOfApply )
    {
        this._dateOfApply = dateOfApply;
    }

    /**
     * Set the date from which the week definition has to be applied
     * 
     * @param dateOfApply
     *            the date to set (in Sql Date format)
     */
    public void setDateOfApply( Date dateOfApply )
    {
        if ( dateOfApply != null )
        {
            this._dateOfApply = dateOfApply.toLocalDate( );
        }
        else
        {
            this._dateOfApply = null;
        }
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
