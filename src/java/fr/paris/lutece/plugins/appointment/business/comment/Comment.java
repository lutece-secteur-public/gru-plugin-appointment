/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.business.comment;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.NotNull;

/**
 * This is the business class for the object Comment
 */
public class Comment implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    private int _nIdForm;
    @NotNull( message = "#i18n{portal.validation.message.notEmpty}" )
    private LocalDate _dateStartingValidityDate;
    @NotNull( message = "#i18n{portal.validation.message.notEmpty}" )
    private LocalDate _dateEndingValidityDate;
    private LocalTime _timeStartingValidityTime;
    private LocalTime _timeEndingValidityTime;
    @NotEmpty( message = "#i18n{appointment.validation.comment.Comment.notEmpty}" )
    private String _strComment;

    private LocalDate _dateCreationDate;

    /**
     * The User who created the comment (if not created by the user himself)
     */
    private String _strCreatorUserConnectId;

    /**
     * Returns the Id
     * 
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * 
     * @param nId
     *            The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the IdForm
     * 
     * @return The IdForm
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Sets the IdForm
     * 
     * @param nIdForm
     *            The IdForm
     */
    public void setIdForm( int nIdForm )
    {
        _nIdForm = nIdForm;
    }

    /**
     * Returns the StartingValidityDate
     * 
     * @return The StartingValidityDate
     */
    public LocalDate getStartingValidityDate( )
    {
        return _dateStartingValidityDate;
    }

    /**
     * Sets the StartingValidityDate
     * 
     * @param dateStartingValidityDate
     *            The StartingValidityDate
     */
    public void setStartingValidityDate( LocalDate dateStartingValidityDate )
    {
        _dateStartingValidityDate = dateStartingValidityDate;
    }

    /**
     * Returns the EndingValidityDate
     * 
     * @return The EndingValidityDate
     */
    public LocalDate getEndingValidityDate( )
    {
        return _dateEndingValidityDate;
    }

    /**
     * Sets the EndingValidityDate
     * 
     * @param dateEndingValidityDate
     *            The EndingValidityDate
     */
    public void setEndingValidityDate( LocalDate dateEndingValidityDate )
    {
        _dateEndingValidityDate = dateEndingValidityDate;
    }

    /**
     * Returns the Comment
     * 
     * @return The Comment
     */
    public String getComment( )
    {
        return _strComment;
    }

    /**
     * Sets the Comment
     * 
     * @param strComment
     *            The Comment
     */
    public void setComment( String strComment )
    {
        _strComment = strComment;
    }

    /**
     * Returns the CreationDate
     * 
     * @return The CreationDate
     */
    public LocalDate getCreationDate( )
    {
        return _dateCreationDate;
    }

    /**
     * Sets the CreationDate
     * 
     * @param dateCreationDate
     *            The CreationDate
     */
    public void setCreationDate( LocalDate dateCreationDate )
    {
        _dateCreationDate = dateCreationDate;
    }

    /**
     * UserConnectId
     * 
     * @return The _strCreatorUserConnectId
     */
    public String getCreatorUserName( )
    {
        return _strCreatorUserConnectId;
    }

    /**
     * Sets the strCreatorUserConnectId
     * 
     * @param creatorUserConnectId
     *            The creatorUserConnectId
     */
    public void setCreatorUserName( String creatorUserConnectId )
    {
        _strCreatorUserConnectId = creatorUserConnectId;
    }

    /**
     * Returns the StartingValidityTime
     * 
     * @return The StartingValidityTime
     */
    public LocalTime getStartingValidityTime( )
    {
        return _timeStartingValidityTime;
    }

    /**
     * Sets the StartingValidityTime
     * 
     * @param timeStartingValidityTime
     *            The StartingValidityTime
     */
    public void setStartingValidityTime( LocalTime timeStartingValidityTime )
    {
        _timeStartingValidityTime = timeStartingValidityTime;
    }

    /**
     * Returns the EndingValidityTime
     * 
     * @return The EndingValidityTime
     */
    public LocalTime getEndingValidityTime( )
    {
        return _timeEndingValidityTime;
    }

    /**
     * Sets the EndingValidityTime
     * 
     * @param timeEndingValidityTime
     *            The EndingValidityTime
     */
    public void setEndingValidityTime( LocalTime timeEndingValidityTime )
    {
        _timeEndingValidityTime = timeEndingValidityTime;
    }

    /**
     * Gets the DateCalendarAllDaySlotEnd
     * 
     * @return The DateCalendarAllDaySlotEnd
     */
    public LocalDate getCalendarAllDaySlotEnd( )
    {
        return _dateEndingValidityDate.plusDays( 1 );
    }
}
