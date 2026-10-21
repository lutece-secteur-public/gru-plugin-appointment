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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.appointment.business.display.CalendarButtonConfig;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * Service class for the "add to calendar" button: options stored as JSON, links to the online calendars and iCalendar
 * (RFC 5545) file. Link formats: https://github.com/InteractionDesignFoundation/add-event-to-calendar-docs
 */
public final class CalendarButtonService
{
    private static final ObjectMapper MAPPER = new ObjectMapper( );
    private static final DateTimeFormatter FORMAT_BASIC = DateTimeFormatter.ofPattern( "yyyyMMdd'T'HHmmss" );
    private static final DateTimeFormatter FORMAT_BASIC_UTC = DateTimeFormatter.ofPattern( "yyyyMMdd'T'HHmmss'Z'" );
    private static final DateTimeFormatter FORMAT_ISO_UTC = DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
    private static final String URL_GOOGLE = "https://calendar.google.com/calendar/render?action=TEMPLATE";
    private static final String URL_OUTLOOKCOM = "https://outlook.live.com/calendar/deeplink/compose?path=/calendar/action/compose&rru=addevent";
    private static final String URL_MS365 = "https://outlook.office.com/calendar/deeplink/compose?path=/calendar/action/compose&rru=addevent";
    private static final String URL_YAHOO = "https://calendar.yahoo.com/?v=60";
    private static final String URL_MSTEAMS = "https://teams.microsoft.com/l/meeting/new?";
    private static final String ICS_LINE_END = "\r\n";
    private static final int ICS_MAX_LINE_OCTETS = 75;

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private CalendarButtonService( )
    {
    }

    /**
     * Read the options stored as JSON
     * 
     * @param strJson
     *            the JSON, may be null
     * @return the options, the defaults when the JSON is empty or unreadable
     */
    public static CalendarButtonConfig fromJson( String strJson )
    {
        if ( StringUtils.isBlank( strJson ) )
        {
            return new CalendarButtonConfig( );
        }
        try
        {
            CalendarButtonConfig config = MAPPER.readValue( strJson, CalendarButtonConfig.class );
            sanitize( config );
            return config;
        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( "Unreadable options of the add to calendar button, defaults used", e );
            return new CalendarButtonConfig( );
        }
    }

    /**
     * Write the options as JSON
     * 
     * @param config
     *            the options, may be null
     * @return the JSON, null when there are no options
     */
    public static String toJson( CalendarButtonConfig config )
    {
        if ( config == null )
        {
            return null;
        }
        try
        {
            return MAPPER.writeValueAsString( config );
        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( "Options of the add to calendar button not written", e );
            return null;
        }
    }

    /**
     * Bring every option back to an allowed value: unknown calendars are dropped, an unknown color or availability takes
     * the default
     * 
     * @param config
     *            the options to clean
     */
    public static void sanitize( CalendarButtonConfig config )
    {
        CalendarButtonConfig defaults = new CalendarButtonConfig( );
        List<String> listOptions = ( config.getOptions( ) == null ) ? List.of( ) : config.getOptions( );
        config.setOptions( CalendarButtonConfig.VALUES_OPTIONS.stream( ).filter( listOptions::contains ).collect( Collectors.toList( ) ) );
        config.setColor( valueOrDefault( config.getColor( ), CalendarButtonConfig.VALUES_COLOR, defaults.getColor( ) ) );
        config.setDisplayMode( valueOrDefault( config.getDisplayMode( ), CalendarButtonConfig.VALUES_DISPLAY_MODE, defaults.getDisplayMode( ) ) );
        config.setTrigger( valueOrDefault( config.getTrigger( ), CalendarButtonConfig.VALUES_TRIGGER, defaults.getTrigger( ) ) );
        config.setButtonsLayout( valueOrDefault( config.getButtonsLayout( ), CalendarButtonConfig.VALUES_BUTTONS_LAYOUT, defaults.getButtonsLayout( ) ) );
        config.setButtonStyle( valueOrDefault( config.getButtonStyle( ), CalendarButtonConfig.VALUES_BUTTON_STYLE, defaults.getButtonStyle( ) ) );
        config.setAvailability( valueOrDefault( config.getAvailability( ), CalendarButtonConfig.VALUES_AVAILABILITY, StringUtils.EMPTY ) );
        config.setLabel( StringUtils.trimToEmpty( config.getLabel( ) ) );
        config.setLocation( StringUtils.trimToEmpty( config.getLocation( ) ) );
    }

