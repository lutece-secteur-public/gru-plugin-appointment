package fr.paris.lutece.plugins.appointment.business;

import java.io.Serializable;
import java.sql.Date;

import fr.paris.lutece.plugins.appointment.business.user.User;

public final class AppointmentFilter extends User implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -8087511361613314595L;

    /**
     * The form Id
     */
    private int _nIdForm;

    /**
     * The starting date for the search
     */
    private Date _startingDateOfSearch;

    /**
     * the ending date for the search
     */
    private Date _endingDateOfSearch;

    /**
     * The starting time for the search
     */
    private String _strStartingTimeOfSearch;

    /**
     * the ending time for the search
     */
    private String _strEndingTimeOfSearch;

    /**
     * The reference of the appointment to search
     */
    private String _strReference;

    /**
     * Get the form id
     * 
     * @return the form id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the form id
     * 
     * @param nIdForm
     *            the form id to set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

    /**
     * Get the starting date of search
     * 
     * @return the starting date of search
     */
    public Date getStartingDateOfSearch( )
    {
        if ( _startingDateOfSearch != null )
        {
            return (Date) _startingDateOfSearch.clone( );
        }
        else
        {
            return null;
        }
    }

    /**
     * Set the starting date of search
     * 
     * @param startingDateOfSearch
     *            the starting date to set
     */
    public void setStartingDateOfSearch( Date startingDateOfSearch )
    {
        if ( startingDateOfSearch != null )
        {
            this._startingDateOfSearch = (Date) startingDateOfSearch.clone( );
        }
        else
        {
            this._startingDateOfSearch = null;
        }
    }

    /**
     * Get the ending date of search
     * 
     * @return the ending date
     */
    public Date getEndingDateOfSearch( )
    {
        if ( _endingDateOfSearch != null )
        {
            return (Date) _endingDateOfSearch.clone( );
        }
        else
        {
            return null;
        }

    }

    /**
     * Set the ending date of search to the filter
     * 
     * @param endingDateOfSearch
     *            the ending date of search to set
     */
    public void setEndingDateOfSearch( Date endingDateOfSearch )
    {
        if ( endingDateOfSearch != null )
        {
            this._endingDateOfSearch = (Date) endingDateOfSearch.clone( );
        }
        else
        {
            this._endingDateOfSearch = null;
        }
    }

    /**
     * Get the starting time of search
     * 
     * @return the starting time of search
     */
    public String getStartingTimeOfSearch( )
    {
        return _strStartingTimeOfSearch;
    }

    /**
     * Set the starting time of search
     * 
     * @param strStartingTimeOfSearch
     *            the starting time to set
     */
    public void setStartingTimeOfSearch( String strStartingTimeOfSearch )
    {
        this._strStartingTimeOfSearch = strStartingTimeOfSearch;
    }

    /**
     * Get the ending time of search
     * 
     * @return the ending time of search
     */
    public String getEndingTimeOfSearch( )
    {
        return _strEndingTimeOfSearch;
    }

    /**
     * Set the ending time of search
     * 
     * @param strEndingTimeOfSearch
     *            the ending time to set
     */
    public void setEndingTimeOfSearch( String strEndingTimeOfSearch )
    {
        this._strEndingTimeOfSearch = strEndingTimeOfSearch;
    }

    /**
     * Get the reference entered in the filter
     * 
     * @return the reference
     */
    public String getReference( )
    {
        return _strReference;
    }

    /**
     * Set the reference to the filter
     * 
     * @param strReference
     *            the reference to set
     */
    public void setReference( String strReference )
    {
        this._strReference = strReference;
    }

}
