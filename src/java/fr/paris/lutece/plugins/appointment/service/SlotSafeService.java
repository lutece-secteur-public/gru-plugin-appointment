package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Period;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.exception.AppointmentSavedException;
import fr.paris.lutece.plugins.appointment.exception.SlotFullException;
import fr.paris.lutece.plugins.appointment.service.listeners.AppointmentListenerManager;
import fr.paris.lutece.plugins.appointment.service.listeners.SlotListenerManager;
import fr.paris.lutece.plugins.appointment.service.lock.TimerForLockOnSlot;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.sql.TransactionManager;

public final class SlotSafeService {
	
    private static final String PROPERTY_REF_ENCRYPTION_ALGORITHM = "appointment.refEncryptionAlgorithm";
    private static final String CONSTANT_SHA256 = "SHA-256";
    private static final String PROPERTY_REF_SIZE_RANDOM_PART = "appointment.refSizeRandomPart";
    private static final String CONSTANT_SEPARATOR = "$";
	 
    /**
     * Get the number of characters of the random part of appointment reference
     */
    private static final int CONSTANT_REF_SIZE_RANDOM_PART = 5;
    
    private static final ConcurrentMap<Integer, Object> _listSlot= new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, Object> _lockFormId = new ConcurrentHashMap<>( );
    

    
    /**
     * Private constructor - this class does not need to be instantiated
     */
    private SlotSafeService( )
    {
    }
    /**
     * Get the slot in memory
     * @return Map of slot 
     */
    public static Map<Integer, Object> getListSlotInMemory(){
    	
    	return _listSlot;
    }
    /**
     * get lock for slot
     * @param nIdSlot the Id Slot
     * @return return the lock
     */
    public static Object getLockOnSlot( int nIdSlot )
    {
    	if( nIdSlot == 0){
    		return new Object();
    	}
    	_listSlot.putIfAbsent( nIdSlot, new Object( ) );
        return _listSlot.get( nIdSlot );
    }
  
    /**
     * remove slot in map memory
     * @param nIdSlot the Id Slot
     */
    public static void removeSlotInMemory( int nIdSlot ){
    	
    	_listSlot.remove(nIdSlot);
    }
	  /**
	  * get lock for form 
	  * @param nIdform Id from
	  * @return return lock 
	  */
    private static Object getLockOnForm( int nIdform )
    {
        _lockFormId.putIfAbsent( nIdform, new Object( ) );
        return _lockFormId.get( nIdform );
    }
    /**
     * Create slot 
     * @param slot 
     * @return slot 
     */
    public static Slot createSlot( Slot slot )
    {
        Object formLock = getLockOnForm( slot.getIdForm( ) );
        synchronized ( formLock )
        {
    	    Slot slotSaved= null;
    	    HashMap<LocalDateTime, Slot> slotInDbMap = SlotService.buildMapSlotsByIdFormAndDateRangeWithDateForKey(slot.getIdForm(), slot.getStartingDateTime(), slot.getEndingDateTime() );
            if ( !slotInDbMap.isEmpty( ) )
            {
                slotSaved = slotInDbMap.get( slot.getStartingDateTime( ) );
            }else{
            	
            	 slotSaved = SlotHome.create( slot );
                 SlotListenerManager.notifyListenersSlotCreation( slot.getIdSlot( ) );
            }
        
            return slotSaved;

        }
    }

    

