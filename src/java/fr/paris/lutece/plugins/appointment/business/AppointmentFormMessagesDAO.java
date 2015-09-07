/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.appointment.business;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 * DAO for appointment form messages objects
 */
public class AppointmentFormMessagesDAO implements IAppointmentFormMessagesDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_form, calendar_title, field_firstname_title, field_firstname_help, field_lastname_title, field_lastname_help, field_email_title, field_email_help, text_appointment_created, url_redirect_after_creation, text_appointment_canceled, label_button_redirection, no_available_slot, calendar_description, calendar_reserve_label, calendar_full_label, nb_alerts FROM appointment_form_messages WHERE id_form = ?";
    private static final String SQL_QUERY_INSERT_FORM_MESSAGE = " INSERT INTO appointment_form_messages(id_form, calendar_title, field_firstname_title, field_firstname_help, field_lastname_title, field_lastname_help, field_email_title, field_email_help, text_appointment_created, url_redirect_after_creation, text_appointment_canceled, label_button_redirection, no_available_slot, calendar_description, calendar_reserve_label, calendar_full_label, nb_alerts ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE_FORM_MESSAGE = "UPDATE appointment_form_messages SET calendar_title = ?, field_firstname_title = ?, field_firstname_help = ?, field_lastname_title = ?, field_lastname_help = ?, field_email_title = ?, field_email_help = ?, text_appointment_created = ?, url_redirect_after_creation = ?, text_appointment_canceled = ?, label_button_redirection = ?, no_available_slot = ?, calendar_description = ?, calendar_reserve_label = ?, calendar_full_label = ? , nb_alerts= ? WHERE id_form = ?";
    private static final String SQL_QUERY_DELETE_FORM_MESSAGE = "DELETE FROM appointment_form_messages WHERE id_form = ?";
    
    private static final String SQL_QUERY_FIND_REMINDER_APPOINTMENT_BY_PRIMARY_KEY = "SELECT id_form, rank, time_to_alert, email_notify, sms_notify, alert_message, alert_subject FROM appointment_reminder WHERE id_form = ? ";
    private static final String SQL_QUERY_INSERT_REMINDER_APPOINTMENT_FORM_MESSAGE = "INSERT INTO appointment_reminder(id_form, rank, time_to_alert, email_notify, sms_notify, alert_message, alert_subject) VALUES(?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE_REMINDER_APPOINTMENT_FORM_MESSAGE = "UPDATE appointment_reminder SET time_to_alert = ?, email_notify = ?, sms_notify = ?, alert_message = ?, alert_subject = ? WHERE id_form = ? ";
    private static final String SQL_QUERY_DELETE_REMINDER_APPOINTMENT_FORM_MESSAGE = "DELETE FROM appointment_reminder WHERE id_form = ? ";
    private static final String SQL_QUERY_RANK = " AND rank = ?";
    private static final String SQL_QUERY_ORDER_BY_RANK = " ORDER BY rank";
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( AppointmentFormMessages formMessage, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_FORM_MESSAGE, plugin );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, formMessage.getIdForm(  ) );
        daoUtil.setString( nIndex++, formMessage.getCalendarTitle(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldFirstNameTitle(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldFirstNameHelp(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldLastNameTitle(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldLastNameHelp(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldEmailTitle(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldEmailHelp(  ) );
        daoUtil.setString( nIndex++, formMessage.getTextAppointmentCreated(  ) );
        daoUtil.setString( nIndex++, formMessage.getUrlRedirectAfterCreation(  ) );
        daoUtil.setString( nIndex++, formMessage.getTextAppointmentCanceled(  ) );
        daoUtil.setString( nIndex++, formMessage.getLabelButtonRedirection(  ) );
        daoUtil.setString( nIndex++, formMessage.getNoAvailableSlot(  ) );
        daoUtil.setString( nIndex++, formMessage.getCalendarDescription(  ) );
        daoUtil.setString( nIndex++, formMessage.getCalendarReserveLabel(  ) );
        daoUtil.setString( nIndex++, formMessage.getCalendarFullLabel(  ) );
        daoUtil.setInt( nIndex, formMessage.getNbAlerts( ) );
        
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
        
        if ( formMessage.getListReminderAppointment( ).size() > 0 )
        {
        	insertListReminderAppointment( formMessage.getListReminderAppointment( ), plugin ) ;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( AppointmentFormMessages formMessage, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_FORM_MESSAGE, plugin );
        int nIndex = 1;
        daoUtil.setString( nIndex++, formMessage.getCalendarTitle(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldFirstNameTitle(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldFirstNameHelp(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldLastNameTitle(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldLastNameHelp(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldEmailTitle(  ) );
        daoUtil.setString( nIndex++, formMessage.getFieldEmailHelp(  ) );
        daoUtil.setString( nIndex++, formMessage.getTextAppointmentCreated(  ) );
        daoUtil.setString( nIndex++, formMessage.getUrlRedirectAfterCreation(  ) );
        daoUtil.setString( nIndex++, formMessage.getTextAppointmentCanceled(  ) );
        daoUtil.setString( nIndex++, formMessage.getLabelButtonRedirection(  ) );
        daoUtil.setString( nIndex++, formMessage.getNoAvailableSlot(  ) );
        daoUtil.setString( nIndex++, formMessage.getCalendarDescription(  ) );
        daoUtil.setString( nIndex++, formMessage.getCalendarReserveLabel(  ) );
        daoUtil.setString( nIndex++, formMessage.getCalendarFullLabel(  ) );
        daoUtil.setInt( nIndex++, formMessage.getNbAlerts( ) );
        daoUtil.setInt( nIndex, formMessage.getIdForm(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
        if ( formMessage.getListReminderAppointment( ).size() > 0 )
        {
        	storeReminderAppointment( formMessage.getListReminderAppointment( ), plugin );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nAppointmentFormId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_FORM_MESSAGE, plugin );
        daoUtil.setInt( 1, nAppointmentFormId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
        deleteListReminderAppointment( nAppointmentFormId, plugin ) ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppointmentFormMessages load( int nAppointmentFormId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nAppointmentFormId );
        daoUtil.executeQuery(  );

        AppointmentFormMessages formMessage;
        
        List <ReminderAppointment> list = loadListReminderAppointment( nAppointmentFormId , plugin );

        if ( daoUtil.next(  ) )
        {
            formMessage = new AppointmentFormMessages(  );

            int nIndex = 1;
            formMessage.setIdForm( daoUtil.getInt( nIndex++ ) );
            formMessage.setCalendarTitle( daoUtil.getString( nIndex++ ) );
            formMessage.setFieldFirstNameTitle( daoUtil.getString( nIndex++ ) );
            formMessage.setFieldFirstNameHelp( daoUtil.getString( nIndex++ ) );
            formMessage.setFieldLastNameTitle( daoUtil.getString( nIndex++ ) );
            formMessage.setFieldLastNameHelp( daoUtil.getString( nIndex++ ) );
            formMessage.setFieldEmailTitle( daoUtil.getString( nIndex++ ) );
            formMessage.setFieldEmailHelp( daoUtil.getString( nIndex++ ) );
            formMessage.setTextAppointmentCreated( daoUtil.getString( nIndex++ ) );
            formMessage.setUrlRedirectAfterCreation( daoUtil.getString( nIndex++ ) );
            formMessage.setTextAppointmentCanceled( daoUtil.getString( nIndex++ ) );
            formMessage.setLabelButtonRedirection( daoUtil.getString( nIndex++ ) );
            formMessage.setNoAvailableSlot( daoUtil.getString( nIndex++ ) );
            formMessage.setCalendarDescription( daoUtil.getString( nIndex++ ) );
            formMessage.setCalendarReserveLabel( daoUtil.getString( nIndex++ ) );
            formMessage.setCalendarFullLabel( daoUtil.getString( nIndex++ ) );
            formMessage.setNbAlerts( daoUtil.getInt( nIndex ) );
            
            if( list!=null )
            {
            	formMessage.setListReminderAppointment( list );
            }
        }
        else
        {
            formMessage = null;
        }
        daoUtil.free(  );

        return formMessage;
    }
    /**
     * Load Reminder Appointment
     * @param nAppointmentFormId id appointment form
     * @param rank the rank reminder
     * @param plugin the plugin
     * @return Reminder Appointment
     */
    private ReminderAppointment loadReminderAppointment( int nAppointmentFormId , int rank, Plugin plugin )
    {

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_REMINDER_APPOINTMENT_BY_PRIMARY_KEY + SQL_QUERY_RANK , plugin );
        daoUtil.setInt( 1, nAppointmentFormId );
        daoUtil.setInt( 2, rank );
        daoUtil.executeQuery(  );

        ReminderAppointment reminderAppointment;
        if ( daoUtil.next(  ) )
        {
        	reminderAppointment = new ReminderAppointment(  );

            int nIndex = 1;
           
            reminderAppointment.setIdForm( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setRank( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setTimeToAlert( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setEmailNotify( daoUtil.getBoolean( nIndex++ ) );
            reminderAppointment.setSmsNotify( daoUtil.getBoolean( nIndex++ ) );
            reminderAppointment.setAlertMessage( daoUtil.getString( nIndex++ ) );
            reminderAppointment.setAlertSubject( daoUtil.getString( nIndex ) );
        }
        else
        {
        	reminderAppointment = null;
        }
        daoUtil.free(  );

        return reminderAppointment;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public List <ReminderAppointment> loadListReminderAppointment( int nAppointmentFormId, Plugin plugin )
    {

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_REMINDER_APPOINTMENT_BY_PRIMARY_KEY + SQL_QUERY_ORDER_BY_RANK, plugin );
        daoUtil.setInt( 1, nAppointmentFormId );
        daoUtil.executeQuery(  );

        ReminderAppointment reminderAppointment;
        List <ReminderAppointment> list = new ArrayList <ReminderAppointment>( );
        while ( daoUtil.next(  ) )
        {
        	reminderAppointment = new ReminderAppointment(  );

            int nIndex = 1;
           
            reminderAppointment.setIdForm( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setRank( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setTimeToAlert( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setEmailNotify( daoUtil.getBoolean( nIndex++ ) );
            reminderAppointment.setSmsNotify( daoUtil.getBoolean( nIndex++ ) );
            reminderAppointment.setAlertMessage( daoUtil.getString( nIndex++ ) );
            reminderAppointment.setAlertSubject( daoUtil.getString( nIndex ) );
            list.add( reminderAppointment );
        }
        daoUtil.free(  );

        return list;
    }
    /**
     * Insert Reminder Appointment
     * @param reminderAppointment the reminder appointment
     * @param plugin the plugin
     */
    private void insertReminderAppointment( ReminderAppointment reminderAppointment , Plugin plugin )
    {
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_REMINDER_APPOINTMENT_FORM_MESSAGE, plugin );
	        int nIndex = 1;
	       
	        daoUtil.setInt( nIndex++, reminderAppointment.getIdForm(  ) );
	        daoUtil.setInt( nIndex++, reminderAppointment.getRank( ) );
	        daoUtil.setInt( nIndex++, reminderAppointment.getTimeToAlert( ) );
	        daoUtil.setBoolean( nIndex++, reminderAppointment.isEmailNotify( ) );
	        daoUtil.setBoolean( nIndex++, reminderAppointment.isSmsNotify( ) );
	        daoUtil.setString( nIndex++ , reminderAppointment.getAlertMessage( ) );
	        daoUtil.setString( nIndex , reminderAppointment.getAlertSubject( ) );
	        daoUtil.executeUpdate(  );
	        daoUtil.free(  );
    }
    /**
     * Insert List Reminder Appointment
     * @param listReminderAppointment List Reminder
     * @param plugin the plugin
     */
    private void insertListReminderAppointment( List <ReminderAppointment> listReminderAppointment , Plugin plugin )
    {
    	for ( ReminderAppointment reminderAppointment : listReminderAppointment )
        {
    		insertReminderAppointment ( reminderAppointment, plugin ) ;
        }
    }
    /**
     * Store Reminder Appointment
     * @param listReminderAppointment list reminder appointment
     * @param plugin the plugin
     */
    private void storeReminderAppointment( List <ReminderAppointment> listReminderAppointment , Plugin plugin )
    {
        
        for ( ReminderAppointment reminderAppointment : listReminderAppointment )
        {
        	if ( loadReminderAppointment ( reminderAppointment.getIdForm( ) , reminderAppointment.getRank( ) , plugin ) == null )
        	{
        		insertReminderAppointment( reminderAppointment , plugin ) ;
        	}
        	else
        	{
        		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_REMINDER_APPOINTMENT_FORM_MESSAGE + SQL_QUERY_RANK, plugin );
		        int nIndex = 1;
		        
		        daoUtil.setInt( nIndex++, reminderAppointment.getTimeToAlert( ) );
		        daoUtil.setBoolean( nIndex++, reminderAppointment.isEmailNotify( ) );
		        daoUtil.setBoolean( nIndex++, reminderAppointment.isSmsNotify( ) );
		        daoUtil.setString( nIndex++ , reminderAppointment.getAlertMessage( ) );
		        daoUtil.setString( nIndex++ , reminderAppointment.getAlertSubject( ) );
		        daoUtil.setInt( nIndex++, reminderAppointment.getIdForm(  ) );
		        daoUtil.setInt( nIndex, reminderAppointment.getRank(  ) );
		
		        daoUtil.executeUpdate(  );
		        daoUtil.free(  );
        	}
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteReminderAppointment( int nAppointmentFormId , int rank, boolean b,  Plugin plugin )
    {
    	DAOUtil daoUtil = null ;
    	if ( b )
    	{
    		daoUtil = new DAOUtil( SQL_QUERY_DELETE_REMINDER_APPOINTMENT_FORM_MESSAGE , plugin );
    		daoUtil.setInt( 1, nAppointmentFormId );
    	}
    	else
    	{
    		daoUtil = new DAOUtil( SQL_QUERY_DELETE_REMINDER_APPOINTMENT_FORM_MESSAGE + SQL_QUERY_RANK, plugin );
    		daoUtil.setInt( 1, nAppointmentFormId );
    	    daoUtil.setInt( 2, rank );
    	}

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
    /**
     * Delete List Reminder Appointment
     * @param nAppointmentFormId id appointment form
     * @param plugin the plugin
     */
    private void deleteListReminderAppointment( int nAppointmentFormId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_REMINDER_APPOINTMENT_FORM_MESSAGE, plugin );
        daoUtil.setInt( 1, nAppointmentFormId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
    
}
