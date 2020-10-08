
/*
 * Copyright (c) 2002-2020, Mairie de Paris
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
import java.util.Date;

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
    private Date _dateStartingValidityDate;
    @NotNull( message = "#i18n{portal.validation.message.notEmpty}" )
    private Date _dateEndingValidityDate;
    @NotEmpty( message = "#i18n{appointment.validation.comment.Comment.notEmpty}" )
    private String _strComment;
    
    private Date _dateCreationDate;
    
    /**
     * The User who created the comment (if not created by the user himself)
     */
    private String _strCreatorUserConnectId;

    /**
     * Returns the Id
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * @param nId The Id
     */ 
    public void setId( int nId )
    {
        _nId = nId;
    }
    
    /**
     * Returns the IdForm
     * @return The IdForm
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Sets the IdForm
     * @param nIdForm The IdForm
     */ 
    public void setIdForm( int nIdForm )
    {
        _nIdForm = nIdForm;
    }
    
    /**
     * Returns the StartingValidityDate
     * @return The StartingValidityDate
     */
    public Date getStartingValidityDate( )
    {
        return _dateStartingValidityDate;
    }

    /**
     * Sets the StartingValidityDate
     * @param dateStartingValidityDate The StartingValidityDate
     */ 
    public void setStartingValidityDate( Date dateStartingValidityDate )
    {
        _dateStartingValidityDate = dateStartingValidityDate;
    }
    
    /**
     * Returns the EndingValidityDate
     * @return The EndingValidityDate
     */
    public Date getEndingValidityDate( )
    {
        return _dateEndingValidityDate;
    }

    /**
     * Sets the EndingValidityDate
     * @param dateEndingValidityDate The EndingValidityDate
     */ 
    public void setEndingValidityDate( Date dateEndingValidityDate )
    {
        _dateEndingValidityDate = dateEndingValidityDate;
    }
    
    /**
     * Returns the Comment
     * @return The Comment
     */
    public String getComment( )
    {
        return _strComment;
    }

    /**
     * Sets the Comment
     * @param strComment The Comment
     */ 
    public void setComment( String strComment )
    {
        _strComment = strComment;
    }
    
    /**
     * Returns the CreationDate
     * @return The CreationDate
     */
    public Date getCreationDate( )
    {
    	return _dateCreationDate;
    }
    
    /**
     * Sets the CreationDate
     * @param dateCreationDate The CreationDate
     */ 
    public void setCreationDate( Date dateCreationDate ) 
    {
    	_dateCreationDate = dateCreationDate;
    }
    
    /**UserConnectId
     * @return The _strCreatorUserConnectId
     */
    public String getCreatorUserName( )
    {
    	return _strCreatorUserConnectId;
    }
    
    /**
     * Sets the strCreatorUserConnectId
     * @param creatorUserConnectId The creatorUserConnectId
     */ 
    public void setCreatorUserName( String creatorUserConnectId ) 
    {
    	_strCreatorUserConnectId = creatorUserConnectId;
    }

}
