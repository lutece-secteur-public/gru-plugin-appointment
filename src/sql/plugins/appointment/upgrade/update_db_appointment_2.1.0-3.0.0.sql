ALTER TABLE appointment_form_rule  ADD bo_overbooking BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE appointment_appointment DROP COLUMN id_slot;
ALTER TABLE appointment_form ADD COLUMN is_multislot_appointment BOOLEAN NOT NULL DEFAULT FALSE;
-- -----------------------------------------------------
-- Table appointment_appointment_slot
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_appointment_slot (

	id_appointment INT NOT NULL, 
	id_slot INT NOT NULL,
	nb_places INT NOT NULL,
	PRIMARY KEY (id_appointment, id_slot ),
	CONSTRAINT fk_appointment_appointment_slot_appointment
    FOREIGN KEY (id_appointment)
    REFERENCES appointment_appointment (id_appointment),
    CONSTRAINT fk_appointment_appointment_slot_slot
    FOREIGN KEY (id_slot)
    REFERENCES appointment_slot (id_slot)
);
INSERT INTO appointment_calendar_template (id_calendar_template, title, description, template_path) VALUES (5,'Liste des creneaux disponible regroupés','Liste des creneaux disponible regroupés','skin/plugins/appointment/calendar/appointment_form_list_open_slots_grouped.html' );
--
-- New table appointment_comment since 3.0.0
--
CREATE TABLE appointment_comment ( 
id_comment int AUTO_INCREMENT, 
id_form int default '0' NOT NULL, 
starting_validity_date date NOT NULL, 
starting_validity_time TIME,
ending_validity_date date NOT NULL,
ending_validity_time TIME, 
comment long varchar NOT NULL, 
comment_creation_date date NOT NULL, 
comment_user_creator VARCHAR(255) NOT NULL, 
PRIMARY KEY (id_comment), 
CONSTRAINT fk_appointment_comment FOREIGN KEY (id_form) REFERENCES appointment_form (id_form) 
);

ALTER TABLE appointment_form ADD role_fo varchar(255);
SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE appointment_appointment MODIFY id_appointment INT NOT NULL;
ALTER TABLE appointment_appointment DROP PRIMARY KEY, ADD PRIMARY KEY (id_appointment);
ALTER TABLE appointment_appointment MODIFY id_appointment INT AUTO_INCREMENT;

ALTER TABLE appointment_slot MODIFY id_slot INT NOT NULL;
ALTER TABLE appointment_slot DROP PRIMARY KEY, ADD PRIMARY KEY (id_slot);
ALTER TABLE appointment_slot MODIFY id_slot INT AUTO_INCREMENT;

ALTER TABLE appointment_appointment_response MODIFY id_appointment_response INT NOT NULL;
ALTER TABLE appointment_appointment_response DROP PRIMARY KEY, ADD PRIMARY KEY (id_appointment_response);
ALTER TABLE appointment_appointment_response MODIFY id_appointment_response INT AUTO_INCREMENT;

ALTER TABLE appointment_form_message MODIFY id_form_message INT NOT NULL;
ALTER TABLE appointment_form_message DROP PRIMARY KEY, ADD PRIMARY KEY (id_form_message);
ALTER TABLE appointment_form_message MODIFY id_form_message INT AUTO_INCREMENT;

ALTER TABLE appointment_week_definition MODIFY id_week_definition INT NOT NULL;
ALTER TABLE appointment_week_definition DROP PRIMARY KEY, ADD PRIMARY KEY (id_week_definition);
ALTER TABLE appointment_week_definition MODIFY id_week_definition INT AUTO_INCREMENT;

ALTER TABLE appointment_working_day MODIFY id_working_day INT NOT NULL;
ALTER TABLE appointment_working_day DROP PRIMARY KEY, ADD PRIMARY KEY (id_working_day);
ALTER TABLE appointment_working_day MODIFY id_working_day INT AUTO_INCREMENT;

ALTER TABLE appointment_time_slot MODIFY id_time_slot INT NOT NULL;
ALTER TABLE appointment_time_slot DROP PRIMARY KEY, ADD PRIMARY KEY (id_time_slot);
ALTER TABLE appointment_time_slot MODIFY id_time_slot INT AUTO_INCREMENT;

