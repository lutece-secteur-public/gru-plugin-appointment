ALTER TABLE appointment_form_rule  ADD bo_overbooking BOOLEAN NOT NULL DEFAULT FALSE;
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (5,'Liste des creneaux disponible regroupés','Liste des creneaux disponible regroupés','skin/plugins/appointment/calendar/appointment_form_list_open_slots_grouped.html' );

ALTER TABLE appointment_form ADD role_fo varchar(255);
