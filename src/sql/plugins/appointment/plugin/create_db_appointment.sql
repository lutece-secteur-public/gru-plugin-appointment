DROP TABLE IF EXISTS workflow_task_manual_app_notify;
DROP TABLE IF EXISTS workflow_task_update_admin_appointment;
DROP TABLE IF EXISTS appointment_appointment_response;
DROP TABLE IF EXISTS appointment_appointment;
DROP TABLE IF EXISTS appointment_slot;
DROP TABLE IF EXISTS appointment_day;
DROP TABLE IF EXISTS appointment_form_messages;
DROP TABLE IF EXISTS appointment_form;
DROP TABLE IF EXISTS appointment_calendar_template;
DROP TABLE IF EXISTS appointment_holidays;

CREATE TABLE appointment_form (
	id_form int NOT NULL,
	title varchar(255) NOT NULL default '',
	description long varchar NOT NULL,
	time_start varchar(10) NOT NULL default '0',
	time_end varchar(10) NOT NULL default '0',
	duration_appointments INT NOT NULL default '0',
	is_open_monday SMALLINT NOT NULL,
	is_open_tuesday SMALLINT NOT NULL,
	is_open_wednesday SMALLINT NOT NULL,
	is_open_thursday SMALLINT NOT NULL,
	is_open_friday SMALLINT NOT NULL,
	is_open_saturday SMALLINT NOT NULL,
	is_open_sunday SMALLINT NOT NULL,
	date_start_validity date NULL,
	date_end_validity date NULL,
	is_active SMALLINT NOT NULL,
	dispolay_title_fo SMALLINT NOT NULL,
	nb_weeks_to_display INT NOT NULL default '0',
	people_per_appointment INT NOT NULL default '0',
	id_workflow INT NOT NULL default '0',
	is_captcha_enabled SMALLINT NOT NULL,
	users_can_cancel_appointments SMALLINT NOT NULL,
	min_days_before_app SMALLINT NOT NULL,
	id_calendar_template INT NOT NULL,
	max_appointment_mail INT NOT NULL default '0',
	nb_appointment_week  INT NOT NULL default '0',
	reference varchar(10),
	is_form_step SMALLINT NOT NULL default '0',
	is_confirmEmail_enabled SMALLINT NOT NULL,
	is_mandatoryEmail_enabled SMALLINT NOT NULL,
	icon_form_content long varbinary NULL,
	icon_form_mime_type varchar(255) default NULL,
	PRIMARY KEY (id_form)
);

CREATE TABLE appointment_day (
	id_day INT NOT NULL,
	id_form INT NOT NULL,
	is_open SMALLINT NOT NULL,
	date_day DATE NOT NULL,
	opening_hour INT NOT NULL,
	opening_minute INT NOT NULL,
	closing_hour INT NOT NULL,
	closing_minute INT NOT NULL,
	appointment_duration INT NOT NULL,
	people_per_appointment INT NOT NULL,
	free_places INT NOT NULL,
	PRIMARY KEY (id_day)
);
CREATE INDEX idx_appointment_day_id_form ON appointment_day (id_form);

CREATE TABLE appointment_slot (
	id_slot INT NOT NULL,
	id_form INT NOT NULL,
	id_day INT NOT NULL,
	day_of_week INT NOT NULL,
	nb_places INT NOT NULL,
	starting_hour INT NOT NULL,
	starting_minute INT NOT NULL,
	ending_hour INT NOT NULL,
	ending_minute INT NOT NULL,
	is_enabled SMALLINT NOT NULL,
	PRIMARY KEY (id_slot)
);
CREATE INDEX idx_appointment_slot_id_form ON appointment_slot (id_form);
CREATE INDEX idx_appointment_slot_id_day ON appointment_slot (id_day);

--
-- Structure for table appointment_appointment
--
CREATE TABLE appointment_appointment (
	id_appointment int NOT NULL default '0',
	first_name varchar(255) NOT NULL default '',
	last_name varchar(255) NOT NULL default '',
	email varchar(255) default '',
	id_user varchar(255) NULL default '',
	authentication_service varchar(255) NULL default '',
	localization varchar(255) NULL default '',
	date_appointment DATE NOT NULL,
	id_slot int NOT NULL,
	status smallint NOT NULL,
	id_action_cancel int NOT NULL,
	id_admin_user int DEFAULT NULL,
	has_notify INT DEFAULT 0,
	PRIMARY KEY (id_appointment)
);
CREATE INDEX idx_appointment_id_slot ON appointment_appointment (id_slot);
CREATE INDEX idx_appointment_date_app ON appointment_appointment (date_appointment);

CREATE TABLE appointment_appointment_response (
	id_appointment INT NOT NULL,
	id_response INT NOT NULL,
	PRIMARY KEY (id_appointment,id_response)
);

CREATE TABLE appointment_form_messages (
	id_form INT NOT NULL,
	calendar_title varchar(255) NOT NULL default '',
	field_firstname_title varchar(255) NOT NULL default '',
	field_firstname_help varchar(255) NOT NULL default '',
	field_lastname_title varchar(255) NOT NULL default '',
	field_lastname_help varchar(255) NOT NULL default '',
	field_email_title varchar(255) NOT NULL default '',
	field_email_help varchar(255) NOT NULL default '',
	text_appointment_created long varchar NOT NULL,
	url_redirect_after_creation varchar(255) NOT NULL default '',
	text_appointment_canceled long varchar NOT NULL,
	label_button_redirection varchar(255) NOT NULL default '',
	no_available_slot varchar(255) NOT NULL default '',
	calendar_description long varchar NOT NULL,
	calendar_reserve_label varchar(255) NOT NULL default '',
	calendar_full_label varchar(255) NOT NULL default '',
	PRIMARY KEY (id_form)
);

CREATE TABLE appointment_calendar_template (
	id INT NOT NULL,
	title varchar(255) NOT NULL default '',
	description varchar(255) NOT NULL default '',
	template_path varchar(255) NOT NULL default '',
	PRIMARY KEY(id)
);

CREATE TABLE appointment_holidays
(
	id_form int NOT NULL,
	date_day DATE NOT NULL,
	PRIMARY KEY (id_form,date_day)
)

ALTER TABLE appointment_form ADD CONSTRAINT fk_app_form_template FOREIGN KEY (id_calendar_template)
      REFERENCES appointment_calendar_template (id) ON DELETE CASCADE ON UPDATE RESTRICT ;
ALTER TABLE appointment_day ADD CONSTRAINT fk_appointment_day_id_form FOREIGN KEY (id_form)
      REFERENCES appointment_form (id_form) ON DELETE CASCADE ON UPDATE RESTRICT ;
ALTER TABLE appointment_slot ADD CONSTRAINT fk_appointment_slot_id_form FOREIGN KEY (id_form)
      REFERENCES appointment_form (id_form) ON DELETE CASCADE ON UPDATE RESTRICT ;
ALTER TABLE appointment_appointment ADD CONSTRAINT fk_appointment_id_slot FOREIGN KEY (id_slot)
      REFERENCES appointment_slot (id_slot) ON DELETE CASCADE ON UPDATE RESTRICT ;
ALTER TABLE appointment_form_messages ADD CONSTRAINT fk_app_form_messages_id_form FOREIGN KEY (id_form)
      REFERENCES appointment_form (id_form) ON DELETE CASCADE ON UPDATE RESTRICT ;
ALTER TABLE appointment_appointment_response ADD CONSTRAINT fk_app_response_id_app FOREIGN KEY (id_appointment)
      REFERENCES appointment_appointment (id_appointment) ON DELETE CASCADE ON UPDATE RESTRICT ;
      