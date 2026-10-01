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
