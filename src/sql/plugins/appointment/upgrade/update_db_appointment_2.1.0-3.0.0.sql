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
-- New table appointment_comment since 2.2.0
--
CREATE TABLE appointment_comment ( 
id_comment int AUTO_INCREMENT, 
id_form int default '0' NOT NULL, 
starting_validity_date date NOT NULL, 
ending_validity_date date NOT NULL, 
comment long varchar NOT NULL, 
comment_creation_date date NOT NULL, 
comment_user_creator VARCHAR(255) NOT NULL, 
PRIMARY KEY (id_comment), 
CONSTRAINT fk_appointment_comment FOREIGN KEY (id_form) REFERENCES appointment_form (id_form) 
)
ENGINE = InnoDB;
ALTER TABLE appointment_form ADD role_fo varchar(255);
