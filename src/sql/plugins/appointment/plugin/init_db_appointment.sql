-- liquibase formatted sql
-- changeset appointment:init_db_appointment.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (1,'Calendrier','Calendrier des créneaux disponibles et indisponibles','skin/plugins/appointment/calendar/appointment_form_calendar.html' );
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (2,'Calendrier jours ouverts','Calendrier des créneaux disponibles et indisponibles (jours ouverts)','skin/plugins/appointment/calendar/appointment_form_calendar_opendays.html' );
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (3,'Liste des creneaux disponibles','Liste des créneaux disponibles','skin/plugins/appointment/calendar/appointment_form_list_open_slots.html' );
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (4,'Liste des creneaux disponibles jours ouverts','Liste des créneaux disponibles (jours ouverts)','skin/plugins/appointment/calendar/appointment_form_list_open_slots_opendays.html' );
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (5,'Liste des creneaux disponible regroupés','Liste des creneaux disponible regroupés','skin/plugins/appointment/calendar/appointment_form_list_open_slots_grouped.html' );
INSERT INTO appointment_comment_notification_cf values ('CREATE', 'noreplay','Notification comment appointment',' ');
INSERT INTO appointment_comment_notification_cf values ('DELETE', 'noreplay','Notification comment appointment',' ');
INSERT INTO appointment_comment_notification_cf values ('UPDATE', 'noreplay','Notification comment appointment',' ');