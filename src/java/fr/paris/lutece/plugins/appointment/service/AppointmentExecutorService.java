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
package fr.paris.lutece.plugins.appointment.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public enum AppointmentExecutorService
{
    INSTANCE;
    private static final String PROPERTY_THREAD_APPOINTMENT_POOL_MAX_SIZE = "appointment.executor.thread.pool.max.size";
    private final ExecutorService _executorService = Executors.newFixedThreadPool(
            AppPropertiesService.getPropertyInt( PROPERTY_THREAD_APPOINTMENT_POOL_MAX_SIZE, Runtime.getRuntime( ).availableProcessors( ) ),
            new CustomizableThreadFactory( "Lutece-AppointmentExecutor-thread-" ) );

    /**
     * Executes the given command at some time in the future. The command may execute in a new thread, in a pooled thread, or in the calling thread, at the
     * discretion of the {@code Executor} implementation.
     *
     * @param command
     *            the runnable task
     * @throws RejectedExecutionException
     *             if this task cannot be accepted for execution
     * @throws NullPointerException
     *             if command is null
     */
    public void execute( Runnable task )
    {
        _executorService.execute( task );
    }

    /**
     * Submits a Runnable task for execution and returns a Future representing that task. The Future's ge} method will return null upon <em>successful</em>
     * completion.
     *
     * @param task
     *            the task to submit
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException
     *             if the task cannot be scheduled for execution
     * @throws NullPointerException
     *             if the task is null
     */
    public Future<?> submit( Runnable task )
    {

        return _executorService.submit( task );
    }

    /**
     * The following method shuts down the _executorService in two phases, first by calling shutdown to reject incoming tasks, and then calling shutdownNow, if
     * necessary, to cancel any lingering tasks:
     */
    public void shutdown( )
    {

        _executorService.shutdown( );
        try
        {
            if ( !_executorService.awaitTermination( 60, TimeUnit.SECONDS ) )
            {
                _executorService.shutdownNow( );
            }
        }
        catch( InterruptedException e )
        {
            // (Re-)Cancel if current thread also interrupted
            AppLogService.error( e.getMessage( ), e );
            _executorService.shutdownNow( );
            Thread.currentThread().interrupt();
        }
    }

}
