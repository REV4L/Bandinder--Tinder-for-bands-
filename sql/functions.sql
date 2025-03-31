CREATE EXTENSION IF NOT EXISTS pgcrypto;


-- -- tries to find a matching user, returns found id or returns -1 if not found

CREATE OR REPLACE FUNCTION login(IN _email VARCHAR, IN _pasw VARCHAR) RETURNS INTEGER AS $$
	DECLARE
    	userid INTEGER;
    	st INTEGER;
	BEGIN

	    userid := -1;
        SELECT INTO st COUNT(*) FROM organizatorji o WHERE o.email = _email AND o.pasw = encode(sha256(_pasw::bytea), 'hex');

	    IF st > 0 THEN
            SELECT INTO userid id FROM organizatorji o WHERE o.email = _email AND o.pasw = encode(sha256(_pasw::bytea), 'hex') LIMIT 1;

        END IF;

        RETURN userId;
    END;
$$ LANGUAGE 'plpgsql';

-- gets user name from id
CREATE OR REPLACE FUNCTION getName(IN userid INTEGER) RETURNS VARCHAR AS $$
	DECLARE
    	userid INTEGER;
	    st INTEGER;
    	n VARCHAR;
	BEGIN

	    userid := -1;
        SELECT INTO st COUNT(*) FROM organizatorji o WHERE o.id = userid;

	    IF st > 0 THEN
            SELECT INTO n o.ime FROM organizatorji o WHERE o.id = userid;

        END IF;

        RETURN n;
    END;
$$ LANGUAGE 'plpgsql';

DROP FUNCTION getOrganizers(userid INTEGER);
-- retrieves data about user from id
CREATE OR REPLACE FUNCTION getOrganizers(IN userid INTEGER)
RETURNS TABLE(id INT, ime VARCHAR, opis TEXT, email VARCHAR, pasw TEXT, telefon VARCHAR, naslov VARCHAR, kraj_id INTEGER, settings_id INTEGER, st_dogodkov INTEGER) AS $$
BEGIN
    RETURN QUERY
    SELECT o.id, o.ime, o.opis, o.email, o.pasw, o.telefon, o.naslov, o.kraj_id, o.settings_id, o.st_dogodkov
    FROM organizatorji o
    WHERE o.id = userid;
END;
$$ LANGUAGE plpgsql;

-- CREATE OR REPLACE FUNCTION getOrganizerById(IN userid INTEGER)
-- RETURNS organizatorji%ROWTYPE AS $$
-- DECLARE
--     result organizatorji%ROWTYPE;
-- BEGIN
--     SELECT o.*
--     INTO result
--     FROM organizatorji o
--     WHERE o.id = userid;
--
--     IF result.id IS NULL THEN
--         RAISE EXCEPTION 'Organizer not found for ID: %', userid;
--     END IF;
--
--     RETURN result;
-- END;
-- $$ LANGUAGE plpgsql;


-- retrieves font name
CREATE OR REPLACE FUNCTION getFont(IN userid INTEGER) RETURNS VARCHAR AS $$
	DECLARE
    	userid INTEGER;
	    st INTEGER;
    	f VARCHAR;
	BEGIN

	    userid := -1;
        SELECT INTO st COUNT(*) FROM organizatorji o WHERE o.id = userid;

	    IF st > 0 THEN
            SELECT INTO f s.font FROM settings s INNER JOIN public.organizatorji o2 on s.id = o2.settings_id;

        END IF;

        RETURN f;
    END;
$$ LANGUAGE 'plpgsql';

-- retrieves all kraji data (places)
CREATE OR REPLACE FUNCTION getKraji()
RETURNS TABLE(id INT, ime VARCHAR, postna VARCHAR, vel_uporabnik varchar) AS $$
BEGIN
    RETURN QUERY
    SELECT k.id, k.ime, k.postna, k.vel_uporabnik
    FROM kraji k
    order by k.postna;
END;
$$ LANGUAGE plpgsql;

-- retrieves kraj data (place) with a specified id
CREATE OR REPLACE FUNCTION getKraj(IN kid INTEGER)
RETURNS TABLE(id INT, ime VARCHAR, postna VARCHAR, vel_uporabnik varchar) AS $$
BEGIN
    RETURN QUERY
    SELECT k.id, k.ime, k.postna, k.vel_uporabnik
    FROM kraji k
    WHERE k.id = kid;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION get_izvajalec(p_id INT);
