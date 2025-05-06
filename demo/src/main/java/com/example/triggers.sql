CREATE OR REPLACE FUNCTION log_band_profile_update()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO log(dt, data)
  VALUES (NOW(), 'Band #' || NEW.id || ' updated profile (name, email, etc.)');
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_band_profile_update
AFTER UPDATE ON bands
FOR EACH ROW
WHEN (
  OLD.name IS DISTINCT FROM NEW.name OR
  OLD.bio IS DISTINCT FROM NEW.bio OR
  OLD.phone IS DISTINCT FROM NEW.phone OR
  OLD.email IS DISTINCT FROM NEW.email OR
  OLD.kraj_id IS DISTINCT FROM NEW.kraj_id
)
EXECUTE FUNCTION log_band_profile_update();


CREATE OR REPLACE FUNCTION log_image_upload()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO log(dt, data)
  VALUES (NOW(), 'Band #' || NEW.band_id || ' uploaded an image (image_id=' || NEW.image_id || ')');
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_image_upload
AFTER INSERT ON bands_images
FOR EACH ROW
EXECUTE FUNCTION log_image_upload();


CREATE OR REPLACE FUNCTION log_tag_assignment()
RETURNS TRIGGER AS $$
DECLARE
  tag_name TEXT;
BEGIN
  SELECT name INTO tag_name FROM tags WHERE id = NEW.tags_id;
  INSERT INTO log(dt, data)
  VALUES (NOW(), 'Band #' || NEW.band_id || ' assigned tag #' || tag_name);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_tag_assignment
AFTER INSERT ON bands_tags
FOR EACH ROW
EXECUTE FUNCTION log_tag_assignment();




CREATE OR REPLACE FUNCTION update_tag_usage_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP IN ('DELETE', 'UPDATE') THEN
        UPDATE tags
        SET num = num - 1
        WHERE id = OLD.tags_id;
    END IF;

    IF TG_OP IN ('INSERT', 'UPDATE') THEN
        UPDATE tags
        SET num = num + 1
        WHERE id = NEW.tags_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_tag_count
AFTER INSERT OR DELETE OR UPDATE ON bands_tags
FOR EACH ROW
EXECUTE FUNCTION update_tag_usage_count();




CREATE OR REPLACE FUNCTION update_band_imgcount()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP IN ('DELETE', 'UPDATE') THEN
        UPDATE bands
        SET imgcount = imgcount - 1
        WHERE id = OLD.band_id;
    END IF;

    IF TG_OP IN ('INSERT', 'UPDATE') THEN
        UPDATE bands
        SET imgcount = imgcount + 1
        WHERE id = NEW.band_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_band_imgcount
AFTER INSERT OR DELETE OR UPDATE ON bands_images
FOR EACH ROW
EXECUTE FUNCTION update_band_imgcount();


CREATE OR REPLACE FUNCTION log_suggestion_update()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.accepted1 IS DISTINCT FROM OLD.accepted1 OR
       NEW.accepted2 IS DISTINCT FROM OLD.accepted2 THEN
        INSERT INTO log(dt, data)
        VALUES (NOW(), 'Suggestion ' || OLD.id || ' updated: accepted1=' || NEW.accepted1 || ', accepted2=' || NEW.accepted2);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_log_suggestion_update
AFTER UPDATE ON suggestions
FOR EACH ROW
EXECUTE FUNCTION log_suggestion_update();