    /**
     * Update potential remaining places
     * @param nbPotentialRemainingPlaces the nbPotentialRemainingPlaces
     * @param nIdSlot the is Slot
     * @param timer the timer
     */
	public  static  void incrementPotentialRemainingPlaces( int nbPotentialRemainingPlaces, int nIdSlot, TimerForLockOnSlot timer){
	    
		
		 Object lock = getLockOnSlot( nIdSlot );
		 synchronized (lock) {
			 Slot slot= SlotService.findSlotById( nIdSlot);
			 if( timer!= null && !timer.isCancelled() && slot != null ){
					 
				 int nNewPotentialRemainingPlaces = slot.getNbPotentialRemainingPlaces() + nbPotentialRemainingPlaces;
			     slot.setNbPotentialRemainingPlaces( nNewPotentialRemainingPlaces );
			     SlotHome.updatePotentialRemainingPlaces(nNewPotentialRemainingPlaces, nIdSlot);
			     
			 }
	 	 }   	
	}
	/**
     * Update potential remaining places
     * @param nbPotentialRemainingPlaces the nbPotentialRemainingPlaces
     * @param nIdSlot the is Slot
     */
	public  static void decrementPotentialRemainingPlaces( int nbPotentialRemainingPlaces, int nIdSlot){
    	
		 Object lock = getLockOnSlot( nIdSlot );
		 synchronized (lock) {
			   Slot slot= SlotService.findSlotById( nIdSlot);
			   if( slot!= null ){
			    	int nNewPotentialRemainingPlaces = slot.getNbPotentialRemainingPlaces() - nbPotentialRemainingPlaces;
			    	slot.setNbPotentialRemainingPlaces( nNewPotentialRemainingPlaces );
			    	SlotHome.updatePotentialRemainingPlaces(nNewPotentialRemainingPlaces, nIdSlot);
			   }

		 }		
			 
    }