-- Function to get a single izvajalec by id
CREATE OR REPLACE FUNCTION get_izvajalec(p_id INT)
RETURNS TABLE(id INT, ime VARCHAR, opis TEXT, telefon VARCHAR, st_dogodkov INT) AS $$
BEGIN
    RETURN QUERY
    SELECT i.id, i.ime, i.opis, i.telefon, i.st_dogodkov
    FROM izvajalci i
    WHERE i.id = p_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION del_izvajalec(p_id INT)
RETURNS VOID AS $$
BEGIN

    DELETE FROM izvajalci
    WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;

-- gets all prostori
CREATE OR REPLACE FUNCTION getProstori()
RETURNS TABLE (
    id INT,
    ime VARCHAR(100),
    opis TEXT,
    kapaciteta INT,
    naslov VARCHAR(20),
    kraj_id INT,
    st_dogodkov INT
) AS
$$
BEGIN
    RETURN QUERY
    SELECT
        p.id,
        p.ime,
        p.opis,
        p.kapaciteta,
        p.naslov,
        p.kraj_id,
        p.st_dogodkov
    FROM prostori p;
END;
$$ LANGUAGE plpgsql;

-- gets prostor with specified id
CREATE OR REPLACE FUNCTION getProstor(p_id INT)
RETURNS TABLE (
    id INT,
    ime VARCHAR(100),
    opis TEXT,
    kapaciteta INT,
    naslov VARCHAR(20),
    kraj_id INT,
    st_dogodkov INT
) AS
$$
BEGIN
    RETURN QUERY
    SELECT
        p.id,
        p.ime,
        p.opis,
        p.kapaciteta,
        p.naslov,
        p.kraj_id,
        p.st_dogodkov
    FROM prostori p WHERE p.id = p_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insertProstor(
    p_ime VARCHAR(100),
    p_opis TEXT,
    p_kapaciteta INT,
    p_naslov VARCHAR(20),
    p_kraj_id INT
)
RETURNS INT AS
$$
DECLARE
    new_id INT;
BEGIN
    -- Insert new prostor into the table
    INSERT INTO prostori (ime, opis, kapaciteta, naslov, kraj_id, st_dogodkov)
    VALUES (p_ime, p_opis, p_kapaciteta, p_naslov, p_kraj_id, 0)
    RETURNING id INTO new_id;

    -- Return the generated id
    RETURN new_id;
END;
$$ LANGUAGE plpgsql;

-- DROP FUNCTION updateprostor();
CREATE OR REPLACE FUNCTION updateProstor(
    p_id INT,
    p_ime VARCHAR(100),
    p_opis TEXT,
    p_kapaciteta INT,
    p_naslov VARCHAR(20),
    p_kraj_id INT
)
RETURNS VOID AS
$$
BEGIN
    -- Update the existing prostor in the table
    UPDATE prostori
    SET
        ime = p_ime,
        opis = p_opis,
        kapaciteta = p_kapaciteta,
        naslov = p_naslov,
        kraj_id = p_kraj_id
    WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;



-- delete prostor with specified id
CREATE OR REPLACE FUNCTION delProstor(p_id INT)
RETURNS VOID AS
$$
BEGIN
    DELETE FROM prostori WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;



-- DROP FUNCTION get_all_izvajalci();
-- -- Function to get all izvajalci
-- CREATE OR REPLACE FUNCTION get_all_izvajalci()
-- RETURNS TABLE(id INT, ime VARCHAR, opis TEXT, telefon VARCHAR, st_dogodkov INT) AS $$
-- BEGIN
--     RETURN QUERY
--     SELECT i.id, i.ime, i.opis, i.telefon, i.st_dogodkov
--     FROM izvajalci i ORDER BY i.id;
-- END;
-- $$ LANGUAGE plpgsql;
-- Function to get all izvajalci
CREATE OR REPLACE FUNCTION getIzvajalci()
RETURNS TABLE(id INT, ime VARCHAR, opis TEXT, telefon VARCHAR, st_dogodkov INT) AS $$
BEGIN
    RETURN QUERY
    SELECT i.id, i.ime, i.opis, i.telefon, i.st_dogodkov
    FROM izvajalci i ORDER BY i.id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_izvajalec(
    p_ime TEXT,
    p_opis TEXT,
    p_telefon TEXT
)
RETURNS INT AS $$
DECLARE
    new_id INT;