    /**
     * The values an administrator can choose, by option
     * 
     * @return the lists of values, keyed by option name
     */
    public static Map<String, List<String>> getChoices( )
    {
        Map<String, List<String>> mapChoices = new LinkedHashMap<>( );
        mapChoices.put( "options", CalendarButtonConfig.VALUES_OPTIONS );
        mapChoices.put( "displayMode", CalendarButtonConfig.VALUES_DISPLAY_MODE );
        mapChoices.put( "trigger", CalendarButtonConfig.VALUES_TRIGGER );
        mapChoices.put( "buttonsLayout", CalendarButtonConfig.VALUES_BUTTONS_LAYOUT );
        mapChoices.put( "buttonStyle", CalendarButtonConfig.VALUES_BUTTON_STYLE );
        mapChoices.put( "color", CalendarButtonConfig.VALUES_COLOR );
        mapChoices.put( "availability", CalendarButtonConfig.VALUES_AVAILABILITY );
        return mapChoices;
    }

    /**
     * The location of the event: the one of the options, or the address of the form
     * 
     * @param config
     *            the options
     * @param strFormAddress
     *            the address of the form, may be null
     * @return the location, empty when there is none
     */
    public static String getLocation( CalendarButtonConfig config, String strFormAddress )
    {
        return StringUtils.defaultIfBlank( config.getLocation( ), StringUtils.trimToEmpty( strFormAddress ) );
    }

    /**
     * Build the links of the calendars proposed by the options. Yahoo has no time zone and ignores the Z suffix: it
     * receives the local time of the appointment, the other calendars receive UTC. Microsoft Teams has no location: it
     * opens the content of the meeting. Apple and the iCal file both download the iCalendar file.
     * 
     * @param config
     *            the options
     * @param strTitle
     *            the title of the event
     * @param dateStart
     *            the start of the event, in the time zone of the server
     * @param dateEnd
     *            the end of the event, in the time zone of the server
     * @param strLocation
     *            the location of the event, may be empty
     * @param strDescription
     *            the description of the event, plain text, may be empty
     * @param strIcsUrl
     *            the URL of the iCalendar file of the event
     * @return the links, keyed by calendar, in the order of the options
     */
    public static Map<String, String> buildLinks( CalendarButtonConfig config, String strTitle, LocalDateTime dateStart, LocalDateTime dateEnd,
            String strLocation, String strDescription, String strIcsUrl )
    {
        String strUtcStart = toUtc( dateStart ).format( FORMAT_ISO_UTC );
        String strUtcEnd = toUtc( dateEnd ).format( FORMAT_ISO_UTC );
        boolean bFree = CalendarButtonConfig.AVAILABILITY_FREE.equals( config.getAvailability( ) );
        boolean bAvailabilitySet = StringUtils.isNotEmpty( config.getAvailability( ) );

        Map<String, String> mapLinks = new LinkedHashMap<>( );
        for ( String strOption : config.getOptions( ) )
        {
            StringBuilder sbUrl = new StringBuilder( );
            switch( strOption )
            {
                case CalendarButtonConfig.OPTION_GOOGLE:
                    sbUrl.append( URL_GOOGLE ).append( "&text=" ).append( encode( strTitle ) );
                    sbUrl.append( "&dates=" ).append( toUtc( dateStart ).format( FORMAT_BASIC_UTC ) ).append( '/' )
                            .append( toUtc( dateEnd ).format( FORMAT_BASIC_UTC ) );
                    appendIfNotEmpty( sbUrl, "location", strLocation );
                    appendIfNotEmpty( sbUrl, "details", strDescription );
                    if ( bAvailabilitySet )
                    {
                        sbUrl.append( "&crm=" ).append( bFree ? "AVAILABLE" : "BUSY" );
                    }
                    break;
                case CalendarButtonConfig.OPTION_OUTLOOKCOM:
                case CalendarButtonConfig.OPTION_MS365:
                    sbUrl.append( CalendarButtonConfig.OPTION_MS365.equals( strOption ) ? URL_MS365 : URL_OUTLOOKCOM );
                    sbUrl.append( "&subject=" ).append( encode( strTitle ) );
                    sbUrl.append( "&startdt=" ).append( strUtcStart ).append( "&enddt=" ).append( strUtcEnd );
                    appendIfNotEmpty( sbUrl, "location", strLocation );
                    appendIfNotEmpty( sbUrl, "body", strDescription );
                    if ( bAvailabilitySet )
                    {
                        sbUrl.append( "&freebusy=" ).append( config.getAvailability( ) );
                    }
                    break;
                case CalendarButtonConfig.OPTION_MSTEAMS:
                    sbUrl.append( URL_MSTEAMS ).append( "subject=" ).append( encode( strTitle ) );
                    sbUrl.append( "&startTime=" ).append( strUtcStart ).append( "&endTime=" ).append( strUtcEnd );
                    appendIfNotEmpty( sbUrl, "content", StringUtils.isEmpty( strLocation ) ? strDescription
                            : StringUtils.joinWith( "\n\n", strLocation, strDescription ).trim( ) );
                    break;
                case CalendarButtonConfig.OPTION_YAHOO:
                    sbUrl.append( URL_YAHOO ).append( "&TITLE=" ).append( encode( strTitle ) );
                    sbUrl.append( "&ST=" ).append( dateStart.format( FORMAT_BASIC ) ).append( "&ET=" ).append( dateEnd.format( FORMAT_BASIC ) );
                    appendIfNotEmpty( sbUrl, "in_loc", strLocation );
                    appendIfNotEmpty( sbUrl, "DESC", strDescription );
                    break;
                default:
                    sbUrl.append( strIcsUrl );
                    break;
            }
            mapLinks.put( strOption, sbUrl.toString( ) );
        }
        return mapLinks;
    }