    /**
     * Save a slot in database
     * 
     * @param slot
     *            the slot to save
     * @return the slot saved
     */
	public  static int saveAppointment( AppointmentDTO appointmentDTO, HttpServletRequest request ) 
    {
    	boolean bIsUpdate= false;
    	 Slot slot = appointmentDTO.getSlot( );
    	 Object lock = getLockOnSlot( slot.getIdSlot() );
		 synchronized (lock) {
	    	 //avoid duplicate appointment
	         if( appointmentDTO.getIsSaved( ) ){
	    		 throw new AppointmentSavedException( "Appointment is already saved " );
	    	 }
	         
	    	 if ( appointmentDTO.getSlot( ).getIdSlot( ) != 0 )
	         {
	    		 //recovery of the slot in the bdd to manage the concurrent access
	             slot = SlotService.findSlotById( appointmentDTO.getSlot( ).getIdSlot( ) );
	         }
	    	   
	    	   
	    	 if ( slot == null || appointmentDTO.getNbBookedSeats( ) > slot.getNbRemainingPlaces( ) || slot.getEndingDateTime().isBefore(LocalDateTime.now( )))
	         {
	    		 throw new SlotFullException( "ERROR SLOT FULL" );
	         
	         }
	    	// Create or update the user
		    User user = UserService.saveUser( appointmentDTO );
	    	TransactionManager.beginTransaction( AppointmentPlugin.getPlugin( ) );
	
	        try
	        {
		        // if it's an update for modification of the date of the appointment
		        if ( appointmentDTO.getIdAppointment( ) != 0 && appointmentDTO.getSlot( ).getIdSlot( ) != appointmentDTO.getIdSlot( ) )
		        {
		            // Need to update the old slot
		            updateRemaningPlacesWithAppointmentMovedDeletedOrCanceled( appointmentDTO.getNbBookedSeats( ), appointmentDTO.getIdSlot( ) );
		            // Need to remove the workflow resource to reload again the workflow
		            // at the first step
		            bIsUpdate=true;		            	            
		        }
		        // Update of the remaining places of the slot
		      
		        int oldNbRemainingPLaces = slot.getNbRemainingPlaces( );
		        int nbMaxPotentialBookedSeats = appointmentDTO.getNbMaxPotentialBookedSeats( );
		        int oldNbPotentialRemaningPlaces = slot.getNbPotentialRemainingPlaces( );
		        int oldNbPlacesTaken = slot.getNbPlacesTaken( );
		        int effectiveBookedSeats = appointmentDTO.getNbBookedSeats( );
		        int newNbRemainingPlaces = 0;
		        int newPotentialRemaningPlaces = 0;
		        int newNbPlacesTaken = 0;
		        if ( appointmentDTO.getIdAppointment( ) == 0 || appointmentDTO.getSlot( ).getIdSlot( ) != appointmentDTO.getIdSlot( ) )
		        {
		            newNbRemainingPlaces = oldNbRemainingPLaces - effectiveBookedSeats;
		            newPotentialRemaningPlaces = oldNbPotentialRemaningPlaces + nbMaxPotentialBookedSeats - effectiveBookedSeats;
		            newNbPlacesTaken = oldNbPlacesTaken + effectiveBookedSeats;
		        }
		        else
		        {
		            // It is an update of the appointment
		            Appointment oldAppointment = AppointmentService.findAppointmentById( appointmentDTO.getIdAppointment( ) );
		            newNbRemainingPlaces = oldNbRemainingPLaces + oldAppointment.getNbPlaces( ) - effectiveBookedSeats;
		            newPotentialRemaningPlaces = oldNbPotentialRemaningPlaces + nbMaxPotentialBookedSeats - effectiveBookedSeats;
		            newNbPlacesTaken = oldNbPlacesTaken - oldAppointment.getNbPlaces( ) + effectiveBookedSeats;
		        }
		        slot.setNbRemainingPlaces( newNbRemainingPlaces );
		        slot.setNbPlacestaken( newNbPlacesTaken );
		        slot.setNbPotentialRemainingPlaces( Math.min( newPotentialRemaningPlaces, newNbRemainingPlaces ) );
		
		        
		        if(slot.getNbPlacesTaken() > slot.getMaxCapacity()){
		     	    
		        	throw new SlotFullException( "case of overbooking" );
		        }
		        slot = saveSlot( slot );
		      
		        // Create or update the appointment
		        Appointment appointment = AppointmentService.buildAndCreateAppointment( appointmentDTO, user, slot );
		        String strEmailLastNameFirstName = new StringJoiner( StringUtils.SPACE ).add( user.getEmail( ) ).add( CONSTANT_SEPARATOR ).add( user.getLastName( ) )
		                .add( CONSTANT_SEPARATOR ).add( user.getFirstName( ) ).toString( );
		        // Create a unique reference for a new appointment
		        if ( appointmentDTO.getIdAppointment( ) == 0 )
		        {
		            String strReference = appointment.getIdAppointment( )
		                    + CryptoService.encrypt( appointment.getIdAppointment( ) + strEmailLastNameFirstName,
		                            AppPropertiesService.getProperty( PROPERTY_REF_ENCRYPTION_ALGORITHM, CONSTANT_SHA256 ) ).substring( 0,
		                            AppPropertiesService.getPropertyInt( PROPERTY_REF_SIZE_RANDOM_PART, CONSTANT_REF_SIZE_RANDOM_PART ) );
		            appointment.setReference( strReference );
		            AppointmentHome.update( appointment );
		            AppointmentListenerManager.notifyListenersAppointmentUpdated(appointment.getIdAppointment( ));
		
		        }
		        else
		        {
		            AppointmentResponseService.removeResponsesByIdAppointment( appointment.getIdAppointment( ) );
		        }
		        if ( CollectionUtils.isNotEmpty( appointmentDTO.getListResponse( ) ) )
		        {
		            for ( Response response : appointmentDTO.getListResponse( ) )
		            {
		                ResponseHome.create( response );
		                AppointmentResponseService.insertAppointmentResponse( appointment.getIdAppointment( ), response.getIdResponse( ) );
		            }
		        }
		        if( bIsUpdate ){
	                WorkflowService.getInstance( ).doRemoveWorkFlowResource( appointmentDTO.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE );
		        }
		        Form form = FormService.findFormLightByPrimaryKey( slot.getIdForm( ) );
		        if ( form.getIdWorkflow( ) > 0 )
		        {
		            
		                WorkflowService.getInstance( ).getState( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow( ),
		                        form.getIdForm( ) );
		                WorkflowService.getInstance( ).executeActionAutomatic( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE,
		                        form.getIdWorkflow( ), form.getIdForm( ) );
		            
		        }
		    TransactionManager.commitTransaction( AppointmentPlugin.getPlugin( ) );
		    appointmentDTO.setIdAppointment( appointment.getIdAppointment( ));
		    appointmentDTO.setIsSaved(true);
		    if( request!= null ){
		    	AppointmentUtilities.killTimer( request );
		    }
	
		    return appointment.getIdAppointment( );
	        }
	        catch( Exception e )
	        {
	            TransactionManager.rollBack( AppointmentPlugin.getPlugin( ) );
	            AppLogService.error( "Error Save appointment " + e.getMessage(), e );
	            throw new SlotFullException( e.getMessage( ), e );
	        }
        }
    }
	
	
	 /**
     * Set the new number of remaining places (and potential) when an appointment is deleted or cancelled This new value must take in account the capacity of
     * the slot, in case of the slot was already over booked
     * 
     * @param nbPlaces
     *            the nb places taken of the appointment that we want to delete (or cancel, or move)
     * @param slot
     *            the related slot
     */
    public static void updateRemaningPlacesWithAppointmentMovedDeletedOrCanceled( int nbPlaces, int  nIdSlot )
    {
        // The capacity of the slot (that can be less than the number of places
        // taken on the slot --> overbook)
    	
    	 Object lock = getLockOnSlot( nIdSlot );
		 synchronized (lock) {
			 Slot slot = SlotService.findSlotById(nIdSlot);
			 if(slot != null){
		        int nMaxCapacity = slot.getMaxCapacity( );
		        // The old remaining places of the slot (before we delete or cancel or move the
		        // appointment
		        int nOldRemainingPlaces = slot.getNbRemainingPlaces( );
		        int nOldPotentialRemaningPlaces = slot.getNbPotentialRemainingPlaces( );
		        int nOldPlacesTaken = slot.getNbPlacesTaken( );
		        int nNewPlacesTaken = nOldPlacesTaken - nbPlaces;
		        // The new value of the remaining places of the slot is the minimal
		        // value between :
		        // - the minimal value between the potentially new max capacity and the old remaining places plus the number of places released by the appointment
		        // - and the capacity of the slot minus the new places taken on the slot (0 if negative)
		        int nNewRemainingPlaces = Math.min( Math.min( nMaxCapacity, nOldRemainingPlaces + nbPlaces ), Math.max( 0, nMaxCapacity - nNewPlacesTaken ) );
		
		        int nNewPotentialRemainingPlaces = Math.min( Math.min( nMaxCapacity, nOldPotentialRemaningPlaces + nbPlaces ),
		                Math.max( 0, nMaxCapacity - nNewPlacesTaken ) );
		
		        slot.setNbRemainingPlaces( nNewRemainingPlaces );
		        slot.setNbPotentialRemainingPlaces( nNewPotentialRemainingPlaces );
		        slot.setNbPlacestaken( nNewPlacesTaken );
		        updateSlot( slot );
			 }
	     }
    	 
    }
    
