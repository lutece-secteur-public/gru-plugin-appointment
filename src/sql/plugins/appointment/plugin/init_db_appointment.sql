-- liquibase formatted sql
-- changeset appointment:init_db_appointment.sql
-- validCheckSum: 8:a381ed1bddca27fc5b94d56b594c1ca0
-- validCheckSum: 9:dcd01123d5849b571c0605dcc02b54f8
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (1,'Calendrier','Calendrier des créneaux disponibles et indisponibles','skin/plugins/appointment/calendar/appointment_form_calendar.html' );
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (2,'Calendrier jours ouverts','Calendrier des créneaux disponibles et indisponibles (jours ouverts)','skin/plugins/appointment/calendar/appointment_form_calendar_opendays.html' );
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (3,'Liste des créneaux disponibles','Liste des créneaux disponibles','skin/plugins/appointment/calendar/appointment_form_list_open_slots.html' );
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (4,'Liste des créneaux disponibles jours ouverts','Liste des créneaux disponibles (jours ouverts)','skin/plugins/appointment/calendar/appointment_form_list_open_slots_opendays.html' );
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (5,'Liste des créneaux disponibles regroupés','Liste des créneaux disponibles regroupés','skin/plugins/appointment/calendar/appointment_form_list_open_slots_grouped.html' );
INSERT INTO appointment_comment_notification_cf (notify_type, sender_name, subject, message) VALUES ('CREATE', 'noreply', 'Notification comment appointment', ' ');
INSERT INTO appointment_comment_notification_cf (notify_type, sender_name, subject, message) VALUES ('DELETE', 'noreply', 'Notification comment appointment', ' ');
INSERT INTO appointment_comment_notification_cf (notify_type, sender_name, subject, message) VALUES ('UPDATE', 'noreply', 'Notification comment appointment', ' ');