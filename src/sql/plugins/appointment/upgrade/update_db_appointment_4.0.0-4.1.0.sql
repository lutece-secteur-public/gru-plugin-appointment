-- liquibase formatted sql
-- Added after the release of plugin-appointment 4.0.0: they were first written in update_db_appointment_3.0.13-4.0.0.sql,
-- which a site already on 4.0.x never plays again. The preconditions read the schema, so a site that played them under their
-- former ids (3.0.13-4.0.0 file of a development version) marks them as ran.

-- changeset appointment:update_db_appointment_4.0.0-4.1.0-reservation-rule-enable.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=database() AND table_name='appointment_reservation_rule' AND column_name='is_actif'
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=database() AND table_name='appointment_reservation_rule' AND column_name='enable'
-- The 2.1.0 to 3.0.0 upgrade added the column is_actif to appointment_reservation_rule, while the create script and the plugin use enable:
-- on a database upgraded from 2.1.0 the typical weeks could not be read. The column keeps its values under the right name.
ALTER TABLE appointment_reservation_rule RENAME COLUMN is_actif TO enable;

-- changeset appointment:update_db_appointment_4.0.0-4.1.0-comment-notification-log.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=database() AND table_name='appointment_comment_notification_log'
-- The notifications of the comments, shown in the notification zone of the admin header.
CREATE TABLE IF NOT EXISTS appointment_comment_notification_log (
	id_notification INT AUTO_INCREMENT,
	notify_type VARCHAR(45) NOT NULL,
	id_form INT DEFAULT 0 NOT NULL,
	id_comment INT DEFAULT 0 NOT NULL,
	comment LONG VARCHAR,
	comment_user_creator VARCHAR(255),
	starting_validity_date DATE,
	ending_validity_date DATE,
	date_notification TIMESTAMP NOT NULL,
	PRIMARY KEY (id_notification),
	CONSTRAINT fk_appointment_comment_notification_log FOREIGN KEY (id_form) REFERENCES appointment_form (id_form)
);
CREATE INDEX idx_appointment_comment_notification_log_date ON appointment_comment_notification_log (date_notification);

-- changeset appointment:update_db_appointment_4.0.0-4.1.0-comment-notification-read.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=database() AND table_name='appointment_comment_notification_read'
-- The comment notifications each admin user has read: they are no longer shown to him.
CREATE TABLE IF NOT EXISTS appointment_comment_notification_read (
	id_notification INT NOT NULL,
	id_user INT NOT NULL,
	PRIMARY KEY (id_notification, id_user),
	CONSTRAINT fk_appointment_comment_notification_read FOREIGN KEY (id_notification) REFERENCES appointment_comment_notification_log (id_notification)
);
