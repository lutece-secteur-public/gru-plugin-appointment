-- liquibase formatted sql
-- Added after the release of plugin-appointment 4.0.0: they were first written in update_db_core_3.0.13-4.0.0.sql,
-- which a site already on 4.0.x never plays again. A site that played them under their former ids marks them as ran.

-- changeset appointment:update_db_core_4.0.0-4.1.0-right-level.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM DATABASECHANGELOG WHERE id = 'update_db_core_3.0.13-4.0.0.sql-rev6.sql'
-- The appointment form and category rights get the level of the plugin descriptor (LUT-34563). No right is granted.
UPDATE core_admin_right SET level_right = 2 WHERE id_right IN ( 'APPOINTMENT_FORM_MANAGEMENT', 'APPOINTMENT_CATEGORY_MANAGEMENT' );

-- changeset appointment:update_db_core_4.0.0-4.1.0-comment-notification-dashboard.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_dashboard WHERE dashboard_name = 'APPOINTMENT_COMMENT_NOTIFICATION'
-- comment The comment notifications in the notification zone of the admin header (zone 5 with lutece.dashboard.zones.count=4)
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('APPOINTMENT_COMMENT_NOTIFICATION', 5, 2);