BEGIN
    INSERT INTO izvajalci (ime, opis, telefon, st_dogodkov)
    VALUES (p_ime, p_opis, p_telefon, 0)  -- Insert blank values
    RETURNING id INTO new_id;  -- Get the inserted ID

    RETURN new_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_izvajalec(
    p_id INT,
    p_ime TEXT,
    p_opis TEXT,
    p_telefon TEXT
)
RETURNS VOID AS $$
BEGIN
    UPDATE izvajalci
    SET ime = p_ime,
        opis = p_opis,
        telefon = p_telefon
    WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION update_organizator(
    p_id INT,
    p_ime TEXT,
    p_opis TEXT,
    p_email TEXT,
    p_telefon TEXT,
    p_naslov TEXT,
    p_kraj_id INT
)
RETURNS VOID AS $$
BEGIN
    UPDATE organizatorji
    SET ime = p_ime,
        opis = p_opis,
        email = p_email,
        telefon = p_telefon,
        naslov = p_naslov,
        kraj_id = p_kraj_id
    WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;




-- trigger for tracking st_dogodkov (number of events)
CREATE OR REPLACE FUNCTION update_st_dogodkov()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        UPDATE organizatorji
        SET st_dogodkov = st_dogodkov + 1
        WHERE id = NEW.organizator_id;

        UPDATE izvajalci
        SET st_dogodkov = st_dogodkov + 1
        WHERE id = NEW.izvajalec_id;

        UPDATE prostori
        SET st_dogodkov = st_dogodkov + 1
        WHERE id = NEW.prostor_id;

    ELSIF (TG_OP = 'UPDATE') THEN
        UPDATE organizatorji
        SET st_dogodkov = st_dogodkov + 1
        WHERE id = NEW.organizator_id;

        UPDATE izvajalci
        SET st_dogodkov = st_dogodkov + 1
        WHERE id = NEW.izvajalec_id;

        UPDATE prostori
        SET st_dogodkov = st_dogodkov + 1
        WHERE id = NEW.prostor_id;

        UPDATE organizatorji
        SET st_dogodkov = st_dogodkov - 1
        WHERE id = OLD.organizator_id;

        UPDATE izvajalci
        SET st_dogodkov = st_dogodkov - 1
        WHERE id = OLD.izvajalec_id;

        UPDATE prostori
        SET st_dogodkov = st_dogodkov - 1
        WHERE id = OLD.prostor_id;

    ELSIF (TG_OP = 'DELETE') THEN
        UPDATE organizatorji
        SET st_dogodkov = st_dogodkov - 1
        WHERE id = OLD.organizator_id;

        UPDATE izvajalci
        SET st_dogodkov = st_dogodkov - 1
        WHERE id = OLD.izvajalec_id;

        UPDATE prostori
        SET st_dogodkov = st_dogodkov - 1
        WHERE id = OLD.prostor_id;
    END IF;

    -- RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION dogodek_del()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        UPDATE organizatorji
        SET st_dogodkov = st_dogodkov - 1
        WHERE id = OLD.organizator_id;

        UPDATE izvajalci
        SET st_dogodkov = st_dogodkov - 1
        WHERE id = OLD.izvajalec_id;

        UPDATE prostori
        SET st_dogodkov = st_dogodkov - 1
        WHERE id = OLD.prostor_id;

        INSERT INTO dogodek_log(cas, data, organizator_id) VALUES (NOW(), 'deleted dogodek ' || OLD.ime, OLD.organizator_id);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- Trigger for INSERT on dogodki
CREATE TRIGGER trg_insert_dogodek
AFTER INSERT ON dogodki
FOR EACH ROW
EXECUTE FUNCTION update_st_dogodkov();

-- Trigger for UPDATE on dogodki
CREATE TRIGGER trg_update_dogodek
AFTER UPDATE ON dogodki
FOR EACH ROW
EXECUTE FUNCTION update_st_dogodkov();

-- Trigger for DELETE on dogodki
CREATE OR REPLACE TRIGGER trg_delete_dogodek
AFTER DELETE ON dogodki
FOR EACH ROW
EXECUTE FUNCTION dogodek_del();

CREATE OR REPLACE FUNCTION create_user(
    _ime VARCHAR,
    _opis TEXT,
    _email VARCHAR,
    _pasw TEXT,
    _telefon VARCHAR,
    _naslov VARCHAR,
    _kraj_id INT

)
RETURNS INTEGER AS $$
DECLARE
    new_id INT;
    s_id INT;
BEGIN
    new_id := -1;
    s_id := 0;

    INSERT INTO settings(font, color) VALUES ('Trebuchet MS', '#FFFFFF') RETURNING id INTO s_id;
    INSERT INTO organizatorji (ime, opis, email, pasw, telefon, naslov, kraj_id, settings_id, st_dogodkov)
    VALUES (
            _ime, _opis, _email, _pasw, _telefon, _naslov, _kraj_id, s_id, 0
           ) RETURNING id INTO new_id;

    RETURN new_id;
END;
$$ LANGUAGE plpgsql;

