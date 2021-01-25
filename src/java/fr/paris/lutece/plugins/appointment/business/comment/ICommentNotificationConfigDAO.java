package fr.paris.lutece.plugins.appointment.business.comment;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface ICommentNotificationConfigDAO 
{

    /**
     * update CommentNotificationConfig in the table.
     *
     * @param config
     *            instance of the CommentNotificationConfig object to update
     * @param plugin
     *            the Plugin          
     */
    void store( CommentNotificationConfig config, Plugin plugin );

    /**
     * Load the CommentNotificationConfig Object
     * 
     * @param plugin
     *            the Plugin
     * @return the CommentNotificationConfig Object
     */
    CommentNotificationConfig load( Plugin plugin );

}
