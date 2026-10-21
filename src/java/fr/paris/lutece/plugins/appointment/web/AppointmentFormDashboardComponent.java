/*
 * Copyright (c) 2002-2025, City of Paris
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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Calendar Dashboard Component
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentFormDashboardComponent extends DashboardComponent
{
    // MARKS
    private static final String MARK_URL = "url";
    private static final String MARK_ICON = "icon";
    private static final String MARK_APPOINTMENTFORM_LIST = "appointmentform_list";
    private static final String MARK_PERMISSION_CREATE = "permission_create";
    private static final String MARK_BASE_URL = "baseUrl";
    private static final String MARK_AVAILABLE_SUFFIX = "_available";
    private static final String MARK_NB_FORMS = "nb_forms";

    // CONSTANTS
    private static final int MAX_DISPLAYED_FORMS = 10;
    
    // VIEWS
    private static final String VIEW_PERMISSIONS_FORM = "permissions";
    
    // PARAMETERS
    private static final String PARAMETER_APPOINTMENT = "appointment";

    // TEMPLATES
    private static final String TEMPLATE_DASHBOARD = "/admin/plugins/appointment/appointment_form_dashboard.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
    	Collection<Plugin> pluginsList = PluginService.getPluginList( );
        List<AppointmentFormDTO> listAppointmentForm = FormService.buildAllAppointmentFormLight( );
        listAppointmentForm = ( List<AppointmentFormDTO> ) AdminWorkgroupService.getAuthorizedCollection( listAppointmentForm, ( User ) user );
        List<AppointmentFormDTO> listAuthorizedForms = RBACService
                .getAuthorizedCollection( listAppointmentForm, AppointmentResourceIdService.PERMISSION_VIEW_FORM, ( User ) user ).stream( )
                .sorted( Comparator.comparing( AppointmentFormDTO::getIsActive ).reversed( ).thenComparing( AppointmentFormDTO::getTitle,
                        String.CASE_INSENSITIVE_ORDER ) )
                .collect( Collectors.toList( ) );
        List<AppointmentFormDTO> listDisplayedForms = listAuthorizedForms.stream( ).limit( MAX_DISPLAYED_FORMS ).collect( Collectors.toList( ) );
        Map<String, Object> model = new HashMap<>( );
        Plugin plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );
        model.put( MARK_APPOINTMENTFORM_LIST, listDisplayedForms );
        model.put( MARK_NB_FORMS, listAuthorizedForms.size( ) );
        model.put( MARK_ICON, plugin.getIconUrl( ) );
        model.put( MARK_BASE_URL, AppPathService.getProdUrl( request ) );
        model.put( MARK_URL, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        model.put( MARK_PERMISSION_CREATE, String.valueOf(
                RBACService.isAuthorized( AppointmentFormDTO.RESOURCE_TYPE_CREATE, NumberUtils.INTEGER_ZERO.toString( ) , AppointmentResourceIdService.PERMISSION_CREATE_FORM, ( User ) user ) ) );
        for( Plugin currentPlugin : pluginsList )
        {
        	if ( currentPlugin.getName().contains( PARAMETER_APPOINTMENT ))
        	{
        		model.put( currentPlugin.getName( ).replaceAll( "-","_" ) + MARK_AVAILABLE_SUFFIX, currentPlugin.isInstalled( ) );
        	}
        }
        
        model.put( VIEW_PERMISSIONS_FORM, AppointmentUtilities.getPermissions( listDisplayedForms, AdminUserService.getAdminUser( request ) ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, AdminUserService.getLocale( request ), model );
        return template.getHtml( );
    }

}
