SET character_set_server=utf8;
SET collation_server=utf8_bin;

-- -------------------------------------
-- COUNTRY CODES
-- -------------------------------------
DELETE FROM gimmi.country;

LOAD DATA LOCAL INFILE '/home/bbz/workspace/giMMI database/bootstrap/ISO3166-1_alpha-2.csv' REPLACE
INTO TABLE gimmi.country
CHARACTER SET utf8
FIELDS TERMINATED BY';'
LINES TERMINATED BY '\n'
IGNORE 2 LINES
(country_code, name_en);

-- -------------------------------------
-- LANGUAGE CODES
-- -------------------------------------
DELETE FROM gimmi.language;

LOAD DATA LOCAL INFILE '/home/bbz/workspace/giMMI database/bootstrap/ISO639-alpha-3.csv' REPLACE
INTO TABLE gimmi.language
CHARACTER SET utf8
FIELDS TERMINATED BY'|'
LINES TERMINATED BY '\n'
IGNORE 2 LINES
(lang_code, @ignore, @ignore, name_en);