    /**
     * Update a slot in database and possibly all the slots after (if the ending hour has changed, all the next slots are impacted in case of the user decide to
     * shift the next slots)
     * 
     * @param slot
     *            the slot to update
     * @param bEndingTimeHasChanged
     *            true if the ending time has changed
     * @param previousEndingTime
     *            the previous ending time
     * @param bShifSlot
     *            true if the user has decided to shift the next slots
     */
    public static void updateSlot(  Slot slot, boolean bEndingTimeHasChanged, LocalTime previousEndingTime, boolean bShifSlot )
    {
        	 
       slot.setIsSpecific( SlotService.isSpecificSlot( slot ) );
	   if ( bEndingTimeHasChanged )
	   {
	       // If we don't want to shift the next slots
	       if ( !bShifSlot )
	       {
	           updateSlotWithoutShift( slot );
	        }
	        else
	        {
	        // We want to shift the next slots at the end of the current
	        // slot
	             updateSlotWithShift( slot, previousEndingTime );
	         }
	       }
	       else
	        {
	            // The ending time of the slot has not changed
	            // If it's an update of an existing slot
	            if ( slot.getIdSlot( ) != 0 )
	            {
	               updateRemainingPlaces( slot );
	            }
	           saveSlot( slot );
	        }
        

    }

