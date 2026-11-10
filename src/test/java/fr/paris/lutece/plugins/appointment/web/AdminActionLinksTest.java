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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;

/**
 * An MVC action changes data: the back office templates post it from a form, never call it with a link or a URL opened by GET.
 */
public class AdminActionLinksTest
{
    private static final Path DIR_CONTROLLERS = Paths.get( "src/java/fr/paris/lutece/plugins/appointment/web" );
    private static final Path DIR_TEMPLATES = Paths.get( "webapp/WEB-INF/templates/admin/plugins/appointment" );
    private static final Pattern PATTERN_ACTION_IN_URL = Pattern.compile( "[?&](?:amp;)?action=([A-Za-z_]\\w*)" );

    @Test
    public void testNoActionIsCalledByGet( ) throws IOException
    {
        Set<String> actions = findActions( );
        assertFalse( actions.isEmpty( ), "No @Action found in the controllers" );

        List<String> getCalls = new ArrayList<>( );
        try ( Stream<Path> templates = Files.walk( DIR_TEMPLATES ) )
        {
            for ( Path template : (Iterable<Path>) templates.filter( p -> p.toString( ).endsWith( ".html" ) ).sorted( )::iterator )
            {
                List<String> lines = Files.readAllLines( template, StandardCharsets.UTF_8 );
                for ( int i = 0; i < lines.size( ); i++ )
                {
                    Matcher matcher = PATTERN_ACTION_IN_URL.matcher( lines.get( i ) );
                    while ( matcher.find( ) )
                    {
                        if ( actions.contains( matcher.group( 1 ) ) )
                        {
                            getCalls.add( DIR_TEMPLATES.relativize( template ) + ":" + ( i + 1 ) + " " + matcher.group( 1 ) );
                        }
                    }
                }
            }
        }
        assertTrue( getCalls.isEmpty( ), "Actions called by GET: " + getCalls );
    }

    /**
     * Reads the @Action values of the controllers of the plugin
     */
    private static Set<String> findActions( ) throws IOException
    {
        Set<String> actions = new HashSet<>( );
        try ( Stream<Path> sources = Files.walk( DIR_CONTROLLERS ) )
        {
            for ( Path source : (Iterable<Path>) sources.filter( p -> p.toString( ).endsWith( ".java" ) )::iterator )
            {
                String strClass = "fr.paris.lutece.plugins.appointment.web."
                        + DIR_CONTROLLERS.relativize( source ).toString( ).replace( ".java", "" ).replace( '/', '.' );
                Class<?> clazz;
                try
                {
                    clazz = Class.forName( strClass, false, AdminActionLinksTest.class.getClassLoader( ) );
                }
                catch( ClassNotFoundException e )
                {
                    continue;
                }
                for ( Method method : clazz.getDeclaredMethods( ) )
                {
                    Action action = method.getAnnotation( Action.class );
                    if ( action != null )
                    {
                        actions.add( action.value( ) );
                    }
                }
            }
        }
        return actions;
    }
}
