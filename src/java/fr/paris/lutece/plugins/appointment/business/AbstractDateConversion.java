package fr.paris.lutece.plugins.appointment.business;

import java.sql.Date;
import java.time.LocalDate;

public class AbstractDateConversion
{

    /**
     * Date from which the week definition has to be applied
     */
    private LocalDate _dateOfApply;

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
    public void setSqlDateOfApply( Date dateOfApply )
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
}
