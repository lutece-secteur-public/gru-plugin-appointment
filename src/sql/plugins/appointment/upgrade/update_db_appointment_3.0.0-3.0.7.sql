ALTER TABLE appointment_appointment DROP FOREIGN KEY fk_appointment_appointment_appointment_slot;
ALTER  TABLE appointment_form  ADD nb_consecutive_slots INT DEFAULT 0 NOT NULL ;
ALTER TABLE appointment_form ADD COLUMN is_anonymizable BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE appointment_form ADD COLUMN anonymization_pattern VARCHAR(3) DEFAULT NULL;