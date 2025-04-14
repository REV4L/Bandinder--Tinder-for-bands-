/*
Created: 3/25/2025
Modified: 4/13/2025
Model: PostgreSQL 10
Database: PostgreSQL 10
*/


-- Create tables section -------------------------------------------------

-- Table kraji

CREATE TABLE "kraji"(
 "id" Serial NOT NULL,
 "ime" Character varying(200) NOT NULL,
 "postna" Character varying(200) NOT NULL,
 "vel_uporabnik" Character varying(200)
)
WITH (
 autovacuum_enabled=true)
;

-- Add keys for table kraji

ALTER TABLE "kraji" ADD CONSTRAINT "PK_kraji" PRIMARY KEY ("id")
;

-- Table tags

CREATE TABLE "tags"(
 "id" Serial NOT NULL,
 "name" Character varying NOT NULL,
 "num" Integer DEFAULT 0 NOT NULL,
 "description" Text
)
WITH (
 autovacuum_enabled=true)
;

-- Add keys for table tags

ALTER TABLE "tags" ADD CONSTRAINT "PK_tags" PRIMARY KEY ("id")
;

-- Table bands

CREATE TABLE "bands"(
 "id" Serial NOT NULL,
 "name" Character varying(1000) NOT NULL,
 "bio" Text NOT NULL,
 "dt" Timestamp NOT NULL,
 "phone" Character varying(20) NOT NULL,
 "lastlogin" Timestamp,
 "email" Character varying(999) NOT NULL,
 "pasw" Character varying(999999) NOT NULL,
 "kraj_id" Integer
)
WITH (
 autovacuum_enabled=true)
;

-- Create indexes for table bands

CREATE INDEX "IX_Relationship22" ON "bands" ("kraj_id")
;

-- Add keys for table bands

ALTER TABLE "bands" ADD CONSTRAINT "PK_bands" PRIMARY KEY ("id")
;

-- Table suggestions

CREATE TABLE "suggestions"(
 "id" Serial NOT NULL,
 "dt" Timestamp NOT NULL,
 "accepted1" Integer DEFAULT 0 NOT NULL,
 "accepted2" Integer DEFAULT 0 NOT NULL,
 "band1_id" Integer,
 "band2_id" Integer
)
WITH (
 autovacuum_enabled=true)
;

-- Create indexes for table suggestions

CREATE INDEX "IX_Relationship27" ON "suggestions" ("band1_id")
;

CREATE INDEX "IX_Relationship28" ON "suggestions" ("band2_id")
;

-- Add keys for table suggestions

ALTER TABLE "suggestions" ADD CONSTRAINT "PK_suggestions" PRIMARY KEY ("id")
;

-- Table images

CREATE TABLE "images"(
 "id" Serial NOT NULL,
 "data" Bytea
)
WITH (
 autovacuum_enabled=true)
;

-- Add keys for table images

ALTER TABLE "images" ADD CONSTRAINT "PK_images" PRIMARY KEY ("id")
;

-- Table log

CREATE TABLE "log"(
 "id" Serial NOT NULL,
 "dt" Timestamp NOT NULL,
 "data" Text DEFAULT 0 NOT NULL
)
WITH (
 autovacuum_enabled=true)
;

-- Add keys for table log

ALTER TABLE "log" ADD CONSTRAINT "PK_log" PRIMARY KEY ("id")
;

-- Table bands_tags

CREATE TABLE "bands_tags"(
 "id" Serial NOT NULL,
 "tags_id" Integer,
 "band_id" Integer
)
WITH (
 autovacuum_enabled=true)
;

-- Create indexes for table bands_tags

CREATE INDEX "IX_Relationship23" ON "bands_tags" ("band_id")
;

CREATE INDEX "IX_Relationship24" ON "bands_tags" ("tags_id")
;

-- Add keys for table bands_tags

ALTER TABLE "bands_tags" ADD CONSTRAINT "PK_bands_tags" PRIMARY KEY ("id")
;

-- Table bands_images

CREATE TABLE "bands_images"(
 "id" Serial NOT NULL,
 "band_id" Integer,
 "image_id" Integer
)
WITH (
 autovacuum_enabled=true)
;

-- Create indexes for table bands_images

CREATE INDEX "IX_Relationship25" ON "bands_images" ("band_id")
;

CREATE INDEX "IX_Relationship26" ON "bands_images" ("image_id")
;

-- Add keys for table bands_images

ALTER TABLE "bands_images" ADD CONSTRAINT "PK_bands_images" PRIMARY KEY ("id")
;
-- Create foreign keys (relationships) section -------------------------------------------------

ALTER TABLE "bands" ADD CONSTRAINT "Relationship22" FOREIGN KEY ("kraj_id") REFERENCES "kraji" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE "bands_tags" ADD CONSTRAINT "Relationship23" FOREIGN KEY ("band_id") REFERENCES "bands" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE "bands_tags" ADD CONSTRAINT "Relationship24" FOREIGN KEY ("tags_id") REFERENCES "tags" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE "bands_images" ADD CONSTRAINT "Relationship25" FOREIGN KEY ("band_id") REFERENCES "bands" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE "bands_images" ADD CONSTRAINT "Relationship26" FOREIGN KEY ("image_id") REFERENCES "images" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE "suggestions" ADD CONSTRAINT "Relationship27" FOREIGN KEY ("band1_id") REFERENCES "bands" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE "suggestions" ADD CONSTRAINT "Relationship28" FOREIGN KEY ("band2_id") REFERENCES "bands" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE bands_images ADD COLUMN slot INTEGER DEFAULT 0;