    /**
     * Update the current slot and don't shift the next slots
     * 
     * @param slot
     *            the current slot
     */
    private static void updateSlotWithoutShift( Slot slot )
    {
        List<Slot> listSlotToCreate = new ArrayList<>( );
        // Need to get all the slots until the new end of this slot
        List<Slot> listSlotToDelete = SlotService.findSlotsByIdFormAndDateRange( slot.getIdForm( ), slot.getStartingDateTime( ).plusMinutes( 1 ),
                slot.getEndingDateTime( ) );
        SlotService.deleteListSlots( listSlotToDelete );
        // Get the list of slot after the modified slot
        HashMap<LocalDateTime, Slot> mapNextSlot = SlotService.buildMapSlotsByIdFormAndDateRangeWithDateForKey( slot.getIdForm( ), slot.getEndingDateTime( ),
                slot.getDate( ).atTime( LocalTime.MAX ) );
        List<LocalDateTime> listStartingDateTimeNextSlot = new ArrayList<>( mapNextSlot.keySet( ) );
        // Get the next date time slot
        LocalDateTime nextStartingDateTime = null;
        if ( CollectionUtils.isNotEmpty( listStartingDateTimeNextSlot ) )
        {
            nextStartingDateTime = Utilities.getClosestDateTimeInFuture( listStartingDateTimeNextSlot, slot.getEndingDateTime( ) );
        }
        else
        {
            LocalDate dateOfSlot = slot.getDate( );
            WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( slot.getIdForm( ), dateOfSlot );
            WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( weekDefinition.getListWorkingDay( ), dateOfSlot.getDayOfWeek( ) );
            // No slot after this one.
            // Need to compute between the end of this slot and the next
            // time slot
            if ( workingDay != null )
            {
                List<TimeSlot> nextTimeSlots = TimeSlotService.getNextTimeSlotsInAListOfTimeSlotAfterALocalTime( workingDay.getListTimeSlot( ),
                        slot.getEndingTime( ) );
                TimeSlot nextTimeSlot = null;
                if ( CollectionUtils.isNotEmpty( nextTimeSlots ) )
                {
                    nextTimeSlot = nextTimeSlots.stream( ).min( ( t1, t2 ) -> t1.getStartingTime( ).compareTo( t2.getStartingTime( ) ) ).get( );
                }
                if ( nextTimeSlot != null )
                {
                    nextStartingDateTime = nextTimeSlot.getStartingTime( ).atDate( dateOfSlot );
                }
            }
            else
            {
                // This is not a working day
                // Generated the new slots at the end of the modified
                // slot
                listSlotToCreate.addAll( generateListSlotToCreateAfterATime( slot.getEndingDateTime( ), slot.getIdForm( ) ) );
            }
        }
        // Need to create a slot between these two dateTime
        if ( nextStartingDateTime != null && !slot.getEndingDateTime( ).isEqual( nextStartingDateTime ) )
        {
            Slot slotToCreate = SlotService.buildSlot( slot.getIdForm( ), new Period( slot.getEndingDateTime( ), nextStartingDateTime ), slot.getMaxCapacity( ),
                    slot.getMaxCapacity( ), slot.getMaxCapacity( ), 0, Boolean.FALSE, Boolean.TRUE );
            listSlotToCreate.add( slotToCreate );
        }
        // If it's an update of an existing slot
        if ( slot.getIdSlot( ) != 0 )
        {
            updateRemainingPlaces( slot );
        }
        saveSlot( slot );
        createListSlot( listSlotToCreate );
    }

