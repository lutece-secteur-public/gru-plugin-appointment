ALTER TABLE appointment_day ADD COLUMN free_places INT NOT NULL;

ALTER TABLE appointment_form_messages ADD COLUMN no_available_slot varchar(255) NOT NULL default '';
UPDATE appointment_form_messages SET no_available_slot = 'Aucun créneau disponible n''a été trouvé. Veuillez réiterer votre recherche ultérieurement.';

ALTER TABLE appointment_form_messages ADD COLUMN calendar_description long varchar NOT NULL;
ALTER TABLE appointment_form_messages ADD COLUMN calendar_reserve_label varchar(255) NOT NULL default '';
UPDATE appointment_form_messages SET calendar_reserve_label = 'Réserver';
ALTER TABLE appointment_form_messages ADD COLUMN calendar_full_label varchar(255) NOT NULL default '';
UPDATE appointment_form_messages SET calendar_full_label = 'Complet';
ALTER TABLE appointment_day ADD CONSTRAINT fk_appointment_day_id_form FOREIGN KEY (id_form)
      REFERENCES appointment_form (id_form) ON DELETE RESTRICT ON UPDATE RESTRICT ;
ALTER TABLE appointment_slot ADD CONSTRAINT fk_appointment_slot_id_form FOREIGN KEY (id_form)
      REFERENCES appointment_form (id_form) ON DELETE RESTRICT ON UPDATE RESTRICT ;
ALTER TABLE appointment_appointment ADD CONSTRAINT fk_appointment_id_slot FOREIGN KEY (id_slot)
      REFERENCES appointment_slot (id_slot) ON DELETE RESTRICT ON UPDATE RESTRICT ;
ALTER TABLE appointment_appointment_response ADD CONSTRAINT fk_app_response_id_app FOREIGN KEY (id_appointment)
      REFERENCES appointment_appointment (id_appointment) ON DELETE RESTRICT ON UPDATE RESTRICT ;
ALTER TABLE appointment_form_messages ADD CONSTRAINT fk_app_form_messages_id_form FOREIGN KEY (id_form)
      REFERENCES appointment_form (id_form) ON DELETE RESTRICT ON UPDATE RESTRICT ;
ALTER TABLE appointment_appointment_response ADD CONSTRAINT fk_app_response_id_resp FOREIGN KEY (id_response)
      REFERENCES genatt_response (id_response) ON DELETE RESTRICT ON UPDATE RESTRICT ;

DROP TABLE IF EXISTS appointment_calendar_template;
CREATE TABLE appointment_calendar_template (
	id INT NOT NULL,
	title varchar(255) NOT NULL default '',
	description varchar(255) NOT NULL default '',
	template_path varchar(255) NOT NULL default '',
	PRIMARY KEY(id)
);

INSERT INTO appointment_calendar_template (id, title, description, template_path) VALUES (1,'Calendrier','Calendrier des créneaux disponibles et indisponibles','skin/plugins/appointment/calendar/appointment_form_calendar.html' );
INSERT INTO appointment_calendar_template (id, title, description, template_path) VALUES (2,'Liste des créneaux disponibles','Liste des créneaux disponibles','skin/plugins/appointment/calendar/appointment_form_list_open_slots.html' );

ALTER TABLE appointment_form ADD COLUMN id_calendar_template INT NOT NULL;
UPDATE appointment_form SET id_calendar_template = 1;
ALTER TABLE appointment_form ADD CONSTRAINT fk_app_form_template FOREIGN KEY (id_calendar_template)
      REFERENCES appointment_calendar_template (id) ON DELETE RESTRICT ON UPDATE RESTRICT ;
