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
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
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
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.date.DateUtil;

/**
 * JspBean to manage calendar slots
 * 
 * @author Laurent Payen
 *
 */
@Controller( controllerJsp = AppointmentAnnualCalendarJspBean.JSP_MANAGE_ANNUAL_CALENDAR, controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM )
public class AppointmentAnnualCalendarJspBean extends AbstractAppointmentFormAndSlotJspBean
{
    /**
     * JSP of this JSP Bean
     */
    public static final String JSP_MANAGE_ANNUAL_CALENDAR = "ManageAnnualCalendar.jsp";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 2376721852596997810L;

    // Messages
    private static final String MESSAGE_ANNUAL_CALENDAR_PAGE_TITLE = "appointment.annual.calendar.pageTitle";
    private static final String MESSAGE_ERROR_REMOVE_WEEK = "appointment.message.error.removeWeek";
    private static final String MESSAGE_ERROR_MODIFICATION = "appointment.message.error.errorModification";
    private static final String MESSAGE_ERROR_REMOVE_WEEK_DATE_PASSED ="appointment.message.error.removeWeek.date.passed";
    private static final String MESSAGE_ERROR_MODIFY_FORM_HAS_APPOINTMENTS_AFTER_DATE_OF_MODIFICATION = "appointment.message.error.refreshDays.modifyFormHasAppointments";
    private static final String MESSAGE_INFO_VALIDATED_APPOINTMENTS_IMPACTED = "appointment.modifyCalendarSlots.messageValidatedAppointmentsImpacted";
    private static final String MESSAGE_INFO_MULTI_SURBOOKING = "appointment.modifyCalendarMultiSlots.messageSurbooking";
    private static final String INFO_PARAMETER_REMOVED = "appointment.info.advancedparameters.removed";
    private static final String INFO_ADVANCED_PARAMETERS_UPDATED = "appointment.info.advancedparameters.updated";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointmentform.attribute.";
   
    // Parameters
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ID_WEEK_DEFINITION = "id_week_definition";
    private static final String PARAMETER_DATE_OF_APPLY = "date_of_apply";
    private static final String PARAMETER_DATE_END_OF_APPLY = "ending_date_of_apply";
    private static final String PARAMETER_ID_RESERVATION_RULE = "id_reservation_rule";
    private static final String PARAMETER_START_YEAR= "start_year";
    // Mrker
    private static final String MARK_LIST_RESERVATION_RULE = "listReservationRule";
    private static final String MARK_LIST_WEEK_DEFINITION = "listWeekDefinition";
    private static final String MARK_ID_FORM = "id_form";
    private static final String MARK_LOCALE_TINY = "locale";
    private static final String MARK_START_YEAR= "start_year";
    // Views
    private static final String VIEW_MANAGE_ANNUAL_CALENDAR = "manageAnnualCalendar";
    // Actions
    private static final String ACTION_UNASSIGN_WEEK = "doUnssignWeek";
    private static final String ACTION_REMOVE_WEEK_FROM_CALENDAR = "doRemoveWeekFromCalendar";
    private static final String ACTION_ASSIGN_WEEK = "doAssignWeek";

    // Templates
    private static final String TEMPLATE_MANAGE_ANNUAL_CALENDAR = "admin/plugins/appointment/slots/manage_annual_calendar.html";
   
    // Session variable to store working values
   
    // Porperties

    // Infos
 
