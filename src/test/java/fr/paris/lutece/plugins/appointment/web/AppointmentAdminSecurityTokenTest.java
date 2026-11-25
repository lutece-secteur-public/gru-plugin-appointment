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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenHandler;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.cdi.mvc.event.MvcEvent.ControllerInvocationType;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The back office actions of the plugin are protected by the security token
 */
public class AppointmentAdminSecurityTokenTest extends LuteceTestCase
{
    private static final String PACKAGE = "fr.paris.lutece.plugins.appointment.";
    private static final String PATH_CATEGORY = "/jsp/admin/plugins/appointment/ManageAppointmentCategory.jsp";
    private static final String PATH_SPECIFIC_WEEK = "/jsp/admin/plugins/appointment/ManageSpecificWeek.jsp";
    private static final Set<String> VIEWS_WITHOUT_SHARED_TOKEN = Set.of( "downloadFileFromSession", "downloadFile" );

    @AfterEach
    public void clearLocalVariables( )
    {
        LocalVariables.setLocal( null, null, null );
    }

    /**
     * Every back office controller of the plugin enables the security token, and its views and actions share the token key of the plugin
     */
    @Test
    public void testEveryControllerEnablesTheToken( )
    {
        List<String> listControllers = new ArrayList<>( );
        for ( Bean<?> bean : CDI.current( ).getBeanManager( ).getBeans( MVCAdminJspBean.class, Any.Literal.INSTANCE ) )
        {
            Class<?> clazz = bean.getBeanClass( );
            Controller controller = clazz.getAnnotation( Controller.class );
            if ( controller == null || !clazz.getName( ).startsWith( PACKAGE ) )
            {
                continue;
            }
            listControllers.add( clazz.getSimpleName( ) );
            assertTrue( controller.securityTokenEnabled( ), clazz.getSimpleName( ) + " does not enable the security token" );
            for ( Method method : clazz.getDeclaredMethods( ) )
            {
                Action action = method.getAnnotation( Action.class );
                if ( action != null )
                {
                    assertEquals( AbstractAppointmentJspBean.SECURITY_TOKEN_ACTION, action.securityTokenAction( ),
                            clazz.getSimpleName( ) + "." + method.getName( ) );
                    assertFalse( action.securityTokenDisabled( ), clazz.getSimpleName( ) + "." + method.getName( ) );
                }
                View view = method.getAnnotation( View.class );
                if ( view != null && !VIEWS_WITHOUT_SHARED_TOKEN.contains( view.value( ) ) )
                {
                    assertEquals( AbstractAppointmentJspBean.SECURITY_TOKEN_ACTION, view.securityTokenAction( ),
                            clazz.getSimpleName( ) + "." + method.getName( ) );
                }
            }
        }
        assertTrue( listControllers.size( ) >= 11, listControllers.toString( ) );
    }

    /**
     * An action posted without token is checked by the filter and refused
     */
    @Test
    public void testActionWithoutTokenIsRefused( )
    {
        SecurityTokenHandler handler = CDI.current( ).select( SecurityTokenHandler.class ).get( );
        handler.registerAdminControllers( null );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setMethod( "POST" );
        request.setServletPath( PATH_CATEGORY );
        request.addParameter( "action_createCategory", "" );
        request.addParameter( "label", "bh_token" );

        assertFalse( handler.shouldNotFilter( request ) );
        assertFalse( handler.validate( request, handler.resolveTokenAction( request, "createCategory" ) ) );
    }

    /**
     * The core disables the token of an action when the controller has a method of the same name without @Action nor @View (rule of the XPages
     * that do not use the MVC model): no such method may hide an action of the plugin
     */
    @Test
    public void testNoMethodDisablesTheTokenOfAnAction( )
    {
        for ( Bean<?> bean : CDI.current( ).getBeanManager( ).getBeans( MVCAdminJspBean.class, Any.Literal.INSTANCE ) )
        {
            Class<?> clazz = bean.getBeanClass( );
            if ( clazz.getAnnotation( Controller.class ) == null || !clazz.getName( ).startsWith( PACKAGE ) )
            {
                continue;
            }
            Set<String> setActions = new java.util.HashSet<>( );
            List<Method> listMethods = new ArrayList<>( );
            for ( Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass( ) )
            {
                listMethods.addAll( List.of( c.getDeclaredMethods( ) ) );
            }
            for ( Method method : listMethods )
            {
                if ( method.isAnnotationPresent( Action.class ) )
                {
                    setActions.add( method.getAnnotation( Action.class ).value( ) );
                }
            }
            for ( Method method : listMethods )
            {
                if ( method.getName( ).startsWith( "do" ) && !method.isAnnotationPresent( Action.class ) && !method.isAnnotationPresent( View.class ) )
                {
                    assertFalse( setActions.contains( method.getName( ) ),
                            clazz.getSimpleName( ) + "." + method.getName( ) + " disables the security token of the action " + method.getName( ) );
                }
            }
        }
    }