    /**
     * Build the iCalendar file (RFC 5545) of an event
     * 
     * @param strUid
     *            the unique identifier of the event
     * @param strTitle
     *            the title of the event
     * @param dateStart
     *            the start of the event, in the time zone of the server
     * @param dateEnd
     *            the end of the event, in the time zone of the server
     * @param strLocation
     *            the location of the event, may be empty
     * @param strDescription
     *            the description of the event, plain text, may be empty
     * @param strAvailability
     *            busy, free or empty
     * @return the content of the file
     */
    public static String buildIcs( String strUid, String strTitle, LocalDateTime dateStart, LocalDateTime dateEnd, String strLocation,
            String strDescription, String strAvailability )
    {
        StringBuilder sbIcs = new StringBuilder( );
        appendIcsLine( sbIcs, "BEGIN:VCALENDAR" );
        appendIcsLine( sbIcs, "VERSION:2.0" );
        appendIcsLine( sbIcs, "PRODID:-//Lutece//Appointment//EN" );
        appendIcsLine( sbIcs, "CALSCALE:GREGORIAN" );
        appendIcsLine( sbIcs, "METHOD:PUBLISH" );
        appendIcsLine( sbIcs, "BEGIN:VEVENT" );
        appendIcsLine( sbIcs, "UID:" + escapeIcsText( strUid ) );
        appendIcsLine( sbIcs, "DTSTAMP:" + ZonedDateTime.now( ZoneOffset.UTC ).format( FORMAT_BASIC_UTC ) );
        appendIcsLine( sbIcs, "DTSTART:" + toUtc( dateStart ).format( FORMAT_BASIC_UTC ) );
        appendIcsLine( sbIcs, "DTEND:" + toUtc( dateEnd ).format( FORMAT_BASIC_UTC ) );
        appendIcsLine( sbIcs, "SUMMARY:" + escapeIcsText( strTitle ) );
        if ( StringUtils.isNotEmpty( strLocation ) )
        {
            appendIcsLine( sbIcs, "LOCATION:" + escapeIcsText( strLocation ) );
        }
        if ( StringUtils.isNotEmpty( strDescription ) )
        {
            appendIcsLine( sbIcs, "DESCRIPTION:" + escapeIcsText( strDescription ) );
        }
        if ( StringUtils.isNotEmpty( strAvailability ) )
        {
            appendIcsLine( sbIcs, "TRANSP:" + ( CalendarButtonConfig.AVAILABILITY_FREE.equals( strAvailability ) ? "TRANSPARENT" : "OPAQUE" ) );
        }
        appendIcsLine( sbIcs, "END:VEVENT" );
        appendIcsLine( sbIcs, "END:VCALENDAR" );
        return sbIcs.toString( );
    }

