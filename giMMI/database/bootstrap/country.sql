SET character_set_server=utf8;
SET collation_server=utf8_bin;

DELETE FROM gimmi.country;

LOAD DATA LOCAL INFILE 'ISO3166-1_alpha-2.csv' REPLACE
INTO TABLE gimmi.country
CHARACTER SET utf8
FIELDS TERMINATED BY';'
LINES TERMINATED BY '\n'
IGNORE 2 LINES
(country_code, name_en);