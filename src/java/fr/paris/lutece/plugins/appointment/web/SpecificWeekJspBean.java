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
package fr.paris.lutece.plugins.appointment.web;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.comment.CommentHome;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.planning.ClosingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Period;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.log.LogUtilities;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.ClosingDayService;
import fr.paris.lutece.plugins.appointment.service.DisplayService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.ReservationRuleService;
import fr.paris.lutece.plugins.appointment.service.SlotSafeService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.WorkingDayService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

/**
 * JspBean to manage calendar slots
 * 
 * @author Laurent Payen
 *
 */
@Controller( controllerJsp = SpecificWeekJspBean.JSP_MANAGE_APPOINTMENT_SLOTS, controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class SpecificWeekJspBean extends AbstractAppointmentFormAndSlotJspBean
{
    /**
     * JSP of this JSP Bean
     */
    public static final String JSP_MANAGE_APPOINTMENT_SLOTS = "ManageSpecificWeek.jsp";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 2376721852596997810L;

    // Messages
    private static final String MESSAGE_SPECIFIC_WEEK_PAGE_TITLE = "appointment.specificWeek.pageTitle";
    private static final String MESSAGE_MODIFY_SLOT_PAGE_TITLE = "appointment.modifyCalendarSlots.pageTitle";
    private static final String MESSAGE_ERROR_TIME_END_BEFORE_TIME_START = "appointment.modifyCalendarSlots.errorTimeEndBeforeTimeStart";
    private static final String MESSAGE_SLOT_CAN_NOT_END_AFTER_DAY_OR_FORM = "appointment.message.error.slotCanNotEndAfterDayOrForm";
    private static final String MESSAGE_ERROR_APPOINTMENT_ON_SLOT = "appointment.message.error.appointmentOnSlot";
    private static final String MESSAGE_INFO_SLOT_UPDATED = "appointment.modifyCalendarSlots.messageSlotUpdated";
    private static final String MESSAGE_INFO_VALIDATED_APPOINTMENTS_IMPACTED = "appointment.modifyCalendarSlots.messageValidatedAppointmentsImpacted";
    private static final String MESSAGE_INFO_SURBOOKING = "appointment.modifyCalendarSlots.messageSurbooking";
    private static final String MESSAGE_INFO_MULTI_SURBOOKING = "appointment.modifyCalendarMultiSlots.messageSurbooking";

    private static final String MESSAGE_INFO_OVERLOAD = "appointment.modifyCalendarSlots.messageOverload";
    private static final String MESSAGE_ERROR_PARSING_JSON = "appointment.message.error.parsing.json";

    // Parameters
    private static final String PARAMETER_ENDING_DATE_TO_APPLY = "ending_date_apply";
    private static final String PARAMETER_STARTING_DATE_TO_APPLY = "starting_date_apply";
    private static final String PARAMETER_ENDING_DATE_OF_DISPLAY = "ending_date_of_display";
    private static final String PARAMETER_DATE_OF_DISPLAY = "date_of_display";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ID_SLOT = "id_slot";
    private static final String PARAMETER_STARTING_DATE_TIME = "starting_date_time";
    private static final String PARAMETER_ENDING_DATE_TIME = "ending_date_time";
    private static final String PARAMETER_EVENTS_COMMENTS = "comment_events";
    private static final String PARAMETER_DAY_OF_WEEK = "dow";
    private static final String PARAMETER_EVENTS = "events";
    private static final String PARAMETER_MIN_DURATION = "min_duration";
    private static final String PARAMETER_MIN_TIME = "min_time";
    private static final String PARAMETER_MAX_TIME = "max_time";
    private static final String PARAMETER_IS_OPEN = "is_open";
    private static final String PARAMETER_IS_SPECIFIC = "is_specific";
    private static final String PARAMETER_ENDING_TIME = "ending_time";
    private static final String PARAMETER_MAX_CAPACITY = "max_capacity";


    private static final String PARAMETER_SHIFT_SLOT = "shift_slot";
    private static final String PARAMETER_DATA = "slotsData";
    private static final String PARAMETER_IDENTICAL = "identical";

    // Marks
    private static final String MARK_SLOT = "slot";
    private static final String MARK_LOCALE_TINY = "locale";
    // Views
    private static final String VIEW_MANAGE_SPECIFIC_WEEK = "manageSpecificWeek";
    private static final String VIEW_MODIFY_SLOT = "viewModifySlot";

    // Actions
    private static final String ACTION_DO_MODIFY_SLOT = "doModifySlot";
    private static final String ACTION_DO_MODIFY_LIST_SLOT = "doModifyListSlot";

    // Templates
    private static final String TEMPLATE_MANAGE_SPECIFIC_WEEK = "admin/plugins/appointment/slots/manage_specific_week.html";
    private static final String TEMPLATE_MODIFY_SLOT = "admin/plugins/appointment/slots/modify_slot.html";

    // Porperties
    private static final String PROPERTY_NB_WEEKS_TO_DISPLAY_IN_BO = "appointment.nbWeeksToDisplayInBO";

    // Infos    
    private AppointmentFormDTO _appointmentForm;
    private Slot _slot;
    /**
     * Get the view of the specific week
     * 
     * @param request
     *            the request
     * @return the page
     */
    @View( defaultView = true, value = VIEW_MANAGE_SPECIFIC_WEEK )
    public String getViewManageSpecificWeek( HttpServletRequest request )
    {
    	_slot = null;
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        Form form = FormService.findFormLightByPrimaryKey( nIdForm );
        // Get the nb weeks to display
        Display display = DisplayService.findDisplayWithFormId( nIdForm );
        int nNbWeeksToDisplay = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_DISPLAY_IN_BO, display.getNbWeeksToDisplay( ) );
        if ( ( _appointmentForm == null ) || ( nIdForm != _appointmentForm.getIdForm( ) ) )
        {
            _appointmentForm = FormService.buildAppointmentForm( nIdForm, 0 );
        }
        LocalDate dateOfDisplay = LocalDate.now( );
        if ( _appointmentForm.getDateStartValidity( ) != null && _appointmentForm.getDateStartValidity( ).toLocalDate( ).isAfter( dateOfDisplay ) )
        {
            dateOfDisplay = _appointmentForm.getDateStartValidity( ).toLocalDate( );
        }
        LocalDate endingDateOfDisplay = LocalDate.now( ).plusWeeks( nNbWeeksToDisplay );
        LocalDate endingValidityDate = form.getEndingValidityDate( );
        if ( endingValidityDate != null && endingDateOfDisplay.isAfter( endingValidityDate ) )
        {
            endingDateOfDisplay = endingValidityDate;
        }
        // Get all the week definitions
        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        Map<WeekDefinition, ReservationRule> mapReservationRule = ReservationRuleService.findAllReservationRule( nIdForm, listWeekDefinition );
        List<ReservationRule> listReservationRules = new ArrayList<> (mapReservationRule.values( ));

        // Get the min time of all the week definitions
        LocalTime minStartingTime = WeekDefinitionService.getMinStartingTimeOfAListOfWeekDefinition( listReservationRules );
        // Get the max time of all the week definitions
        LocalTime maxEndingTime = WeekDefinitionService.getMaxEndingTimeOfAListOfWeekDefinition( listReservationRules );
        // Get all the working days of all the week definitions
        List<String> listDayOfWeek = new ArrayList<>( WeekDefinitionService.getSetDaysOfWeekOfAListOfWeekDefinitionForFullCalendar( listReservationRules ) );
        // Build the slots
        List<Slot> listSlot = SlotService.buildListSlot( nIdForm, mapReservationRule, dateOfDisplay, endingDateOfDisplay );
        listSlot = listSlot.stream( ).filter( s -> s.getEndingDateTime( ).isAfter( LocalDateTime.now( ) ) ).collect( Collectors.toList( ) );
        String strDateOfDisplay = request.getParameter( PARAMETER_DATE_OF_DISPLAY );
        if ( StringUtils.isNotEmpty( strDateOfDisplay ) )
        {
            dateOfDisplay = LocalDate.parse( strDateOfDisplay );
        }
        addInfo( MESSAGE_INFO_OVERLOAD, getLocale( ) );
        Map<String, Object> model = getModel( );
        model.put( PARAMETER_DATE_OF_DISPLAY, dateOfDisplay );
        model.put( PARAMETER_ENDING_DATE_OF_DISPLAY, endingDateOfDisplay );
        model.put( PARAMETER_DAY_OF_WEEK, listDayOfWeek );
        model.put( PARAMETER_EVENTS, listSlot );
        model.put( PARAMETER_MIN_TIME, minStartingTime );
        model.put( PARAMETER_MAX_TIME, maxEndingTime );
        model.put( PARAMETER_MIN_DURATION, LocalTime.MIN.plusMinutes( AppointmentUtilities.THIRTY_MINUTES ) );
        model.put( PARAMETER_ID_FORM, nIdForm );
        model.put( PARAMETER_EVENTS_COMMENTS,
                CommentHome.selectCommentsList( Date.valueOf( dateOfDisplay ), Date.valueOf( endingDateOfDisplay ), nIdForm ) );
        AppointmentFormJspBean.addElementsToModel( request, _appointmentForm, getUser( ), getLocale( ), model );
        model.put(MARK_LOCALE_TINY, getLocale( ) );
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
        if ( _slot == null )
        {
            int nIdSlot = Integer.parseInt( request.getParameter( PARAMETER_ID_SLOT ) );
            // If nIdSlot == 0, the slot has not been created yet
            if ( nIdSlot == 0 )
            {
                // Need to get all the informations to create the slot
                LocalDateTime startingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_STARTING_DATE_TIME ) );
                LocalDateTime endingDateTime = LocalDateTime.parse( request.getParameter( PARAMETER_ENDING_DATE_TIME ) );
                boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );
                boolean bIsSpecific = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_SPECIFIC ) );
                int nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
                _slot = SlotService.buildSlot( nIdForm, new Period( startingDateTime, endingDateTime ), nMaxCapacity, nMaxCapacity, nMaxCapacity, 0, bIsOpen,
                        bIsSpecific );
            }
            else
            {
                _slot = SlotService.findSlotById( nIdSlot );
            }
        }
        Map<String, Object> model = getModel( );
        model.put( PARAMETER_DATE_OF_DISPLAY, _slot.getDate( ) );
        model.put( MARK_SLOT, _slot );
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
        boolean bOpeningHasChanged = false;
        String strIdSlot = request.getParameter( PARAMETER_ID_SLOT );
        LocalTime endingTime = LocalTime.parse( request.getParameter( PARAMETER_ENDING_TIME ) );
        boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );
        int nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
        boolean bEndingTimeHasChanged = false;

        boolean bShiftSlot = Boolean.parseBoolean( request.getParameter( PARAMETER_SHIFT_SLOT ) );
        int nIdSlot = Integer.parseInt( strIdSlot );
        Lock lock = SlotSafeService.getLockOnSlot( nIdSlot );
        lock.lock( );
        try
        {
            if ( nIdSlot != 0 )
            {
            	_slot = SlotService.findSlotById( nIdSlot );
            }
            
            if ( bIsOpen != _slot.getIsOpen( ) )
            {
            	_slot.setIsOpen( bIsOpen );
                bOpeningHasChanged = true;
            }

            // If we edit the slot, we need to check if this slot is not a closing
            // day
            ClosingDay closingDay = ClosingDayService.findClosingDayByIdFormAndDateOfClosingDay( _slot.getIdForm( ),
            		_slot.getDate( ) );
            if ( closingDay != null )
            {
                // If the slot is a closing day, we need to remove it from the table
                // closing day so that the slot is not in conflict with the
                // definition of the closing days
                ClosingDayService.removeClosingDay( closingDay );
            }
            if ( nMaxCapacity != _slot.getMaxCapacity( ) )
            {
            	_slot.setMaxCapacity( nMaxCapacity );
                // Need to set also the nb remaining places and the nb potential
                // remaining places
                // If the slot already exist, the good values will be set at the
                // update of the slot with taking the old values
                // If it is a new slot, the value set here will be good
            	_slot.setNbRemainingPlaces( nMaxCapacity );
            	_slot.setNbPotentialRemainingPlaces( nMaxCapacity );
            }
            LocalTime previousEndingTime = _slot.getEndingTime( );
            if ( !endingTime.equals( previousEndingTime ) )
            {
            	_slot.setEndingTime( endingTime );
            	_slot.setEndingDateTime( _slot.getDate( ).atTime( endingTime ) );
                bEndingTimeHasChanged = true;
            }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
            if ( ( bEndingTimeHasChanged && !checkNoAppointmentsOnThisSlotOrOnTheSlotsImpacted( _slot, bShiftSlot ) )
                    || ( bEndingTimeHasChanged && !checkEndingTimeOfSlot( endingTime, _slot ) ) )
            {
                return redirect( request, VIEW_MODIFY_SLOT, PARAMETER_ID_FORM, _slot.getIdForm( ) );
            }
            SlotSafeService.updateSlot( _slot, bEndingTimeHasChanged, previousEndingTime, bShiftSlot );

        }
        finally
        {

            lock.unlock( );
        }
        AppLogService.info( LogUtilities.buildLog( ACTION_DO_MODIFY_SLOT, strIdSlot, getUser( ) ) );
        addInfo( MESSAGE_INFO_SLOT_UPDATED, getLocale( ) );
        boolean appointmentsImpacted = !AppointmentUtilities.checkNoValidatedAppointmentsOnThisSlot( _slot );
        if ( appointmentsImpacted && bOpeningHasChanged )
        {
            addInfo( MESSAGE_INFO_VALIDATED_APPOINTMENTS_IMPACTED, getLocale( ) );
        }
        if ( appointmentsImpacted && nMaxCapacity < _slot.getNbPlacesTaken( ) )
        {
            addInfo( MESSAGE_INFO_SURBOOKING, getLocale( ) );
        }

        Map<String, String> additionalParameters = new HashMap<>( );
        additionalParameters.put( PARAMETER_ID_FORM, Integer.toString( _slot.getIdForm( ) ) );
        additionalParameters.put( PARAMETER_DATE_OF_DISPLAY, _slot.getDate( ).toString( ) );
        return redirect( request, VIEW_MANAGE_SPECIFIC_WEEK, additionalParameters );
    }
  
    /**
     * Do modify a list of slot selected
     * 
     * @param request
     *            the request
     * @return to the page of the specific week
     */
    @Action( ACTION_DO_MODIFY_LIST_SLOT )
    public String doModifyListSlots( HttpServletRequest request )
    {        
        int nVarMaxCapacity= 0;
        int nMaxCapacity= -1; 
        boolean bShiftSlot = false;       
        LocalTime endingTime = null;
        		
        String strIdForm =  request.getParameter( PARAMETER_ID_FORM ) ;
        String strShiftSlot= request.getParameter( PARAMETER_SHIFT_SLOT );
        String strEndingTime = request.getParameter( PARAMETER_ENDING_TIME );
        String strDateOfDisplay = request.getParameter( PARAMETER_DATE_OF_DISPLAY );
        String strApplyOnIdentical = request.getParameter( PARAMETER_IDENTICAL );

        boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );        
        String strCap = request.getParameter(PARAMETER_CAPACITY_MOD);
        
        if( strCap.equals( VAR_CAP )) {     	
        
        	nVarMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
       
        }else if ( strCap.equals( NEW_CAP )){
        	
            nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );

        }
        
        if( !StringUtils.isEmpty( strShiftSlot ) && !StringUtils.isEmpty( strEndingTime )) {
        	
        	 bShiftSlot = Boolean.parseBoolean( request.getParameter( PARAMETER_SHIFT_SLOT ) );
        	 endingTime= LocalTime.parse( strEndingTime );
        }
          
        
        String strJson= request.getParameter( PARAMETER_DATA );
        AppLogService.debug( "slot - Received strJson : " + strJson); 
   	    ObjectMapper mapper = new ObjectMapper( );
   		mapper.registerModule(new JavaTimeModule( ));
        mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        
        List<Slot> listSlot = new ArrayList< >();
		try {
			
			listSlot = mapper.readValue(strJson, new TypeReference<List<Slot>>(){});
			for(Slot slt: listSlot) {
				
				if( slt.getIdSlot() != 0 ) {
					
	                Slot slot = SlotService.findSlotById( slt.getIdSlot( ) );
	                slt.setNbPlacestaken( slot.getNbPlacesTaken( ) );
	                slt.setNbRemainingPlaces( slot.getNbRemainingPlaces( ) );
	                slt.setNbPotentialRemainingPlaces(slot.getNbPotentialRemainingPlaces( ));
				}else {
					
	                slt.setNbRemainingPlaces( slt.getMaxCapacity( ));
	                slt.setNbPotentialRemainingPlaces( slt.getMaxCapacity( ) );
				}

			}
			if( !StringUtils.isEmpty( strApplyOnIdentical ) && Boolean.parseBoolean( strApplyOnIdentical ) ) {

      			 LocalDate startingDate = LocalDate.parse( request.getParameter( PARAMETER_STARTING_DATE_TO_APPLY ) );
	             LocalDate endingDate = LocalDate.parse( request.getParameter( PARAMETER_ENDING_DATE_TO_APPLY ) );
				
				listSlot = buildListSlotsToUpdate( listSlot, Integer.parseInt( strIdForm ) ,  startingDate,  endingDate );
	        }
		
		} catch (  IOException e ) {

	    	AppLogService.error( MESSAGE_ERROR_PARSING_JSON + e.getMessage(), e );
            addError( MESSAGE_ERROR_PARSING_JSON, getLocale( ) );

		}
		           	
		updateListSlots( listSlot, nVarMaxCapacity, nMaxCapacity, bIsOpen, bShiftSlot, endingTime );
        
        Map<String, String> additionalParameters = new HashMap<>( );
        additionalParameters.put( PARAMETER_ID_FORM, strIdForm );
        additionalParameters.put( PARAMETER_DATE_OF_DISPLAY, strDateOfDisplay );
        return redirect( request, VIEW_MANAGE_SPECIFIC_WEEK, additionalParameters );
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
        ReservationRule resrvationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( slot.getIdForm( ), dateOfSlot);
        WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( resrvationRule.getListWorkingDay( ), dateOfSlot.getDayOfWeek( ) );
        LocalTime maxEndingTime = null;
        if ( workingDay == null )
        {
            maxEndingTime = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( resrvationRule.getListWorkingDay( ) );
        }
        else
        {
            maxEndingTime = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
        }
        if ( endingTime.isAfter( maxEndingTime ) )
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
        List<Slot> listSlotImpacted = SlotService.findSlotsByIdFormAndDateRange( slot.getIdForm( ), slot.getStartingDateTime( ), endingDateTime );
        List<Appointment> listAppointment = AppointmentService.findListAppointmentByListSlot( listSlotImpacted );
        if ( CollectionUtils.isNotEmpty( listAppointment ) )
        {
            bReturn = false;
            addError( MESSAGE_ERROR_APPOINTMENT_ON_SLOT, getLocale( ) );
        }
        return bReturn;
    }

    
    /**
     * Update a list of slot
     * @param listSlot the list of slot to update
     * @param nVarMaxCapacity the var capacity
     * @param nMaxCapacity the Max capacity
     * @param bIsOpen he new boolean opening value
     * @param bShiftSlot The shift 
     * @param endingTime rhe Ending time
     */
    private void updateListSlots( List< Slot > listSlot, int nVarMaxCapacity, int nMaxCapacity, boolean bIsOpen, boolean bShiftSlot, LocalTime endingTime ){
    	
    	 int nNewMaxCapacity= 0;
         boolean bOpeningHasChanged = false;
         boolean appointmentsImpacted = false;
         boolean bEndingTimeHasChanged = false;
         boolean bNoApptImpacted= true;
     	 StringBuilder sbAlert = new StringBuilder( );


    	 for(Slot slot: listSlot ){
    		 
	    	 Lock lock = SlotSafeService.getLockOnSlot( slot.getIdSlot( ) );
		        lock.lock( );
		        try
		        {
		            
		            if ( bIsOpen != slot.getIsOpen( ) )
		            {
		            	slot.setIsOpen( bIsOpen );
		            	bOpeningHasChanged = true;
		            }
		
		            // If we edit the slot, we need to check if this slot is not a closing
		            // day
		            ClosingDay closingDay = ClosingDayService.findClosingDayByIdFormAndDateOfClosingDay( slot.getIdForm( ),
		            		slot.getDate( ) );
		            if ( closingDay != null )
		            {
		                // If the slot is a closing day, we need to remove it from the table
		                // closing day so that the slot is not in conflict with the
		                // definition of the closing days
		                ClosingDayService.removeClosingDay( closingDay );
		            }
		            if ( nVarMaxCapacity != 0 || ( nMaxCapacity >= 0 && nMaxCapacity != slot.getMaxCapacity( )) )
		            {
		            	nNewMaxCapacity= (nVarMaxCapacity != 0)? slot.getMaxCapacity() + nVarMaxCapacity : nMaxCapacity;
		            	if( nNewMaxCapacity < 0) {
		            		
		            		nNewMaxCapacity= 0;
		            	}
		            	
		            	slot.setMaxCapacity( nNewMaxCapacity );
		                // Need to set also the nb remaining places and the nb potential
		                // remaining places
		                // If the slot already exist, the good values will be set at the
		                // update of the slot with taking the old values
		                // If it is a new slot, the value set here will be good
		            	slot.setNbRemainingPlaces( nNewMaxCapacity );
		            	slot.setNbPotentialRemainingPlaces( nNewMaxCapacity );
		            }
		            LocalTime previousEndingTime = slot.getEndingTime( );
		            if ( endingTime != null && !endingTime.equals( previousEndingTime ) )
		            {
		            	bNoApptImpacted= checkNoAppointmentsOnThisSlotOrOnTheSlotsImpacted( slot, bShiftSlot );
		                slot.setEndingTime( endingTime );
		                slot.setEndingDateTime( slot.getDate( ).atTime( endingTime ) );
		                bEndingTimeHasChanged = true;
		            }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
		            if ( ( bEndingTimeHasChanged && !bNoApptImpacted )
		                    || ( bEndingTimeHasChanged && !checkEndingTimeOfSlot( endingTime, slot ) ) )
		            {
		                addWarning( MESSAGE_ERROR_APPOINTMENT_ON_SLOT, getLocale( ) );
	
		            }else {
		            	
		                SlotSafeService.updateSlot( slot, bEndingTimeHasChanged, previousEndingTime, bShiftSlot );
		                if( !appointmentsImpacted && slot.getNbPlacesTaken() > 0 ) {
		                	
		                	appointmentsImpacted= true;
		                }
			            AppLogService.info( LogUtilities.buildLog( ACTION_DO_MODIFY_SLOT, String.valueOf( slot.getIdSlot( )), getUser( ) ) );
		
			            if( slot.getMaxCapacity( ) < slot.getNbPlacesTaken( ) ) {
			            	
			            	sbAlert.append( slot.getStartingDateTime() );
			            	sbAlert.append( "-" );
			            	sbAlert.append( slot.getEndingDateTime() );
			            	sbAlert.append( ", " );
		
			            }
		            }
		           
		        }
		        finally
		        {
		
		            lock.unlock( );
		        }
    	 }
    	 
         if( CollectionUtils.isNotEmpty( listSlot ) ) {
         	
         	addInfo( MESSAGE_INFO_SLOT_UPDATED, getLocale( ) );
         }
         
         if ( appointmentsImpacted && bOpeningHasChanged )
         {
        	 addWarning( MESSAGE_INFO_VALIDATED_APPOINTMENTS_IMPACTED, getLocale( ) );
         }
         
         if ( !StringUtils.isEmpty( sbAlert.toString( )) )
         { 
             Object [ ] args = {
             		sbAlert.toString( )
             };            
             addWarning( I18nService.getLocalizedString( MESSAGE_INFO_MULTI_SURBOOKING, args, getLocale( ) ) );
         }
    }
    /**
     * Build list of slot 
     * @param listSlotSelected the list of slot builded
     * @param nIdForm the id form
     * @param startingDate the starting date
     * @param endingDate the ending date
     * @return the list builded
     */
    private List<Slot> buildListSlotsToUpdate( List<Slot> listSlotSelected, int nIdForm, LocalDate startingDate, LocalDate endingDate ) {
    	
    	List<Slot> listBuilded= new ArrayList< >( );
    	listBuilded.addAll(listSlotSelected);
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( nIdForm );
        List<Slot> listSlots = SlotService.buildListSlot( nIdForm, mapWeekDefinition, startingDate, endingDate );

        for(Slot slot: listSlotSelected ) {
        	
        	listBuilded.addAll( listSlots.stream().filter(slt -> slt.getStartingTime().equals(slot.getStartingTime( ))  
        			&& slt.getEndingTime().equals(slot.getEndingTime() )
        			&& slt.getDate().getDayOfWeek().getValue()== slot.getDate().getDayOfWeek().getValue())
        			.collect( Collectors.toList( )));
        }
    	
        return listBuilded;
    }   
}