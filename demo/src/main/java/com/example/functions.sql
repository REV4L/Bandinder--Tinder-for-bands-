
-- get band
DROP FUNCTION IF EXISTS get_band_info(p_band_id INT)
CREATE OR REPLACE FUNCTION get_band_info(p_band_id INT)
RETURNS TABLE (
    id INT,
    name VARCHAR,
    bio TEXT,
    email VARCHAR,
    phone VARCHAR,
    dt TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT b.id, b.name, b.bio, b.email, b.phone, b.dt
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
    band_id INT,
    band_name VARCHAR,
    shared_tags_count INT,
    last_suggested TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        b.id,
        b.name,
        COALESCE(shared.shared_tags, 0)::INT AS shared_tags_count,
        MAX(s.dt) AS last_suggested
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
    AND (
        shared.shared_tags IS NULL
        OR shared.shared_tags >= min_shared_tags
    )
    GROUP BY b.id, b.name, shared.shared_tags
    ORDER BY last_suggested NULLS FIRST, shared_tags_count DESC
    LIMIT 1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_best_band_match(p_band_id INT, min_shared_tags INT)
RETURNS TABLE (
    band_id INT,
    band_name VARCHAR,
    shared_tags_count INT,
    last_suggested TIMESTAMP
) AS $$
DECLARE
    matched_id INT;
BEGIN
    -- Find the best match
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
    GROUP BY b.id, b.name, shared.shared_tags
    ORDER BY MAX(s.dt) NULLS FIRST, COALESCE(shared.shared_tags, 0) DESC
    LIMIT 1;

    IF matched_id IS NOT NULL THEN
        -- Insert into suggestions
        INSERT INTO suggestions (dt, band1_id, band2_id)
        VALUES (NOW(), p_band_id, matched_id);

        -- Return the match info
        RETURN QUERY
        SELECT
            b.id,
            b.name,
            COALESCE(shared.shared_tags, 0)::INT,
            MAX(s.dt)
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
    END IF;
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







