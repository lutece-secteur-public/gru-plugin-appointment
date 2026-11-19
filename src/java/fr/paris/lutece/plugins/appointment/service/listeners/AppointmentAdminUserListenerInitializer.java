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
package fr.paris.lutece.plugins.appointment.service.listeners;

import fr.paris.lutece.portal.service.util.RemovalListenerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.ServletContext;

/**
 * Registers at startup the listener refusing the deletion of a back office user to whom upcoming appointments are assigned
 */
@ApplicationScoped
public class AppointmentAdminUserListenerInitializer
{
    // Name of the admin user removal service of lutece-core (BeanUtils.BEAN_ADMIN_USER_REMOVAL_SERVICE), looked up by name so that the plugin
    // still starts on a core that does not provide it
    private static final String BEAN_ADMIN_USER_REMOVAL_SERVICE = "adminUserRemovalService";

    /**
     * Registers the listener
     *
     * @param context
     *            the servlet context
     */
    public void onStartup( @Observes @Initialized( ApplicationScoped.class ) ServletContext context )
    {
        Instance<RemovalListenerService> service = CDI.current( ).select( RemovalListenerService.class, NamedLiteral.of( BEAN_ADMIN_USER_REMOVAL_SERVICE ) );

        if ( service.isResolvable( ) )
        {
            service.get( ).registerListener( new AdminUserAppointmentRemovalListener( ) );
        }
    }
}
