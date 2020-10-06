ALTER TABLE appointment_form_rule  ADD bo_overbooking BOOLEAN NOT NULL DEFAULT FALSE;
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (5,'Liste des creneaux disponible regroupés','Liste des creneaux disponible regroupés','skin/plugins/appointment/calendar/appointment_form_list_open_slots_grouped.html' );

--
-- New table appointment_comment since 2.2.0
--
CREATE TABLE appointment_comment ( id_comment int AUTO_INCREMENT, id_form int default '0' NOT NULL, starting_validity_date date NOT NULL, ending_validity_date date NOT NULL, comment long varchar NOT NULL, comment_creation_date date NOT NULL, comment_user_creator VARCHAR(255) NOT NULL, PRIMARY KEY (id_comment), CONSTRAINT fk_appointment_comment FOREIGN KEY (id_form) REFERENCES appointment_form (id_form) );