    /**
     * update the current slot and shift the next slots at the end of the current slot
     * 
     * @param slot
     *            the current slot
     * @param previousEndingTime
     *            the previous ending time of the current slot
     */
    private static void updateSlotWithShift( Slot slot, LocalTime previousEndingTime )
    {
        // We want to shift all the next slots
        LocalDate dateOfSlot = slot.getDate( );
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( slot.getIdForm( ) );
        // Build or get all the slots of the day
        List<Slot> listAllSlotsOfThisDayToBuildOrInDb = SlotService.buildListSlot( slot.getIdForm( ), mapWeekDefinition, dateOfSlot, dateOfSlot );
        // Remove the current slot and all the slot before it
        listAllSlotsOfThisDayToBuildOrInDb = listAllSlotsOfThisDayToBuildOrInDb.stream( )
                .filter( slotToKeep -> slotToKeep.getStartingDateTime( ).isAfter( slot.getStartingDateTime( ) ) ).collect( Collectors.toList( ) );
        // Need to delete all the slots until the new end of this slot
        List<Slot> listSlotToDelete = listAllSlotsOfThisDayToBuildOrInDb
                .stream( )
                .filter(
                        slotToDelete -> slotToDelete.getStartingDateTime( ).isAfter( slot.getStartingDateTime( ) )
                                && !slotToDelete.getEndingDateTime( ).isAfter( slot.getEndingDateTime( ) ) && slotToDelete.getIdSlot( ) != 0 )
                .collect( Collectors.toList( ) );
        SlotService.deleteListSlots( listSlotToDelete );
        listAllSlotsOfThisDayToBuildOrInDb.removeAll( listSlotToDelete );
        // Need to find all the existing slots
        List<Slot> listExistingSlots = listAllSlotsOfThisDayToBuildOrInDb.stream( ).filter( existingSlot -> existingSlot.getIdSlot( ) != 0 )
                .collect( Collectors.toList( ) );
        // Remove them from the list of slot to build
        listAllSlotsOfThisDayToBuildOrInDb.removeAll( listExistingSlots );
        // Save this list
        createListSlot( listAllSlotsOfThisDayToBuildOrInDb );
        List<Slot> listSlotToShift = new ArrayList<>( );
        listSlotToShift.addAll( listExistingSlots );
        listSlotToShift.addAll( listAllSlotsOfThisDayToBuildOrInDb );
        // Need to order the list of slot to shift according to the shift
        // if the new ending time is before the previous ending time,
        // the list has to be ordered in chronological order ascending
        // and the first slot to shift is the closest to the current
        // slot
        // (because we have an integrity constraint for the slot, it
        // can't have the same starting or ending time as another slot
        listSlotToShift = listSlotToShift.stream( ).sorted( ( slot1, slot2 ) -> slot1.getStartingDateTime( ).compareTo( slot2.getStartingDateTime( ) ) )
                .collect( Collectors.toList( ) );
        boolean bNewEndingTimeIsAfterThePreviousTime = false;
        // Need to know the ending time of the day
        LocalDateTime endingDateTimeOfTheDay = null;
        WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( slot.getIdForm( ), dateOfSlot );
        WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( weekDefinition.getListWorkingDay( ), dateOfSlot.getDayOfWeek( ) );
        LocalTime endingTimeOfTheDay;
        if ( workingDay != null )
        {
            endingTimeOfTheDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
        }
        else
        {
            endingTimeOfTheDay = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( weekDefinition.getListWorkingDay( ) );
        }
        endingDateTimeOfTheDay = endingTimeOfTheDay.atDate( dateOfSlot );
        long timeToAdd = 0;
        long timeToSubstract = 0;
        if ( previousEndingTime.isBefore( slot.getEndingTime( ) ) )
        {
            bNewEndingTimeIsAfterThePreviousTime = true;
            // Need to find the next available slot, to know how to
            // add to the starting time of the next slot to match
            // with the new end of the current slot
            if ( CollectionUtils.isNotEmpty( listSlotToShift ) )
            {
                Slot nextSlot = listSlotToShift.stream( ).min( ( s1, s2 ) -> s1.getStartingDateTime( ).compareTo( s2.getStartingDateTime( ) ) ).get( );
                if ( slot.getEndingDateTime( ).isAfter( nextSlot.getStartingDateTime( ) ) )
                {
                    timeToAdd = nextSlot.getStartingDateTime( ).until( slot.getEndingDateTime( ), ChronoUnit.MINUTES );
                }
                else
                {
                    timeToAdd = slot.getEndingDateTime( ).until( nextSlot.getStartingDateTime( ), ChronoUnit.MINUTES );
                }
                Collections.reverse( listSlotToShift );
            }
            else
            {
                timeToAdd = previousEndingTime.until( slot.getEndingTime( ), ChronoUnit.MINUTES );
            }
        }
        else
        {
            timeToSubstract = slot.getEndingTime( ).until( previousEndingTime, ChronoUnit.MINUTES );
        }
        // If it's an update of an existing slot
        if ( slot.getIdSlot( ) != 0 )
        {
            updateRemainingPlaces( slot );
        }
        saveSlot( slot );
        // Need to set the new starting and ending time of all the slots
        // to shift and update them
        for ( Slot slotToShift : listSlotToShift )
        {
            // If the new ending time is after the previous time
            if ( bNewEndingTimeIsAfterThePreviousTime )
            {
                // If the starting time + the time to add is before the
                // ending time of the day
                if ( slotToShift.getStartingDateTime( ).plus( timeToAdd, ChronoUnit.MINUTES ).isBefore( endingDateTimeOfTheDay ) )
                {
                    slotToShift.setStartingDateTime( slotToShift.getStartingDateTime( ).plus( timeToAdd, ChronoUnit.MINUTES ) );
                    // if the ending time is after the ending time of
                    // the day, we set the new ending time to the ending
                    // time of the day
                    if ( slotToShift.getEndingDateTime( ).plus( timeToAdd, ChronoUnit.MINUTES ).isAfter( endingDateTimeOfTheDay ) )
                    {
                        slotToShift.setEndingDateTime( endingDateTimeOfTheDay );
                    }
                    else
                    {
                        slotToShift.setEndingDateTime( slotToShift.getEndingDateTime( ).plus( timeToAdd, ChronoUnit.MINUTES ) );
                    }
                    slotToShift.setIsSpecific( SlotService.isSpecificSlot( slotToShift ) );
                    saveSlot( slotToShift );
                }
                else
                {
                    // Delete this slot (the slot can not be after the
                    // ending time of the day)
                    SlotService.deleteSlot( slotToShift );
                }
            }
            else
            {
                // The new ending time is before the previous ending
                // time
                slotToShift.setStartingDateTime( slotToShift.getStartingDateTime( ).minus( timeToSubstract, ChronoUnit.MINUTES ) );
                slotToShift.setEndingDateTime( slotToShift.getEndingDateTime( ).minus( timeToSubstract, ChronoUnit.MINUTES ) );
                slotToShift.setIsSpecific( SlotService.isSpecificSlot( slotToShift ) );
                saveSlot( slotToShift );
            }
        }
        if ( !bNewEndingTimeIsAfterThePreviousTime )
        {
            // If the slots have been shift earlier,
            // there is no slot(s) between the last slot created
            // and the ending time of the day, need to create it(them)
            List<Slot> listSlotsToAdd = generateListSlotToCreateAfterATime( endingDateTimeOfTheDay.minusMinutes( timeToSubstract ), slot.getIdForm( ) );
            createListSlot( listSlotsToAdd );
        }

    }
    
