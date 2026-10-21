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
package fr.paris.lutece.plugins.appointment.business.display;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Options of the "add to calendar" button shown on the confirmation page of an appointment. Stored as JSON in
 * appointment_display.calendar_button_config. An empty String means the default is used.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public final class CalendarButtonConfig implements Serializable
{
    public static final String OPTION_GOOGLE = "google";
    public static final String OPTION_OUTLOOKCOM = "outlookcom";
    public static final String OPTION_MS365 = "ms365";
    public static final String OPTION_MSTEAMS = "msteams";
    public static final String OPTION_YAHOO = "yahoo";
    public static final String OPTION_APPLE = "apple";
    public static final String OPTION_ICAL = "ical";
    public static final List<String> VALUES_OPTIONS = List.of( OPTION_GOOGLE, OPTION_OUTLOOKCOM, OPTION_MS365, OPTION_MSTEAMS, OPTION_YAHOO, OPTION_APPLE,
            OPTION_ICAL );
    public static final List<String> VALUES_OPTIONS_ICS = List.of( OPTION_APPLE, OPTION_ICAL );
    public static final List<String> DEFAULT_OPTIONS = List.of( OPTION_GOOGLE, OPTION_OUTLOOKCOM, OPTION_MS365, OPTION_YAHOO, OPTION_APPLE, OPTION_ICAL );
    public static final String DISPLAY_DROPDOWN = "dropdown";
    public static final String DISPLAY_BUTTONS = "buttons";
    public static final String DISPLAY_CALENDAR = "calendar";
    public static final List<String> VALUES_DISPLAY_MODE = List.of( DISPLAY_DROPDOWN, DISPLAY_BUTTONS, DISPLAY_CALENDAR );
    public static final List<String> VALUES_TRIGGER = List.of( "click", "hover" );
    public static final List<String> VALUES_BUTTONS_LAYOUT = List.of( "row", "column" );
    public static final List<String> VALUES_BUTTON_STYLE = List.of( "bootstrap", "default", "simple", "3d", "flat", "round", "text" );
    public static final List<String> VALUES_COLOR = List.of( "primary", "secondary", "success", "info", "warning", "danger", "light", "dark" );
    public static final String AVAILABILITY_BUSY = "busy";
    public static final String AVAILABILITY_FREE = "free";
    public static final List<String> VALUES_AVAILABILITY = List.of( AVAILABILITY_BUSY, AVAILABILITY_FREE );

    private static final long serialVersionUID = 4373384157253041961L;

    private boolean _bEnabled = false;
    private List<String> _listOptions = new ArrayList<>( DEFAULT_OPTIONS );
    private String _strLabel = "";
    private String _strColor = "primary";
    private String _strDisplayMode = DISPLAY_DROPDOWN;
    private String _strButtonsLayout = "row";
    private String _strTrigger = "click";
    private boolean _bShowButtonIcon = true;
    private boolean _bShowLinkIcons = true;
    private String _strButtonStyle = "bootstrap";
    private String _strLocation = "";
    private String _strAvailability = "";

    /**
     * @return true if the button is displayed on the confirmation page, false by default
     */
    public boolean isEnabled( )
    {
        return _bEnabled;
    }

    /**
     * @param bEnabled
     *            true to display the button on the confirmation page
     */
    public void setEnabled( boolean bEnabled )
    {
        _bEnabled = bEnabled;
    }

    /**
     * @return the calendars proposed, in the order of VALUES_OPTIONS
     */
    public List<String> getOptions( )
    {
        return _listOptions;
    }

    /**
     * @param listOptions
     *            the calendars proposed
     */
    public void setOptions( List<String> listOptions )
    {
        _listOptions = listOptions;
    }

    /**
     * @return the button text, empty for the default text
     */
    public String getLabel( )
    {
        return _strLabel;
    }

    /**
     * @param strLabel
     *            the button text
     */
    public void setLabel( String strLabel )
    {
        _strLabel = strLabel;
    }

    /**
     * @return the Bootstrap color of the button, also the accent of the predefined styles
     */
    public String getColor( )
    {
        return _strColor;
    }

    /**
     * @param strColor
     *            the Bootstrap color of the button
     */
    public void setColor( String strColor )
    {
        _strColor = strColor;
    }

    /**
     * @return how the calendars are proposed: dropdown, buttons (one button per calendar) or calendar (event card with a
     *         "+" menu)
     */
    public String getDisplayMode( )
    {
        return _strDisplayMode;
    }

    /**
     * @param strDisplayMode
     *            how the calendars are proposed
     */
    public void setDisplayMode( String strDisplayMode )
    {
        _strDisplayMode = strDisplayMode;
    }

    /**
     * @return row or column: the layout of the buttons when one button per calendar is displayed
     */
    public String getButtonsLayout( )
    {
        return _strButtonsLayout;
    }

    /**
     * @param strButtonsLayout
     *            row or column
     */
    public void setButtonsLayout( String strButtonsLayout )
    {
        _strButtonsLayout = strButtonsLayout;
    }

    /**
     * @return click or hover: what opens the menu of the "dropdown" and "calendar" displays
     */
    public String getTrigger( )
    {
        return _strTrigger;
    }

    /**
     * @param strTrigger
     *            click or hover
     */
    public void setTrigger( String strTrigger )
    {
        _strTrigger = strTrigger;
    }

    /**
     * @return true if the "Add to calendar" button shows its icon
     */
    public boolean isShowButtonIcon( )
    {
        return _bShowButtonIcon;
    }

    /**
     * @param bShowButtonIcon
     *            true to show the icon of the "Add to calendar" button
     */
    public void setShowButtonIcon( boolean bShowButtonIcon )
    {
        _bShowButtonIcon = bShowButtonIcon;
    }

    /**
     * @return true if the links of the calendars show the icon of their service
     */
    public boolean isShowLinkIcons( )
    {
        return _bShowLinkIcons;
    }

    /**
     * @param bShowLinkIcons
     *            true to show the icon of the service on the links of the calendars
     */
    public void setShowLinkIcons( boolean bShowLinkIcons )
    {
        _bShowLinkIcons = bShowLinkIcons;
    }

    /**
     * @return the predefined style of the buttons; bootstrap uses the Bootstrap button of the color
     */
    public String getButtonStyle( )
    {
        return _strButtonStyle;
    }

    /**
     * @param strButtonStyle
     *            the predefined style of the buttons
     */
    public void setButtonStyle( String strButtonStyle )
    {
        _strButtonStyle = strButtonStyle;
    }

    /**
     * @return the event location, empty for the address of the form
     */
    public String getLocation( )
    {
        return _strLocation;
    }

    /**
     * @param strLocation
     *            the event location
     */
    public void setLocation( String strLocation )
    {
        _strLocation = strLocation;
    }

    /**
     * @return busy or free, empty for the setting of the calendar of the user
     */
    public String getAvailability( )
    {
        return _strAvailability;
    }

    /**
     * @param strAvailability
     *            busy or free
     */
    public void setAvailability( String strAvailability )
    {
        _strAvailability = strAvailability;
    }
}
