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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.planning.ClosingDay;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Period;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.log.LogUtilities;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.ClosingDayService;
import fr.paris.lutece.plugins.appointment.service.DisplayService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.ReservationRuleService;
import fr.paris.lutece.plugins.appointment.service.SlotSafeService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.TimeSlotService;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.WorkingDayService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

/**
 * JspBean to manage calendar slots
 * 
 * @author Laurent Payen
 *
 */
@Controller( controllerJsp = AppointmentSlotJspBean.JSP_MANAGE_APPOINTMENT_SLOTS, controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentSlotJspBean extends AbstractAppointmentFormAndSlotJspBean
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
	private static final String MESSAGE_ERROR_LAST_WEEK_DEFINITION = "appointment.message.error.lastWeekDefinition";
	private static final String MESSAGE_INFO_SLOT_UPDATED = "appointment.modifyCalendarSlots.messageSlotUpdated";
	private static final String MESSAGE_INFO_VALIDATED_APPOINTMENTS_IMPACTED = "appointment.modifyCalendarSlots.messageValidatedAppointmentsImpacted";
	private static final String MESSAGE_INFO_SURBOOKING = "appointment.modifyCalendarSlots.messageSurbooking";
	private static final String MESSAGE_INFO_OVERLOAD = "appointment.modifyCalendarSlots.messageOverload";
	private static final String MESSAGE_ERROR_START_DATE_EMPTY = "appointment.message.error.startDateEmpty";
	private static final String MESSAGE_ERROR_MODIFY_FORM_HAS_APPOINTMENTS_AFTER_DATE_OF_MODIFICATION = "appointment.message.error.refreshDays.modifyFormHasAppointments";
	private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointmentform.attribute.";
	private static final String MESSAGE_CONFIRM_REMOVE_WEEK_DEFINITION = "appointment.message.confirmRemoveWeekDefinition";


	// Parameters
	private static final String PARAMETER_ENDING_DATE_OF_DISPLAY = "ending_date_of_display";
	private static final String PARAMETER_DATE_OF_DISPLAY = "date_of_display";
	private static final String PARAMETER_ERROR_MODIFICATION = "error_modification";
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
	private static final String PARAMETER_IS_SPECIFIC = "is_specific";
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
	private static final String ACTION_MODIFY_ADVANCED_PARAMETERS = "modifyAdvancedParameters";
	private static final String ACTION_CONFIRM_REMOVE_PARAMETER = "confirmRemoveParameter";
	private static final String ACTION_REMOVE_PARAMETER = "doRemoveParameter";

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

	// Infos
	private static final String INFO_ADVANCED_PARAMETERS_UPDATED = "appointment.info.advancedparameters.updated";
	private static final String INFO_PARAMETER_REMOVED = "appointment.info.advancedparameters.removed";

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
			if ( weekDefinition != null )
			{
				nIdWeekDefinition = weekDefinition.getIdWeekDefinition( );
			}
		}
		Map<String, Object> model = getModel( );
		AppointmentFormDTO appointmentForm = null;
		if ( request.getParameter( PARAMETER_ERROR_MODIFICATION ) != null )
		{
			appointmentForm = (AppointmentFormDTO) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
			model.put( PARAMETER_ERROR_MODIFICATION, Boolean.TRUE );
		}
		List<String> listDayOfWeek = new ArrayList<>( );
		List<TimeSlot> listTimeSlot = new ArrayList<>( );
		LocalTime minStartingTime = LocalTime.MIN;
		LocalTime maxEndingTime = LocalTime.MAX;
		if ( weekDefinition == null )
		{
			appointmentForm = FormService.buildAppointmentForm( nIdForm, 0, 0 );

		}
		else
		{
			if ( appointmentForm == null )
			{
				int nIdReservationRule = 0;
				ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndDateOfApply( nIdForm, weekDefinition.getDateOfApply( ) );
				if ( reservationRule != null )
				{
					nIdReservationRule = reservationRule.getIdReservationRule( );
				}
				appointmentForm = FormService.buildAppointmentForm( nIdForm, nIdReservationRule, nIdWeekDefinition );
			}
			List<WorkingDay> listWorkingDay = weekDefinition.getListWorkingDay( );
			listDayOfWeek = new ArrayList<>( WorkingDayService.getSetDaysOfWeekOfAListOfWorkingDayForFullCalendar( listWorkingDay ) );
			listTimeSlot = TimeSlotService.getListTimeSlotOfAListOfWorkingDay( listWorkingDay, dateOfApply );
			minStartingTime = WorkingDayService.getMinStartingTimeOfAListOfWorkingDay( listWorkingDay );
			maxEndingTime = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( listWorkingDay );
		}
		model.put( PARAMETER_DAY_OF_WEEK, listDayOfWeek );
		model.put( PARAMETER_EVENTS, listTimeSlot );
		model.put( PARAMETER_MIN_TIME, minStartingTime );
		model.put( PARAMETER_MAX_TIME, maxEndingTime );
		model.put( PARAMETER_MIN_DURATION, LocalTime.MIN.plusMinutes( AppointmentUtilities.THIRTY_MINUTES ) );
		model.put( PARAMETER_ID_WEEK_DEFINITION, nIdWeekDefinition );
		model.put( MARK_LIST_DATE_OF_MODIFICATION, WeekDefinitionService.findAllDateOfWeekDefinition( nIdForm ) );
		AppointmentFormJspBean.addElementsToModel( request, appointmentForm, getUser( ), getLocale( ), model );
		return getPage( MESSAGE_TYPICAL_WEEK_PAGE_TITLE, TEMPLATE_MANAGE_TYPICAL_WEEK, model );
	}

	/**
	 * Do modify a form (advanced parameters part)
	 * 
	 * @param request
	 *            the request
	 * @return the JSP URL to display the form to modify appointment forms
	 * @throws AccessDeniedException
	 */
	@Action( ACTION_MODIFY_ADVANCED_PARAMETERS )
	public String doModifyAdvancedParameters( HttpServletRequest request ) throws AccessDeniedException
	{
		String strIdForm = request.getParameter( PARAMETER_ID_FORM );
		int nIdForm = Integer.parseInt( strIdForm );
		if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM,
				getUser( ) ) )
		{
			throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM );
		}
		AppointmentFormDTO appointmentForm = (AppointmentFormDTO) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
		populate( appointmentForm, request );
		if ( appointmentForm.getDateOfModification( ) == null )
		{
			request.getSession( ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM, appointmentForm );
			addError( MESSAGE_ERROR_START_DATE_EMPTY, getLocale( ) );
			return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ERROR_MODIFICATION, 1 );
		}
		LocalDate dateOfModification = appointmentForm.getDateOfModification( ).toLocalDate( );
		// Get all the appointments after this date and until the next week
		// definition if it exists
		WeekDefinition nextWeekDefinition = WeekDefinitionService.findNextWeekDefinition( nIdForm, dateOfModification );
		// We can't use the LocalDateTime.MAX value because of the bug of the
		// year 2038 for Timestamp
		// (https://fr.wikipedia.org/wiki/Bug_de_l%27an_2038)
		LocalDateTime endingDateTimeOfSearch = LocalDateTime.of( LocalDate.of( 9999, 12, 31 ), LocalTime.MAX );
		if ( nextWeekDefinition != null )
		{
			endingDateTimeOfSearch = nextWeekDefinition.getDateOfApply( ).atTime( LocalTime.MIN );
		}
		if ( !validateBean( appointmentForm, VALIDATION_ATTRIBUTES_PREFIX ) || !checkConstraints( appointmentForm ) )
		{
			request.getSession( ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM, appointmentForm );
			return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ERROR_MODIFICATION, 1 );
		}
		List<Slot> listSlotsImpacted = SlotService.findSlotsByIdFormAndDateRange( nIdForm, dateOfModification.atStartOfDay( ), endingDateTimeOfSearch );
		List<Appointment> listAppointmentsImpacted = AppointmentService.findListAppointmentByListSlot( listSlotsImpacted );
		// if there are slots impacted
		if ( CollectionUtils.isNotEmpty( listSlotsImpacted ) )
		{
			// if there are appointments impacted
			if ( CollectionUtils.isNotEmpty( listAppointmentsImpacted ) )
			{
				if ( !AppointmentUtilities.checkNoAppointmentsImpacted( listAppointmentsImpacted, nIdForm, dateOfModification, appointmentForm ) )
				{
					request.getSession( ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM, appointmentForm );
					addError( MESSAGE_ERROR_MODIFY_FORM_HAS_APPOINTMENTS_AFTER_DATE_OF_MODIFICATION, getLocale( ) );
					return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ERROR_MODIFICATION, 1 );
				}
				manageTheSlotsAndAppointmentsImpacted( listAppointmentsImpacted, listSlotsImpacted, Boolean.TRUE, appointmentForm.getMaxCapacityPerSlot( ),
						Boolean.FALSE, Boolean.FALSE );
			}
			else
			{
				// No check, delete all the slots
				SlotService.deleteListSlots( listSlotsImpacted );
			}
		}
		FormService.updateAdvancedParameters( appointmentForm, dateOfModification );

		AppLogService.info( LogUtilities.buildLog( ACTION_MODIFY_ADVANCED_PARAMETERS, strIdForm, getUser( ) ) );
		request.getSession( ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
		addInfo( INFO_ADVANCED_PARAMETERS_UPDATED, getLocale( ) );
		ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, dateOfModification );
		WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndDateOfApply( nIdForm, reservationRule.getDateOfApply( ) );
		return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_WEEK_DEFINITION, weekDefinition.getIdWeekDefinition( ) );
	}

	/**
	 * Manages the removal form of a appointment whose identifier is in the HTTP request
	 * 
	 * @param request
	 *            The HTTP request
	 * @return the HTML code to confirm
	 */
	@Action( ACTION_CONFIRM_REMOVE_PARAMETER )
	public String getConfirmRemoveParameter( HttpServletRequest request )
	{
		UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_PARAMETER ) );
		url.addParameter( PARAMETER_ID_WEEK_DEFINITION, request.getParameter( PARAMETER_ID_WEEK_DEFINITION ) );
		url.addParameter( PARAMETER_ID_FORM, request.getParameter( PARAMETER_ID_FORM ) );
		String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_WEEK_DEFINITION, url.getUrl( ),
				AdminMessage.TYPE_CONFIRMATION );
		return redirect( request, strMessageUrl );
	}

	/**
	 * Handles the removal form of a appointment
	 * 
	 * @param request
	 *            The HTTP request
	 * @return the JSP URL to display the form to manage appointments
	 * @throws AccessDeniedException
	 *             If the user is not authorized to access this feature
	 */
	@Action( ACTION_REMOVE_PARAMETER )
	public String doRemoveParameter( HttpServletRequest request ) throws AccessDeniedException
	{
		String strIdForm = request.getParameter( PARAMETER_ID_FORM );
		if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM,
				getUser( ) ) )
		{
			throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM );
		}
		int nIdWeekDefinition = Integer.parseInt( request.getParameter( PARAMETER_ID_WEEK_DEFINITION ) );
		int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
		WeekDefinition weekDefinitionToRemove = WeekDefinitionService.findWeekDefinitionById( nIdWeekDefinition );
		// Check if there are other week definitions
		List<WeekDefinition> listOfOtherWeekDefinitionOfTheForm = new ArrayList<>( );
		listOfOtherWeekDefinitionOfTheForm.addAll( WeekDefinitionService.findAllWeekDefinition( nIdForm ).values( ) );
		listOfOtherWeekDefinitionOfTheForm = listOfOtherWeekDefinitionOfTheForm.stream( ).filter( w -> w.getIdWeekDefinition( ) != nIdWeekDefinition )
				.collect( Collectors.toList( ) );
		if ( CollectionUtils.isEmpty( listOfOtherWeekDefinitionOfTheForm ) )
		{
			addError( MESSAGE_ERROR_LAST_WEEK_DEFINITION, getLocale( ) );
			return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_WEEK_DEFINITION, nIdWeekDefinition );
		}
		// Check if there are appointments on this week definition
		LocalDate beginDateOfApply = weekDefinitionToRemove.getDateOfApply( );
		WeekDefinition nextWeekDefinition = WeekDefinitionService.findNextWeekDefinition( nIdForm, beginDateOfApply );
		LocalDate endDateOfApply = LocalDate.MAX;
		if ( nextWeekDefinition != null )
		{
			endDateOfApply = nextWeekDefinition.getDateOfApply( );
		}
		List<Slot> listSlotImpacted = SlotService.findSlotsByIdFormAndDateRange( nIdForm, beginDateOfApply.atTime( LocalTime.MIN ),
				endDateOfApply.atTime( LocalTime.MAX ) );
		List<Appointment> listAppointment = AppointmentService.findListAppointmentByListSlot( listSlotImpacted );
		if ( CollectionUtils.isNotEmpty( listAppointment ) )
		{
			addError( MESSAGE_ERROR_APPOINTMENT_ON_SLOT, getLocale( ) );
			return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_WEEK_DEFINITION, nIdWeekDefinition );
		}
		ReservationRule reservationRuleToRemove = ReservationRuleService.findReservationRuleByIdFormAndDateOfApply( nIdForm, beginDateOfApply );
		ReservationRuleService.removeReservationRule( reservationRuleToRemove );
		WeekDefinitionService.removeWeekDefinition( nIdWeekDefinition, nIdForm );
		addInfo( INFO_PARAMETER_REMOVED, getLocale( ) );
		return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm );
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
		boolean bOpeningHasChanged = false;
		int nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
		LocalTime endingTime = LocalTime.parse( request.getParameter( PARAMETER_ENDING_TIME ) );
		boolean bShiftSlot = Boolean.parseBoolean( request.getParameter( PARAMETER_SHIFT_SLOT ) );
		boolean bEndingTimeHasChanged = false;
		boolean bMaxCapacityHasChanged = false;
		if ( bIsOpen != timeSlotFromSession.getIsOpen( ) )
		{
			timeSlotFromSession.setIsOpen( bIsOpen );
			bOpeningHasChanged = true;
		}
		if ( nMaxCapacity != timeSlotFromSession.getMaxCapacity( ) )
		{
			timeSlotFromSession.setMaxCapacity( nMaxCapacity );
			bMaxCapacityHasChanged = true;
		}
		LocalTime previousEndingTime = timeSlotFromSession.getEndingTime( );
		if ( !endingTime.equals( previousEndingTime ) )
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
			bEndingTimeHasChanged = true;
		}
		List<Slot> listSlotsImpacted = AppointmentUtilities.findSlotsImpactedByThisTimeSlot( timeSlotFromSession, nIdForm, nIdWeekDefinition, bShiftSlot );
		List<Appointment> listAppointmentsImpacted = AppointmentService.findListAppointmentByListSlot( listSlotsImpacted );
		// If there are slots impacted
		if ( CollectionUtils.isNotEmpty( listSlotsImpacted ) )
		{
			// if there are appointments impacted
			if ( CollectionUtils.isNotEmpty( listAppointmentsImpacted ) )
			{
				// If the ending time of the time slot has changed or if the max
				// capacity has decreased
				if ( bEndingTimeHasChanged || nMaxCapacity < timeSlotFromSession.getMaxCapacity( ) )
				{
					// Error, the time slot can't be changed
					addError( MESSAGE_ERROR_APPOINTMENT_ON_SLOT, getLocale( ) );
					addError( listAppointmentsImpacted.size( ) + " rendez-vous impacté(s)" );
					// addError( "dont un le " + SlotService.findSlotById( listAppointmentsImpacted.get( 0 ).getIdSlot( ) ).getStartingDateTime( ) );
					Map<String, String> additionalParameters = new HashMap<>( );
					additionalParameters.put( PARAMETER_ID_FORM, strIdForm );
					additionalParameters.put( PARAMETER_ID_WEEK_DEFINITION, strIdWeekDefinition );
					additionalParameters.put( PARAMETER_ID_TIME_SLOT, strIdTimeSlot );
					request.getSession( ).setAttribute( SESSION_ATTRIBUTE_TIME_SLOT, timeSlotFromSession );
					return redirect( request, VIEW_MODIFY_TIME_SLOT, additionalParameters );
				}
				// Get the validated appointment (the appointments that are not
				// cancelled)
				List<Appointment> listValidatedAppointments = listAppointmentsImpacted.stream( ).filter( appointment -> appointment.getIsCancelled( ) == false )
						.collect( Collectors.toList( ) );
				if ( bOpeningHasChanged && CollectionUtils.isNotEmpty( listValidatedAppointments ) )
				{
					addInfo( MESSAGE_INFO_VALIDATED_APPOINTMENTS_IMPACTED, getLocale( ) );
				}
				manageTheSlotsAndAppointmentsImpacted( listAppointmentsImpacted, listSlotsImpacted, bMaxCapacityHasChanged, nMaxCapacity, bOpeningHasChanged,
						bIsOpen );
			}
			else
			{
				// no need to check appointments, delete all the slots
				SlotService.deleteListSlots( listSlotsImpacted );
			}
		}
		TimeSlotService.updateTimeSlot( timeSlotFromSession, bEndingTimeHasChanged, previousEndingTime, bShiftSlot );

		AppLogService.info( LogUtilities.buildLog( ACTION_DO_MODIFY_TIME_SLOT, strIdTimeSlot, getUser( ) ) );
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
		Form form = FormService.findFormLightByPrimaryKey( nIdForm );
		// Get the nb weeks to display
		Display display = DisplayService.findDisplayWithFormId( nIdForm );
		int nNbWeeksToDisplay = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_DISPLAY_IN_BO, display.getNbWeeksToDisplay( ) );
		AppointmentFormDTO appointmentForm = (AppointmentFormDTO) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT_FORM );
		if ( ( appointmentForm == null ) || ( nIdForm != appointmentForm.getIdForm( ) ) )
		{
			appointmentForm = FormService.buildAppointmentForm( nIdForm, 0, 0 );
		}
		LocalDate dateOfDisplay = LocalDate.now( );
		if ( appointmentForm.getDateStartValidity( ) != null && appointmentForm.getDateStartValidity( ).toLocalDate( ).isAfter( dateOfDisplay ) )
		{
			dateOfDisplay = appointmentForm.getDateStartValidity( ).toLocalDate( );
		}
		LocalDate endingDateOfDisplay = LocalDate.now( ).plusWeeks( nNbWeeksToDisplay );
		LocalDate endingValidityDate = form.getEndingValidityDate( );
		if ( endingValidityDate != null )
		{
			if ( endingDateOfDisplay.isAfter( endingValidityDate ) )
			{
				endingDateOfDisplay = endingValidityDate;
			}
		}
		// Get all the week definitions
		HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( nIdForm );
		List<WeekDefinition> listWeekDefinition = new ArrayList<>( mapWeekDefinition.values( ) );
		// Get the min time of all the week definitions
		LocalTime minStartingTime = WeekDefinitionService.getMinStartingTimeOfAListOfWeekDefinition( listWeekDefinition );
		// Get the max time of all the week definitions
		LocalTime maxEndingTime = WeekDefinitionService.getMaxEndingTimeOfAListOfWeekDefinition( listWeekDefinition );
		// Get all the working days of all the week definitions
		List<String> listDayOfWeek = new ArrayList<>( WeekDefinitionService.getSetDaysOfWeekOfAListOfWeekDefinitionForFullCalendar( listWeekDefinition ) );
		// Build the slots
		List<Slot> listSlot = SlotService.buildListSlot( nIdForm, mapWeekDefinition, dateOfDisplay, endingDateOfDisplay );
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
		AppointmentFormJspBean.addElementsToModel( request, appointmentForm, getUser( ), getLocale( ), model );
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
				boolean bIsSpecific = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_SPECIFIC ) );
				int nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
				slot = SlotService.buildSlot( nIdForm, new Period( startingDateTime, endingDateTime ), nMaxCapacity, nMaxCapacity, nMaxCapacity, 0, bIsOpen,
						bIsSpecific );
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
		boolean bOpeningHasChanged = false;
		Slot slotFromSessionOrFromDb = null;
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
				slotFromSessionOrFromDb = SlotService.findSlotById( nIdSlot );
			}
			else
			{
				slotFromSessionOrFromDb = (Slot) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_SLOT );
			}

			if ( bIsOpen != slotFromSessionOrFromDb.getIsOpen( ) )
			{
				slotFromSessionOrFromDb.setIsOpen( bIsOpen );
				bOpeningHasChanged = true;
			}

			// If we edit the slot, we need to check if this slot is not a closing
			// day
			ClosingDay closingDay = ClosingDayService.findClosingDayByIdFormAndDateOfClosingDay( slotFromSessionOrFromDb.getIdForm( ),
					slotFromSessionOrFromDb.getDate( ) );
			if ( closingDay != null )
			{
				// If the slot is a closing day, we need to remove it from the table
				// closing day so that the slot is not in conflict with the
				// definition of the closing days
				ClosingDayService.removeClosingDay( closingDay );
			}
			if ( nMaxCapacity != slotFromSessionOrFromDb.getMaxCapacity( ) )
			{
				slotFromSessionOrFromDb.setMaxCapacity( nMaxCapacity );
				// Need to set also the nb remaining places and the nb potential
				// remaining places
				// If the slot already exist, the good values will be set at the
				// update of the slot with taking the old values
				// If it is a new slot, the value set here will be good
				slotFromSessionOrFromDb.setNbRemainingPlaces( nMaxCapacity );
				slotFromSessionOrFromDb.setNbPotentialRemainingPlaces( nMaxCapacity );
			}
			LocalTime previousEndingTime = slotFromSessionOrFromDb.getEndingTime( );
			if ( !endingTime.equals( previousEndingTime ) )
			{
				slotFromSessionOrFromDb.setEndingTime( endingTime );
				slotFromSessionOrFromDb.setEndingDateTime( slotFromSessionOrFromDb.getDate( ).atTime( endingTime ) );
				bEndingTimeHasChanged = true;
			}
			if ( bEndingTimeHasChanged && !checkNoAppointmentsOnThisSlotOrOnTheSlotsImpacted( slotFromSessionOrFromDb, bShiftSlot )
					|| bEndingTimeHasChanged && !checkEndingTimeOfSlot( endingTime, slotFromSessionOrFromDb ) )
			{
				request.getSession( ).setAttribute( SESSION_ATTRIBUTE_SLOT, slotFromSessionOrFromDb );
				return redirect( request, VIEW_MODIFY_SLOT, PARAMETER_ID_FORM, slotFromSessionOrFromDb.getIdForm( ) );
			}
			SlotSafeService.updateSlot( slotFromSessionOrFromDb, bEndingTimeHasChanged, previousEndingTime, bShiftSlot );

		}
		finally
		{

			lock.unlock( );
		}
		AppLogService.info( LogUtilities.buildLog( ACTION_DO_MODIFY_SLOT, strIdSlot, getUser( ) ) );
		addInfo( MESSAGE_INFO_SLOT_UPDATED, getLocale( ) );
		boolean appointmentsImpacted = !AppointmentUtilities.checkNoValidatedAppointmentsOnThisSlot( slotFromSessionOrFromDb );
		if ( appointmentsImpacted && bOpeningHasChanged )
		{
			addInfo( MESSAGE_INFO_VALIDATED_APPOINTMENTS_IMPACTED, getLocale( ) );
		}
		if ( appointmentsImpacted && nMaxCapacity < slotFromSessionOrFromDb.getNbPlacesTaken( ) )
		{
			addInfo( MESSAGE_INFO_SURBOOKING, getLocale( ) );
		}

		request.getSession( ).removeAttribute( SESSION_ATTRIBUTE_SLOT );
		Map<String, String> additionalParameters = new HashMap<>( );
		additionalParameters.put( PARAMETER_ID_FORM, Integer.toString( slotFromSessionOrFromDb.getIdForm( ) ) );
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
		LocalTime maxEndingTime = null;
		if ( workingDay == null )
		{
			maxEndingTime = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( weekDefinition.getListWorkingDay( ) );
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
	 * Update the slots with appointments impacted by a modification of a typical week or a modification of a timeSlot Delete the slots with no appointments
	 * 
	 * @param listAppointmentsImpacted
	 *            the appointments impacted
	 * @param listSlotsImpacted
	 *            the slots impacted
	 * @param bMaxCapacityHasChanged
	 *            True if the capacity has changed
	 * @param nMaxCapacity
	 *            the max capacity
	 * @param bOpeningHasChanged
	 *            true if the opening has changed
	 * @param bIsOpen
	 *            the new boolean opening value
	 */
	private void manageTheSlotsAndAppointmentsImpacted( List<Appointment> listAppointmentsImpacted, List<Slot> listSlotsImpacted,
			boolean bMaxCapacityHasChanged, int nMaxCapacity, boolean bOpeningHasChanged, boolean bIsOpen )
	{
		// Need to delete the slots that are impacted but with no
		// appointments
		HashSet<Integer> setSlotsImpactedWithAppointments = new HashSet<>( );
		for ( Appointment appointment : listAppointmentsImpacted )
		{
			for ( AppointmentSlot apptSlot : appointment.getListAppointmentSlot( ) )
			{
				setSlotsImpactedWithAppointments.add( apptSlot.getIdSlot( ) );
			}
		}
		List<Slot> listSlotsImpactedWithoutAppointments = listSlotsImpacted.stream( )
				.filter( slot -> !setSlotsImpactedWithAppointments.contains( slot.getIdSlot( ) ) ).collect( Collectors.toList( ) );
		List<Slot> listSlotsImpactedWithAppointments = listSlotsImpacted.stream( )
				.filter( slot -> setSlotsImpactedWithAppointments.contains( slot.getIdSlot( ) ) ).collect( Collectors.toList( ) );

		SlotService.deleteListSlots( listSlotsImpactedWithoutAppointments );

		for ( Slot slotImpacted : listSlotsImpactedWithAppointments )
		{
			Lock lock = SlotSafeService.getLockOnSlot( slotImpacted.getIdSlot( ) );

			lock.lock( );
			try
			{
				// If the max capacity has changed,
				// need to update it for all the slots that already have
				// appointments
				if ( bMaxCapacityHasChanged )
				{
					slotImpacted.setMaxCapacity( nMaxCapacity );
					SlotSafeService.updateRemainingPlaces( slotImpacted );
				}
				// if the opening of the timeslot has changed and there are
				// appointments impacted,
				// all the corresponding slots are marked as specific
				if ( bOpeningHasChanged )
				{
					slotImpacted.setIsSpecific( bIsOpen );
				}
				SlotSafeService.updateSlot( slotImpacted );
			}
			finally
			{
				lock.unlock( );
			}
		}
	}


}
