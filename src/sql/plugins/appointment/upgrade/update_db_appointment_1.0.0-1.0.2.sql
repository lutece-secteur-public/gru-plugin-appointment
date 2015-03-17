ALTER TABLE appointment_form ADD COLUMN max_appointment_mail INT NOT NULL;
UPDATE appointment_form SET max_appointment_mail = 0;
ALTER TABLE appointment_form ADD COLUMN nb_appointment_week INT NOT NULL;
UPDATE appointment_form SET nb_appointment_week = 0;