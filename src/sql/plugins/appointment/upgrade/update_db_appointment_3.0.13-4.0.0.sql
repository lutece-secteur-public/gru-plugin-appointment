-- liquibase formatted sql
-- changeset appointment:update_db_appointment_3.0.13-4.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- The coordinates of a form location keep the precision of the address service (FLOAT rounded them to about 5 m).
ALTER TABLE appointment_localization MODIFY longitude DOUBLE NULL;
ALTER TABLE appointment_localization MODIFY latitude DOUBLE NULL;
-- The default sender of the comment notifications was misspelt; a sender an administrator changed is kept.
UPDATE appointment_comment_notification_cf SET sender_name = 'noreply' WHERE sender_name = 'noreplay';
-- The group entry type named a Font Awesome icon the Tabler theme does not carry: the button showed no icon.
UPDATE genatt_entry_type SET icon_name = 'indent-increase' WHERE class_name = 'appointment.entryTypeGroup' AND icon_name = 'indent';
-- The shipped titles of the slot list templates missed their accents; a title an administrator changed is kept.
UPDATE appointment_calendar_template SET title = 'Liste des créneaux disponibles' WHERE id_calendar_template = 3 AND title = 'Liste des creneaux disponibles';
UPDATE appointment_calendar_template SET title = 'Liste des créneaux disponibles jours ouverts' WHERE id_calendar_template = 4 AND title = 'Liste des creneaux disponibles jours ouverts';
UPDATE appointment_calendar_template SET title = 'Liste des créneaux disponibles regroupés', description = 'Liste des créneaux disponibles regroupés' WHERE id_calendar_template = 5 AND title = 'Liste des creneaux disponible regroupés';
-- The comment management feature showed its name as its description.
UPDATE core_admin_right SET description = 'appointment.adminFeature.manageComment.description' WHERE id_right = 'APPOINTMENT_COMMENT_MANAGEMENT' AND description = 'appointment.adminFeature.manageComment.name';

-- changeset appointment:update_db_appointment_3.0.13-4.0.0-calendar-button.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=database() AND table_name='appointment_display' AND column_name='calendar_button_config'
-- Options of the "add to calendar" button of the confirmation page, as JSON; NULL keeps the defaults.
    ALTER TABLE appointment_display ADD COLUMN calendar_button_config LONG VARCHAR NULL;

-- changeset appointment:update_db_appointment_3.0.13-4.0.0-reservation-rule-enable.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=database() AND table_name='appointment_reservation_rule' AND column_name='is_actif'
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=database() AND table_name='appointment_reservation_rule' AND column_name='enable'
-- The 2.1.0 to 3.0.0 upgrade added the column is_actif to appointment_reservation_rule, while the create script and the plugin use enable:
-- on a database upgraded from 2.1.0 the typical weeks could not be read. The column keeps its values under the right name.
ALTER TABLE appointment_reservation_rule RENAME COLUMN is_actif TO enable;

-- changeset appointment:update_db_appointment_3.0.13-4.0.0-comment-notification-log.sql
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

-- changeset appointment:update_db_appointment_3.0.13-4.0.0-comment-notification-read.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=database() AND table_name='appointment_comment_notification_read'
-- The comment notifications each admin user has read: they are no longer shown to him.
CREATE TABLE IF NOT EXISTS appointment_comment_notification_read (
	id_notification INT NOT NULL,
	id_user INT NOT NULL,
	PRIMARY KEY (id_notification, id_user),
	CONSTRAINT fk_appointment_comment_notification_read FOREIGN KEY (id_notification) REFERENCES appointment_comment_notification_log (id_notification)
);
