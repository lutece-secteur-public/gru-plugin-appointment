/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 * DAO for appointment form messages objects
 */
public class AppointmentFormMessagesDAO implements IAppointmentFormMessagesDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_form, calendar_title, field_firstname_title, field_firstname_help, field_lastname_title, field_lastname_help, field_email_title, field_email_help, text_appointment_created, url_redirect_after_creation, text_appointment_canceled, label_button_redirection FROM appointment_form_messages WHERE id_form = ?";
    private static final String SQL_QUERY_INSERT_FORM_MESSAGE = " INSERT INTO appointment_form_messages(id_form, calendar_title, field_firstname_title, field_firstname_help, field_lastname_title, field_lastname_help, field_email_title, field_email_help, text_appointment_created, url_redirect_after_creation, text_appointment_canceled, label_button_redirection) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE_FORM_MESSAGE = "UPDATE appointment_form_messages SET calendar_title = ?, field_firstname_title = ?, field_firstname_help = ?, field_lastname_title = ?, field_lastname_help = ?, field_email_title = ?, field_email_help = ?, text_appointment_created = ?, url_redirect_after_creation = ?, text_appointment_canceled = ?, label_button_redirection = ? WHERE id_form = ?";
    private static final String SQL_QUERY_DELETE_FORM_MESSAGE = "DELETE FROM appointment_form_messages WHERE id_form = ?";

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
        daoUtil.setString( nIndex++, formMessage.getTextAppointmentCanceled( ) );
        daoUtil.setString( nIndex, formMessage.getLabelButtonRedirection(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
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
        daoUtil.setString( nIndex++, formMessage.getTextAppointmentCanceled( ) );
        daoUtil.setString( nIndex++, formMessage.getUrlRedirectAfterCreation(  ) );
        daoUtil.setString( nIndex++, formMessage.getLabelButtonRedirection(  ) );
        daoUtil.setInt( nIndex, formMessage.getIdForm(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
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
            formMessage.setTextAppointmentCanceled( daoUtil.getString( nIndex++ ) );
            formMessage.setUrlRedirectAfterCreation( daoUtil.getString( nIndex++ ) );
            formMessage.setLabelButtonRedirection( daoUtil.getString( nIndex ) );
        }
        else
        {
            formMessage = null;
        }

        daoUtil.free(  );

        return formMessage;
    }
}
