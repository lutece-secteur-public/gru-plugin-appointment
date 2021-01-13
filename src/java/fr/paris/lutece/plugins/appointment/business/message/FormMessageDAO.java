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
package fr.paris.lutece.plugins.appointment.business.message;

import java.sql.Statement;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form Message objects
 * 
 * @author Laurent Payen
 *
 */
public final class FormMessageDAO implements IFormMessageDAO
{

    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_form_message( calendar_title, field_firstname_title, field_firstname_help, field_lastname_title, field_lastname_help, field_email_title, field_email_help, field_confirmationEmail_title, field_confirmationEmail_help, text_appointment_created, url_redirect_after_creation, text_appointment_canceled, label_button_redirection, no_available_slot, calendar_description, calendar_reserve_label, calendar_full_label, id_form) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_form_message SET calendar_title = ?, field_firstname_title = ?, field_firstname_help = ?, field_lastname_title = ?, field_lastname_help = ?, field_email_title = ?, field_email_help = ?, field_confirmationEmail_title = ?, field_confirmationEmail_help = ?, text_appointment_created = ?, url_redirect_after_creation = ?, text_appointment_canceled = ?, label_button_redirection = ?, no_available_slot = ?, calendar_description = ?, calendar_reserve_label = ?, calendar_full_label = ?, id_form = ? WHERE id_form_message = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_form_message WHERE id_form_message = ?";
    private static final String SQL_QUERY_DELETE_BY_ID_FORM = "DELETE FROM appointment_form_message WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_form_message, calendar_title, field_firstname_title, field_firstname_help, field_lastname_title, field_lastname_help, field_email_title, field_email_help,field_confirmationEmail_title, field_confirmationEmail_help, text_appointment_created, url_redirect_after_creation, text_appointment_canceled, label_button_redirection, no_available_slot, calendar_description, calendar_reserve_label, calendar_full_label, id_form FROM appointment_form_message";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form_message = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";

    @Override
    public void insert( FormMessage formMessage, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, formMessage, plugin, true ) )
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                formMessage.setIdFormMessage( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    @Override
    public void update( FormMessage formMessage, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, formMessage, plugin, false ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdFormMessage, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdFormMessage );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void deleteByIdForm( int nIdForm, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public FormMessage select( int nIdFormMessage, Plugin plugin )
    {
        FormMessage formMessage = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdFormMessage );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                formMessage = buildFormMessage( daoUtil );
            }
        }
        return formMessage;
    }

    @Override
    public FormMessage findByIdForm( int nIdForm, Plugin plugin )
    {
        FormMessage formMessage = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                formMessage = buildFormMessage( daoUtil );
            }
        }
        return formMessage;
    }

    /**
     * Build a Form Message business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new Form Message with all its attributes assigned
     */
    private FormMessage buildFormMessage( DAOUtil daoUtil )
    {
        int nIndex = 1;
        FormMessage formMessage = new FormMessage( );
        formMessage.setIdFormMessage( daoUtil.getInt( nIndex++ ) );
        formMessage.setCalendarTitle( daoUtil.getString( nIndex++ ) );
        formMessage.setFieldFirstNameTitle( daoUtil.getString( nIndex++ ) );
        formMessage.setFieldFirstNameHelp( daoUtil.getString( nIndex++ ) );
        formMessage.setFieldLastNameTitle( daoUtil.getString( nIndex++ ) );
        formMessage.setFieldLastNameHelp( daoUtil.getString( nIndex++ ) );
        formMessage.setFieldEmailTitle( daoUtil.getString( nIndex++ ) );
        formMessage.setFieldEmailHelp( daoUtil.getString( nIndex++ ) );
        formMessage.setFieldConfirmationEmail( daoUtil.getString( nIndex++ ) );
        formMessage.setFieldConfirmationEmailHelp( daoUtil.getString( nIndex++ ) );
        formMessage.setTextAppointmentCreated( daoUtil.getString( nIndex++ ) );
        formMessage.setUrlRedirectAfterCreation( daoUtil.getString( nIndex++ ) );
        formMessage.setTextAppointmentCanceled( daoUtil.getString( nIndex++ ) );
        formMessage.setLabelButtonRedirection( daoUtil.getString( nIndex++ ) );
        formMessage.setNoAvailableSlot( daoUtil.getString( nIndex++ ) );
        formMessage.setCalendarDescription( daoUtil.getString( nIndex++ ) );
        formMessage.setCalendarReserveLabel( daoUtil.getString( nIndex++ ) );
        formMessage.setCalendarFullLabel( daoUtil.getString( nIndex++ ) );
        formMessage.setIdForm( daoUtil.getInt( nIndex ) );
        return formMessage;
    }

    /**
     * Build a daoUtil object with the form message
     * 
     * @param query
     *            the query
     * @param formMessage
     *            the form message
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, FormMessage formMessage, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = null;
        if ( isInsert )
        {
            daoUtil = new DAOUtil( query, Statement.RETURN_GENERATED_KEYS, plugin );
        }
        else
        {
            daoUtil = new DAOUtil( query, plugin );
        }
        daoUtil.setString( nIndex++, formMessage.getCalendarTitle( ) );
        daoUtil.setString( nIndex++, formMessage.getFieldFirstNameTitle( ) );
        daoUtil.setString( nIndex++, formMessage.getFieldFirstNameHelp( ) );
        daoUtil.setString( nIndex++, formMessage.getFieldLastNameTitle( ) );
        daoUtil.setString( nIndex++, formMessage.getFieldLastNameHelp( ) );
        daoUtil.setString( nIndex++, formMessage.getFieldEmailTitle( ) );
        daoUtil.setString( nIndex++, formMessage.getFieldEmailHelp( ) );
        daoUtil.setString( nIndex++, formMessage.getFieldConfirmationEmail( ) );
        daoUtil.setString( nIndex++, formMessage.getFieldConfirmationEmailHelp( ) );
        daoUtil.setString( nIndex++, formMessage.getTextAppointmentCreated( ) );
        daoUtil.setString( nIndex++, formMessage.getUrlRedirectAfterCreation( ) );
        daoUtil.setString( nIndex++, formMessage.getTextAppointmentCanceled( ) );
        daoUtil.setString( nIndex++, formMessage.getLabelButtonRedirection( ) );
        daoUtil.setString( nIndex++, formMessage.getNoAvailableSlot( ) );
        daoUtil.setString( nIndex++, formMessage.getCalendarDescription( ) );
        daoUtil.setString( nIndex++, formMessage.getCalendarReserveLabel( ) );
        daoUtil.setString( nIndex++, formMessage.getCalendarFullLabel( ) );
        daoUtil.setInt( nIndex++, formMessage.getIdForm( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, formMessage.getIdFormMessage( ) );
        }
        return daoUtil;
    }
}
