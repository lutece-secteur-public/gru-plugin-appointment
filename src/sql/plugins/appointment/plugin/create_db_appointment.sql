--
-- Structure for table appointment_appointment
--
DROP TABLE IF EXISTS appointment_appointment;
CREATE TABLE appointment_appointment (
	id_appointment int(11) NOT NULL default '0',
	first_name varchar(255) NOT NULL default '',
	last_name varchar(255) NOT NULL default '',
	email varchar(255) NOT NULL default '',
	id_user varchar(255) NOT NULL default '',
	id_slot int(11) NOT NULL,
	PRIMARY KEY (id_appointment)
);

--
-- Structure for table appointment_form
--
DROP TABLE IF EXISTS appointment_form;
CREATE TABLE appointment_form (
	id_form int(11) NOT NULL,
	title varchar(255) NOT NULL default '',
	description long varchar NOT NULL,
	time_start varchar(10) NOT NULL default '0',
	time_end varchar(10) NOT NULL default '0',
	duration_appointments INT(11) NOT NULL default '0',
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
	nb_weeks_to_display INT(11) NOT NULL default '0',
	people_per_appointment INT(11) NOT NULL default '0',
	id_workflow INT(11) NOT NULL default '0',
	is_captcha_enabled SMALLINT NOT NULL,
	PRIMARY KEY (id_form)
);


DROP TABLE IF EXISTS appointment_day;
CREATE TABLE appointment_day (
	id_day INT(11) NOT NULL,
	id_form INT(11) NOT NULL,
	is_open SMALLINT NOT NULL,
	date_day DATE NOT NULL,
	opening_hour INT(11) NOT NULL,
	opening_minute INT(11) NOT NULL,
	closing_hour INT(11) NOT NULL,
	closing_minute INT(11) NOT NULL,
	appointment_duration INT(11) NOT NULL,
	people_per_appointment INT(11) NOT NULL,
	PRIMARY KEY (id_day)
);

DROP TABLE IF EXISTS appointment_slot;
CREATE TABLE appointment_slot (
	id_slot INT(11) NOT NULL,
	id_form INT(11) NOT NULL,
	id_day INT(11) NOT NULL,
	day_of_week INT(11) NOT NULL,
	nb_free_places INT(11) NOT NULL,
	starting_hour INT(11) NOT NULL,
	starting_minute INT(11) NOT NULL,
	ending_hour INT(11) NOT NULL,
	ending_minute INT(11) NOT NULL,
	is_enabled SMALLINT NOT NULL,
	PRIMARY KEY (id_slot)
);