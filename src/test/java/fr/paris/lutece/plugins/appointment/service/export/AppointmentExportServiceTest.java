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
package fr.paris.lutece.plugins.appointment.service.export;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;

/**
 * The custom columns of the export are the questions of the appointment form only
 */
public class AppointmentExportServiceTest extends LuteceTestCase
{
    private static final int ID_RESOURCE = 987654;
    private static final int ID_ENTRY_TYPE_CHECKBOX = 124;

    @Test
    public void testCustomColumnsOfTheAppointmentFormOnly( )
    {
        int nIdEntryAppointment = createEntry( Form.RESOURCE_TYPE, "Q-APPOINTMENT" );
        int nIdEntryOtherPlugin = createEntry( "FORMS_FORM", "Q-FORMS-ONLY" );

        try
        {
            ReferenceList listColumns = AppointmentExportService.getCustomColumnList( String.valueOf( ID_RESOURCE ) );

            assertTrue( listColumns.stream( ).anyMatch( item -> String.valueOf( nIdEntryAppointment ).equals( item.getCode( ) ) ) );
            assertFalse( listColumns.stream( ).anyMatch( item -> String.valueOf( nIdEntryOtherPlugin ).equals( item.getCode( ) ) ) );
        }
        finally
        {
            EntryHome.remove( nIdEntryAppointment );
            EntryHome.remove( nIdEntryOtherPlugin );
        }
    }

    private static int createEntry( String strResourceType, String strTitle )
    {
        EntryType entryType = new EntryType( );
        entryType.setIdType( ID_ENTRY_TYPE_CHECKBOX );
        Entry entry = new Entry( );
        entry.setIdResource( ID_RESOURCE );
        entry.setResourceType( strResourceType );
        entry.setTitle( strTitle );
        entry.setEntryType( entryType );

        return EntryHome.create( entry );
    }
}
