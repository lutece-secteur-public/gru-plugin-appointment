/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This class provides the user interface to manage Appointment features (
 * manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageAppointments.jsp", controllerPath = "jsp/admin/plugins/appointment/", right = AppointmentJspBean.RIGHT_MANAGEAPPOINTMENT )
public class AppointmentJspBean extends MVCAdminJspBean
{
    /**
     * Right to manage appointments
     */
    public static final String RIGHT_MANAGEAPPOINTMENT = "APPOINTMENT_MANAGEMENT";
    private static final long serialVersionUID = 1978001810468444844L;
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_APPOINTMENTS = "/admin/plugins/appointment/manage_appointments.html";
    private static final String TEMPLATE_CREATE_APPOINTMENT = "/admin/plugins/appointment/create_appointment.html";
    private static final String TEMPLATE_MODIFY_APPOINTMENT = "/admin/plugins/appointment/modify_appointment.html";

    // Parameters
    private static final String PARAMETER_ID_APPOINTMENT = "id_appointment";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS = "appointment.manage_appointments.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENT = "appointment.modify_appointment.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT = "appointment.create_appointment.pageTitle";

    // Markers
    private static final String MARK_APPOINTMENT_LIST = "appointment_list";
    private static final String MARK_APPOINTMENT = "appointment";
    private static final String JSP_MANAGE_APPOINTMENTS = "jsp/admin/plugins/appointment/ManageAppointments.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_APPOINTMENT = "appointment.message.confirmRemoveAppointment";
    private static final String PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE = "appointment.listAppointments.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "appointment.model.entity.appointment.attribute.";

    // Views
    private static final String VIEW_MANAGE_APPOINTMENTS = "manageAppointments";
    private static final String VIEW_CREATE_APPOINTMENT = "createAppointment";
    private static final String VIEW_MODIFY_APPOINTMENT = "modifyAppointment";
    private static final String VIEW_VIEW_APPOINTMENT = "viewAppointment";

    // Actions
    private static final String ACTION_CREATE_APPOINTMENT = "createAppointment";
    private static final String ACTION_MODIFY_APPOINTMENT = "modifyAppointment";
    private static final String ACTION_REMOVE_APPOINTMENT = "removeAppointment";
    private static final String ACTION_CONFIRM_REMOVE_APPOINTMENT = "confirmRemoveAppointment";

    // Infos
    private static final String INFO_APPOINTMENT_CREATED = "appointment.info.appointment.created";
    private static final String INFO_APPOINTMENT_UPDATED = "appointment.info.appointment.updated";
    private static final String INFO_APPOINTMENT_REMOVED = "appointment.info.appointment.removed";
    private static final String SESSION_ATTRIBUTE_APPOINTMENT = "appointment.session.appointment";
    private static final String SESSION_CURRENT_PAGE_INDEX = "appointment.session.currentPageIndex";
    private static final String SESSION_ITEMS_PER_PAGE = "appointment.session.itemsPerPage";
    private static final String DEFAULT_CURRENT_PAGE = "1";

    // Session variable to store working values
    private int _nDefaultItemsPerPage;

    /**
     * Default constructor
     */
    public AppointmentJspBean(  )
    {
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_APPOINTMENT_PER_PAGE, 50 );
    }

    /**
     * Get the page to manage appointments
     * @param request The request
     * @return The HTML code to display
     */
    @View( value = VIEW_MANAGE_APPOINTMENTS, defaultView = true )
    public String getManageAppointments( HttpServletRequest request )
    {
        String strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                (String) request.getSession(  ).getAttribute( SESSION_CURRENT_PAGE_INDEX ) );

        if ( strCurrentPageIndex == null )
        {
            strCurrentPageIndex = DEFAULT_CURRENT_PAGE;
        }

        request.getSession(  ).setAttribute( SESSION_CURRENT_PAGE_INDEX, strCurrentPageIndex );

        int nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                getIntSessionAttribute( request.getSession(  ), SESSION_ITEMS_PER_PAGE ), _nDefaultItemsPerPage );
        request.getSession(  ).setAttribute( SESSION_ITEMS_PER_PAGE, nItemsPerPage );

        request.getSession(  ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT );

        UrlItem url = new UrlItem( JSP_MANAGE_APPOINTMENTS );
        String strUrl = url.getUrl(  );
        List<Appointment> listAppointments = (List<Appointment>) AppointmentHome.getAppointmentsList(  );

        // PAGINATOR
        LocalizedPaginator<Appointment> paginator = new LocalizedPaginator<Appointment>( listAppointments,
                nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, strCurrentPageIndex, getLocale(  ) );

        Map<String, Object> model = getModel(  );

        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_APPOINTMENT_LIST, paginator.getPageItems(  ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_APPOINTMENTS, TEMPLATE_MANAGE_APPOINTMENTS, model );
    }

    /**
     * Returns the form to create a appointment
     * @param request The HTTP request
     * @return the HTML code of the appointment form
     */
    @View( VIEW_CREATE_APPOINTMENT )
    public String getCreateAppointment( HttpServletRequest request )
    {
        Appointment appointment = (Appointment) request.getSession(  ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT );

        if ( appointment == null )
        {
            appointment = new Appointment(  );
            request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT, appointment );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_APPOINTMENT, appointment );
        request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT, appointment );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_APPOINTMENT, TEMPLATE_CREATE_APPOINTMENT, model );
    }

    /**
     * Process the data capture form of a new appointment
     * @param request The HTTP Request
     * @return The JSP URL of the process result
     */
    @Action( ACTION_CREATE_APPOINTMENT )
    public String doCreateAppointment( HttpServletRequest request )
    {
        Appointment appointment = (Appointment) request.getSession(  ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT );
        populate( appointment, request );

        // Check constraints
        if ( !validateBean( appointment, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_APPOINTMENT );
        }

        AppointmentHome.create( appointment );
        request.getSession(  ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT );
        addInfo( INFO_APPOINTMENT_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_APPOINTMENTS );
    }

    /**
     * Manages the removal form of a appointment whose identifier is in the HTTP
     * request
     * @param request The HTTP request
     * @return the HTML code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_APPOINTMENT )
    public String getConfirmRemoveAppointment( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPOINTMENT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_APPOINTMENT ) );
        url.addParameter( PARAMETER_ID_APPOINTMENT, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_APPOINTMENT,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a appointment
     * @param request The HTTP request
     * @return the JSP URL to display the form to manage appointments
     */
    @Action( ACTION_REMOVE_APPOINTMENT )
    public String doRemoveAppointment( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPOINTMENT ) );
        AppointmentHome.remove( nId );
        addInfo( INFO_APPOINTMENT_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_APPOINTMENTS );
    }

    /**
     * Returns the form to update info about a appointment
     * @param request The HTTP request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_APPOINTMENT )
    public String getModifyAppointment( HttpServletRequest request )
    {
        Appointment appointment = (Appointment) request.getSession(  ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT );

        if ( appointment == null )
        {
            int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_APPOINTMENT ) );
            appointment = AppointmentHome.findByPrimaryKey( nId );
            request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_APPOINTMENT, appointment );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_APPOINTMENT, appointment );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_APPOINTMENT, TEMPLATE_MODIFY_APPOINTMENT, model );
    }

    /**
     * Process the change form of a appointment
     * @param request The HTTP request
     * @return The JSP URL of the process result
     */
    @Action( ACTION_MODIFY_APPOINTMENT )
    public String doModifyAppointment( HttpServletRequest request )
    {
        Appointment appointment = (Appointment) request.getSession(  ).getAttribute( SESSION_ATTRIBUTE_APPOINTMENT );
        populate( appointment, request );

        // Check constraints
        if ( !validateBean( appointment, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_APPOINTMENT, PARAMETER_ID_APPOINTMENT,
                appointment.getIdAppointment(  ) );
        }

        AppointmentHome.update( appointment );
        request.getSession(  ).removeAttribute( SESSION_ATTRIBUTE_APPOINTMENT );
        addInfo( INFO_APPOINTMENT_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_APPOINTMENTS );
    }

    /**
     * Get an integer attribute from the session
     * @param session The session
     * @param strSessionKey The session key of the item
     * @return The value of the attribute, or 0 if the key is not associated
     *         with any value
     */
    private int getIntSessionAttribute( HttpSession session, String strSessionKey )
    {
        Integer nAttr = (Integer) session.getAttribute( strSessionKey );

        if ( nAttr != null )
        {
            return nAttr;
        }

        return 0;
    }
}
