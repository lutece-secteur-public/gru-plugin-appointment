/*
 * Copyright (c) 2002-2020, City of Paris
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

/**
 *
 * CommentNotificationConfig
 *
 */
public class CommentNotificationConfig implements Serializable
{
    /**
	 * Variables declarations 
	 */
	private static final long serialVersionUID = -6494118423858717180L;
	
	private NotificationType _type;
	private String _strSubject;
    private String _strMessage;
    private String _strSenderName;

    /**
     * Enum to specify if the notification type of the comment
     */
    public enum NotificationType
    {
        CREATE,
        UPDATE,
        DELETE
        
        
    }
    

    /**
     * Returns the Type
     * @return The Type
     */ 
     public NotificationType getType()
     {
         return _type;
     }
 
    /**
     * Sets the Type
     * @param type The Type
     */ 
     public void setType( NotificationType type )
     {
    	 _type = type;
     }
    
    /**
     *
     * @return the subject of the message
     */
    public String getSubject( )
    {
        return _strSubject;
    }

    /**
     * set the subject of the message
     * 
     * @param subject
     *            the subject of the message
     */
    public void setSubject( String subject )
    {
        _strSubject = subject;
    }

    /**
     *
     * @return the message of the notification
     */
    public String getMessage( )
    {
        return _strMessage;
    }

    /**
     * set the message of the notification
     * 
     * @param message
     *            the message of the notifictaion
     */
    public void setMessage( String message )
    {
        _strMessage = message;
    }

    /**
     *
     * @return the sender name
     */
    public String getSenderName( )
    {
        return _strSenderName;
    }

    /**
     * set the sender name
     * 
     * @param senderName
     *            the sender name
     */
    public void setSenderName( String senderName )
    {
        _strSenderName = senderName;
    }
}
