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
package fr.paris.lutece.plugins.appointment.business;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.validation.constraints.Pattern;

/**
 * Pattern of the first name and the last name of the user who takes an appointment
 */
public class UserNameValidationTest extends LuteceTestCase
{
    private static final String FIELD_FIRST_NAME = "_strFirstName";
    private static final String FIELD_LAST_NAME = "_strLastName";

    /**
     * Compound names are names: hyphen, space or apostrophe between two letters
     *
     * @throws NoSuchFieldException
     *             if a name field is renamed
     */
    @Test
    public void testCompoundNamesAreAccepted( ) throws NoSuchFieldException
    {
        for ( String strField : new String [ ] {
                FIELD_FIRST_NAME, FIELD_LAST_NAME
        } )
        {
            String strRegexp = getRegexp( strField );
            for ( String strName : new String [ ] {
                    "Martin", "Jean-Pierre", "Le Gall", "O'Neil", "D’Artagnan", "Éloïse"
            } )
            {
                assertTrue( strField + " " + strName, strName.matches( strRegexp ) );
            }
        }
    }

    /**
     * Digits, other characters, and separators that are not between two letters stay refused
     *
     * @throws NoSuchFieldException
     *             if a name field is renamed
     */
    @Test
    public void testOtherCharactersAreRefused( ) throws NoSuchFieldException
    {
        for ( String strField : new String [ ] {
                FIELD_FIRST_NAME, FIELD_LAST_NAME
        } )
        {
            String strRegexp = getRegexp( strField );
            for ( String strName : new String [ ] {
                    "Jean2", "Martin<b>", "-Jean", "Martin-", "Jean--Pierre", "Le  Gall", " Martin", "Jean_Pierre"
            } )
            {
                assertFalse( strField + " " + strName, strName.matches( strRegexp ) );
            }
        }
    }

    /**
     * An empty name is left to @NotBlank and its own message: the pattern does not refuse it too
     *
     * @throws NoSuchFieldException
     *             if a name field is renamed
     */
    @Test
    public void testEmptyNameIsLeftToNotBlank( ) throws NoSuchFieldException
    {
        assertTrue( "".matches( getRegexp( FIELD_FIRST_NAME ) ) );
        assertTrue( "".matches( getRegexp( FIELD_LAST_NAME ) ) );
    }

    private static String getRegexp( String strField ) throws NoSuchFieldException
    {
        return User.class.getDeclaredField( strField ).getAnnotation( Pattern.class ).regexp( );
    }
}