    /**
     * Get the view of the typical week
     * 
     * @param request
     *            the request
     * @return the page
     */
    @View( value = VIEW_MANAGE_ANNUAL_CALENDAR)
    public String getViewManageAnnualCalendar( HttpServletRequest request )
    {
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        AppointmentFormDTO form = buildAppointmentFormLight( nIdForm );
        String strStartYear = request.getParameter( PARAMETER_START_YEAR );
        int nStartYear;
        List<WeekDefinition> listWeek= WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<ReservationRule> listRule= ReservationRuleHome.findByIdForm( nIdForm );
        if( StringUtils.isNotEmpty( strStartYear) && StringUtils.isNumeric( strStartYear )) {        	
        	nStartYear= Integer.parseInt(strStartYear);        
        }else {
        	
        	nStartYear= LocalDate.now( ).getYear();
        }        
        Map<String, Object> model = getModel( );
        model.put( MARK_LIST_WEEK_DEFINITION, listWeek );
        model.put( MARK_LIST_RESERVATION_RULE, listRule );
        model.put( MARK_ID_FORM,  nIdForm );
        model.put(MARK_START_YEAR, nStartYear);
        model.put(MARK_LOCALE_TINY, getLocale( ) );
        AppointmentFormJspBean.addElementsToModel( request, form, getUser( ), getLocale( ), model );

        return getPage( MESSAGE_ANNUAL_CALENDAR_PAGE_TITLE, TEMPLATE_MANAGE_ANNUAL_CALENDAR, model );
    	
    }
    /**
     * Assign a wek to the annual calendar
     * @param request the request
     * @return the page
     * @throws AccessDeniedException the AccessDeniedException
     */
    @Action( ACTION_ASSIGN_WEEK)
    public String doAssignWeek( HttpServletRequest request ) throws AccessDeniedException
    {
    	 String strIdForm = request.getParameter( PARAMETER_ID_FORM );    	 
         int nIdForm = Integer.parseInt( strIdForm );
         List<Slot> listSlotsImpacted= new ArrayList<>();  
         List<WeekDefinition> listWeek= new ArrayList<>();        
         AppointmentFormDTO form = buildAppointmentFormLight( nIdForm );

        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM,
                (User) getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM );
        }
    	WeekDefinition newWeek= new WeekDefinition( );   	
        populate(newWeek, request);
        
        if ( !validateBean( newWeek, VALIDATION_ATTRIBUTES_PREFIX ) || !checkConstraints( newWeek )  )
        {       	
        	addError( MESSAGE_ERROR_MODIFICATION, getLocale( ) );
            return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, newWeek.getDateOfApply().getYear() );
        }
        listWeek.add( newWeek );
        if( AppointmentUtilities.weekIsOpenInFO( form, listWeek , getLocale( ))) {
			
			addError( ERROR_MESSAGE_WEEK_IS_OPEN_FO , getLocale( ));
            return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, newWeek.getDateOfApply().getYear() );
   
		}
        ReservationRule reservationRule= ReservationRuleService.findReservationRuleById( newWeek.getIdReservationRule( ));
        listSlotsImpacted.addAll( SlotHome.findByIdFormAndDateRange( nIdForm, newWeek.getDateOfApply( ).atStartOfDay( ), newWeek.getEndingDateOfApply( ).atTime( LocalTime.MAX ) ));    
        List<Slot> listSlotsImpactedWithAppointment= SlotService.findSlotWithAppointmentByDateRange( nIdForm, newWeek.getDateOfApply( ).atStartOfDay( ), newWeek.getEndingDateOfApply( ).atTime( LocalTime.MAX ) );
      
        if ( CollectionUtils.isNotEmpty( listSlotsImpacted ) )
        {
            // if there are appointments impacted           
            if ( CollectionUtils.isNotEmpty( listSlotsImpactedWithAppointment ) && !AppointmentUtilities.checkNoAppointmentsImpacted( listSlotsImpactedWithAppointment, reservationRule.getIdReservationRule( ) ) )
            {
                addError( MESSAGE_ERROR_MODIFY_FORM_HAS_APPOINTMENTS_AFTER_DATE_OF_MODIFICATION, getLocale( ) );
                return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, newWeek.getDateOfApply().getYear() );
            }   
            updateSlotImpacted( listSlotsImpacted, listSlotsImpactedWithAppointment,reservationRule );
        }        
        WeekDefinitionService.assignWeekDefinition( nIdForm, newWeek );
        addInfo(INFO_ADVANCED_PARAMETERS_UPDATED,getLocale( ));

        return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, newWeek.getDateOfApply().getYear() );

    }
    /**
     * Unassign a week
     * @param request the request
     * @return the page
     * @throws AccessDeniedException
     */
    @Action( ACTION_UNASSIGN_WEEK)
    public String doUnassignWeek( HttpServletRequest request ) throws AccessDeniedException
    {
    	String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );
        AppointmentFormDTO form = buildAppointmentFormLight( nIdForm ) ;
        List<Slot> listSlotsImpacted= new ArrayList<>();     
        List<WeekDefinition> listWeek= new ArrayList<>();        
        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM,
                (User) getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM );
        }
        
        WeekDefinition week= new WeekDefinition( );    	
        populate(week, request);
        week.setIdReservationRule( 0 );
        
        if ( !checkConstraints( week )  )
        {        	
        	addError( MESSAGE_ERROR_REMOVE_WEEK_DATE_PASSED, getLocale( ) );
            return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, week.getDateOfApply().getYear() );
        }
        listWeek.add( week );
        if( AppointmentUtilities.weekIsOpenInFO( form, listWeek , getLocale( ))) {
        	
			addError( ERROR_MESSAGE_WEEK_IS_OPEN_FO , getLocale( ));
            return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, week.getDateOfApply().getYear() );
		}
        listSlotsImpacted.addAll( SlotHome.findByIdFormAndDateRange( nIdForm, week.getDateOfApply( ).atStartOfDay( ), week.getEndingDateOfApply( ).atTime( LocalTime.MAX ) ));    
        List<Slot> listSlotsImpactedWithAppointment= SlotService.findSlotWithAppointmentByDateRange( nIdForm, week.getDateOfApply( ).atStartOfDay( ), week.getEndingDateOfApply( ).atTime( LocalTime.MAX ) );
      
        if ( CollectionUtils.isNotEmpty( listSlotsImpacted ) )
        {
            // if there are appointments impacted            
            if ( CollectionUtils.isNotEmpty( listSlotsImpactedWithAppointment ) )
            {
                addError( MESSAGE_ERROR_MODIFY_FORM_HAS_APPOINTMENTS_AFTER_DATE_OF_MODIFICATION, getLocale( ) );
                return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, week.getDateOfApply().getYear() );
            }   
            SlotService.deleteListSlots( listSlotsImpacted );
        }
        
        WeekDefinitionService.assignWeekDefinition( nIdForm, week );      
        addInfo(INFO_PARAMETER_REMOVED,getLocale( ));

        return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, week.getDateOfApply().getYear() );
    }
    /**
     * Unassign a week
     * @param request the request
     * @return the page
     * @throws AccessDeniedException
     */
    @Action( ACTION_REMOVE_WEEK_FROM_CALENDAR )
    public String doRemoveWeekFromCalendar( HttpServletRequest request ) throws AccessDeniedException
    {
    	String strIdForm = request.getParameter( PARAMETER_ID_FORM );
    	String strIdWeekDefinition = request.getParameter( PARAMETER_ID_WEEK_DEFINITION );
        int nIdForm = Integer.parseInt( strIdForm );
        AppointmentFormDTO form = buildAppointmentFormLight( nIdForm ) ;
        List<WeekDefinition> listWeek= new ArrayList<>();        
        List<Slot> listSlotsImpacted= new ArrayList<>();               		

        if ( !RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE, strIdForm, AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM,
                (User) getUser( ) ) )
        {
            throw new AccessDeniedException( AppointmentResourceIdService.PERMISSION_MODIFY_ADVANCED_SETTING_FORM );
        }
        WeekDefinition weekToDelete= WeekDefinitionService.findWeekDefinitionById(Integer.parseInt( strIdWeekDefinition ));
        ReservationRule rule= ReservationRuleHome.findByPrimaryKey( weekToDelete.getIdReservationRule( ) );
        if( rule.getIdForm( ) !=  nIdForm) {
        	
        	 addError( MESSAGE_ERROR_REMOVE_WEEK, getLocale() );
             return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, weekToDelete.getDateOfApply().getYear() );

        }
        LocalDate dateNow= LocalDate.now( );
        if( weekToDelete.getDateOfApply().isBefore( dateNow )) {
        	
        	 addError( MESSAGE_ERROR_REMOVE_WEEK_DATE_PASSED, getLocale() );
             return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, weekToDelete.getDateOfApply().getYear() );

        }
        listWeek.add( weekToDelete );
        if( AppointmentUtilities.weekIsOpenInFO( form, listWeek , getLocale( ))) {
        	
			addError( ERROR_MESSAGE_WEEK_IS_OPEN_FO , getLocale( ));
            return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, weekToDelete.getDateOfApply().getYear() );
		}
        List<Slot> listSlotsImpactedWithAppointment= SlotHome.findSlotWithAppointmentByDateRange(nIdForm, weekToDelete.getDateOfApply( ).atStartOfDay( ), weekToDelete.getEndingDateOfApply( ).atTime( LocalTime.MAX ) );
        
        if ( CollectionUtils.isNotEmpty( listSlotsImpactedWithAppointment ))
        {
            addError( MESSAGE_INFO_VALIDATED_APPOINTMENTS_IMPACTED, getLocale() );
            return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, weekToDelete.getDateOfApply().getYear() );
        }             
        listSlotsImpacted.addAll( SlotService.findSlotsByIdFormAndDateRange( nIdForm, weekToDelete.getDateOfApply( ).atStartOfDay( ), weekToDelete.getEndingDateOfApply( ).atTime( LocalTime.MAX ) ));       
        SlotService.deleteListSlots( listSlotsImpacted );
		WeekDefinitionService.removeWeekDefinition( weekToDelete.getIdWeekDefinition( ) );      
        addInfo(INFO_PARAMETER_REMOVED,getLocale( ));

        return redirect( request, VIEW_MANAGE_ANNUAL_CALENDAR, PARAMETER_ID_FORM, nIdForm, PARAMETER_START_YEAR, weekToDelete.getDateOfApply().getYear() );


    }
    /**
     * Update the slots with appointments impacted by a modification of a typical week or a modification of a timeSlot Delete the slots with no appointments
     * 
     * @param listSlotsImpacted
     *            the slots impacted
     * @param nMaxCapacity
     *            the max capacity
     */  
    private void updateSlotImpacted( List<Slot> listSlotsImpacted, List<Slot> listSlotsImpactedWithAppointments, ReservationRule reservationRule )
    {
        // Need to delete the slots that are impacted but with no appointments
    	List<Integer> listIdSlotsImpactedWithAppointments = listSlotsImpactedWithAppointments.stream().map( Slot::getIdSlot).collect(Collectors.toList());    	
        List<Slot> listslotImpactedWithoutAppointments =  listSlotsImpacted.stream().filter( p -> !listIdSlotsImpactedWithAppointments.contains( p.getIdSlot( ) ) ).collect(Collectors.toList());
        int nMaxCapacity= reservationRule.getMaxCapacityPerSlot( );
        WorkingDay workingDay= null;
        TimeSlot timeSlot= null;
    	StringBuilder sbAlert = new StringBuilder( );
        boolean bOpeningHasChanged = false;
        SlotService.deleteListSlots( listslotImpactedWithoutAppointments );

        for ( Slot slotImpacted : listSlotsImpactedWithAppointments )
        {
        	workingDay= WorkingDayService.getWorkingDayOfDayOfWeek( reservationRule.getListWorkingDay( ), slotImpacted.getStartingDateTime().getDayOfWeek( ) );
        	timeSlot= TimeSlotService.getTimeSlotInListOfTimeSlotWithStartingTime( workingDay.getListTimeSlot( ), slotImpacted.getStartingTime( ));
        	nMaxCapacity= timeSlot.getMaxCapacity( );
            Lock lock = SlotSafeService.getLockOnSlot( slotImpacted.getIdSlot( ) );
            lock.lock( );
            try
            {
               	
            	slotImpacted = SlotHome.findByPrimaryKey( slotImpacted.getIdSlot( ) );
                 int nOldBnMaxCapacity = slotImpacted.getMaxCapacity( );
                 // If the max capacity has been modified
                 if ( nMaxCapacity != nOldBnMaxCapacity )
                 {
                 // Need to update the remaining places
                 // Need to add the diff between the old value and the new value
                 // to the remaining places (if the new is higher)
                    if ( nMaxCapacity > nOldBnMaxCapacity )
                    {
                       int nValueToAdd = nMaxCapacity - nOldBnMaxCapacity;
                       slotImpacted.setNbPotentialRemainingPlaces( slotImpacted.getNbPotentialRemainingPlaces( ) + nValueToAdd );
                       slotImpacted.setNbRemainingPlaces( slotImpacted.getNbRemainingPlaces( ) + nValueToAdd );
                    }
                    else
                    {
                    // the new value is lower than the previous capacity
                    // !!!! If there are appointments on this slot and if the
                    // slot is already full, the slot will be surbooked !!!!
                       int nValueToSubstract = nOldBnMaxCapacity - nMaxCapacity;
                       slotImpacted.setNbPotentialRemainingPlaces(  slotImpacted.getNbPotentialRemainingPlaces( ) - nValueToSubstract  );
                       slotImpacted.setNbRemainingPlaces(  slotImpacted.getNbRemainingPlaces( ) - nValueToSubstract  );
                    }
                    }
	                if( slotImpacted.getIsOpen( ) && !timeSlot.getIsOpen( )) {
	                	 
	                	bOpeningHasChanged= true;
	                }
                    slotImpacted.setIsOpen(timeSlot.getIsOpen( ));
                    slotImpacted.setIsSpecific( false );
                    slotImpacted.setMaxCapacity( nMaxCapacity );
                    
                    if( slotImpacted.getMaxCapacity( ) < slotImpacted.getNbPlacesTaken( ) ) {		            	
		            	sbAlert.append( slotImpacted.getStartingDateTime() );
		            	sbAlert.append( "-" );
		            	sbAlert.append( slotImpacted.getEndingDateTime() );
		            	sbAlert.append( ", " );
		            }
                    
                SlotSafeService.updateSlot( slotImpacted );
            }
            finally
            {
                lock.unlock( );
            }
        }
        if ( bOpeningHasChanged )
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
	 * Check Constraints
	 * @param week the week 
	 * @return boolean
	 */
    private boolean checkConstraints( WeekDefinition week )
    {
        LocalDate dateNow= LocalDate.now( );
    	if( week.getDateOfApply().isAfter( week.getEndingDateOfApply( ) ) || dateNow.isAfter( week.getDateOfApply( ) )) {
        	
    		addError( MESSAGE_ERROR_MODIFICATION, getLocale( ) );
    		return false;
    	}
    
    	return true;
    }
    private void populate(WeekDefinition week, HttpServletRequest request) {
    	
   	 	String dateOfApplay = request.getParameter( PARAMETER_DATE_OF_APPLY );
   	 	String dateEndOfApplay = request.getParameter( PARAMETER_DATE_END_OF_APPLY );
   	 	String idReservationRule = request.getParameter( PARAMETER_ID_RESERVATION_RULE );
   	     
   	 	week.setDateOfApply(DateUtil.formatDate( dateOfApplay , getLocale( ) ).toInstant( ).atZone( ZoneId.systemDefault( ) ).toLocalDate( ));
   	 	week.setEndingDateOfApply(DateUtil.formatDate( dateEndOfApplay , getLocale( ) ).toInstant( ).atZone( ZoneId.systemDefault( ) ).toLocalDate( ));
   	 	week.setIdReservationRule(Integer.parseInt( idReservationRule ));
    } 
    /**
     * Build an appointmentForm light
     * 
     * @param nIdForm
     *            the form Id
     * @return the appointmentForm DTO
     */
    private AppointmentFormDTO buildAppointmentFormLight( int nIdForm ) {
    	
        AppointmentFormDTO form = FormService.buildAppointmentFormLight(nIdForm) ;
        Display display = DisplayService.findDisplayWithFormId( form.getIdForm( ) );
        form.setNbWeeksToDisplay( display.getNbWeeksToDisplay( ) );
        return form;
    }

}
