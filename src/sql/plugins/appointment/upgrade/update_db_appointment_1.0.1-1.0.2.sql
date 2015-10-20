ALTER TABLE appointment_form ADD COLUMN max_appointment_mail INT NOT NULL;
UPDATE appointment_form SET max_appointment_mail = 0;
ALTER TABLE appointment_form ADD COLUMN nb_appointment_week INT NOT NULL;
UPDATE appointment_form SET nb_appointment_week = 0;
ALTER TABLE appointment_form ADD COLUMN reference VARCHAR ( 10 );
ALTER TABLE appointment_form ADD COLUMN is_form_step SMALLINT NOT NULL;
UPDATE appointment_form SET is_form_step = 0;

ALTER TABLE appointment_form ADD is_confirmEmail_enabled SMALLINT NOT NULL;
ALTER TABLE appointment_form ADD is_mandatoryEmail_enabled SMALLINT NOT NULL;
ALTER TABLE appointment_appointment MODIFY email varchar(255) default '';
ALTER TABLE appointment_form ADD icon_form_content long varbinary NULL;
ALTER TABLE appointment_form ADD icon_form_mime_type varchar(255) default NULL;

ALTER TABLE appointment_appointment ADD has_notify INT DEFAULT 0 ;

DROP TABLE IF EXISTS appointment_holidays;
CREATE TABLE appointment_holidays
(
	id_form int NOT NULL,
	date_day DATE NOT NULL,
	PRIMARY KEY (id_form,date_day)
)

ALTER TABLE appointment_form_messages ADD field_confirmationEmail_title varchar(255) NOT NULL default '';
ALTER TABLE appointment_form_messages ADD field_confirmationEmail_help varchar(255) NOT NULL default '';

	



