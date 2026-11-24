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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.CategoryTest;
import fr.paris.lutece.plugins.appointment.business.category.Category;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;

/**
 * The pagination of the lists of categories is shown on every page of a list of several pages, the last one included
 */
public class ManageListsPaginationRenderTest extends LuteceTestCase
{
    private static final String TEMPLATE_MANAGE_CATEGORY = "admin/plugins/appointment/category/manage_category.html";
    private static final String JSP = "jsp/admin/plugins/appointment/ManageAppointmentCategory.jsp";

    private static String renderPage( int nItems, String strPageIndex )
    {
        List<Category> list = new ArrayList<>( );
        for ( int i = 0; i < nItems; i++ )
        {
            Category category = CategoryTest.buildCategory( "bh category " + i );
            category.setIdCategory( i + 1 );
            list.add( category );
        }
        LocalizedPaginator<Category> paginator = new LocalizedPaginator<>( list, 10, JSP, "page_index", strPageIndex, Locale.FRENCH );
        Map<String, Object> model = new HashMap<>( );
        model.put( "paginator", paginator );
        model.put( "category_list", paginator.getPageItems( ) );
        model.put( "nb_items_per_page", "10" );
        model.put( "infos", List.of( ) );
        model.put( "errors", List.of( ) );
        return AppTemplateService.getTemplate( TEMPLATE_MANAGE_CATEGORY, Locale.FRENCH, model ).getHtml( );
    }

    @Test
    public void testLastPageHasThePagination( )
    {
        String strHtml = renderPage( 12, "2" );

        assertTrue( strHtml.contains( "bh category 11" ) );
        assertTrue( strHtml.contains( "page_index=1" ), strHtml );
    }

    @Test
    public void testFirstPageHasThePagination( )
    {
        assertTrue( renderPage( 12, "1" ).contains( "page_index=2" ) );
    }
}
