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

ALTER TABLE appointment_form DROP COLUMN id_slot;
