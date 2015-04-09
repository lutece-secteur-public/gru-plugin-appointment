/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Calendar Dashboard Component
 * This component displays directories
 */
public class AppointmentFormDashboardComponent extends DashboardComponent
{
    // MARKS
    private static final String MARK_URL = "url";
    private static final String MARK_ICON = "icon";
    private static final String MARK_APPOINTMENTFORM_LIST = "appointmentform_list";
    private static final String MARK_PERMISSION_CREATE = "permission_create";
    private static final String MARK_PERMISSION_CHANGE = "permission_change";
    private static final String MARK_PERMISSION_DELETE = "permission_delete";
    private static final String MARK_PERMISSION_MODIFY = "permission_modify";

    // TEMPALTES
    private static final String TEMPLATE_DASHBOARD = "/admin/plugins/appointment/appointment_form_dashboard.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        List<AppointmentForm> listAppointmentForms = AppointmentFormHome.getAppointmentFormsList(  );

        Map<String, Object> model = new HashMap<String, Object>(  );

        Plugin plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

        model.put( MARK_APPOINTMENTFORM_LIST,
            RBACService.getAuthorizedCollection( listAppointmentForms,
                AppointmentResourceIdService.PERMISSION_VIEW_FORM, AdminUserService.getAdminUser( request ) ) );
        model.put( MARK_ICON, plugin.getIconUrl(  ) );
        model.put( MARK_URL, AppointmentFormJspBean.getURLManageAppointmentForms( request ) );
        model.put( MARK_PERMISSION_CREATE,
            RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, "0",
                AppointmentResourceIdService.PERMISSION_CREATE_FORM, user ) );

        model.put( MARK_PERMISSION_CHANGE, RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, "0",
                AppointmentResourceIdService.PERMISSION_CHANGE_STATE, user ) );
        model.put( MARK_PERMISSION_MODIFY, RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE, "0",
                AppointmentResourceIdService.PERMISSION_MODIFY_FORM, user ) );
        model.put( MARK_PERMISSION_DELETE, RBACService.isAuthorized( AppointmentForm.RESOURCE_TYPE,  "0",
                AppointmentResourceIdService.PERMISSION_DELETE_FORM, user ) );
                
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DASHBOARD,
                AdminUserService.getLocale( request ), model );

        return template.getHtml(  );
    }
}
