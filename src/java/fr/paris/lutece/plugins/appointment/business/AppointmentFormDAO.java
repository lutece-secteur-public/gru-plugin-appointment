/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *         and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *         and the following disclaimer in the documentation and/or other materials
 *         provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *         contributors may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
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

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.sql.DAOUtil;

import org.apache.commons.lang.StringUtils;

import java.sql.Date;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * This class provides Data Access methods for AppointmentForm objects
 */
public final class AppointmentFormDAO implements IAppointmentFormDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_form ) FROM appointment_form";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_form, title, description, time_start, time_end, duration_appointments, is_open_monday, is_open_tuesday, is_open_wednesday, is_open_thursday, is_open_friday, is_open_saturday, is_open_sunday, date_start_validity, date_end_validity, is_active, dispolay_title_fo, nb_weeks_to_display, people_per_appointment, id_workflow, is_captcha_enabled, users_can_cancel_appointments, min_days_before_app, id_calendar_template, max_appointment_mail, nb_appointment_week, reference, is_form_step, is_confirmEmail_enabled, is_mandatoryEmail_enabled, icon_form_content, icon_form_mime_type, seizure_duration FROM appointment_form ";
    private static final String SQL_QUERY_SELECTALL = SQL_QUERY_SELECT_COLUMNS + " ORDER BY title";
    private static final String SQL_QUERY_SELECTALL_ENABLED = SQL_QUERY_SELECT_COLUMNS + " WHERE is_active = 1";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_form ( id_form, title, description, time_start, time_end, duration_appointments, is_open_monday, is_open_tuesday, is_open_wednesday, is_open_thursday, is_open_friday, is_open_saturday, is_open_sunday, date_start_validity, date_end_validity, is_active, dispolay_title_fo, nb_weeks_to_display, people_per_appointment, id_workflow, is_captcha_enabled, users_can_cancel_appointments, min_days_before_app, id_calendar_template, max_appointment_mail, nb_appointment_week, reference, is_form_step, is_confirmEmail_enabled, is_mandatoryEmail_enabled, icon_form_content, icon_form_mime_type, seizure_duration ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_form WHERE id_form = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_form SET title = ?, description = ?, time_start = ?, time_end = ?, duration_appointments = ?, is_open_monday = ?, is_open_tuesday = ?, is_open_wednesday = ?, is_open_thursday = ?, is_open_friday = ?, is_open_saturday = ?, is_open_sunday = ?, date_start_validity = ?, date_end_validity = ?, is_active = ?, dispolay_title_fo = ?, nb_weeks_to_display = ?, people_per_appointment = ?, id_workflow = ?, is_captcha_enabled = ?, users_can_cancel_appointments = ?, min_days_before_app = ?, id_calendar_template = ?, max_appointment_mail = ?, nb_appointment_week = ?, reference = ?, is_form_step = ?, is_confirmEmail_enabled = ?, is_mandatoryEmail_enabled = ?, icon_form_content = ?, icon_form_mime_type = ?, seizure_duration = ? WHERE id_form = ?";
    private static final String SQL_QUERY_GET_MAX_APPOINTMENT = "select distinct count(*) nbre,form.max_appointment_mail,apmt.date_appointment," +
        " ADDDATE(apmt.date_appointment, INTERVAL (form.nb_appointment_week-1) DAY) date_max," +
        " ADDDATE(apmt.date_appointment, INTERVAL -(form.nb_appointment_week-1) DAY) date_min" +
        " from appointment_appointment apmt, appointment_day myday, appointment_form form where" + " apmt.status <>" +
        Appointment.Status.STATUS_UNRESERVED.getValeur(  ) + "  and myday.date_day=apmt.date_appointment" +
        " and myday.date_day BETWEEN ADDDATE(?,INTERVAL -(form.nb_appointment_week-1) DAY)" +
        " and ADDDATE(?,INTERVAL (form.nb_appointment_week-1) DAY)" +
        " and TRIM(UCASE(apmt.email)) = TRIM(UCASE(?)) and myday.id_form = ?" + " and form.id_form=myday.id_form" +
        " group by apmt.email, apmt.date_appointment order by apmt.date_appointment";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( AppointmentForm appointmentForm, Plugin plugin )
    {
        appointmentForm.setIdForm( newPrimaryKey( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, appointmentForm.getIdForm(  ) );
        daoUtil.setString( nIndex++, appointmentForm.getTitle(  ) );
        daoUtil.setString( nIndex++, appointmentForm.getDescription(  ) );
        daoUtil.setString( nIndex++, appointmentForm.getTimeStart(  ) );
        daoUtil.setString( nIndex++, appointmentForm.getTimeEnd(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getDurationAppointments(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenMonday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenTuesday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenWednesday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenThursday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenFriday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenSaturday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenSunday(  ) );
        daoUtil.setDate( nIndex++, appointmentForm.getDateStartValidity(  ) );
        daoUtil.setDate( nIndex++, appointmentForm.getDateEndValidity(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsActive(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getDisplayTitleFo(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getNbWeeksToDisplay(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getPeoplePerAppointment(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getIdWorkflow(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getEnableCaptcha(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getAllowUsersToCancelAppointments(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getMinDaysBeforeAppointment(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getCalendarTemplateId(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getMaxAppointments(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getWeeksLimits(  ) );
        daoUtil.setString( nIndex++, StringUtils.isEmpty( appointmentForm.getReference(  ) ) ? null : appointmentForm.getReference(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsFormStep(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getEnableConfirmEmail(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getEnableMandatoryEmail(  ) );
        daoUtil.setBytes( nIndex++, appointmentForm.getIcon(  ).getImage(  ) );
        daoUtil.setString( nIndex++, appointmentForm.getIcon(  ).getMimeType(  ) );
        daoUtil.setInt( nIndex, appointmentForm.getSeizureDuration() );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AppointmentForm load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        AppointmentForm appointmentForm = null;

        if ( daoUtil.next(  ) )
        {
            appointmentForm = getAppointmentFormData( daoUtil );
        }

        daoUtil.free(  );

        return appointmentForm;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nAppointmentFormId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nAppointmentFormId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( AppointmentForm appointmentForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nIndex = 1;

        daoUtil.setString( nIndex++, appointmentForm.getTitle(  ) );
        daoUtil.setString( nIndex++, appointmentForm.getDescription(  ) );
        daoUtil.setString( nIndex++, appointmentForm.getTimeStart(  ) );
        daoUtil.setString( nIndex++, appointmentForm.getTimeEnd(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getDurationAppointments(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenMonday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenTuesday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenWednesday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenThursday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenFriday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenSaturday(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsOpenSunday(  ) );
        daoUtil.setDate( nIndex++, appointmentForm.getDateStartValidity(  ) );
        daoUtil.setDate( nIndex++, appointmentForm.getDateEndValidity(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsActive(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getDisplayTitleFo(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getNbWeeksToDisplay(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getPeoplePerAppointment(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getIdWorkflow(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getEnableCaptcha(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getAllowUsersToCancelAppointments(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getMinDaysBeforeAppointment(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getCalendarTemplateId(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getMaxAppointments(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getWeeksLimits(  ) );
        daoUtil.setString( nIndex++, StringUtils.isEmpty( appointmentForm.getReference(  ) ) ? null : appointmentForm.getReference(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getIsFormStep(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getEnableConfirmEmail(  ) );
        daoUtil.setBoolean( nIndex++, appointmentForm.getEnableMandatoryEmail(  ) );
        daoUtil.setBytes( nIndex++, appointmentForm.getIcon(  ).getImage(  ) );
        daoUtil.setString( nIndex++, appointmentForm.getIcon(  ).getMimeType(  ) );
        daoUtil.setInt( nIndex++, appointmentForm.getSeizureDuration( ) );
        daoUtil.setInt( nIndex, appointmentForm.getIdForm(  ) );
        

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<AppointmentForm> selectAppointmentFormsList( Plugin plugin )
    {
        List<AppointmentForm> appointmentFormList = new ArrayList<AppointmentForm>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            appointmentFormList.add( getAppointmentFormData( daoUtil ) );
        }

        daoUtil.free(  );

        return appointmentFormList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentForm> selectActiveAppointmentFormsList( Plugin plugin )
    {
        List<AppointmentForm> appointmentFormList = new ArrayList<AppointmentForm>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ENABLED, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            appointmentFormList.add( getAppointmentFormData( daoUtil ) );
        }

        daoUtil.free(  );

        return appointmentFormList;
    }

    /**
     * Get data of an appointment form from a daoUtil
     * @param daoUtil The daoUtil to get data from
     * @return The appointment form with data of the current row of the daoUtil
     */
    private AppointmentForm getAppointmentFormData( DAOUtil daoUtil )
    {
        AppointmentForm appointmentForm = new AppointmentForm(  );
        ImageResource img = new ImageResource(  );

        int nIndex = 1;
        appointmentForm.setIdForm( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setTitle( daoUtil.getString( nIndex++ ) );
        appointmentForm.setDescription( daoUtil.getString( nIndex++ ) );
        appointmentForm.setTimeStart( daoUtil.getString( nIndex++ ) );
        appointmentForm.setTimeEnd( daoUtil.getString( nIndex++ ) );
        appointmentForm.setDurationAppointments( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setIsOpenMonday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenTuesday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenWednesday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenThursday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenFriday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenSaturday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setIsOpenSunday( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setDateStartValidity( daoUtil.getDate( nIndex++ ) );
        appointmentForm.setDateEndValidity( daoUtil.getDate( nIndex++ ) );
        appointmentForm.setIsActive( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setDisplayTitleFo( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setNbWeeksToDisplay( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setPeoplePerAppointment( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setIdWorkflow( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setEnableCaptcha( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setAllowUsersToCancelAppointments( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setMinDaysBeforeAppointment( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setCalendarTemplateId( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setMaxAppointmentMail( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setNbWeeksLimits( daoUtil.getInt( nIndex++ ) );
        appointmentForm.setReference( daoUtil.getString( nIndex++ ) );
        appointmentForm.setIsFormStep( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setEnableConfirmEmail( daoUtil.getBoolean( nIndex++ ) );
        appointmentForm.setEnableMandatoryEmail( daoUtil.getBoolean( nIndex++ ) );
        img.setImage( daoUtil.getBytes( nIndex++ ) );
        img.setMimeType( daoUtil.getString( nIndex++ ) );
        appointmentForm.setIcon( img );
        appointmentForm.setSeizureDuration(daoUtil.getInt( nIndex ));
        return appointmentForm;
    }

    /**
     * Get count of an appointment form from a user
     * @param String Email from user
     * @param int The Day concerned
     * @param plugin The Plugin
     * @return The sum of appointment by the user from date limited
     */
    @Override
    public List<Date> getUnavailableDatesLimitedByMail( Date startDate, Date[] endDate, int nForm, String strEmail,
        Plugin plugin )
    {
        List<Date> nReturn = new ArrayList<Date>(  );
        List<String[]> tabInfos = new ArrayList<String[]>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_GET_MAX_APPOINTMENT, plugin );

        if ( endDate != null )
        {
            daoUtil.setDate( 1, endDate[0] );
            daoUtil.setDate( 2, endDate[1] );
        }
        else
        {
            daoUtil.setDate( 1, startDate );
            daoUtil.setDate( 2, startDate );
        }

        daoUtil.setString( 3, strEmail );
        daoUtil.setInt( 4, nForm );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            String[] strInfos = new String[5];
            strInfos[0] = daoUtil.getString( 1 ); //Get the number of appointments from this user from this date
            strInfos[1] = daoUtil.getString( 2 ); //Get the max Limits email recessed
            strInfos[2] = Long.valueOf( daoUtil.getDate( 5 ).getTime(  ) ).toString(  ); //Date Limited start Off
            strInfos[3] = Long.valueOf( daoUtil.getDate( 4 ).getTime(  ) ).toString(  ); //Date Limited End Off
            strInfos[4] = Long.valueOf( daoUtil.getDate( 3 ).getTime(  ) ).toString(  ); //Valid Date Appointment
            tabInfos.add( strInfos );
        }

        daoUtil.free(  );

        if ( tabInfos.size(  ) > 0 )
        {
            for ( String[] tmpCount : tabInfos )
            {
                nReturn = computeDays( nReturn, tmpCount );
            }
        }

        return nReturn;
    }

    /**
     * @param nReturn
     * @param tmpCount
     * @throws NumberFormatException
     */
    private static List<Date> computeDays( List<Date> nReturn, String[] tmpCount )
    {
        for ( int i = 0;
                i <= getNumbersDay( new Date( Long.valueOf( tmpCount[2] ) ), new Date( Long.valueOf( tmpCount[3] ) ) );
                i++ )
        {
            GregorianCalendar startCount = new GregorianCalendar(  );
            GregorianCalendar validDate = new GregorianCalendar(  );
            validDate.setTimeInMillis( Long.valueOf( tmpCount[4] ) );
            startCount.setTimeInMillis( Long.valueOf( tmpCount[2] ) );
            startCount.add( GregorianCalendar.DATE, i );

            if ( ( Integer.valueOf( tmpCount[0] ) > 0 ) && ( Integer.valueOf( tmpCount[1] ) > 0 ) &&
                    ( Integer.valueOf( tmpCount[0] ) >= Integer.valueOf( tmpCount[1] ) ) )
            {
                nReturn.add( new Date( startCount.getTimeInMillis(  ) ) );
            }
        }

        return nReturn;
    }

    /**
     * Compute Days beetween date
     * @param nStart
     * @param nEnd
     * @return
     */
    private static int getNumbersDay( Date nStart, Date nEnd )
    {
        long timeDiff = nEnd.getTime(  ) - nStart.getTime(  );
        timeDiff = timeDiff / 1000 / ( 24 * 60 * 60 );

        return Integer.valueOf( String.valueOf( timeDiff ) );
    }
}