    /**
     * Generate the list of slot to create after a slot (taking into account the week definition and the rules to apply)
     * 
     * @param slot
     *            the slot
     * @return the list of next slots
     */
    private static List<Slot> generateListSlotToCreateAfterATime( LocalDateTime dateTimeToStartCreation, int nIdForm )
    {
        List<Slot> listSlotToCreate = new ArrayList<>( );
        LocalDate dateOfCreation = dateTimeToStartCreation.toLocalDate( );
        ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, dateOfCreation );
        int nMaxCapacity = reservationRule.getMaxCapacityPerSlot( );
        WeekDefinition weekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, dateOfCreation );
        WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek( weekDefinition.getListWorkingDay( ), dateOfCreation.getDayOfWeek( ) );
        LocalTime endingTimeOfTheDay = null;
        List<TimeSlot> listTimeSlot = new ArrayList<>( );
        int nDurationSlot = 0;
        if ( workingDay != null )
        {
            endingTimeOfTheDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDay );
            nDurationSlot = WorkingDayService.getMinDurationTimeSlotOfAWorkingDay( workingDay );
            listTimeSlot = TimeSlotService.findListTimeSlotByWorkingDay( workingDay.getIdWorkingDay( ) );
        }
        else
        {
            endingTimeOfTheDay = WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( weekDefinition.getListWorkingDay( ) );
            nDurationSlot = WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay( weekDefinition.getListWorkingDay( ) );
        }
        LocalDateTime endingDateTimeOfTheDay = endingTimeOfTheDay.atDate( dateOfCreation );
        LocalDateTime startingDateTime = dateTimeToStartCreation;
        LocalDateTime endingDateTime = startingDateTime.plusMinutes( nDurationSlot );
        while ( !endingDateTime.isAfter( endingDateTimeOfTheDay ) )
        {
            Slot slotToCreate = SlotService.buildSlot( nIdForm, new Period( startingDateTime, endingDateTime ), nMaxCapacity, nMaxCapacity, nMaxCapacity, 0, Boolean.FALSE,
                    Boolean.TRUE );
            slotToCreate.setIsSpecific( SlotService.isSpecificSlot( slotToCreate, workingDay, listTimeSlot, nMaxCapacity ) );
            startingDateTime = endingDateTime;
            endingDateTime = startingDateTime.plusMinutes( nDurationSlot );
            listSlotToCreate.add( slotToCreate );
        }
        if ( startingDateTime.isBefore( endingDateTimeOfTheDay ) && endingDateTime.isAfter( endingDateTimeOfTheDay ) )
        {
            Slot slotToCreate = SlotService.buildSlot( nIdForm, new Period( startingDateTime, endingDateTimeOfTheDay ), nMaxCapacity, nMaxCapacity, nMaxCapacity, 0,
                    Boolean.FALSE, Boolean.TRUE );
            slotToCreate.setIsSpecific( SlotService.isSpecificSlot( slotToCreate, workingDay, listTimeSlot, nMaxCapacity ) );
            listSlotToCreate.add( slotToCreate );
        }
        return listSlotToCreate;
    }

    /**
     * Build a Slot  object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object

     */
    /**
     * Update the capacity of the slot
     * 
     * @param slot
     *            the slot to update
     */
    public static void updateRemainingPlaces( Slot slot )
    {
        Slot oldSlot = SlotHome.findByPrimaryKey( slot.getIdSlot( ) );
        int nNewNbMaxCapacity = slot.getMaxCapacity( );
        int nOldBnMaxCapacity = oldSlot.getMaxCapacity( );
        // If the max capacity has been modified
        if ( nNewNbMaxCapacity != nOldBnMaxCapacity )
        {
            // Need to update the remaining places

            // Need to add the diff between the old value and the new value
            // to the remaining places (if the new is higher)
            if ( nNewNbMaxCapacity > nOldBnMaxCapacity )
            {
                int nValueToAdd = nNewNbMaxCapacity - nOldBnMaxCapacity;
                slot.setNbPotentialRemainingPlaces( oldSlot.getNbPotentialRemainingPlaces( ) + nValueToAdd );
                slot.setNbRemainingPlaces( oldSlot.getNbRemainingPlaces( ) + nValueToAdd );
            }
            else
            {
                // the new value is lower than the previous capacity
                // !!!! If there are appointments on this slot and if the
                // slot is already full, the slot will be surbooked !!!!
                int nValueToSubstract = nOldBnMaxCapacity - nNewNbMaxCapacity;
                slot.setNbPotentialRemainingPlaces( Math.max( 0, oldSlot.getNbPotentialRemainingPlaces( ) - nValueToSubstract ) );
                slot.setNbRemainingPlaces( Math.max( 0, oldSlot.getNbRemainingPlaces( ) - nValueToSubstract ) );
            }
        }
    }
    
    /**
     * Save a slot in database
     * 
     * @param slot
     *            the slot to save
     * @return the slot saved
     */
    public static Slot saveSlot( Slot slot )
    {
        Slot slotSaved = null;
        if ( slot.getIdSlot( ) == 0 )
        {
            slotSaved = createSlot( slot );
        }
        else
        {
            slotSaved = updateSlot( slot );
        }
        return slotSaved;
    }
    
    /**
     * Update a slot
     * 
     * @param slot
     *            the slot updated
     */
    public static Slot updateSlot( Slot slot )
    {
			Slot slotToReturn = SlotHome.update( slot );
        	SlotListenerManager.notifyListenersSlotChange( slot.getIdSlot( ) );
         	return slotToReturn;
		 
    }

    /**
     * Create in database the slots given
     * 
     * @param listSlotToCreate
     *            the list of slots to create in database
     */
    private static void createListSlot( List<Slot> listSlotToCreate )
    {
        if ( CollectionUtils.isNotEmpty( listSlotToCreate ) )
        {
            for ( Slot slotTemp : listSlotToCreate )
            {
                createSlot( slotTemp );
            }
        }
    }
    /**
     * Clean slotlist
     */
    public static void cleanSlotlist(){
    	
    	Iterator<Integer> it = _listSlot.keySet().iterator();
    	int idSlot;
    	while(it.hasNext()){
    		
    		idSlot= it.next();
    		Slot slot=SlotService.findSlotById(idSlot);
    		if(slot == null || slot.getStartingDateTime().isBefore(LocalDateTime.now()) || slot.getMaxCapacity() <= slot.getNbPlacesTaken( )){
    			
    			_listSlot.remove(idSlot);
    		}
    		
    	}
    	
    	
    }


}