    /**
     * The modification of an answer posted without token is refused (a private method named as the action disabled its token)
     */
    @Test
    public void testModifyFieldWithoutTokenIsRefused( )
    {
        SecurityTokenHandler handler = CDI.current( ).select( SecurityTokenHandler.class ).get( );
        handler.registerAdminControllers( null );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setMethod( "POST" );
        request.setServletPath( "/jsp/admin/plugins/appointment/ManageAppointmentFormFields.jsp" );
        request.addParameter( "action", "doModifyField" );
        request.addParameter( "id_field", "1" );

        assertFalse( handler.shouldNotFilter( request ) );
    }

    /**
     * The token of a page of the plugin is accepted by an action of another controller of the plugin (a modal of the calendar of the appointments
     * posts to the specific week), once
     */
    @Test
    public void testTokenOfAPageOfThePluginIsAcceptedOnce( )
    {
        SecurityTokenHandler handler = CDI.current( ).select( SecurityTokenHandler.class ).get( );
        handler.registerAdminControllers( null );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        String strToken = CDI.current( ).select( ISecurityTokenService.class ).get( ).getToken( request, AbstractAppointmentJspBean.SECURITY_TOKEN_ACTION );
        request.setMethod( "POST" );
        request.setServletPath( PATH_SPECIFIC_WEEK );
        request.addParameter( "action_doModifyListSlot", "" );
        request.addParameter( SecurityTokenHandler.MARK_CSRF_TOKEN, strToken );

        assertFalse( handler.shouldNotFilter( request ) );
        assertTrue( handler.validate( request, handler.resolveTokenAction( request, "doModifyListSlot" ) ) );
        assertFalse( handler.validate( request, handler.resolveTokenAction( request, "doModifyListSlot" ) ) );
    }

    /**
     * A page rendered with a model built by the controller carries the token of the page in its forms
     */
    @Test
    public void testPageWithItsOwnModelCarriesTheToken( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );
        AppointmentCategoryJspBean instance = bean( request );
        SecurityTokenHandler handler = CDI.current( ).select( SecurityTokenHandler.class ).get( );
        handler.handle( request );
        handler.handleToken( request, ControllerInvocationType.VIEW, AppointmentCategoryJspBean.class.getMethod( "getCreateCategory", jakarta.servlet.http.HttpServletRequest.class ) );
        String strToken = handler.resolveTokenValue( request );
        assertNotNull( strToken );

        Method getPage = AbstractAppointmentJspBean.class.getDeclaredMethod( "getPage", String.class, String.class, Map.class );
        getPage.setAccessible( true );
        Map<String, Object> model = new HashMap<>( );
        model.put( "category", new fr.paris.lutece.plugins.appointment.business.category.Category( ) );
        String strHtml = (String) getPage.invoke( ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( ), "appointment.manage_category.pageTitle",
                "admin/plugins/appointment/category/create_category.html", model );

        assertTrue( strHtml.contains( "name=\"_csrftoken\" value=\"" + strToken + "\"" ), strHtml );
    }

    private static AppointmentCategoryJspBean bean( MockHttpServletRequest request ) throws Exception
    {
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( "super_admin", new RBACRole( "super_admin", "super_admin" ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, AppointmentCategoryJspBean.RIGHT_MANAGECATEGORY );
        AppointmentCategoryJspBean instance = CDI.current( ).select( AppointmentCategoryJspBean.class ).get( );
        instance.init( request, AppointmentCategoryJspBean.RIGHT_MANAGECATEGORY );
        return instance;
    }
}
