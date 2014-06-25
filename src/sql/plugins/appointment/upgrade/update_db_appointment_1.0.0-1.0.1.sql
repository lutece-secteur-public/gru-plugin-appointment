ALTER TABLE appointment_day ADD COLUMN free_places INT NOT NULL;

ALTER TABLE appointment_form_messages ADD COLUMN no_available_slot varchar(255) NOT NULL default '';
UPDATE appointment_form_messages SET no_available_slot = 'Aucun créneau disponible n''a été trouvé. Veuillez réiterer votre recherche ultérieurement.';