-- CREATE OR REPLACE FUNCTION create_random_user() RETURNS INTEGER AS $$
--     DECLARE
--     new_id INT;
--     s_id INT;
-- BEGIN
--     new_id := -1;
--     s_id := 0;
--
--     INSERT INTO settings(font, color) VALUES ('Trebuchet MS', '#FFFFFF') RETURNING id INTO s_id;
--     INSERT INTO organizatorji (ime, opis, email, pasw, telefon, naslov, kraj_id, settings_id, st_dogodkov)
--     VALUES (
--             (LEFT(encode(sha256(round(RANDOM()*100)::bytea), 10))),
--             (encode(sha256(round(RANDOM()*100)::bytea))),
--             (concat_ws(encode(sha256(round(RANDOM()*100)), '@gmail.com'))),
--             (encode(sha256(round(RANDOM()*100)::bytea))),
--             '000000000',
--             'nekje',
--             (SELECT id FROM kraji ORDER BY RANDOM() LIMIT 1),
--             s_id, 0
--            ) RETURNING id INTO new_id;
--
--     RETURN new_id;
-- END;
-- $$ LANGUAGE plpgsql;

--
CREATE OR REPLACE FUNCTION insert_dogodek(
    p_organizator_id INT,
    p_prostor_id INT,
    p_izvajalec_id INT,
    p_cena_vstopnice REAL,
    p_cas TIMESTAMP,
    p_ime VARCHAR(1000),
    p_opis TEXT
) RETURNS INT AS $$
DECLARE
    new_id INT;
BEGIN
    INSERT INTO dogodek_log(cas, data, organizator_id) VALUES (NOW(), 'vstavil dogodek z imenom ' || p_ime,p_organizator_id);

    INSERT INTO dogodki(organizator_id, prostor_id, izvajalec_id, cena_vstopnice, cas, ime, opis)
    VALUES (p_organizator_id, p_prostor_id, p_izvajalec_id, p_cena_vstopnice, p_cas, p_ime, p_opis)
    RETURNING id INTO new_id;

    RETURN new_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_dogodek(
    p_id INT,
    p_organizator_id INT,
    p_prostor_id INT,
    p_izvajalec_id INT,
    p_cena_vstopnice REAL,
    p_cas TIMESTAMP,
    p_ime VARCHAR(1000),
    p_opis TEXT
) RETURNS VOID AS $$
BEGIN
    UPDATE dogodki
    SET organizator_id = p_organizator_id,
        prostor_id = p_prostor_id,
        izvajalec_id = p_izvajalec_id,
        cena_vstopnice = p_cena_vstopnice,
        cas = p_cas,
        ime = p_ime,
        opis = p_opis
    WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION delete_dogodek(p_id INT) RETURNS VOID AS $$
BEGIN
    DELETE FROM dogodki WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_dogodek(p_id INT)
RETURNS TABLE (
    id INT,
    organizator_id INT,
    prostor_id INT,
    izvajalec_id INT,
    cena_vstopnice REAL,
    cas TIMESTAMP,
    ime VARCHAR(1000),
    opis TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT d.id, d.organizator_id, d.prostor_id, d.izvajalec_id, d.cena_vstopnice, d.cas, d.ime, d.opis
    FROM dogodki d
    WHERE d.id = p_id;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION get_all_dogodki(p_organizator_id INT)
RETURNS TABLE (
    id INT,
    organizator_id INT,
    prostor_id INT,
    izvajalec_id INT,
    cena_vstopnice REAL,
    cas TIMESTAMP,
    ime VARCHAR(1000),
    opis TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT d.id, d.organizator_id, d.prostor_id, d.izvajalec_id, d.cena_vstopnice, d.cas, d.ime, d.opis
    FROM dogodki d
    WHERE d.organizator_id = p_organizator_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_organizator_settings(org_id INT)
RETURNS TABLE(font VARCHAR(100), color VARCHAR(100)) AS
$$
BEGIN
    RETURN QUERY
    SELECT s.font, s.color
    FROM settings s
    JOIN organizatorji o ON o.settings_id = s.id
    WHERE o.id = org_id;
END;
$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_organizator_settings(
    organizator_id INT,
    f VARCHAR,
    c VARCHAR
) RETURNS VOID AS $$
BEGIN
    -- Update the settings table with the new font and color
    UPDATE settings
    SET font = f,
        color = c
    WHERE id = (SELECT settings_id FROM organizatorji WHERE id = organizator_id);

    -- Optionally: You can return a message if you need confirmation, for now it just updates the values
END;
$$ LANGUAGE plpgsql;


