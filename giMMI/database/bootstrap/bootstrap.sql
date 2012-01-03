SET character_set_server=utf8;
SET collation_server=utf8_bin;

-- -------------------------------------
-- COUNTRY CODES
-- -------------------------------------
DELETE FROM gimmi.country;

LOAD DATA LOCAL INFILE 'countries.csv' REPLACE
INTO TABLE gimmi.country
CHARACTER SET utf8
FIELDS TERMINATED BY';'
LINES TERMINATED BY '\n'
IGNORE 5 LINES
(country_id, country_code, tld, name_en, name_de);

-- -------------------------------------
-- LANGUAGE CODES
-- -------------------------------------
DELETE FROM gimmi.language;

LOAD DATA LOCAL INFILE 'languages.csv' REPLACE
INTO TABLE gimmi.language
CHARACTER SET utf8
FIELDS TERMINATED BY';'
LINES TERMINATED BY '\n'
IGNORE 5 LINES
(language_id, lang_code, name_en, name_de);