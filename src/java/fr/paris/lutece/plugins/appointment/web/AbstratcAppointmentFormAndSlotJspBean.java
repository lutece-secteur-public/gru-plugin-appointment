package fr.paris.lutece.plugins.appointment.web;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;

public abstract class AbstratcAppointmentFormAndSlotJspBean extends MVCAdminJspBean
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 7709182167218092169L;
    static final String ERROR_MESSAGE_TIME_START_AFTER_TIME_END = "appointment.message.error.timeStartAfterTimeEnd";
    static final String ERROR_MESSAGE_TIME_START_AFTER_DATE_END = "appointment.message.error.dateStartAfterTimeEnd";
    static final String ERROR_MESSAGE_NO_WORKING_DAY_CHECKED = "appointment.message.error.noWorkingDayChecked";
    static final String ERROR_MESSAGE_APPOINTMENT_SUPERIOR_MIDDLE = "appointment.message.error.formatDaysBeforeAppointmentMiddleSuperior";
    static final String MESSAGE_ERROR_DAY_DURATION_APPOINTMENT_NOT_MULTIPLE_FORM = "appointment.message.error.durationAppointmentDayNotMultipleForm";
    private static final String MESSAGE_ERROR_NUMBER_OF_SEATS_BOOKED = "appointment.message.error.numberOfSeatsBookedAndConcurrentAppointments";

    /**
     * Check Constraints
     * 
     * @param appointmentForm
     * @return
     * @throws ParseException
     */
    protected boolean checkConstraints( AppointmentForm appointmentForm )
    {
        return checkStartingAndEndingTime( appointmentForm ) && checkStartingAndEndingValidityDate( appointmentForm )
                && checkSlotCapacityAndPeoplePerAppointment( appointmentForm ) && checkAtLeastOneWorkingDayOpen( appointmentForm );
    }

    /**
     * Check that the user has checked as least one working day on its form
     * 
     * @param appointmentForm
     *            the appointForm DTO
     * @return true if at least one working day is checked, false otherwise
     */
    private boolean checkAtLeastOneWorkingDayOpen( AppointmentForm appointmentForm )
    {
        boolean bReturn = true;
        if ( !( appointmentForm.getIsOpenMonday( ) || appointmentForm.getIsOpenTuesday( ) || appointmentForm.getIsOpenWednesday( )
                || appointmentForm.getIsOpenThursday( ) || appointmentForm.getIsOpenFriday( ) || appointmentForm.getIsOpenSaturday( ) || appointmentForm
                    .getIsOpenSunday( ) ) )
        {
            bReturn = false;
            addError( ERROR_MESSAGE_NO_WORKING_DAY_CHECKED, getLocale( ) );
        }
        return bReturn;
    }

    /**
     * Check the starting time and the ending time of the appointmentFormDTO
     * 
     * @param appointmentForm
     *            the appointmentForm DTO
     * @return false if there is an error
     */
    private boolean checkStartingAndEndingTime( AppointmentForm appointmentForm )
    {
        boolean bReturn = true;
        LocalTime startingTime = LocalTime.parse( appointmentForm.getTimeStart( ) );
        LocalTime endingTime = LocalTime.parse( appointmentForm.getTimeEnd( ) );
        if ( startingTime.isAfter( endingTime ) )
        {
            bReturn = false;
            addError( ERROR_MESSAGE_TIME_START_AFTER_TIME_END, getLocale( ) );
        }
        long lMinutes = startingTime.until( endingTime, ChronoUnit.MINUTES );
        if ( appointmentForm.getDurationAppointments( ) > lMinutes )
        {
            bReturn = false;
            addError( ERROR_MESSAGE_APPOINTMENT_SUPERIOR_MIDDLE, getLocale( ) );
        }
        if ( ( lMinutes % appointmentForm.getDurationAppointments( ) ) != 0 )
        {
            bReturn = false;
            addError( MESSAGE_ERROR_DAY_DURATION_APPOINTMENT_NOT_MULTIPLE_FORM, getLocale( ) );
        }
        return bReturn;
    }

    /**
     * Check the starting and the ending validity date of the appointmentForm DTO
     * 
     * @param appointmentForm
     *            the appointmentForm DTO
     * @return false if there is an error
     */
    private boolean checkStartingAndEndingValidityDate( AppointmentForm appointmentForm )
    {
        boolean bReturn = true;
        if ( ( appointmentForm.getDateStartValidity( ) != null ) && ( appointmentForm.getDateEndValidity( ) != null ) )
        {
            if ( appointmentForm.getDateStartValidity( ).toLocalDate( ).isAfter( appointmentForm.getDateEndValidity( ).toLocalDate( ) ) )
            {
                bReturn = false;
                addError( ERROR_MESSAGE_TIME_START_AFTER_DATE_END, getLocale( ) );
            }
        }
        return bReturn;
    }

    /**
     * Check the slot capacity and the max people per appointment of the appointmentForm DTO
     * 
     * @param appointmentForm
     *            athe appointmentForm DTO
     * @return false if the maximum number of people per appointment is bigger than the maximum capacity of the slot
     */
    private boolean checkSlotCapacityAndPeoplePerAppointment( AppointmentForm appointmentForm )
    {
        boolean bReturn = true;
        if ( appointmentForm.getMaxPeoplePerAppointment( ) > appointmentForm.getMaxCapacityPerSlot( ) )
        {
            bReturn = false;
            addError( MESSAGE_ERROR_NUMBER_OF_SEATS_BOOKED, getLocale( ) );
        }
        return bReturn;
    }
}
