/*
 * Copyright (c) 2002-2015, Mairie de Paris
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.DisplayService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.TimeSlotService;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.WorkingDayService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

/**
 * JspBean to manage calendar slots
 * 
 * @author Laurent Payen
 *
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
    private static final String MESSAGE_SPECIFIC_WEEK_PAGE_TITLE = "appointment.specificWeek.pageTitle";
    private static final String MESSAGE_TYPICAL_WEEK_PAGE_TITLE = "appointment.typicalWeek.pageTitle";
    private static final String MESSAGE_MODIFY_SLOT_PAGE_TITLE = "appointment.modifyCalendarSlots.pageTitle";
    private static final String MESSAGE_MODIFY_TIME_SLOT_PAGE_TITLE = "appointment.modifyCalendarSlots.pageTitle";
    private static final String MESSAGE_WARNING_CHANGES_APPLY_TO_ALL = "appointment.modifyCalendarSlots.warningModifiyingEndingTime";
    private static final String MESSAGE_ERROR_TIME_END_BEFORE_TIME_START = "appointment.modifyCalendarSlots.errorTimeEndBeforeTimeStart";
    private static final String MESSAGE_SLOT_CAN_NOT_END_AFTER_DAY_OR_FORM = "appointment.message.error.slotCanNotEndAfterDayOrForm";
    private static final String MESSAGE_ERROR_APPOINTMENT_ON_SLOT = "appointment.message.error.appointmentOnSlot";
    private static final String MESSAGE_INFO_SLOT_UPDATED = "appointment.modifyCalendarSlots.messageSlotUpdated";

    // Parameters
    private static final String PARAMETER_NB_WEEKS_TO_DISPLAY = "nb_weeks_to_display";
    private static final String PARAMETER_DATE_OF_DISPLAY = "date_of_display";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ID_SLOT = "id_slot";
    private static final String PARAMETER_STARTING_DATE_TIME = "starting_date_time";
    private static final String PARAMETER_ENDING_DATE_TIME = "ending_date_time";
    private static final String PARAMETER_ID_TIME_SLOT = "id_time_slot";
    private static final String PARAMETER_DAY_OF_WEEK = "dow";
    private static final String PARAMETER_EVENTS = "events";
    private static final String PARAMETER_MIN_DURATION = "min_duration";
    private static final String PARAMETER_MIN_TIME = "min_time";
    private static final String PARAMETER_MAX_TIME = "max_time";
    private static final String PARAMETER_IS_OPEN = "is_open";
    private static final String PARAMETER_ENDING_TIME = "ending_time";
    private static final String PARAMETER_MAX_CAPACITY = "max_capacity";
    private static final String PARAMETER_ID_WEEK_DEFINITION = "id_week_definition";
    private static final String PARAMETER_SHIFT_SLOT = "shift_slot";

    // Marks
    private static final String MARK_TIME_SLOT = "timeSlot";
    private static final String MARK_SLOT = "slot";
    private static final String MARK_LIST_DATE_OF_MODIFICATION = "listDateOfModification";

    // Views
    private static final String VIEW_MANAGE_SPECIFIC_WEEK = "manageSpecificWeek";
    private static final String VIEW_MANAGE_TYPICAL_WEEK = "manageTypicalWeek";
    private static final String VIEW_MODIFY_TIME_SLOT = "viewModifyTimeSlot";
    private static final String VIEW_MODIFY_SLOT = "viewModifySlot";

    // Actions
    private static final String ACTION_DO_MODIFY_TIME_SLOT = "doModifyTimeSlot";
    private static final String ACTION_DO_MODIFY_SLOT = "doModifySlot";

    // Templates
    private static final String TEMPLATE_MANAGE_SPECIFIC_WEEK = "admin/plugins/appointment/slots/manage_specific_week.html";
    private static final String TEMPLATE_MANAGE_TYPICAL_WEEK = "admin/plugins/appointment/slots/manage_typical_week.html";
    private static final String TEMPLATE_MODIFY_TIME_SLOT = "admin/plugins/appointment/slots/modify_time_slot.html";
    private static final String TEMPLATE_MODIFY_SLOT = "admin/plugins/appointment/slots/modify_slot.html";

    // Session variable to store working values
    private static final String SESSION_ATTRIBUTE_TIME_SLOT = "appointment.session.timeSlot";
    private static final String SESSION_ATTRIBUTE_SLOT = "appointment.session.slot";
    private static final String SESSION_ATTRIBUTE_APPOINTMENT_FORM = "appointment.session.appointmentForm";

    // Porperties
    private static final String PROPERTY_NB_WEEKS_TO_DISPLAY_IN_BO = "appointment.nbWeeksToDisplayInBO";

    /**
     * Get the view of the typical week
     * 
     * @param request
     *            the request
     * @return the page
     * @throws AccessDeniedException
     */
    @View( value = VIEW_MANAGE_TYPICAL_WEEK )
    public String getViewManageTypicalWeek( HttpServletRequest request ) throws AccessDeniedException
    {
        request.getSession( ).removeAttribute( SESSION_ATTRIBUTE_TIME_SLOT );
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        String strIdWeekDefinition = request.getParameter( PARAMETER_ID_WEEK_DEFINITION );
        int nIdWeekDefinition = 0;
        if ( StringUtils.isNotEmpty( strIdWeekDefinition ) )
        {
            nIdWeekDefinition = Integer.parseInt( strIdWeekDefinition );
        }
        LocalDate dateOfApply = LocalDate.now( );
        WeekDefinition weekDefinition;
        if ( nIdWeekDefinition != 0 )
        {
            weekDefinition = WeekDefinitionService.findWeekDefinitionById( nIdWeekDefinition );
        }
        else
        {
            weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, dateOfApply );
        }
        List<WorkingDay> listWorkingDay = weekDefinition.getListWorkingDay( );
        List<String> listDayOfWeek = new ArrayList<>( WorkingDayService.getSetDayOfWeekOfAListOfWorkingDay( listWorkingDay ) );
        List<TimeSlot> listTimeSlot = TimeSlotService.getListTimeSlotOfAListOfWorkingDay( listWorkingDay, dateOfApply );
        LocalTime minStartingTime = WorkingDayService.getMinStartingTimeOfAListOfWorkingDay( listWorkingDay );
        LocalTime maxEndingTime = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( listWorkingDay );
        int nMinDuration = WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay( listWorkingDay );
        AppointmentForm appointmentForm = (AppointmentForm) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        if ( ( appointmentForm == null ) || ( nIdForm != appointmentForm.getIdForm( ) ) )
        {
            appointmentForm = FormService.buildAppointmentForm( nIdForm, 0, 0 );
        }
        Map<String, Object> model = getModel( );
        model.put( PARAMETER_DAY_OF_WEEK, listDayOfWeek );
        model.put( PARAMETER_EVENTS, listTimeSlot );
        model.put( PARAMETER_MIN_TIME, minStartingTime );
        model.put( PARAMETER_MAX_TIME, maxEndingTime );
        model.put( PARAMETER_MIN_DURATION, LocalTime.MIN.plusMinutes( nMinDuration ) );
        model.put( PARAMETER_ID_WEEK_DEFINITION, weekDefinition.getIdWeekDefinition( ) );
        model.put( MARK_LIST_DATE_OF_MODIFICATION, WeekDefinitionService.findAllDateOfWeekDefinition( nIdForm ) );
        AppointmentFormJspBean.addElementsToModelForLeftColumn( request, appointmentForm, getUser( ), getLocale( ), model );
        return getPage( MESSAGE_TYPICAL_WEEK_PAGE_TITLE, TEMPLATE_MANAGE_TYPICAL_WEEK, model );
    }

    /**
     * Get the view to modify a time slot
     * 
     * @param request
     *            the request
     * @return the page
     */
    @View( VIEW_MODIFY_TIME_SLOT )
    public String getViewModifyTimeSlot( HttpServletRequest request )
    {
        int nIdTimeSlot = Integer.parseInt( request.getParameter( PARAMETER_ID_TIME_SLOT ) );
        TimeSlot timeSlot = (TimeSlot) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_TIME_SLOT );
        if ( ( timeSlot == null ) || ( nIdTimeSlot != timeSlot.getIdTimeSlot( ) ) )
        {
            timeSlot = TimeSlotService.findTimeSlotById( nIdTimeSlot );
            request.getSession( ).setAttribute( SESSION_ATTRIBUTE_TIME_SLOT, timeSlot );
        }
        addInfo( MESSAGE_WARNING_CHANGES_APPLY_TO_ALL, getLocale( ) );
        Map<String, Object> model = getModel( );
        model.put( PARAMETER_ID_FORM, request.getParameter( PARAMETER_ID_FORM ) );
        model.put( PARAMETER_ID_WEEK_DEFINITION, request.getParameter( PARAMETER_ID_WEEK_DEFINITION ) );
        model.put( MARK_TIME_SLOT, timeSlot );
        return getPage( MESSAGE_MODIFY_TIME_SLOT_PAGE_TITLE, TEMPLATE_MODIFY_TIME_SLOT, model );
    }

    /**
     * Do modify a time slot
     * 
     * @param request
     *            the request
     * @return to the page of the typical week
     */
    @Action( ACTION_DO_MODIFY_TIME_SLOT )
    public String doModifyTimeSlot( HttpServletRequest request )
    {
        TimeSlot timeSlotFromSession = (TimeSlot) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_TIME_SLOT );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        String strIdWeekDefinition = request.getParameter( PARAMETER_ID_WEEK_DEFINITION );
        int nIdWeekDefinition = Integer.parseInt( strIdWeekDefinition );
        String strIdTimeSlot = request.getParameter( PARAMETER_ID_TIME_SLOT );
        int nIdTimeSlot = Integer.parseInt( strIdTimeSlot );
        if ( timeSlotFromSession == null || nIdTimeSlot != timeSlotFromSession.getIdTimeSlot( ) )
        {
            timeSlotFromSession = TimeSlotService.findTimeSlotById( nIdTimeSlot );
        }
        boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );
        int nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
        LocalTime endingTime = LocalTime.parse( request.getParameter( PARAMETER_ENDING_TIME ) );
        boolean endingTimeHasChanged = false;
        if ( bIsOpen != timeSlotFromSession.getIsOpen( ) )
        {
            timeSlotFromSession.setIsOpen( bIsOpen );
        }
        if ( nMaxCapacity != timeSlotFromSession.getMaxCapacity( ) )
        {
            timeSlotFromSession.setMaxCapacity( nMaxCapacity );
        }
        if ( !endingTime.equals( timeSlotFromSession.getEndingTime( ) ) )
        {
            timeSlotFromSession.setEndingTime( endingTime );
            if ( !checkEndingTimeOfTimeSlot( endingTime, timeSlotFromSession ) )
            {
                Map<String, String> additionalParameters = new HashMap<>( );
                additionalParameters.put( PARAMETER_ID_FORM, strIdForm );
                additionalParameters.put( PARAMETER_ID_WEEK_DEFINITION, strIdWeekDefinition );
                additionalParameters.put( PARAMETER_ID_TIME_SLOT, strIdTimeSlot );
                request.getSession( ).setAttribute( SESSION_ATTRIBUTE_TIME_SLOT, timeSlotFromSession );
                return redirect( request, VIEW_MODIFY_TIME_SLOT, additionalParameters );
            }
            endingTimeHasChanged = true;
        }
        TimeSlotService.updateTimeSlot( timeSlotFromSession, endingTimeHasChanged );
        addInfo( MESSAGE_INFO_SLOT_UPDATED, getLocale( ) );
        request.getSession( ).removeAttribute( SESSION_ATTRIBUTE_TIME_SLOT );
        return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_WEEK_DEFINITION, nIdWeekDefinition );
    }

    /**
     * Get the view of the specific week
     * 
     * @param request
     *            the request
     * @return the page
     * @throws AccessDeniedException
     */
    @View( defaultView = true, value = VIEW_MANAGE_SPECIFIC_WEEK )
    public String getViewManageSpecificWeek( HttpServletRequest request ) throws AccessDeniedException
    {
        request.getSession( ).removeAttribute( SESSION_ATTRIBUTE_SLOT );
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        // Get the nb weeks to display
        Display display = DisplayService.findDisplayWithFormId( nIdForm );
        int nNbWeeksToDisplay = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_DISPLAY_IN_BO, display.getNbWeeksToDisplay( ) );
        AppointmentForm appointmentForm = (AppointmentForm) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
        if ( ( appointmentForm == null ) || ( nIdForm != appointmentForm.getIdForm( ) ) )
        {
            appointmentForm = FormService.buildAppointmentForm( nIdForm, 0, 0 );
        }
        LocalDate dateOfDisplay = LocalDate.now( );
        if ( appointmentForm.getDateStartValidity( ) != null && appointmentForm.getDateStartValidity( ).toLocalDate( ).isAfter( dateOfDisplay ) )
        {
            dateOfDisplay = appointmentForm.getDateStartValidity( ).toLocalDate( );
        }
        // Get all the week definitions
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( nIdForm );
        List<WeekDefinition> listWeekDefinition = new ArrayList<WeekDefinition>( mapWeekDefinition.values( ) );
        // Get the min time of all the week definitions
        LocalTime minStartingTime = WeekDefinitionService.getMinStartingTimeOfAListOfWeekDefinition( listWeekDefinition );
        // Get the max time of all the week definitions
        LocalTime maxEndingTime = WeekDefinitionService.getMaxEndingTimeOfAListOfWeekDefinition( listWeekDefinition );
        // Get the min duration of an appointment of all the week definitions
        int nMinDuration = WeekDefinitionService.getMinDurationTimeSlotOfAListOfWeekDefinition( listWeekDefinition );
        // Get all the working days of all the week definitions
        List<String> listDayOfWeek = new ArrayList<>( WeekDefinitionService.getSetDayOfWeekOfAListOfWeekDefinition( listWeekDefinition ) );
        // Build the slots
        List<Slot> listSlot = SlotService.buildListSlot( nIdForm, mapWeekDefinition, dateOfDisplay, nNbWeeksToDisplay );
        String strDateOfDisplay = request.getParameter( PARAMETER_DATE_OF_DISPLAY );
        if ( StringUtils.isNotEmpty( strDateOfDisplay ) )
        {
            dateOfDisplay = LocalDate.parse( strDateOfDisplay );
        }
        Map<String, Object> model = getModel( );
        model.put( PARAMETER_DATE_OF_DISPLAY, dateOfDisplay );
        model.put( PARAMETER_NB_WEEKS_TO_DISPLAY, nNbWeeksToDisplay );
        model.put( PARAMETER_DAY_OF_WEEK, listDayOfWeek );
        model.put( PARAMETER_EVENTS, listSlot );
        model.put( PARAMETER_MIN_TIME, minStartingTime );
        model.put( PARAMETER_MAX_TIME, maxEndingTime );
        model.put( PARAMETER_MIN_DURATION, LocalTime.MIN.plusMinutes( nMinDuration ) );
        model.put( PARAMETER_ID_FORM, nIdForm );
        AppointmentFormJspBean.addElementsToModelForLeftColumn( request, appointmentForm, getUser( ), getLocale( ), model );
        return getPage( MESSAGE_SPECIFIC_WEEK_PAGE_TITLE, TEMPLATE_MANAGE_SPECIFIC_WEEK, model );
    }

    /**
     * Get the view to modify a slot
     * 
     * @param request
     *            the request
     * @return the page
     */
    @View( VIEW_MODIFY_SLOT )
    public String getViewModifySlot( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        Slot slot = (Slot) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_SLOT );
        if ( slot == null )
        {
            int nIdSlot = Integer.parseInt( request.getParameter( PARAMETER_ID_SLOT ) );
            // If nIdSlot == 0, the slot has not been created yet
            if ( nIdSlot == 0 )
            {
                // Need to get all the informations to create the slot
                LocalDateTime startingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_STARTING_DATE_TIME ) );
                LocalDateTime endingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_ENDING_DATE_TIME ) );
                boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );
                int nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
                slot = SlotService.buildSlot( nIdForm, startingDateTime, endingDateTime, nMaxCapacity, nMaxCapacity, nMaxCapacity, bIsOpen );
            }
            else
            {
                slot = SlotService.findSlotById( nIdSlot );
            }
            request.getSession( ).setAttribute( SESSION_ATTRIBUTE_SLOT, slot );
        }
        Map<String, Object> model = getModel( );
        model.put( PARAMETER_DATE_OF_DISPLAY, slot.getDate( ) );
        model.put( MARK_SLOT, slot );
        return getPage( MESSAGE_MODIFY_SLOT_PAGE_TITLE, TEMPLATE_MODIFY_SLOT, model );
    }

    /**
     * Do modify a slot
     * 
     * @param request
     *            the request
     * @return to the page of the specific week
     */
    @Action( ACTION_DO_MODIFY_SLOT )
    public String doModifySlot( HttpServletRequest request )
    {
        Slot slotFromSessionOrFromDb = null;
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
        int nIdSlot = Integer.parseInt( strIdSlot );
        if ( nIdSlot != 0 )
        {
            slotFromSessionOrFromDb = SlotService.findSlotById( nIdSlot );
        }
        else
        {
            slotFromSessionOrFromDb = (Slot) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_SLOT );
        }
        LocalTime endingTime = LocalTime.parse( request.getParameter( PARAMETER_ENDING_TIME ) );
        boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );
        int nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
        boolean bEndingTimeHasChanged = false;
        boolean bShiftSlot = Boolean.parseBoolean( request.getParameter( PARAMETER_SHIFT_SLOT ) );
        boolean bCapacityChanged = false;
        if ( bIsOpen != slotFromSessionOrFromDb.getIsOpen( ) )
        {
            slotFromSessionOrFromDb.setIsOpen( bIsOpen );
        }
        if ( nMaxCapacity != slotFromSessionOrFromDb.getMaxCapacity( ) )
        {
            slotFromSessionOrFromDb.setMaxCapacity( nMaxCapacity );
            bCapacityChanged = true;
        }
        if ( !endingTime.equals( slotFromSessionOrFromDb.getEndingTime( ) ) )
        {
            slotFromSessionOrFromDb.setEndingTime( endingTime );
            slotFromSessionOrFromDb.setEndingDateTime( slotFromSessionOrFromDb.getDate( ).atTime( endingTime ) );
            bEndingTimeHasChanged = true;
        }
        if ( ( bCapacityChanged || bEndingTimeHasChanged ) && !checkNoAppointmentsOnThisSlotOrOnTheSlotsImpacted( slotFromSessionOrFromDb, bShiftSlot )
                || bEndingTimeHasChanged && !checkEndingTimeOfSlot( endingTime, slotFromSessionOrFromDb ) )
        {
            request.getSession( ).setAttribute( SESSION_ATTRIBUTE_SLOT, slotFromSessionOrFromDb );
            return redirect( request, VIEW_MODIFY_SLOT, PARAMETER_ID_FORM, slotFromSessionOrFromDb.getIdForm( ) );
        }
        SlotService.updateSlot( slotFromSessionOrFromDb, bEndingTimeHasChanged, bShiftSlot );
        addInfo( MESSAGE_INFO_SLOT_UPDATED, getLocale( ) );
        request.getSession( ).removeAttribute( SESSION_ATTRIBUTE_SLOT );
        Map<String, String> additionalParameters = new HashMap<>( );
        additionalParameters.put( PARAMETER_ID_FORM, new Integer( slotFromSessionOrFromDb.getIdForm( ) ).toString( ) );
        additionalParameters.put( PARAMETER_DATE_OF_DISPLAY, slotFromSessionOrFromDb.getDate( ).toString( ) );
        return redirect( request, VIEW_MANAGE_SPECIFIC_WEEK, additionalParameters );
    }

    /**
     * Check the ending time of a time slot
     * 
     * @param endingTime
     *            the new ending time
     * @param timeSlot
     *            the time slot
     * @return false if there is an error
     */
    private boolean checkEndingTimeOfTimeSlot( LocalTime endingTime, TimeSlot timeSlot )
    {
        boolean bReturn = true;
        WorkingDay workingDay = WorkingDayService.findWorkingDayById( timeSlot.getIdWorkingDay( ) );
        if ( endingTime.isAfter( WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay ) ) )
        {
            bReturn = false;
            addError( MESSAGE_SLOT_CAN_NOT_END_AFTER_DAY_OR_FORM, getLocale( ) );
        }
        if ( endingTime.isBefore( timeSlot.getStartingTime( ) ) || endingTime.equals( timeSlot.getStartingTime( ) ) )
        {
            bReturn = false;
            addError( MESSAGE_ERROR_TIME_END_BEFORE_TIME_START, getLocale( ) );
        }
        return bReturn;
    }

    /**
     * Check the ending time of a slot
     * 
     * @param endingTime
     *            the new ending time
     * @param slot
     *            the slot
     * @return false if there is an error
     */
    private boolean checkEndingTimeOfSlot( LocalTime endingTime, Slot slot )
    {
        boolean bReturn = true;
        LocalDate dateOfSlot = slot.getDate( );
        WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( slot.getIdForm( ), dateOfSlot );
        WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( weekDefinition.getListWorkingDay( ), dateOfSlot.getDayOfWeek( ) );
        if ( endingTime.isAfter( WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay ) ) )
        {
            bReturn = false;
            addError( MESSAGE_SLOT_CAN_NOT_END_AFTER_DAY_OR_FORM, getLocale( ) );
        }
        if ( endingTime.isBefore( slot.getStartingTime( ) ) || endingTime.equals( slot.getStartingTime( ) ) )
        {
            bReturn = false;
            addError( MESSAGE_ERROR_TIME_END_BEFORE_TIME_START, getLocale( ) );
        }
        return bReturn;
    }

    /**
     * Check that there is no appointment on a slot or on the impacted slots that will be modified
     * 
     * @param slot
     *            the slot
     * @param bShiftSLot
     *            true if the next slots will be modified
     * @return false if there is an error
     */
    private boolean checkNoAppointmentsOnThisSlotOrOnTheSlotsImpacted( Slot slot, boolean bShiftSLot )
    {
        boolean bReturn = true;
        LocalDateTime endingDateTime = slot.getEndingDateTime( );
        // If all the slot will be shifted,
        // Need to check if there is no appointment until the end of the day
        if ( bShiftSLot )
        {
            endingDateTime = slot.getDate( ).atTime( LocalTime.MAX );
        }
        List<Slot> listSlotImpacted = new ArrayList<>( SlotService.findSlotsByIdFormAndDateRange( slot.getIdForm( ), slot.getStartingDateTime( ),
                endingDateTime ).values( ) );
        List<Appointment> listAppointment = AppointmentService.findListAppointmentByListSlot( listSlotImpacted );
        if ( CollectionUtils.isNotEmpty( listAppointment ) )
        {
            bReturn = false;
            addError( MESSAGE_ERROR_APPOINTMENT_ON_SLOT, getLocale( ) );
        }
        return bReturn;
    }

}
