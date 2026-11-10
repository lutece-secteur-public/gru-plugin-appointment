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
package fr.paris.lutece.plugins.appointment.web.dto;

import java.text.MessageFormat;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.validation.constraints.Pattern;

/**
 * The anonymization pattern of a form is one of the patterns of the list
 */
public class AnonymizationPatternValidationTest extends LuteceTestCase
{
    private static final String FIELD_PATTERN = "_strAnonymizationPattern";
    private static final String KEY_MESSAGE = "appointment.validation.appointmentform.AnonymizationPattern.size";

    /**
     * The patterns of the list, and no pattern, are accepted
     *
     * @throws NoSuchFieldException
     *             if the field is renamed
     */
    @Test
    public void testListedPatternsAreAccepted( ) throws NoSuchFieldException
    {
        for ( String strPattern : new String [ ] {
                "%f", "%g", "%n", ""
        } )
        {
            assertTrue( strPattern, strPattern.matches( getRegexp( ) ) );
        }
    }

    /**
     * Any other value is refused, even when it is short
     *
     * @throws NoSuchFieldException
     *             if the field is renamed
     */
    @Test
    public void testOtherPatternsAreRefused( ) throws NoSuchFieldException
    {
        for ( String strPattern : new String [ ] {
                "%x", "ab", "%", "f", "%f%g"
        } )
        {
            assertFalse( strPattern, strPattern.matches( getRegexp( ) ) );
        }
    }

    /**
     * The validation message goes through MessageFormat: its apostrophes are kept
     */
    @Test
    public void testMessageKeepsApostrophes( )
    {
        String strMessage = MessageFormat.format( I18nService.getLocalizedString( KEY_MESSAGE, Locale.FRENCH ), "", "", "", "" );
        assertTrue( strMessage, strMessage.contains( "Pattern d'anonymisation" ) );
        assertTrue( strMessage, strMessage.contains( "l'un des patterns" ) );
    }

    private static String getRegexp( ) throws NoSuchFieldException
    {
        Pattern pattern = AppointmentFormDTO.class.getDeclaredField( FIELD_PATTERN ).getAnnotation( Pattern.class );
        assertTrue( "the pattern is restricted by @Pattern", pattern != null );
        return pattern.regexp( );
    }
}
