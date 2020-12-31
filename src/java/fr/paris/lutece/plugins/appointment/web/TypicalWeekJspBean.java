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
import java.time.LocalDate;
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

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.plugins.appointment.log.LogUtilities;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.ReservationRuleService;
import fr.paris.lutece.plugins.appointment.service.SlotSafeService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.TimeSlotService;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.WorkingDayService;
import fr.paris.lutece.plugins.appointment.service.listeners.WeekDefinitionManagerListener;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
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
@Controller( controllerJsp = TypicalWeekJspBean.JSP_MANAGE_APPOINTMENT_SLOTS, controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class TypicalWeekJspBean extends AbstractAppointmentFormAndSlotJspBean
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
    private static final String MESSAGE_TYPICAL_WEEK_PAGE_TITLE = "appointment.typicalWeek.pageTitle";
    private static final String MESSAGE_MODIFY_TIME_SLOT_PAGE_TITLE = "appointment.modifyCalendarSlots.pageTitle";
    private static final String MESSAGE_WARNING_CHANGES_APPLY_TO_ALL = "appointment.modifyCalendarSlots.warningModifiyingEndingTime";
    private static final String MESSAGE_ERROR_TIME_END_BEFORE_TIME_START = "appointment.modifyCalendarSlots.errorTimeEndBeforeTimeStart";
    private static final String MESSAGE_SLOT_CAN_NOT_END_AFTER_DAY_OR_FORM = "appointment.message.error.slotCanNotEndAfterDayOrForm";
    private static final String MESSAGE_ERROR_APPOINTMENT_ON_SLOT = "appointment.message.error.appointmentOnSlot";
    private static final String MESSAGE_INFO_SLOT_UPDATED = "appointment.modifyCalendarSlots.messageSlotUpdated";
    private static final String MESSAGE_INFO_VALIDATED_APPOINTMENTS_IMPACTED = "appointment.modifyCalendarSlots.messageValidatedAppointmentsImpacted";

    private static final String MESSAGE_ERROR_MODIFY_FORM_HAS_APPOINTMENTS_AFTER_DATE_OF_MODIFICATION = "appointment.message.error.refreshDays.modifyFormHasAppointments";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointmentform.attribute.";
    private static final String MESSAGE_CONFIRM_REMOVE_WEEK_DEFINITION = "appointment.message.confirmRemoveWeekDefinition";
    private static final String MESSAGE_ERROR_RULE_ASSIGNED = "appointment.message.error.rule.assigned";
    private static final String MESSAGE_ERROR_MODIFICATION_WEEK_ASSIGNED_IN_PAST = "appointment.message.error.week.assigned.past";
    private static final String MESSAGE_ERROR_PARSING_JSON = "appointment.message.error.parsing.json";

    // Parameters
    private static final String PARAMETER_ERROR_MODIFICATION = "error_modification";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ID_TIME_SLOT = "id_time_slot";
    private static final String PARAMETER_DAY_OF_WEEK = "dow";
    private static final String PARAMETER_EVENTS = "events";
    private static final String PARAMETER_MIN_DURATION = "min_duration";
    private static final String PARAMETER_MIN_TIME = "min_time";
    private static final String PARAMETER_MAX_TIME = "max_time";
    private static final String PARAMETER_IS_OPEN = "is_open";
    private static final String PARAMETER_ENDING_TIME = "ending_time";
    private static final String PARAMETER_MAX_CAPACITY = "max_capacity";
    private static final String PARAMETER_ID_RULE = "id_reservation_rule";
    private static final String PARAMETER_TIME_SLOT_DATA = "timeSlotData";
    private static final String PARAMETER_SHIFT_SLOT = "shift_slot";

    // Marks
    private static final String MARK_TIME_SLOT = "timeSlot";
    private static final String MARK_LIST_RESERVATION_RULE = "listReservationRule";
    private static final String MARK_ID_RULE = "id_reservation_rule";
    private static final String CAN_UPDATE_ADVANCED_PARAM = "canUpdateAdvancedParam";

    // Views
    private static final String VIEW_MANAGE_TYPICAL_WEEK = "manageTypicalWeek";
    private static final String VIEW_MODIFY_TIME_SLOT = "viewModifyTimeSlot";

    // Actions
    private static final String ACTION_DO_MODIFY_TIME_SLOT = "doModifyTimeSlot";
    private static final String ACTION_DO_MODIFY_LIST_TIME_SLOT = "doModifyListTimeSlot";
    private static final String ACTION_MODIFY_ADVANCED_PARAMETERS = "modifyAdvancedParameters";
    private static final String ACTION_MODIFY_GLOBAL_PARAM = "modifyGlobalParameters";
    private static final String ACTION_CONFIRM_REMOVE_PARAMETER = "confirmRemoveParameter";
    private static final String ACTION_REMOVE_PARAMETER = "doRemoveParameter";
    private static final String ACTION_CREATE_ADVANCED_PARAMETERS = "createAdvancedParameters";
    private static final String ACTION_DO_COPY_WEEK ="copyTypicalWeek";

    // Templates
    private static final String TEMPLATE_MANAGE_TYPICAL_WEEK = "admin/plugins/appointment/slots/manage_typical_week.html";
    private static final String TEMPLATE_MODIFY_TIME_SLOT = "admin/plugins/appointment/slots/modify_time_slot.html";

    // Porperties

    // Infos
    private static final String INFO_ADVANCED_PARAMETERS_UPDATED = "appointment.info.advancedparameters.updated";
    private static final String INFO_GLOBAL_PARAMETERS_UPDATED = "appointment.info.globalparameters.updated";

    private static final String INFO_PARAMETER_REMOVED = "appointment.info.advancedparameters.removed";
    
    private AppointmentFormDTO _appointmentForm;
    private TimeSlot _timeSlot;

    /**
     * Get the view of the typical week
     * 
     * @param request
     *            the request
     * @return the page
     */
    @View( value = VIEW_MANAGE_TYPICAL_WEEK )
    public String getViewManageTypicalWeek( HttpServletRequest request )
    {
    	_timeSlot= null;
    	boolean bCanUpdateAdvancedParam= true;
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        String strIdReservationRule = request.getParameter( PARAMETER_ID_RULE );

        int nIdReservationRule = 0;
        if ( StringUtils.isNotEmpty( strIdReservationRule ) )
        {
        	nIdReservationRule = Integer.parseInt( strIdReservationRule );
        }
        LocalDate dateOfApply = LocalDate.now( );
        ReservationRule reservationRule;
        if ( nIdReservationRule != 0 )
        {
        	reservationRule = ReservationRuleService.findReservationRuleById( nIdReservationRule );
        	LocalDate dateNow= LocalDate.now( );
     		List<WeekDefinition> listWeekDefinition= WeekDefinitionService.findByReservationRule( nIdReservationRule);    
     		if(listWeekDefinition.stream().anyMatch( week -> week.getDateOfApply().isBefore( dateNow ))) {
     			
     			bCanUpdateAdvancedParam = false;
     		}
        }
        else
        {
        	reservationRule = new ReservationRule( );
        }
        
        Map<String, Object> model = getModel( );       
        List<String> listDayOfWeek = new ArrayList<>( );
        List<TimeSlot> listTimeSlot = new ArrayList<>( );
        LocalTime minStartingTime = LocalTime.MIN;
        LocalTime maxEndingTime = LocalTime.MAX;
        if ( nIdReservationRule == 0 )
        {
        	_appointmentForm = FormService.buildAppointmentFormLight( nIdForm );            

        }
        else
        {   
        	_appointmentForm = FormService.buildAppointmentForm( nIdForm, nIdReservationRule );
            List<WorkingDay> listWorkingDay = reservationRule.getListWorkingDay( );
            listDayOfWeek = new ArrayList<>( WorkingDayService.getSetDaysOfWeekOfAListOfWorkingDayForFullCalendar( listWorkingDay ) );
            listTimeSlot = TimeSlotService.getListTimeSlotOfAListOfWorkingDay( listWorkingDay, dateOfApply );
            minStartingTime = WorkingDayService.getMinStartingTimeOfAListOfWorkingDay( listWorkingDay );
            maxEndingTime = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( listWorkingDay );
        }
           
        model.put( CAN_UPDATE_ADVANCED_PARAM, bCanUpdateAdvancedParam );
        model.put( PARAMETER_DAY_OF_WEEK, listDayOfWeek );
        model.put( PARAMETER_EVENTS, listTimeSlot );
        model.put( PARAMETER_MIN_TIME, minStartingTime );
        model.put( PARAMETER_MAX_TIME, maxEndingTime );
        model.put( PARAMETER_MIN_DURATION, LocalTime.MIN.plusMinutes( AppointmentUtilities.THIRTY_MINUTES ) );
        model.put( MARK_ID_RULE, nIdReservationRule );
        model.put( MARK_LIST_RESERVATION_RULE, ReservationRuleService.findListReservationRule( nIdForm ));
        AppointmentFormJspBean.addElementsToModel( request, _appointmentForm, getUser( ), getLocale( ), model );
        return getPage( MESSAGE_TYPICAL_WEEK_PAGE_TITLE, TEMPLATE_MANAGE_TYPICAL_WEEK, model );
    }
    /**
     * Create typical week
     * @param request the request
     * @return Html Page
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_ADVANCED_PARAMETERS )
    public String doCreateAdvancedParameters( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM,
                (User) getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM );
        }
        populate( _appointmentForm, request );
        _appointmentForm.setCalendarTemplateId( 1 );
        if ( !validateBean( _appointmentForm, VALIDATION_ATTRIBUTES_PREFIX ) || !validateReservationRuleBean( request, VALIDATION_ATTRIBUTES_PREFIX )|| !checkConstraints( _appointmentForm ) )
        {
        	addError( PARAMETER_ERROR_MODIFICATION );
            return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_RULE, _appointmentForm.getIdReservationRule( ) );
        }
             
        int nIdreservationRule= ReservationRuleService.createAdvancedParameters( _appointmentForm );
        AppLogService.info( LogUtilities.buildLog( ACTION_MODIFY_ADVANCED_PARAMETERS, strIdForm, getUser( ) ) );
        addInfo( INFO_ADVANCED_PARAMETERS_UPDATED, getLocale( ) );
        return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_RULE, nIdreservationRule );
    }
    /**
     * Modify typical week
     * @param request the request
     * @return the page
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_ADVANCED_PARAMETERS )
    public String doModifyAdvancedParameters( HttpServletRequest request ) throws AccessDeniedException
    {

        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf( _appointmentForm.getIdForm( ) ), AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM,
                (User) getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM );
        }
        populate( _appointmentForm, request );
        List<Slot> listSlotsImpacted= new ArrayList<>(); 
        List<Slot>  listSlotsImpactedWithAppointment = new ArrayList<>( ); 
        
        LocalDate dateNow= LocalDate.now( );
		List<WeekDefinition> listWeekDefinition= WeekDefinitionService.findByReservationRule( _appointmentForm.getIdReservationRule( ));    
		if(listWeekDefinition.stream().anyMatch( week -> week.getDateOfApply().isBefore( dateNow ))) {
			
        	return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_MODIFICATION_WEEK_ASSIGNED_IN_PAST, AdminMessage.TYPE_STOP ) );
		}
		
        if ( !validateReservationRuleBean( _appointmentForm, VALIDATION_ATTRIBUTES_PREFIX ) || !validateBean( _appointmentForm, VALIDATION_ATTRIBUTES_PREFIX ) || !checkConstraints( _appointmentForm ) )
        {
        	addError( PARAMETER_ERROR_MODIFICATION );
            return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, _appointmentForm.getIdForm( ), PARAMETER_ID_RULE, _appointmentForm.getIdReservationRule( ) );
        }
       
		for( WeekDefinition week: listWeekDefinition ) {
        	
            listSlotsImpacted.addAll( SlotService.findSlotsByIdFormAndDateRange( _appointmentForm.getIdForm( ), week.getDateOfApply( ).atStartOfDay( ), week.getEndingDateOfApply( ).atTime( LocalTime.MAX ) ));
            listSlotsImpactedWithAppointment.addAll( SlotService.findSlotWithAppointmentByDateRange( _appointmentForm.getIdForm( ), week.getDateOfApply( ).atStartOfDay( ), week.getEndingDateOfApply( ).atTime( LocalTime.MAX ) ));

        }
        
        // if there are slots impacted
        if ( CollectionUtils.isNotEmpty( listSlotsImpacted ) )
        {
            // if there are appointments impacted
            if ( CollectionUtils.isNotEmpty( listSlotsImpactedWithAppointment ) )
            {
            	
                if ( !AppointmentUtilities.checkNoAppointmentsImpacted( listSlotsImpactedWithAppointment, _appointmentForm ) )
                {
                    addError( MESSAGE_ERROR_MODIFY_FORM_HAS_APPOINTMENTS_AFTER_DATE_OF_MODIFICATION, getLocale( ) );
                    return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, _appointmentForm.getIdForm( ), PARAMETER_ID_RULE, _appointmentForm.getIdReservationRule( ) );
                }
                manageTheSlotsAndAppointmentsImpacted( listSlotsImpactedWithAppointment, listSlotsImpacted, Boolean.TRUE, _appointmentForm.getMaxCapacityPerSlot( ),
                        Boolean.FALSE, Boolean.FALSE );
            }
            else
            {
                // No check, delete all the slots
                SlotService.deleteListSlots( listSlotsImpacted );
            }
        }
        ReservationRuleService.updateAdvancedParameters( _appointmentForm );

        AppLogService.info( LogUtilities.buildLog( ACTION_MODIFY_ADVANCED_PARAMETERS, String.valueOf( _appointmentForm.getIdForm( ) ), getUser( ) ) );
        addInfo( INFO_ADVANCED_PARAMETERS_UPDATED, getLocale( ) );
        return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, _appointmentForm.getIdForm( ), PARAMETER_ID_RULE, _appointmentForm.getIdReservationRule( ) );
    }
    /**
     * Modify global param of thr typical week
     * @param request the request
     * @return the page
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_GLOBAL_PARAM )
    public String doModifyGlobalParameters( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, String.valueOf(_appointmentForm.getIdForm( )), AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM,
                (User) getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM );
        }
        ReservationRule reservationRule= new ReservationRule( );
        reservationRule.setIdReservationRule( _appointmentForm.getIdReservationRule( ) );
        populate( _appointmentForm, request );
        ReservationRuleService.fillInReservationRule( reservationRule, _appointmentForm, _appointmentForm.getIdForm( ) );
                
        if ( !validateReservationRuleBean( _appointmentForm, VALIDATION_ATTRIBUTES_PREFIX ) || !checkMultiSlotFormTypeBookablePlaces( _appointmentForm ))
        {
        	addError( PARAMETER_ERROR_MODIFICATION );
            return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, _appointmentForm.getIdForm( ), PARAMETER_ID_RULE, _appointmentForm.getIdReservationRule( ) );
        }
      
        ReservationRuleHome.update( reservationRule );
        addInfo( INFO_GLOBAL_PARAMETERS_UPDATED, getLocale( ) );
        return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, _appointmentForm.getIdForm( ), PARAMETER_ID_RULE, _appointmentForm.getIdReservationRule( ) );
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
        url.addParameter( PARAMETER_ID_RULE, request.getParameter( PARAMETER_ID_RULE ) );
        url.addParameter( PARAMETER_ID_FORM, request.getParameter( PARAMETER_ID_FORM ) );
        
        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_WEEK_DEFINITION, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
        return redirect( request, strMessageUrl );
    }
    /**
     * Handles the removal form of a week rul
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    @Action( ACTION_REMOVE_PARAMETER )
    public String doRemoveParameter( HttpServletRequest request ) throws AccessDeniedException
    {
    	String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdReservationRule = Integer.parseInt( request.getParameter( PARAMETER_ID_RULE ) );
        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findByReservationRule( nIdReservationRule );
        if( CollectionUtils.isNotEmpty( listWeekDefinition)) {
        	
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_RULE_ASSIGNED, AdminMessage.TYPE_STOP ) );
        }
        
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM,
                (User) getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM );
        }
        ReservationRuleService.removeReservationRule( nIdReservationRule );
        addInfo( INFO_PARAMETER_REMOVED, getLocale( ) );
        return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, Integer.parseInt( strIdForm ) );        
    }
    /**
     * Copy typical week
     * @param request the request
     * @return Html Page
     * @throws AccessDeniedException
     */
    @Action( ACTION_DO_COPY_WEEK )
    public String doCopyWeek( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        int nIdReservationRule = Integer.parseInt( request.getParameter( PARAMETER_ID_RULE ) );

        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM,
                (User) getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM );
        }
        ReservationRuleService.copyReservationRule( nIdReservationRule );
        return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_RULE, nIdReservationRule  );        

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
        if ( ( _timeSlot == null ) || ( nIdTimeSlot != _timeSlot.getIdTimeSlot( ) ) )
        {
            _timeSlot = TimeSlotService.findTimeSlotById( nIdTimeSlot );
        }
        addInfo( MESSAGE_WARNING_CHANGES_APPLY_TO_ALL, getLocale( ) );
        Map<String, Object> model = getModel( );
        model.put( PARAMETER_ID_FORM, request.getParameter( PARAMETER_ID_FORM ) );
        model.put( MARK_ID_RULE, request.getParameter( PARAMETER_ID_RULE ) );
        model.put( MARK_TIME_SLOT, _timeSlot );
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
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        String strIdReservationRule = request.getParameter( PARAMETER_ID_RULE );
        int nIdReservationRule = Integer.parseInt( strIdReservationRule );
        
        String strIdTimeSlot = request.getParameter( PARAMETER_ID_TIME_SLOT );
        int nIdTimeSlot = Integer.parseInt( strIdTimeSlot );
    	TimeSlot oldTimeSlot = TimeSlotService.findTimeSlotById( nIdTimeSlot );

        if ( _timeSlot == null || nIdTimeSlot != _timeSlot.getIdTimeSlot( ) )
        {
            _timeSlot = oldTimeSlot; 
        }
        boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );
        boolean bOpeningHasChanged = false;
        int nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
        LocalTime endingTime = LocalTime.parse( request.getParameter( PARAMETER_ENDING_TIME ) );
        boolean bShiftSlot = Boolean.parseBoolean( request.getParameter( PARAMETER_SHIFT_SLOT ) );
        boolean bEndingTimeHasChanged = false;
        boolean bMaxCapacityHasChanged = false;
        LocalDate dateNow= LocalDate.now( );
        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findByReservationRule( nIdReservationRule );
        
        if(listWeekDefinition.stream().anyMatch( week -> week.getDateOfApply().isBefore( dateNow ))) {

        	return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_MODIFICATION_WEEK_ASSIGNED_IN_PAST, AdminMessage.TYPE_STOP ) );
		}
	
        if ( bIsOpen != _timeSlot.getIsOpen( ) )
        {
            _timeSlot.setIsOpen( bIsOpen );
            bOpeningHasChanged = true;
        }
        if ( nMaxCapacity != oldTimeSlot.getMaxCapacity( ) )
        {
            _timeSlot.setMaxCapacity( nMaxCapacity );
            bMaxCapacityHasChanged = true;
        }
        LocalTime previousEndingTime = oldTimeSlot.getEndingTime( );
        if ( !endingTime.equals( previousEndingTime ) )
        {
            _timeSlot.setEndingTime( endingTime );
            if ( !checkEndingTimeOfTimeSlot( endingTime, _timeSlot ) )
            {
                Map<String, String> additionalParameters = new HashMap<>( );
                additionalParameters.put( PARAMETER_ID_FORM, strIdForm );
                additionalParameters.put( PARAMETER_ID_RULE, strIdReservationRule );
                additionalParameters.put( PARAMETER_ID_TIME_SLOT, strIdTimeSlot );
                return redirect( request, VIEW_MODIFY_TIME_SLOT, additionalParameters );
            }
            bEndingTimeHasChanged = true;
        }
        List<Slot> listSlotsImpacted= new ArrayList<>();
        List<Slot> listSlotsImpactedByDate= new ArrayList<>();

        for( WeekDefinition week: listWeekDefinition ) {
        	
        	listSlotsImpacted.addAll( AppointmentUtilities.findSlotsImpactedByThisTimeSlot( _timeSlot, nIdForm, week.getIdWeekDefinition( ), bShiftSlot ));
        	listSlotsImpactedByDate.addAll( SlotService.findSlotWithAppointmentByDateRange( nIdForm, week.getDateOfApply( ).atStartOfDay( ), week.getEndingDateOfApply( ).atTime( LocalTime.MAX ) ));

        }

        // If there are slots impacted
        if ( CollectionUtils.isNotEmpty( listSlotsImpacted ) )
        {
        	List<Integer> listIdSlotsImpacted = listSlotsImpacted.stream().map( Slot::getIdSlot).collect(Collectors.toList( ));
        	List<Slot> listSlotsImpactedWithAppointment =listSlotsImpactedByDate.stream().filter(slot -> listIdSlotsImpacted.contains(slot.getIdSlot( ))).collect(Collectors.toList( ));
            // if there are appointments impacted
            if ( CollectionUtils.isNotEmpty( listSlotsImpactedWithAppointment ) )
            {
            	      // If the ending time of the time slot has changed or if the max
                // capacity has decreased
                if ( bEndingTimeHasChanged || nMaxCapacity < oldTimeSlot.getMaxCapacity( ) )
                {
                    // Error, the time slot can't be changed
                    addError( MESSAGE_ERROR_APPOINTMENT_ON_SLOT, getLocale( ) );
                    addError( listSlotsImpactedWithAppointment.size( ) + " slot impacté(s)" );
                    Map<String, String> additionalParameters = new HashMap<>( );
                    additionalParameters.put( PARAMETER_ID_FORM, strIdForm );
                    additionalParameters.put( PARAMETER_ID_RULE, strIdReservationRule );
                    additionalParameters.put( PARAMETER_ID_TIME_SLOT, strIdTimeSlot );
                    return redirect( request, VIEW_MODIFY_TIME_SLOT, additionalParameters );
                }
                // Get the slot whith appointment (the appointments that are not
                // cancelled)
                List<Slot> listSlotsWithAppointmentNotCancelled =listSlotsImpactedWithAppointment.stream().filter(slot -> slot.getNbPlacesTaken( ) > 0 ).collect( Collectors.toList( ) );
                if ( bOpeningHasChanged && CollectionUtils.isNotEmpty( listSlotsWithAppointmentNotCancelled ) )
                {
                    addInfo( MESSAGE_INFO_VALIDATED_APPOINTMENTS_IMPACTED, getLocale( ) );
                }
                manageTheSlotsAndAppointmentsImpacted( listSlotsImpactedWithAppointment, listSlotsImpacted, bMaxCapacityHasChanged, nMaxCapacity, bOpeningHasChanged,
                        bIsOpen );
            }
            else
            {
                // no need to check appointments, delete all the slots
                SlotService.deleteListSlots( listSlotsImpacted );
            }
        }
        TimeSlotService.updateTimeSlot( _timeSlot, bEndingTimeHasChanged, previousEndingTime, bShiftSlot );

        AppLogService.info( LogUtilities.buildLog( ACTION_DO_MODIFY_TIME_SLOT, strIdTimeSlot, getUser( ) ) );
        addInfo( MESSAGE_INFO_SLOT_UPDATED, getLocale( ) );
        return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_RULE, nIdReservationRule );
    }
    
    /**
     * Do modify a time slot
     * 
     * @param request
     *            the request
     * @return to the page of the typical week
     */
    @Action( ACTION_DO_MODIFY_LIST_TIME_SLOT )
    public String doModifyListTimeSlot( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
		String strCap = request.getParameter(PARAMETER_CAPACITY_MOD);
        int nIdForm = Integer.parseInt( strIdForm );
        String strIdReservationRule = request.getParameter( PARAMETER_ID_RULE );
        int nIdReservationRule = Integer.parseInt( strIdReservationRule );
        int nVarMaxCapacity= 0;
        int nMaxCapacity= -1; 
        
        String strJson= request.getParameter( PARAMETER_TIME_SLOT_DATA );
        AppLogService.debug( "slot - Received strJson : " + strJson); 
        ObjectMapper mapper = new ObjectMapper( );
   		mapper.registerModule(new JavaTimeModule( ));
        mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        
        List<TimeSlot> listTimeSlot = new ArrayList< >();
        List<TimeSlot> listTimeSlotJson = new ArrayList< >();

		try {
			
			listTimeSlotJson = mapper.readValue(strJson, new TypeReference<List<TimeSlot>>(){});
			
		} catch (  IOException e ) {

	    	AppLogService.error( MESSAGE_ERROR_PARSING_JSON + e.getMessage(), e );
            addError( MESSAGE_ERROR_PARSING_JSON, getLocale( ) );

		}
        boolean bIsOpen = Boolean.parseBoolean( request.getParameter( PARAMETER_IS_OPEN ) );
        boolean bMaxCapacityIsLower= false;
        LocalDate dateNow= LocalDate.now( );
                
        if( strCap.equals( VAR_CAP )) {     	
        
        	nVarMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );
       
        }else if ( strCap.equals( NEW_CAP )){
        	
            nMaxCapacity = Integer.parseInt( request.getParameter( PARAMETER_MAX_CAPACITY ) );

        }
        
        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findByReservationRule( nIdReservationRule );
        
        if(listWeekDefinition.stream().anyMatch( week -> week.getDateOfApply().isBefore( dateNow ))) {

        	return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_MODIFICATION_WEEK_ASSIGNED_IN_PAST, AdminMessage.TYPE_STOP ) );
		}	
        for( TimeSlot time: listTimeSlotJson ) {
        	
        	TimeSlot timeSlot= TimeSlotService.findTimeSlotById( time.getIdTimeSlot( ) );
        	if ( bIsOpen != timeSlot.getIsOpen( ) )
 	        {
        		 timeSlot.setIsOpen( bIsOpen );
 	        }
 	        if ( nMaxCapacity >= 0 && nMaxCapacity != timeSlot.getMaxCapacity( ) )
 	        {
 	        	timeSlot.setMaxCapacity( nMaxCapacity );
 	        	if( nMaxCapacity < timeSlot.getMaxCapacity( ) ) {
 	    	        
 		        	bMaxCapacityIsLower = true;
 		        } 		       
 	        }
 	        else {
 	        	if( timeSlot.getMaxCapacity( ) + nVarMaxCapacity  > 0 ) {
 	        		
 	        		timeSlot.setMaxCapacity( timeSlot.getMaxCapacity( ) + nVarMaxCapacity );
 	        	
 	        	}else {
 	        		
 	        		timeSlot.setMaxCapacity( 0 );
 	        	}
 	        	if( nVarMaxCapacity < 0 ) {
 	    	        
 		        	bMaxCapacityIsLower = true;
 		        } 	
 	        }
 	       listTimeSlot.add( timeSlot );
        }
                
        List<Slot> listSlotsImpacted= new ArrayList<>();
        List<Slot> listSlotsImpactedByDate= new ArrayList<>();
        
        for( WeekDefinition week: listWeekDefinition ) {
        	for( TimeSlot timeSlot: listTimeSlot ) {        	
        		listSlotsImpacted.addAll( AppointmentUtilities.findSlotsImpactedByThisTimeSlot( timeSlot, nIdForm, week.getIdWeekDefinition( ), false ));       
        	}
        	listSlotsImpactedByDate.addAll( SlotService.findSlotWithAppointmentByDateRange( nIdForm, week.getDateOfApply( ).atStartOfDay( ), week.getEndingDateOfApply( ).atTime( LocalTime.MAX ) ));
        }
                
        // If there are slots impacted
        if ( CollectionUtils.isNotEmpty( listSlotsImpacted ) )
        {
        	List<Integer> listIdSlotsImpacted = listSlotsImpacted.stream().map( Slot::getIdSlot).collect(Collectors.toList());
        	List<Slot> listSlotsImpactedWithAppointment= listSlotsImpactedByDate.stream().filter(slot -> listIdSlotsImpacted.contains(slot.getIdSlot( ))).collect(Collectors.toList());

            // if there are appointments impacted
            if ( CollectionUtils.isNotEmpty( listSlotsImpactedWithAppointment ) )
            {            
	            //if the max capacity has decreased
	            if ( bMaxCapacityIsLower )
	            {
	             // Error, the time slot can't be changed
	                addError( MESSAGE_ERROR_APPOINTMENT_ON_SLOT, getLocale( ) );
	                addError( listSlotsImpactedWithAppointment.size( ) + " slot impacté(s)" );
	                Map<String, String> additionalParameters = new HashMap<>( );
	                additionalParameters.put( PARAMETER_ID_FORM, strIdForm );
	                additionalParameters.put( PARAMETER_ID_RULE, strIdReservationRule );
	                return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, additionalParameters );
	              }
            	                
                manageTheSlotsAndAppointmentsImpacted( listSlotsImpactedWithAppointment, listSlotsImpacted, nMaxCapacity, nVarMaxCapacity,
                        bIsOpen );
            }
            else
            {
                // no need to check appointments, delete all the slots
                SlotService.deleteListSlots( listSlotsImpacted );
            }
        }
        
        TimeSlotService.updateListTimeSlot( listTimeSlot );
        if( CollectionUtils.isNotEmpty(listTimeSlot)  && CollectionUtils.isNotEmpty( listWeekDefinition )) {
        	
        	WeekDefinitionManagerListener.notifyListenersListWeekDefinitionChanged( nIdForm);
        }

        addInfo( MESSAGE_INFO_SLOT_UPDATED, getLocale( ) );
        return redirect( request, VIEW_MANAGE_TYPICAL_WEEK, PARAMETER_ID_FORM, nIdForm, PARAMETER_ID_RULE, nIdReservationRule );
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
    private void manageTheSlotsAndAppointmentsImpacted( List<Slot> listSlotsImpactedWithAppointments, List<Slot> listSlotsImpacted,
            boolean bMaxCapacityHasChanged, int nMaxCapacity, boolean bOpeningHasChanged, boolean bIsOpen )
    {
        // Need to delete the slots that are impacted but with no
        // appointments
    	List<Integer> listIdSlotsImpactedWithAppointments = listSlotsImpactedWithAppointments.stream().map( Slot::getIdSlot).collect(Collectors.toList());    	
        List<Slot> listslotImpactedWithoutAppointments =  listSlotsImpacted.stream().filter( p -> !listIdSlotsImpactedWithAppointments.contains( p.getIdSlot( ) ) ).collect(Collectors.toList());
            
        SlotService.deleteListSlots( listslotImpactedWithoutAppointments );
        for ( Slot slotImpacted : listSlotsImpactedWithAppointments )
        {
            Lock lock = SlotSafeService.getLockOnSlot( slotImpacted.getIdSlot( ) );
            lock.lock( );
            try
            {
            	slotImpacted = updateRemainingPlaces( slotImpacted, bMaxCapacityHasChanged, nMaxCapacity, bOpeningHasChanged, bIsOpen );
                SlotSafeService.updateSlot( slotImpacted );
            }
            finally
            {
                lock.unlock( );
            }
        }
    }
    
    /**
     * Update the slots with appointments impacted by a modification of a typical week or a modification of a timeSlot Delete the slots with no appointments
     * 
     * @param listAppointmentsImpacted
     *            the appointments impacted
     * @param listSlotsImpacted
     *            the slots impacted     
     * @param nMaxCapacity
     *            the max capacity
     * @param bIsOpen
     *            the new boolean opening value
     */
    private void manageTheSlotsAndAppointmentsImpacted( List<Slot> listSlotsImpactedWithAppointments, List<Slot> listSlotsImpacted, int nMaxCapacity, int nVarMaxCapacity,  boolean bIsOpen )
    {
    	boolean bOpeningHasChanged= false;
    	boolean binfoOpeningHasChanged= false;
    	boolean bMaxCapacityHasChanged= false;
        // Need to delete the slots that are impacted but with no
        // appointments
    	List<Integer> listIdSlotsImpactedWithAppointments = listSlotsImpactedWithAppointments.stream().map( Slot::getIdSlot).collect(Collectors.toList());    	
        List<Slot> listslotImpactedWithoutAppointments =  listSlotsImpacted.stream().filter( p -> !listIdSlotsImpactedWithAppointments.contains( p.getIdSlot( ) ) ).collect(Collectors.toList());
         
        SlotService.deleteListSlots( listslotImpactedWithoutAppointments );
        for ( Slot slotImpacted : listSlotsImpactedWithAppointments )
        {
        	bOpeningHasChanged= false;
        	bMaxCapacityHasChanged= false;
        	
            Lock lock = SlotSafeService.getLockOnSlot( slotImpacted.getIdSlot( ) );
            lock.lock( );
            try
            {
            	if( ( nMaxCapacity != -1 && slotImpacted.getMaxCapacity() != nMaxCapacity) ) {
            		
            		bMaxCapacityHasChanged= true;
            	}else if( nVarMaxCapacity != 0 ) {
            		
            		nMaxCapacity= (slotImpacted.getMaxCapacity() + nVarMaxCapacity) >= 0? slotImpacted.getMaxCapacity() + nVarMaxCapacity:0 ;
            		bMaxCapacityHasChanged= true;

            	}
            	if( slotImpacted.getIsOpen( ) != bIsOpen ) {
            		
            		bOpeningHasChanged= true;
            		binfoOpeningHasChanged= true;
            	}
            	slotImpacted = updateRemainingPlaces( slotImpacted, bMaxCapacityHasChanged, nMaxCapacity, bOpeningHasChanged, bIsOpen );
                SlotSafeService.updateSlot( slotImpacted );
            }
            finally
            {
                lock.unlock( );
            }
        }
        // Get the slot whith appointment (the appointments that are not
        // cancelled)
        List<Slot> listSlotsWithAppointmentNotCancelled =listSlotsImpactedWithAppointments.stream().filter(slot -> slot.getNbPlacesTaken( ) > 0 ).collect( Collectors.toList( ) );
        if ( binfoOpeningHasChanged && CollectionUtils.isNotEmpty( listSlotsWithAppointmentNotCancelled ) )
        {
            addInfo( MESSAGE_INFO_VALIDATED_APPOINTMENTS_IMPACTED, getLocale( ) );
        }
    }
    /**
     * Update the capacity of the slot
     * 
     * @param slot
     *            the slot to update
      * @param bMaxCapacityHasChanged
     *            True if the capacity has changed
     * @param nMaxCapacity
     *            the max capacity
     * @param bOpeningHasChanged
     *            true if the opening has changed
     * @param bIsOpen
     *            the new boolean opening value
     * Return the slot updated          
     */
    private static Slot updateRemainingPlaces( Slot slot, boolean bMaxCapacityHasChanged, int nNewNbMaxCapacity, boolean bOpeningHasChanged, boolean bIsOpen )
    {
        slot = SlotHome.findByPrimaryKey( slot.getIdSlot( ) );
        // If the max capacity has been modified
        if ( bMaxCapacityHasChanged )
        {
            int nOldBnMaxCapacity = slot.getMaxCapacity( );            
            nNewNbMaxCapacity= (nNewNbMaxCapacity >= 0)? nNewNbMaxCapacity:0;
            // Need to add the diff between the old value and the new value
            // to the remaining places (if the new is higher)
            if ( nNewNbMaxCapacity > nOldBnMaxCapacity )
            {
                int nValueToAdd = nNewNbMaxCapacity - nOldBnMaxCapacity;
                slot.setNbPotentialRemainingPlaces( slot.getNbPotentialRemainingPlaces( ) + nValueToAdd );
                slot.setNbRemainingPlaces( slot.getNbRemainingPlaces( ) + nValueToAdd );
            }
            else
            {
                // the new value is lower than the previous capacity
                // !!!! If there are appointments on this slot and if the
                // slot is already full, the slot will be surbooked !!!!
                int nValueToSubstract = nOldBnMaxCapacity - nNewNbMaxCapacity;
                slot.setNbPotentialRemainingPlaces(  slot.getNbPotentialRemainingPlaces( ) - nValueToSubstract  );
                slot.setNbRemainingPlaces(  slot.getNbRemainingPlaces( ) - nValueToSubstract  );
            }
            slot.setMaxCapacity(nNewNbMaxCapacity);
        }
        if ( bOpeningHasChanged )
        {
        	slot.setIsSpecific( bIsOpen );
        }

        return slot;
    }
}