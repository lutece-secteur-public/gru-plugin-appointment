/*
 * Copyright (c) 2002-2025, City of Paris
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

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationConfig.NotificationType;

/**
 * A notification sent when a comment is added, modified or removed. It keeps a copy of the comment as it was at that
 * time, so that the notification of a removed comment can still be shown.
 */
public class CommentNotificationLog implements Serializable
{
    private static final long serialVersionUID = 4215716513040286170L;

    private int _nId;
    private NotificationType _type;
    private int _nIdForm;
    private int _nIdComment;
    private String _strComment;
    private String _strCreatorUserName;
    private LocalDate _dateStartingValidityDate;
    private LocalDate _dateEndingValidityDate;
    private Timestamp _dateNotification;
    private String _strFormTitle;

    /**
     * @return the id of the notification
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * @param nId
     *            the id of the notification
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * @return the type of the notification
     */
    public NotificationType getType( )
    {
        return _type;
    }

    /**
     * @param type
     *            the type of the notification
     */
    public void setType( NotificationType type )
    {
        _type = type;
    }

    /**
     * @return the id of the form of the comment
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * @param nIdForm
     *            the id of the form of the comment
     */
    public void setIdForm( int nIdForm )
    {
        _nIdForm = nIdForm;
    }

    /**
     * @return the id of the comment
     */
    public int getIdComment( )
    {
        return _nIdComment;
    }

    /**
     * @param nIdComment
     *            the id of the comment
     */
    public void setIdComment( int nIdComment )
    {
        _nIdComment = nIdComment;
    }

    /**
     * @return the text of the comment
     */
    public String getComment( )
    {
        return _strComment;
    }

    /**
     * @param strComment
     *            the text of the comment
     */
    public void setComment( String strComment )
    {
        _strComment = strComment;
    }

    /**
     * @return the login of the creator of the comment
     */
    public String getCreatorUserName( )
    {
        return _strCreatorUserName;
    }

    /**
     * @param strCreatorUserName
     *            the login of the creator of the comment
     */
    public void setCreatorUserName( String strCreatorUserName )
    {
        _strCreatorUserName = strCreatorUserName;
    }

    /**
     * @return the first day of validity of the comment
     */
    public LocalDate getStartingValidityDate( )
    {
        return _dateStartingValidityDate;
    }

    /**
     * @param dateStartingValidityDate
     *            the first day of validity of the comment
     */
    public void setStartingValidityDate( LocalDate dateStartingValidityDate )
    {
        _dateStartingValidityDate = dateStartingValidityDate;
    }

    /**
     * @return the last day of validity of the comment
     */
    public LocalDate getEndingValidityDate( )
    {
        return _dateEndingValidityDate;
    }

    /**
     * @param dateEndingValidityDate
     *            the last day of validity of the comment
     */
    public void setEndingValidityDate( LocalDate dateEndingValidityDate )
    {
        _dateEndingValidityDate = dateEndingValidityDate;
    }

    /**
     * @return the date of the notification
     */
    public Timestamp getDateNotification( )
    {
        return _dateNotification;
    }

    /**
     * @param dateNotification
     *            the date of the notification
     */
    public void setDateNotification( Timestamp dateNotification )
    {
        _dateNotification = dateNotification;
    }

    /**
     * Get the title of the form of the notification, when it was read with the notification
     *
     * @return the title of the form
     */
    public String getFormTitle( )
    {
        return _strFormTitle;
    }

    /**
     * Set the title of the form of the notification
     *
     * @param strFormTitle
     *            the title of the form
     */
    public void setFormTitle( String strFormTitle )
    {
        _strFormTitle = strFormTitle;
    }
}
