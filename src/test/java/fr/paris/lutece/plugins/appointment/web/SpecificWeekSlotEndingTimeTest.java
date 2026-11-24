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

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.AppointmentTest;
import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.service.SlotSafeService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Modification of the ending time of a slot of the specific week, without shift of the next slots
 */
public class SpecificWeekSlotEndingTimeTest extends LuteceTestCase
{
    private static final String ROLE_SUPER_ADMIN = "super_admin";
    private static final LocalDate DAY = LocalDate.now( ).plusWeeks( 2 ).with( DayOfWeek.MONDAY );

    private int _nIdForm;
    private Slot _slotA;
    private Slot _slotB;
    private AppointmentDTO _appointment;

    @BeforeEach
    public void createSlots( )
    {
        AppointmentFormDTO form = FormServiceTest.buildAppointmentForm( );
        _nIdForm = FormService.createAppointmentForm( form );
        _slotA = SlotService.saveSlot( SlotTest.buildSlot( _nIdForm, DAY.atTime( 10, 0 ), DAY.atTime( 10, 30 ), 3, 3, 0, 3, Boolean.TRUE, Boolean.FALSE ) );
        _slotB = SlotService.saveSlot( SlotTest.buildSlot( _nIdForm, DAY.atTime( 10, 30 ), DAY.atTime( 11, 0 ), 3, 3, 0, 3, Boolean.TRUE, Boolean.FALSE ) );
    }

    @AfterEach
    public void cleanUp( )
    {
        if ( _appointment != null )
        {
            AppointmentHome.delete( _appointment.getIdAppointment( ) );
            User user = UserHome.findByFirstNameLastNameAndEmail( _appointment.getFirstName( ), _appointment.getLastName( ), _appointment.getEmail( ) );
            if ( user != null )
            {
                UserHome.delete( user.getIdUser( ) );
            }
            _appointment = null;
        }
        FormServiceTest.cleanForm( _nIdForm );
    }

    /**
     * A slot extended into the next one, which has no appointment: the next one is replaced, the slots of the day do not overlap
     */
    @Test
    public void testExtendIntoTheNextSlot( )
    {
        Slot slot = SlotService.findSlotById( _slotA.getIdSlot( ) );
        slot.setEndingTime( LocalTime.of( 10, 45 ) );
        slot.setEndingDateTime( DAY.atTime( 10, 45 ) );

        SlotSafeService.updateSlot( slot, true, LocalTime.of( 10, 30 ), false );

        List<Slot> listSlot = slotsOfTheDay( );
        for ( int i = 1; i < listSlot.size( ); i++ )
        {
            Slot previous = listSlot.get( i - 1 );
            assertFalse( listSlot.get( i ).getStartingDateTime( ).isBefore( previous.getEndingDateTime( ) ),
                    "overlap: " + previous.getStartingDateTime( ) + "-" + previous.getEndingDateTime( ) + " / " + listSlot.get( i ).getStartingDateTime( ) );
        }
        assertEquals( DAY.atTime( 10, 45 ), SlotService.findSlotById( _slotA.getIdSlot( ) ).getEndingDateTime( ) );
        assertNull( SlotService.findSlotById( _slotB.getIdSlot( ) ) );
    }

    /**
     * A slot extended into the next one, which has an appointment: refused, nothing changed
     */
    @Test
    public void testExtendIntoTheNextSlotWithAppointment( ) throws Exception
    {
        bookSlotB( );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        SpecificWeekJspBean instance = bean( request );
        request.addParameter( "id_slot", String.valueOf( _slotA.getIdSlot( ) ) );
        request.addParameter( "id_form", String.valueOf( _nIdForm ) );
        request.addParameter( "ending_time", "10:45" );
        request.addParameter( "is_open", "true" );
        request.addParameter( "max_capacity", "3" );
        request.addParameter( "shift_slot", "false" );

        instance.doModifySlot( request );

        assertEquals( DAY.atTime( 10, 30 ), SlotService.findSlotById( _slotA.getIdSlot( ) ).getEndingDateTime( ) );
        assertNotNull( SlotService.findSlotById( _slotB.getIdSlot( ) ) );
        assertEquals( 2, slotsOfTheDay( ).size( ) );
    }

    private void bookSlotB( )
    {
        _appointment = AppointmentTest.buildAppointmentDTO( _nIdForm, _slotB, "bh@mdp.fr", "Jean", "Dupont", LocalTime.of( 10, 30 ), LocalTime.of( 11, 0 ), 1 );
        try
        {
            AppointmentService.saveAppointment( _appointment );
        }
        catch( Exception e )
        {
            fail( e.getLocalizedMessage( ) );
        }
    }

    private List<Slot> slotsOfTheDay( )
    {
        List<Slot> listSlot = SlotService.findSlotsByIdFormAndDateRange( _nIdForm, DAY.atStartOfDay( ), DAY.atTime( LocalTime.MAX ) );
        listSlot.sort( Comparator.comparing( Slot::getStartingDateTime ) );
        return listSlot;
    }

    private static SpecificWeekJspBean bean( MockHttpServletRequest request ) throws Exception
    {
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_SUPER_ADMIN, new RBACRole( ROLE_SUPER_ADMIN, ROLE_SUPER_ADMIN ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );

        SpecificWeekJspBean instance = CDI.current( ).select( SpecificWeekJspBean.class ).get( );
        instance.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        // processController gives the bean its response for the redirects: set it as processController does
        Field fieldResponse = MVCAdminJspBean.class.getDeclaredField( "_response" );
        fieldResponse.setAccessible( true );
        fieldResponse.set( ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( ), new MockHttpServletResponse( ) );
        return instance;
    }
}
