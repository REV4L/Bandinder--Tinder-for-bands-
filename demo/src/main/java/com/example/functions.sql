
-- get band
DROP FUNCTION IF EXISTS get_band_info(p_band_id INT);
CREATE OR REPLACE FUNCTION get_band_info(p_band_id INT)
RETURNS TABLE (
    id INT,
    name VARCHAR,
    bio TEXT,
    email VARCHAR,
    phone VARCHAR,
    dt TIMESTAMP,
    kraj_id INT
) AS $$
BEGIN
    RETURN QUERY
    SELECT b.id, b.name, b.bio, b.email, b.phone, b.dt, b.kraj_id
    FROM bands b
    WHERE b.id = p_band_id;
END;
$$ LANGUAGE plpgsql;


--
CREATE OR REPLACE FUNCTION get_band_images(p_band_id INT)
RETURNS TABLE (
    slot INT,
    image_id INT,
    data BYTEA
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        bi.slot,
        i.id,
        i.data
    FROM bands_images bi
    INNER JOIN images i ON i.id = bi.image_id
    WHERE bi.band_id = p_band_id
    ORDER BY bi.slot;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_best_band_match(p_band_id INT, min_shared_tags INT)
RETURNS TABLE (
    suggestion_id INT,
    band_id INT,
    band_name VARCHAR,
    shared_tags_count INT,
    last_suggested TIMESTAMP
) AS $$
DECLARE
    matched_id INT;
    existing_id INT;
BEGIN
    -- Find best match NOT yet accepted/rejected
    SELECT b.id
    INTO matched_id
    FROM bands b
    LEFT JOIN (
        SELECT bt2.band_id, COUNT(*) AS shared_tags
        FROM bands_tags bt1
        JOIN bands_tags bt2 ON bt1.tags_id = bt2.tags_id
        WHERE bt1.band_id = p_band_id AND bt2.band_id != p_band_id
        GROUP BY bt2.band_id
    ) shared ON shared.band_id = b.id
    LEFT JOIN suggestions s ON (
        (s.band1_id = p_band_id AND s.band2_id = b.id)
        OR (s.band2_id = p_band_id AND s.band1_id = b.id)
    )
    WHERE b.id != p_band_id
      AND (shared.shared_tags IS NULL OR shared.shared_tags >= min_shared_tags)
      AND (
    s.id IS NULL
    OR NOT (s.accepted1 = 2 AND s.accepted2 = 2)
)


    ORDER BY shared.shared_tags DESC NULLS LAST, s.dt NULLS FIRST
    LIMIT 1;

    IF matched_id IS NULL THEN
        RETURN;
    END IF;

    -- Try to find existing suggestion
    SELECT id INTO existing_id
    FROM suggestions
    WHERE (band1_id = p_band_id AND band2_id = matched_id)
       OR (band2_id = p_band_id AND band1_id = matched_id)
    LIMIT 1;

    IF existing_id IS NULL THEN
        INSERT INTO suggestions (dt, band1_id, band2_id)
        VALUES (NOW(), p_band_id, matched_id)
        RETURNING id INTO existing_id;
    END IF;

    RETURN QUERY
    SELECT existing_id, b.id, b.name, COALESCE(shared.shared_tags, 0)::INT, MAX(s.dt)
    FROM bands b
    LEFT JOIN (
        SELECT bt2.band_id, COUNT(*) AS shared_tags
        FROM bands_tags bt1
        JOIN bands_tags bt2 ON bt1.tags_id = bt2.tags_id
        WHERE bt1.band_id = p_band_id AND bt2.band_id != p_band_id
        GROUP BY bt2.band_id
    ) shared ON shared.band_id = b.id
    LEFT JOIN suggestions s ON (
        (s.band1_id = p_band_id AND s.band2_id = b.id)
        OR (s.band2_id = p_band_id AND s.band1_id = b.id)
    )
    WHERE b.id = matched_id
    GROUP BY b.id, b.name, shared.shared_tags;
END;
$$ LANGUAGE plpgsql;



























CREATE OR REPLACE FUNCTION get_best_band_match(p_band_id INT, min_shared_tags INT)
RETURNS TABLE (
    suggestion_id INT,
    band_id INT,
    band_name VARCHAR,
    shared_tags_count INT,
    last_suggested TIMESTAMP
) AS $$
DECLARE
    matched_id INT;
    existing_id INT;
BEGIN
    -- Find best match NOT yet accepted/rejected
    SELECT b.id
    INTO matched_id
    FROM bands b
    LEFT JOIN (
        SELECT bt2.band_id, COUNT(*) AS shared_tags
        FROM bands_tags bt1
        JOIN bands_tags bt2 ON bt1.tags_id = bt2.tags_id
        WHERE bt1.band_id = p_band_id AND bt2.band_id != p_band_id
        GROUP BY bt2.band_id
    ) shared ON shared.band_id = b.id
    LEFT JOIN suggestions s ON (
        (s.band1_id = p_band_id AND s.band2_id = b.id)
        OR (s.band2_id = p_band_id AND s.band1_id = b.id)
    )
    WHERE b.id != p_band_id
      AND (shared.shared_tags IS NULL OR shared.shared_tags >= min_shared_tags)
      ---AND (
      ---  s.id IS NULL -- no suggestion yet
      ---  OR (p_band_id = s.band1_id AND s.accepted1 IS DISTINCT FROM 2)
      ---  OR (p_band_id = s.band2_id AND s.accepted2 IS DISTINCT FROM 2)
      ---)
        AND (
        s.id IS NULL -- no suggestion yet
        OR (p_band_id = s.band1_id AND s.accepted1 = 0)
        OR (p_band_id = s.band2_id AND s.accepted2 = 0)
      )

    ORDER BY shared.shared_tags DESC NULLS LAST, s.dt NULLS FIRST
    LIMIT 1;

    IF matched_id IS NULL THEN
        RETURN;
    END IF;

    -- Try to find existing suggestion
    SELECT id INTO existing_id
    FROM suggestions
    WHERE (band1_id = p_band_id AND band2_id = matched_id)
       OR (band2_id = p_band_id AND band1_id = matched_id)
    LIMIT 1;

    IF existing_id IS NULL THEN
        INSERT INTO suggestions (dt, band1_id, band2_id)
        VALUES (NOW(), p_band_id, matched_id)
        RETURNING id INTO existing_id;
    END IF;

    RETURN QUERY
    SELECT existing_id, b.id, b.name, COALESCE(shared.shared_tags, 0)::INT, MAX(s.dt)
    FROM bands b
    LEFT JOIN (
        SELECT bt2.band_id, COUNT(*) AS shared_tags
        FROM bands_tags bt1
        JOIN bands_tags bt2 ON bt1.tags_id = bt2.tags_id
        WHERE bt1.band_id = p_band_id AND bt2.band_id != p_band_id
        GROUP BY bt2.band_id
    ) shared ON shared.band_id = b.id
    LEFT JOIN suggestions s ON (
        (s.band1_id = p_band_id AND s.band2_id = b.id)
        OR (s.band2_id = p_band_id AND s.band1_id = b.id)
    )
    WHERE b.id = matched_id
    GROUP BY b.id, b.name, shared.shared_tags;
END;
$$ LANGUAGE plpgsql;

   CREATE OR REPLACE FUNCTION resetSwipes(p_band_id INT)
RETURNS VOID AS $$
DECLARE
BEGIN

    --DELETE FROM suggestions
    --WHERE band1_id = p_band_id OR band2_id = p_band_id;

    UPDATE suggestions
    SET accepted1 = 0
    WHERE band1_id = p_band_id;

    UPDATE suggestions
    SET accepted2 = 0
    WHERE band2_id = p_band_id;
END;
$$ LANGUAGE plpgsql;



-- matched
CREATE OR REPLACE FUNCTION get_confirmed_matches(p_band_id INT)
RETURNS TABLE (
    band_id INT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        CASE
            WHEN s.band1_id = p_band_id THEN s.band2_id
            ELSE s.band1_id
        END AS band_id
    FROM suggestions s
    WHERE (s.band1_id = p_band_id OR s.band2_id = p_band_id)
      AND s.accepted1 = 2 AND s.accepted2 = 2
      AND s.band1_id != s.band2_id;
    --RETURN QUERY
    --SELECT band1_id FROM suggestions WHERE band1_id = p_band_id OR band2_id = p_band_id;
--
    --RETURN QUERY
    --(SELECT band1_id FROM suggestions WHERE accepted1 = 1 AND accepted2 = 1 AND band2_id = p_band_id) UNION (SELECT band2_id FROM suggestions WHERE accepted1 = 1 AND accepted2 = 1 AND band1_id = p_band_id);

END;
$$ LANGUAGE plpgsql;

-- get band id from suggestions
CREATE OR REPLACE FUNCTION getBandIdFromSuggestions(suggestion_id INT, bandid INT)
RETURNS INTEGER AS $$
DECLARE
    b INT;
BEGIN
    SELECT INTO b
        CASE
            WHEN s.band1_id = bandid THEN s.band2_id
            ELSE s.band1_id
        END AS band_id
    FROM suggestions s
    WHERE (s.band1_id = bandid OR s.band2_id = bandid) AND s.id = suggestion_id;

    RETURN b;
    --RETURN QUERY
    --SELECT band1_id FROM suggestions WHERE band1_id = p_band_id OR band2_id = p_band_id;
--
    --RETURN QUERY
    --(SELECT band1_id FROM suggestions WHERE accepted1 = 1 AND accepted2 = 1 AND band2_id = p_band_id) UNION (SELECT band2_id FROM suggestions WHERE accepted1 = 1 AND accepted2 = 1 AND band1_id = p_band_id);

END;
$$ LANGUAGE plpgsql;



-- accept suggestion match

CREATE OR REPLACE FUNCTION accept_suggestion(p_suggestion_id INT, p_band_id INT)
RETURNS VOID AS $$
BEGIN
    UPDATE suggestions
    SET
        accepted1 = CASE WHEN band1_id = p_band_id THEN 2 ELSE accepted1 END,
        accepted2 = CASE WHEN band2_id = p_band_id THEN 2 ELSE accepted2 END
    WHERE id = p_suggestion_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION reject_suggestion(p_suggestion_id INT, p_band_id INT)
RETURNS VOID AS $$
BEGIN
    UPDATE suggestions
    SET
        accepted1 = CASE WHEN band1_id = p_band_id THEN 1 ELSE accepted1 END,
        accepted2 = CASE WHEN band2_id = p_band_id THEN 1 ELSE accepted2 END
    WHERE id = p_suggestion_id;
END;
$$ LANGUAGE plpgsql;




-- update band profile
CREATE OR REPLACE FUNCTION update_band_profile(
    p_id INT,
    p_name VARCHAR,
    p_bio TEXT,
    p_email VARCHAR,
    p_phone VARCHAR,
    p_kraj_id INT
)
RETURNS VOID AS $$
BEGIN
    UPDATE bands
    SET name = p_name,
        bio = p_bio,
        email = p_email,
        phone = p_phone,
        kraj_id = p_kraj_id
    WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION getKraji()
RETURNS TABLE (
    id INT,
    ime VARCHAR,
    postna VARCHAR,
    vel_uporabnik VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT k.id, k.ime, k.postna, k.vel_uporabnik
    FROM kraji k
    ORDER BY k.ime;
END;
$$ LANGUAGE plpgsql;





-- tags
CREATE OR REPLACE FUNCTION add_band_tag(p_band_id INT, p_tag TEXT)
RETURNS VOID AS $$
DECLARE
    v_clean_tag TEXT;
    v_tag_id INT;
BEGIN
    -- Clean the tag: lowercase, remove everything except letters, numbers, and dashes
    v_clean_tag := LOWER(REGEXP_REPLACE(TRIM(p_tag), '[^a-z0-9\\-]+', '', 'g'));

    -- If resulting tag is empty, do nothing
    IF v_clean_tag = '' THEN
        RETURN;
    END IF;

    -- Check if tag exists
    SELECT id INTO v_tag_id FROM tags WHERE name = v_clean_tag;

    IF v_tag_id IS NULL THEN
        INSERT INTO tags(name) VALUES (v_clean_tag) RETURNING id INTO v_tag_id;
    END IF;

    -- Link tag to band if not already linked
    INSERT INTO bands_tags(band_id, tags_id)
    SELECT p_band_id, v_tag_id
    WHERE NOT EXISTS (
        SELECT 1 FROM bands_tags WHERE band_id = p_band_id AND tags_id = v_tag_id
    );
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION remove_band_tag(p_band_id INT, p_tag TEXT)
RETURNS VOID AS $$
DECLARE
    v_tag_id INT;
BEGIN
    -- Normalize tag
    p_tag := lower(trim(p_tag));

    -- Get tag ID
    SELECT id INTO v_tag_id FROM tags WHERE name = p_tag;
    IF v_tag_id IS NULL THEN
        RETURN; -- Nothing to remove
    END IF;

    -- Remove link from M:N table
    DELETE FROM bands_tags
    WHERE band_id = p_band_id AND tags_id = v_tag_id;
END;
$$ LANGUAGE plpgsql;

--login
CREATE OR REPLACE FUNCTION login_band(p_email TEXT, p_pass TEXT)
RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    SELECT id INTO v_id FROM bands WHERE email = p_email AND pasw = p_pass;
    RETURN COALESCE(v_id, -1);
END;
$$ LANGUAGE plpgsql;

-- register
CREATE OR REPLACE FUNCTION register_band(p_name TEXT, p_email TEXT, p_pass TEXT)
RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    IF EXISTS (SELECT 1 FROM bands WHERE email = p_email) THEN
        RETURN -1; -- already exists
    END IF;

    INSERT INTO bands(name, email, pasw, bio, dt, phone)
    VALUES(p_name, p_email, p_pass, '', NOW(), '')
    RETURNING id INTO v_id;

    RETURN v_id;
END;
$$ LANGUAGE plpgsql;

-- band images
-- get band image
CREATE OR REPLACE FUNCTION get_band_images(p_band_id INT)
RETURNS TABLE (
    slot INT,
    image_id INT,
    data BYTEA
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        bi.slot,
        i.id,
        i.data
    FROM bands_images bi
    INNER JOIN images i ON i.id = bi.image_id
    WHERE bi.band_id = p_band_id
    ORDER BY bi.slot;
END;
$$ LANGUAGE plpgsql;

-- save band image
CREATE OR REPLACE FUNCTION save_band_image(p_band_id INT, p_data BYTEA, p_slot INT)
RETURNS VOID AS $$
DECLARE
    img_id INT;
BEGIN
    -- If there's already an image in that slot, remove it
    DELETE FROM bands_images
    WHERE band_id = p_band_id AND slot = p_slot;

    -- Insert image and bind to band/slot
    INSERT INTO images(data) VALUES (p_data) RETURNING id INTO img_id;
    INSERT INTO bands_images(band_id, image_id, slot) VALUES (p_band_id, img_id, p_slot);
END;
$$ LANGUAGE plpgsql;

--delete band image
CREATE OR REPLACE FUNCTION delete_band_image(p_band_id INT, p_slot INT)
RETURNS VOID AS $$
DECLARE
    img_id INT;
BEGIN
    SELECT image_id INTO img_id
    FROM bands_images
    WHERE band_id = p_band_id AND slot = p_slot;

    DELETE FROM bands_images WHERE band_id = p_band_id AND slot = p_slot;
    DELETE FROM images WHERE id = img_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_band_profile_and_tags(
    p_band_id INT,
    p_name TEXT,
    p_bio TEXT,
    p_email TEXT,
    p_phone TEXT,
    p_kraj_id INT,
    p_tags TEXT[]
)
RETURNS VOID AS $$
DECLARE
    tag TEXT;
    normalized TEXT;
BEGIN
    -- Update main band profile info
    UPDATE bands
    SET
        name = p_name,
        bio = p_bio,
        email = p_email,
        phone = p_phone,
        kraj_id = p_kraj_id
    WHERE id = p_band_id;

    -- Delete old tag associations
    DELETE FROM bands_tags WHERE band_id = p_band_id;

    -- Reinsert tags
    IF p_tags IS NOT NULL THEN
        FOREACH tag IN ARRAY p_tags LOOP
            normalized := LOWER(regexp_replace(tag, '[^a-z0-9-]', '', 'g'));

            -- Insert tag into tags table if it doesn't exist
            INSERT INTO tags(name)
            SELECT normalized
            WHERE NOT EXISTS (
                SELECT 1 FROM tags WHERE name = normalized
            );

            -- Associate band with tag
            INSERT INTO bands_tags(band_id, tags_id)
            SELECT p_band_id, id FROM tags WHERE name = normalized;
        END LOOP;
    END IF;
END;
$$ LANGUAGE plpgsql;