ALTER TABLE appointment_localization MODIFY id_localization INT NOT NULL;
ALTER TABLE appointment_localization DROP PRIMARY KEY, ADD PRIMARY KEY (id_localization);
ALTER TABLE appointment_localization MODIFY id_localization INT AUTO_INCREMENT;

ALTER TABLE appointment_display MODIFY id_display INT NOT NULL;
ALTER TABLE appointment_display DROP PRIMARY KEY, ADD PRIMARY KEY (id_display);
ALTER TABLE appointment_display MODIFY id_display INT AUTO_INCREMENT;

ALTER TABLE appointment_form_rule MODIFY id_form_rule INT NOT NULL;
ALTER TABLE appointment_form_rule DROP PRIMARY KEY, ADD PRIMARY KEY (id_form_rule);
ALTER TABLE appointment_form_rule MODIFY id_form_rule INT AUTO_INCREMENT;

ALTER TABLE appointment_closing_day MODIFY id_closing_day INT NOT NULL;
ALTER TABLE appointment_closing_day DROP PRIMARY KEY, ADD PRIMARY KEY (id_closing_day);
ALTER TABLE appointment_closing_day MODIFY id_closing_day INT AUTO_INCREMENT;

ALTER TABLE appointment_reservation_rule MODIFY id_reservation_rule INT NOT NULL;
ALTER TABLE appointment_reservation_rule DROP PRIMARY KEY, ADD PRIMARY KEY (id_reservation_rule);
ALTER TABLE appointment_reservation_rule MODIFY id_reservation_rule INT AUTO_INCREMENT;
--------------------------------------------------
-- week type
---------------------------------------------------

ALTER  TABLE appointment_week_definition  ADD ending_date_of_apply DATE NOT NULL;
ALTER  TABLE appointment_week_definition  ADD id_reservation_rule INT NOT NULL;
ALTER  TABLE appointment_week_definition ADD CONSTRAINT CONSTRAINT fk_appointment_week_definition_appointment_form
    FOREIGN KEY (id_form)
    REFERENCES appointment_form (id_form);
ALTER  TABLE appointment_week_definition DROP CONSTRAINT fk_appointment_week_definition_appointment_form;
ALTER  TABLE appointment_week_definition DROP INDEX fk_appointment_week_type_appointment_form_idx;
ALTER  TABLE appointment_week_definition DROP INDEX appointment_week_definition_unique_date;
ALTER  TABLE appointment_week_definition DROP COLUMN id_form;
CREATE UNIQUE INDEX appointment_week_definition_unique_date ON appointment_week_definition (id_reservation_rule,date_of_apply);


ALTER  TABLE appointment_reservation_rule  ADD name VARCHAR(255) NOT NULL;
ALTER  TABLE appointment_reservation_rule  ADD description VARCHAR(255) NOT NULL;
ALTER  TABLE appointment_reservation_rule  ADD color VARCHAR(255) NOT NULL;
ALTER  TABLE appointment_reservation_rule  ADD is_actif BOOLEAN DEFAULT TRUE NOT NULL; 
ALTER  TABLE appointment_reservation_rule  DROP COLUMN date_of_apply;

ALTER  TABLE appointment_working_day DROP CONSTRAINT fk_appointment_working_day_appointment_week_definition;
ALTER  TABLE appointment_working_day DROP INDEX fk_appointment_working_day_appointment_week_definition_idx;
ALTER  TABLE appointment_working_day DROP INDEX appointment_working_day_unique;
ALTER  TABLE appointment_working_day DROP COLUMN id_reservation_rule;
ALTER  TABLE appointment_working_day ADD COLUMN id_reservation_rule INT NOT NULL;
ALTER  TABLE appointment_working_day ADD CONSTRAINT fk_appointment_working_day_appointment_reservation_rule FOREIGN KEY (id_reservation_rule) REFERENCES appointment_reservation_rule (id_reservation_rule);
CREATE INDEX fk_appointment_working_day_appointment_reservation_rule_idx ON appointment_reservation_rule (id_reservation_rule ASC);
CREATE UNIQUE INDEX appointment_working_day_unique ON appointment_working_day (id_reservation_rule,day_of_week);


DROP INDEX IF EXISTS appointment_user_unique_email ON appointment_user ;

SET FOREIGN_KEY_CHECKS = 1;
