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
package fr.paris.lutece.plugins.appointment.web;

import fr.paris.lutece.plugins.appointment.business.AppointmentFilter;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentSlotService;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * JspBean to manage calendar slots
 */
@Controller( controllerJsp = AppointmentSlotJspBean.JSP_MANAGE_APPOINTMENT_SLOTS, controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentSlotJspBean extends MVCAdminJspBean
{
    /**
     * JSP of this JSP Bean
     */
    public static final String JSP_MANAGE_APPOINTMENT_SLOTS = "ManageAppointmentSlots.jsp";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 2376721852596997810L;

    // Messages
    private static final String MESSAGE_MANAGE_SLOTS_PAGE_TITLE = "appointment.manageCalendarSlots.pageTitle";
    private static final String MESSAGE_MODIFY_SLOT_PAGE_TITLE = "appointment.modifyCalendarSlots.pageTitle";
    private static final String MESSAGE_WARNING_CHANGES_APPLY_TO_ALL = "appointment.modifyCalendarSlots.warningModifiyingEndingTime";
    private static final String MESSAGE_ERROR_DAY_HAS_APPOINTMENT = "appointment.modifyCalendarSlots.errorDayHasAppointment";
    private static final String MESSAGE_ERROR_TIME_END_BEFORE_TIME_START = "appointment.modifyCalendarSlots.errorTimeEndBeforeTimeStart";
    private static final String MESSAGE_ERROR_DURATION_MUST_BE_MULTIPLE_OF_REF_DURATION = "appointment.message.error.durationAppointmentSlotNotMultipleRef";
    private static final String MESSAGE_SLOT_CAN_NOT_END_AFTER_DAY_OR_FORM = "appointment.message.error.slotCanNotEndAfterDayOrForm";
    private static final String MESSAGE_INFO_SLOT_UPDATED = "appointment.modifyCalendarSlots.messageSlotUpdated";

    // Parameters
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ID_DAY = "id_day";
    private static final String PARAMETER_ID_SLOT = "id_slot";
    private static final String PARAMETER_NB_PLACES = "nbPlaces";
    private static final String PARAMETER_ENDING_TIME = "timeEnd";

    // Marks
    private static final String MARK_LIST_SLOTS = "listSlots";
    private static final String MARK_DAY = "day";
    private static final String MARK_SLOT = "slot";
    private static final String MARK_MIN_SLOT_DURATION = "minDuration";
    private static final String MARK_MIN_STARTING_HOUR = "minStartingHour";
    private static final String MARK_MIN_STARTING_MINUTE = "minStartingMinute";
    private static final String MARK_MAX_ENDING_HOUR = "maxEndingHour";
    private static final String MARK_MAX_ENDING_MINUTE = "maxEndingMinute";
    private static final String MARK_READ_ONLY="readonly";
    // Views
    private static final String VIEW_MANAGE_APPOINTMENT_SLOTS = "manageAppointmentSlots";
    private static final String VIEW_MODIFY_APPOINTMENT_SLOT = "viewModifySlots";

    // Actions
    private static final String ACTION_DO_CHANGE_SLOT_ENABLING = "doChangeSlotEnabling";
    private static final String ACTION_DO_MODIFY_SLOT = "doModifySlot";

    // JSP URL
    private static final String JSP_URL_MANAGE_APPOINTMENT_SLOT = "jsp/admin/plugins/appointment/" +
        JSP_MANAGE_APPOINTMENT_SLOTS;

    // Templates
    private static final String TEMPLATE_MANAGE_SLOTS = "admin/plugins/appointment/slots/manage_slots.html";
    private static final String TEMPLATE_MODIFY_SLOT = "admin/plugins/appointment/slots/modify_slot.html";
    private AppointmentSlot _slotInSession;

    /**
     * Get the page to manage slots of a form or a day
     * @param request The request
     * @return The HTML content to display or the next URL to redirect to
     */
    @View( defaultView = true, value = VIEW_MANAGE_APPOINTMENT_SLOTS )
    public String getManageSlots( HttpServletRequest request )
    {
        _slotInSession = null;

        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        List<AppointmentSlot> listSlots = null;
        Map<String, Object> model = getModel(  );

        AppointmentForm form = null;

        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );
            listSlots = AppointmentSlotHome.findByIdForm( nIdForm );
            form = AppointmentFormHome.findByPrimaryKey( nIdForm );

            boolean[] bArrayListDays = 
                {
                    form.getIsOpenMonday(  ), form.getIsOpenTuesday(  ), form.getIsOpenWednesday(  ),
                    form.getIsOpenThursday(  ), form.getIsOpenFriday(  ), form.getIsOpenSaturday(  ),
                    form.getIsOpenSunday(  ),
                };
            AppointmentDay day = AppointmentService.getService(  ).getAppointmentDayFromForm( form );
            day.setIsOpen( false );
            model.put(MARK_READ_ONLY, false );
            boolean bHasClosedDay = false;

            for ( int i = 0; i < bArrayListDays.length; i++ )
            {
                if ( !bArrayListDays[i] )
                {
                    listSlots.addAll( AppointmentService.getService(  ).computeDaySlots( day, i + 1 ) );
                    bHasClosedDay = true;
                }
            }

            if ( bHasClosedDay )
            {
                Collections.sort( listSlots );
            }
        }
        else
        {
            String strIdDay = request.getParameter( PARAMETER_ID_DAY );

            if ( StringUtils.isNotBlank( strIdDay ) && StringUtils.isNumeric( strIdDay ) )
            {
                int nIdDay = Integer.parseInt( strIdDay );
                AppointmentDay day = AppointmentDayHome.findByPrimaryKey( nIdDay );

                if ( !day.getIsOpen(  ) )
                {
                    return redirect( request,
                        AppointmentFormDayJspBean.getURLManageAppointmentFormDays( request, strIdDay ) );
                }

                listSlots = AppointmentSlotHome.findByIdDay( nIdDay );
                model.put( MARK_DAY, day );
                model.put(MARK_READ_ONLY, isReadonly ( day.getDate() ));
                form = AppointmentFormHome.findByPrimaryKey( day.getIdForm(  ) );
            }
        }

        if ( listSlots == null )
        {
            return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        }

        int nDuration = 0;
        int nMinStartingTime = 0;
        int nMaxEndingTime = 0;
        int nMinStartingHour = 0;
        int nMinStartingMinute = 0;
        int nMaxEndingHour = 0;
        int nMaxEndingMinute = 0;

        for ( AppointmentSlot slot : listSlots )
        {
            int nSlotStartingTime = ( slot.getStartingHour(  ) * 60 ) + slot.getStartingMinute(  );
            int nSlotEndingTime = ( slot.getEndingHour(  ) * 60 ) + slot.getEndingMinute(  );
            int nSlotDuration = nSlotEndingTime - nSlotStartingTime;

            if ( nSlotDuration < 0 )
            {
                nSlotDuration = -1 * nSlotDuration;
            }

            if ( ( nDuration == 0 ) || ( nSlotDuration < nDuration ) )
            {
                nDuration = nSlotDuration;
            }

            if ( ( nMinStartingTime == 0 ) || ( nSlotStartingTime < nMinStartingTime ) )
            {
                nMinStartingTime = nSlotStartingTime;
                nMinStartingHour = slot.getStartingHour(  );
                nMinStartingMinute = slot.getStartingMinute(  );
            }

            if ( nSlotEndingTime > nMaxEndingTime )
            {
                nMaxEndingTime = nSlotEndingTime;
                nMaxEndingHour = slot.getEndingHour(  );
                nMaxEndingMinute = slot.getEndingMinute(  );
            }
        }

        model.put( MARK_LIST_SLOTS, listSlots );
        model.put( MARK_MIN_SLOT_DURATION, nDuration );
        model.put( MARK_MIN_STARTING_HOUR, nMinStartingHour );
        model.put( MARK_MIN_STARTING_MINUTE, nMinStartingMinute );
        model.put( MARK_MAX_ENDING_HOUR, nMaxEndingHour );
        model.put( MARK_MAX_ENDING_MINUTE, nMaxEndingMinute );
        AppointmentFormJspBean.addElementsToModelForLeftColumn( request, form, getUser(  ), getLocale(  ), model );

        return getPage( MESSAGE_MANAGE_SLOTS_PAGE_TITLE, TEMPLATE_MANAGE_SLOTS, model );
    }

   
    /**
     * Test if date is anterior or not for an readlony
     * @param objdate
     * @return
     */
    private static boolean isReadonly(Date objdate)
    { 
    	Date dateMin = AppointmentService.getService(  ).getDateMonday( 0 );
    	return objdate == null   ? false :  objdate.before(dateMin) ;
    }
    /**
     * Do change the enabling of a slot
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_CHANGE_SLOT_ENABLING )
    public String doChangeSlotEnabling( HttpServletRequest request )
    {
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );

        if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
        {
            int nIdSlot = Integer.parseInt( strIdSlot );
            AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );

            if ( slot.getIdDay(  ) > 0 )
            {
                AppointmentDay day = AppointmentDayHome.findByPrimaryKey( slot.getIdDay(  ) );

                if ( day.getIsOpen(  ) )
                {
                    // we can only change enabling of opened days
                    slot.setIsEnabled( !slot.getIsEnabled(  ) );
                }
            }
            else
            {
                AppointmentForm form = AppointmentFormHome.findByPrimaryKey( slot.getIdForm(  ) );

                if ( form.isDayOfWeekOpened( slot.getDayOfWeek(  ) ) )
                {
                    // we can only change enabling of opened days
                    slot.setIsEnabled( !slot.getIsEnabled(  ) );
                }
            }

            AppointmentSlotHome.update( slot );

            if ( slot.getIdDay(  ) > 0 )
            {
                return redirect( request, VIEW_MANAGE_APPOINTMENT_SLOTS, PARAMETER_ID_DAY, slot.getIdDay(  ) );
            }

            return redirect( request, VIEW_MANAGE_APPOINTMENT_SLOTS, PARAMETER_ID_FORM, slot.getIdForm(  ) );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get the slot modification page
     * @param request The request
     * @return The HTML content to display
     */
    @View( VIEW_MODIFY_APPOINTMENT_SLOT )
    public String getViewModifySlot( HttpServletRequest request )
    {
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
        AppointmentSlot slot;

        if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
        {
            int nIdSlot = Integer.parseInt( strIdSlot );
            slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );
        }
        else
        {
            slot = _slotInSession;
        }

        if ( slot != null )
        {
            addInfo( MESSAGE_WARNING_CHANGES_APPLY_TO_ALL, getLocale(  ) );

            Map<String, Object> model = getModel(  );
            model.put( MARK_SLOT, slot );

            AppointmentForm appointmentForm = AppointmentFormHome.findByPrimaryKey( slot.getIdForm(  ) );
            AppointmentFormJspBean.addElementsToModelForLeftColumn( request, appointmentForm, getUser(  ),
                getLocale(  ), model );

            return getPage( MESSAGE_MODIFY_SLOT_PAGE_TITLE, TEMPLATE_MODIFY_SLOT, model );
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Do modify a slot
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MODIFY_SLOT )
    public String doModifySlot( HttpServletRequest request )
    {
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );

        if ( StringUtils.isNotEmpty( strIdSlot ) && StringUtils.isNumeric( strIdSlot ) )
        {
            int nIdSlot = Integer.parseInt( strIdSlot );
            AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( nIdSlot );

            String strNbPlaces = request.getParameter( PARAMETER_NB_PLACES );

            if ( StringUtils.isNotBlank( strNbPlaces ) && StringUtils.isNumeric( strNbPlaces ) )
            {
                int nNbPlaces = Integer.parseInt( strNbPlaces );

                if ( nNbPlaces > 0 )
                {
                    slot.setNbPlaces( nNbPlaces );
                }
            }

            String strEndingTime = request.getParameter( PARAMETER_ENDING_TIME );

            if ( StringUtils.isNotEmpty( strEndingTime ) &&
                    strEndingTime.matches( AppointmentForm.CONSTANT_TIME_REGEX ) )
            {
                String[] strSplitedEndingTime = strEndingTime.split( AppointmentForm.CONSTANT_H );
                int nEndingHour = Integer.parseInt( strSplitedEndingTime[0] );
                int nEndingMinute = Integer.parseInt( strSplitedEndingTime[1] );

                // If the slot is associated to a day, we check that no slot of this day has an appointment, as they may be removed. 
                boolean bHasEndingTimeBeenModified = ( slot.getEndingHour(  ) != nEndingHour ) ||
                    ( slot.getEndingMinute(  ) != nEndingMinute );

                if ( bHasEndingTimeBeenModified )
                {
                    int nRefDuration;

                    int nRefEndingTime;

                    if ( slot.getIdDay(  ) > 0 )
                    {
                        AppointmentDay day = AppointmentDayHome.findByPrimaryKey( slot.getIdDay(  ) );
                        AppointmentFilter filter = new AppointmentFilter(  );
                        filter.setIdForm( slot.getIdForm(  ) );
                        filter.setDateAppointment( day.getDate(  ) );

                        List<Integer> listIdAppointments = AppointmentHome.getAppointmentIdByFilter( filter );

                        if ( ( listIdAppointments != null ) && ( listIdAppointments.size(  ) > 0 ) )
                        {
                            return redirect( request,
                                AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DAY_HAS_APPOINTMENT ) );
                        }

                        nRefDuration = day.getAppointmentDuration(  );
                        nRefEndingTime = ( day.getClosingHour(  ) * 60 ) + day.getClosingMinutes(  );
                    }
                    else
                    {
                        AppointmentForm form = AppointmentFormHome.findByPrimaryKey( slot.getIdForm(  ) );
                        nRefDuration = form.getDurationAppointments(  );
                        nRefEndingTime = ( form.getClosingHour(  ) * 60 ) + form.getClosingMinutes(  );
                    }

                    int nSlotDuration = ( ( nEndingHour * 60 ) + nEndingMinute ) -
                        ( ( slot.getStartingHour(  ) * 60 ) + slot.getStartingMinute(  ) );

                    if ( ( ( nSlotDuration % nRefDuration ) != 0 ) && ( ( nRefDuration % nSlotDuration ) != 0 ) )
                    {
                        addError( MESSAGE_ERROR_DURATION_MUST_BE_MULTIPLE_OF_REF_DURATION, getLocale(  ) );
                        _slotInSession = slot;

                        return redirectView( request, VIEW_MODIFY_APPOINTMENT_SLOT );
                    }

                    if ( ( ( nEndingHour * 60 ) + nEndingMinute ) > nRefEndingTime )
                    {
                        addError( MESSAGE_SLOT_CAN_NOT_END_AFTER_DAY_OR_FORM, getLocale(  ) );
                        _slotInSession = slot;

                        return redirectView( request, VIEW_MODIFY_APPOINTMENT_SLOT );
                    }
                }

                slot.setEndingHour( nEndingHour );
                slot.setEndingMinute( nEndingMinute );

                if ( ( ( slot.getEndingHour(  ) * 60 ) + slot.getEndingMinute(  ) ) <= ( ( slot.getStartingHour(  ) * 60 ) +
                        slot.getStartingMinute(  ) ) )
                {
                    addError( MESSAGE_ERROR_TIME_END_BEFORE_TIME_START, getLocale(  ) );
                    _slotInSession = slot;

                    return redirectView( request, VIEW_MODIFY_APPOINTMENT_SLOT );
                }

                AppointmentSlotHome.update( slot );

                if ( bHasEndingTimeBeenModified )
                {
                    AppointmentSlotService.getInstance(  ).updateSlotsOfDayAfterSlotModification( slot );
                }

                addInfo( MESSAGE_INFO_SLOT_UPDATED, getLocale(  ) );

                return redirect( request,
                    ( slot.getIdDay(  ) > 0 ) ? getUrlManageSlotsByIdDay( request, slot.getIdDay(  ) )
                                              : getUrlManageSlotsByIdForm( request, slot.getIdForm(  ) ) );
            }
        }

        return redirect( request, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
    }

    /**
     * Get the URL to manage slots associated with a form
     * @param request The request
     * @param nIdForm The id of the form
     * @return The URL to manage slots
     */
    public static String getUrlManageSlotsByIdForm( HttpServletRequest request, int nIdForm )
    {
        return getUrlManageSlotsByIdForm( request, Integer.toString( nIdForm ) );
    }

    /**
     * Get the URL to manage slots associated with a form
     * @param request The request
     * @param strIdForm The id of the form
     * @return The URL to manage slots
     */
    public static String getUrlManageSlotsByIdForm( HttpServletRequest request, String strIdForm )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MANAGE_APPOINTMENT_SLOT );
        urlItem.addParameter( PARAMETER_ID_FORM, strIdForm );

        return urlItem.getUrl(  );
    }

    /**
     * Get the URL to manage slots associated with a day
     * @param request The request
     * @param nIdDay The id of the day
     * @return The URL to manage slots
     */
    public static String getUrlManageSlotsByIdDay( HttpServletRequest request, int nIdDay )
    {
        return getUrlManageSlotsByIdDay( request, Integer.toString( nIdDay ) );
    }

    /**
     * Get the URL to manage slots associated with a day
     * @param request The request
     * @param strIdDay The id of the day
     * @return The URL to manage slots
     */
    public static String getUrlManageSlotsByIdDay( HttpServletRequest request, String strIdDay )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MANAGE_APPOINTMENT_SLOT );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_APPOINTMENT_SLOTS );
        urlItem.addParameter( PARAMETER_ID_DAY, strIdDay );

        return urlItem.getUrl(  );
    }
}
