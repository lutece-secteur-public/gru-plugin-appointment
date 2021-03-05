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
package fr.paris.lutece.plugins.appointment.service;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.business.comment.Comment;
import fr.paris.lutece.plugins.appointment.business.comment.CommentHome;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationConfig;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationConfig.NotificationType;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.template.AppTemplateService;

public class CommentService
{
	 // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFY_MAIL = "admin/plugins/appointment/notification/task_notify_comment_mail.html";
    
    // MARKS
    private static final String MARK_MESSAGE = "message";
    private static final String MARK_COMMENT = "comment";
    private static final String MARK_CREATION_DATE = "creation_date";
    private static final String MARK_FORM_ID = "form_id";
    private static final String MARK_FORM_TITLE = "form_title";
    private static final String MARK_CREATOR_USER_NAME = "creator_user_name";
    private static final String MARK_DATE_END_VALIDITY = "date_end_validity";
    private static final String MARK_TIME_END_VALIDITY = "time_end_validity";
    private static final String MARK_DATE_START_VALIDITY = "date_start_validity";
    private static final String MARK_TIME_START_VALIDITY = "time_start_validity";
    
    private CommentService( )
    {

    }

    /**
     * fin List Comments between two dates
     * 
     * @param startingDate
     *            the date start
     * @param endingDate
     *            the date end
     * @returnThe list of the comments
     */
    public static List<Comment> finListComments( Date startingDate, Date endingDate, int nIdForm )
    {

        return CommentHome.selectCommentsList( startingDate, endingDate, nIdForm );
    }

    /**
     * fin List Comments between two dates
     * 
     * @param startingDate
     *            the date start
     * @param endingDate
     *            the date end
     * @returnThe list of the comments
     */
    public static List<Comment> findListCommentsInclusive( Date startingDate, Date endingDate, int nIdForm )
    {

        return CommentHome.selectCommentsListInclusive( startingDate, endingDate, nIdForm );
    }
    
    /**
     * Create an instance of the comment class and notify the mailing list
     * 
     * @param comment
     *            The instance of the Comment which contains the informations to store
      * @param idMailingList
     * 			the mailing list id to notify
     * @return The instance of comment which has been created with its primary key.
     */
    public static Comment createAndNotifyMailingList( Comment comment, int idMailingList, Locale locale  )
    {
    	CommentHome.create( comment  );
    	sendNotification( comment,  idMailingList, NotificationType.CREATE, locale );
    	
        return comment;
    }

    /**
     * Update of the comment which is specified in parameter and notify the mailing list
     * 
     * @param comment
     *            The instance of the Comment which contains the data to store
      * @param idMailingList
     * 			the mailing list id to notify
     * 
     * @return The instance of the comment which has been updated
     */
    public static Comment updateAndNotifyMailingList( Comment comment,int idMailingList, Locale locale )
    {
    	CommentHome.update( comment  );
    	sendNotification( comment,  idMailingList, NotificationType.UPDATE, locale );

        return comment;
    }

    /**
     * Remove the comment whose identifier is specified in parameter and notify the mailing list
     * 
     * @param nKey
     *            The comment Id
      * @param idMailingList
     * 			the mailing list id to notify
     */
    public static void removeAndNotifyMailingList( int nKey,int idMailingList, Locale locale )
    {
    	Comment comment= CommentHome.findByPrimaryKey(nKey);
    	sendNotification( comment,  idMailingList, NotificationType.DELETE, locale );

    	CommentHome.remove( nKey );
    }
    
    /**
     * 
     * @param comment
     * @param idMailingList
     * 			the mailing list id to notify
     * @param locale
     */
    public static void sendNotification( Comment comment,  int idMailingList, NotificationType type, Locale locale )
    {
    	
    	CommentNotificationConfig config = CommentNotificationHome.loadCommentNotificationConfigByType( type.name() );

    	if( config!= null && idMailingList != -1 )
    	{
    		
        	String strSenderEmail = MailService.getNoReplyEmail( );
            Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( idMailingList );           
            Map<String, Object> model = fillModel( comment, config );

            String strContent = AppTemplateService.getTemplateFromStringFtl( AppTemplateService
                    .getTemplate(  TEMPLATE_TASK_NOTIFY_MAIL, locale, model ).getHtml( ), locale,
                    model ).getHtml( );            
            // Send Mail
            for ( Recipient recipient : listRecipients )
            {
                // Build the mail message
                MailService.sendMailHtml( recipient.getEmail( ), config.getSenderName( ), strSenderEmail, config.getSubject( ), strContent );
            }
    	}
                 
    }
    /**
     * Get a model to generate email content for a given comment and a given task.

     * @param comment
     *            The comment 
     * @param locale
     *            The locale
     * @return The model with data
     */
    private static Map<String, Object> fillModel( Comment comment, CommentNotificationConfig config )
    {
        Map<String, Object> model = new HashMap<>( );
        Form form = FormService.findFormLightByPrimaryKey(  comment.getIdForm() );

        AdminUser user= AdminUserHome.findUserByLogin( comment.getCreatorUserName( ) );
        StringBuilder builder= new StringBuilder( );
        builder.append(user.getFirstName( )).append( StringUtils.SPACE ).append( user.getLastName( ) );
        model.put( MARK_COMMENT, comment.getComment( ));
        model.put( MARK_CREATION_DATE, comment.getCreationDate( ).format( Utilities.getFormatter( ) ) );
        model.put( MARK_FORM_ID, form.getIdForm( ));
        model.put( MARK_FORM_TITLE, form.getTitle( ) );
        model.put( MARK_CREATOR_USER_NAME,  builder.toString( ) );
        model.put( MARK_DATE_END_VALIDITY,comment.getEndingValidityDate( ).format( Utilities.getFormatter( ) ));        
        model.put( MARK_TIME_END_VALIDITY,comment.getEndingValidityTime( ) );        
        model.put( MARK_DATE_START_VALIDITY, comment.getStartingValidityDate( ).format( Utilities.getFormatter( ) ) );
        model.put( MARK_TIME_START_VALIDITY, comment.getStartingValidityTime() );
        model.put( MARK_MESSAGE, config.getMessage( ) );

        
        return model;
    }
    
   
}