    /**
     * Turn a rich text into the plain text of a calendar description: tags removed, entities decoded, a line break for
     * each paragraph, line break and list item, spaces collapsed
     * 
     * @param strHtml
     *            the rich text, may be null
     * @return the plain text, empty when there is none
     */
    public static String toPlainText( String strHtml )
    {
        if ( StringUtils.isBlank( strHtml ) )
        {
            return StringUtils.EMPTY;
        }
        Document document = Jsoup.parse( strHtml );
        document.select( "li" ).prepend( "- " );
        document.select( "p, div, li, tr, h1, h2, h3, h4, h5, h6, ul, ol" ).after( "\n" );
        String strText = document.body( ).wholeText( );
        String strLines = strText.lines( ).map( strLine -> strLine.replaceAll( "[\\s\\u00A0]+", " " ).trim( ) ).collect( Collectors.joining( "\n" ) );
        return strLines.replaceAll( "\n{3,}", "\n\n" ).trim( );
    }

    /**
     * Convert a date of the server time zone to UTC
     * 
     * @param date
     *            the date, in the time zone of the server
     * @return the same instant in UTC
     */
    private static ZonedDateTime toUtc( LocalDateTime date )
    {
        return date.atZone( ZoneId.systemDefault( ) ).withZoneSameInstant( ZoneOffset.UTC );
    }

    /**
     * Encode a value for a URL query, spaces as %20
     * 
     * @param strValue
     *            the value, may be null
     * @return the encoded value
     */
    private static String encode( String strValue )
    {
        return URLEncoder.encode( StringUtils.defaultString( strValue ), StandardCharsets.UTF_8 ).replace( "+", "%20" );
    }

    /**
     * Append a parameter to a URL when its value is not empty
     * 
     * @param sbUrl
     *            the URL being built
     * @param strName
     *            the name of the parameter
     * @param strValue
     *            the value, not encoded
     */
    private static void appendIfNotEmpty( StringBuilder sbUrl, String strName, String strValue )
    {
        if ( StringUtils.isNotEmpty( strValue ) )
        {
            sbUrl.append( '&' ).append( strName ).append( '=' ).append( encode( strValue ) );
        }
    }

    /**
     * Escape a TEXT value of an iCalendar file (RFC 5545, 3.3.11)
     * 
     * @param strValue
     *            the value, may be null
     * @return the escaped value
     */
    private static String escapeIcsText( String strValue )
    {
        return StringUtils.defaultString( strValue ).replace( "\\", "\\\\" ).replace( ";", "\\;" ).replace( ",", "\\," ).replace( "\r\n", "\\n" )
                .replace( "\n", "\\n" ).replace( "\r", "\\n" );
    }

    /**
     * Append a content line, folded at 75 octets as RFC 5545 requires, without splitting a UTF-8 character. A
     * continuation line starts with a space, which counts in its 75 octets.
     * 
     * @param sbIcs
     *            the file being built
     * @param strLine
     *            the content line, unfolded
     */
    private static void appendIcsLine( StringBuilder sbIcs, String strLine )
    {
        int nOctets = 0;
        int nIndex = 0;
        while ( nIndex < strLine.length( ) )
        {
            int nCodePoint = strLine.codePointAt( nIndex );
            String strChar = new String( Character.toChars( nCodePoint ) );
            int nCharOctets = strChar.getBytes( StandardCharsets.UTF_8 ).length;
            if ( nOctets + nCharOctets > ICS_MAX_LINE_OCTETS )
            {
                sbIcs.append( ICS_LINE_END ).append( ' ' );
                nOctets = 1;
            }
            sbIcs.append( strChar );
            nOctets += nCharOctets;
            nIndex += Character.charCount( nCodePoint );
        }
        sbIcs.append( ICS_LINE_END );
    }

    /**
     * Keep a value when it is allowed
     * 
     * @param strValue
     *            the value
     * @param listAllowed
     *            the allowed values
     * @param strDefault
     *            the value used otherwise
     * @return the value or the default
     */
    private static String valueOrDefault( String strValue, List<String> listAllowed, String strDefault )
    {
        return listAllowed.contains( strValue ) ? strValue : strDefault;
    }
}
