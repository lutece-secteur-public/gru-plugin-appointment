ALTER TABLE appointment_appointment DROP FOREIGN KEY fk_appointment_appointment_appointment_slot;
ALTER  TABLE appointment_form  ADD nb_consecutive_slots INT DEFAULT 0 NOT NULL ;