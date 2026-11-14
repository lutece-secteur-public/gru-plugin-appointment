/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;

/**
 * Renders the back office tabs of an appointment form (parameters, entries): each tab page has exactly one level-1
 * heading, the title of the page and the title of the form, escaped.
 */
public class AppointmentFormTabsRenderTest extends LuteceTestCase
{
    private static final String TEMPLATE_MODIFY_APPOINTMENTFORM = "admin/plugins/appointment/appointmentform/modify_appointmentform.html";
    private static final String TEMPLATE_MODIFY_APPOINTMENTFORM_ENTRIES = "admin/plugins/appointment/appointmentform/modify_appointmentform_entries.html";
    private static final String TITLE = "Form <b>\"&'";
    private static final String TITLE_ESCAPED = "Form &lt;b&gt;&quot;&amp;&#39;";
    private static final String H1 = "<h1";

    /**
     * Renders a template with the core configuration.
     *
     * @param strTemplate
     *            the template path
     * @param model
     *            the model
     * @return the HTML
     */
    private static String render( String strTemplate, Map<String, Object> model )
    {
        return AppTemplateService.getTemplate( strTemplate, Locale.FRENCH, model ).getHtml( );
    }

    /**
     * The model shared by the tabs of a form whose title holds markup characters
     *
     * @return the model
     */
    private static Map<String, Object> model( )
    {
        AppointmentFormDTO form = FormServiceTest.buildAppointmentForm( );
        form.setIdForm( 3 );
        form.setTitle( TITLE );
        Map<String, Object> model = new HashMap<>( );
        model.put( "appointmentform", form );
        model.put( "infos", List.of( ) );
        model.put( "errors", List.of( ) );
        return model;
    }

    /**
     * Counts the level-1 headings of a page
     *
     * @param strHtml
     *            the HTML
     * @return the number of h1 elements
     */
    private static int countH1( String strHtml )
    {
        return strHtml.split( H1, -1 ).length - 1;
    }

    /**
     * The expected level-1 heading: the localized title of the page and the escaped title of the form
     *
     * @param strTitleKey
     *            the i18n key of the title of the page
     * @return the h1 element
     */
    private static String heading( String strTitleKey )
    {
        return "<h1 class=\"page-title mb-1\">" + I18nService.getLocalizedString( strTitleKey, Locale.FRENCH ) + " : " + TITLE_ESCAPED + "</h1>";
    }

    /**
     * The parameters tab has one level-1 heading
     */
    @Test
    public void testParametersTabHasOnePageHeading( )
    {
        Map<String, Object> model = model( );
        model.put( "listCategories", new ReferenceList( ) );
        model.put( "refListCalendarTemplates", new ReferenceList( ) );
        model.put( "refListRoles", new ReferenceList( ) );
        model.put( "user_workgroup_list", new ReferenceList( ) );
        model.put( "isCaptchaEnabled", false );

        String strHtml = render( TEMPLATE_MODIFY_APPOINTMENTFORM, model );

        assertEquals( strHtml, 1, countH1( strHtml ) );
        assertTrue( strHtml, strHtml.contains( heading( "appointment.modifyAppointmentForm.titleAlterablesParameters" ) ) );
        assertFalse( strHtml, strHtml.contains( TITLE ) );
    }

    /**
     * The entries tab has one level-1 heading
     */
    @Test
    public void testEntriesTabHasOnePageHeading( )
    {
        Map<String, Object> model = model( );
        model.put( "entry_list", List.of( ) );
        model.put( "entry_type_list", List.of( ) );
        model.put( "entry_group_list", new ReferenceList( ) );

        String strHtml = render( TEMPLATE_MODIFY_APPOINTMENTFORM_ENTRIES, model );

        assertEquals( strHtml, 1, countH1( strHtml ) );
        assertTrue( strHtml, strHtml.contains( heading( "appointment.modifyAppointmentFormEntries.pageTitle" ) ) );
        assertFalse( strHtml, strHtml.contains( TITLE ) );
    }
}
