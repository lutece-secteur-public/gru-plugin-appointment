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

import java.text.ParseException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;

public abstract class AbstractAppointmentFormAndSlotJspBean extends MVCAdminJspBean
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

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "appointment.listItems.itemsPerPage";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Markers
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    // Variables
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * Check Constraints
     * 
     * @param appointmentForm
     * @return
     * @throws ParseException
     */
    protected boolean checkConstraints( AppointmentFormDTO appointmentForm )
    {
        return checkStartingAndEndingTime( appointmentForm ) && checkStartingAndEndingValidityDate( appointmentForm )
                && checkSlotCapacityAndPeoplePerAppointment( appointmentForm ) && checkAtLeastOneWorkingDayOpen( appointmentForm );
    }

    /**
     * Return a model that contains the list and paginator infos
     * 
     * @param request
     *            The HTTP request
     * @param strBookmark
     *            The bookmark
     * @param list
     *            The list of item
     * @param strManageJsp
     *            The JSP
     * @return The model
     */
    protected <T> Map<String, Object> getPaginatedListModel( HttpServletRequest request, String strBookmark, List<T> list, String strManageJsp )
    {
        int nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, nDefaultItemsPerPage );

        UrlItem url = new UrlItem( strManageJsp );
        String strUrl = url.getUrl( );

        // PAGINATOR
        LocalizedPaginator<T> paginator = new LocalizedPaginator<>( list, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale( ) );

        Map<String, Object> model = getModel( );

        model.put( MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( strBookmark, paginator.getPageItems( ) );

        return model;
    }

    /**
     * Check that the user has checked as least one working day on its form
     * 
     * @param appointmentForm
     *            the appointForm DTO
     * @return true if at least one working day is checked, false otherwise
     */
    private boolean checkAtLeastOneWorkingDayOpen( AppointmentFormDTO appointmentForm )
    {
        boolean bReturn = true;
        if ( !( appointmentForm.getIsOpenMonday( ) || appointmentForm.getIsOpenTuesday( ) || appointmentForm.getIsOpenWednesday( )
                || appointmentForm.getIsOpenThursday( ) || appointmentForm.getIsOpenFriday( ) || appointmentForm.getIsOpenSaturday( )
                || appointmentForm.getIsOpenSunday( ) ) )
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
    private boolean checkStartingAndEndingTime( AppointmentFormDTO appointmentForm )
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
    private boolean checkStartingAndEndingValidityDate( AppointmentFormDTO appointmentForm )
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
    private boolean checkSlotCapacityAndPeoplePerAppointment( AppointmentFormDTO appointmentForm )
    {
        boolean bReturn = true;
        if ( appointmentForm.getMaxPeoplePerAppointment( ) > appointmentForm.getMaxCapacityPerSlot( ) && !appointmentForm.getBoOverbooking( ) )
        {
            bReturn = false;
            addError( MESSAGE_ERROR_NUMBER_OF_SEATS_BOOKED, getLocale( ) );
        }
        return bReturn;
    }
}
