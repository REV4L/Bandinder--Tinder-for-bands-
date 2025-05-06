![logo@2x](https://github.com/user-attachments/assets/d802f0ad-8353-4a90-9743-24d1e749c9a7)


# Bandinder – JavaFX Band Matching App

Bandinder is a **Java desktop application** built with **JavaFX** that enables music bands to create profiles, upload images, tag themselves with genres/styles, and swipe to find and match with other bands. It's a Tinder-like experience tailored to musicians looking for collaboration.

The project features a clean, animated interface, swipe gestures, profile editing, and real-time match tracking. The backend relies on a **PostgreSQL** database hosted on **Aiven** for cloud-native performance and scalability.


---

## Database

The PostgreSQL database is designed using **Toad Data Modeler**.

![image](https://github.com/user-attachments/assets/1c168e6c-c1a8-4e65-8040-ed55395e338b)


---

## Table of Contents

- [Bands](#bands)
- [Tags](#tags)
- [Suggestions (Swipes & Matches)](#suggestions-swipes--matches)
- [Images](#images)
- [Places (Kraji)](#places-kraji)
- [Login & Registration](#login--registration)
- [Triggers](#triggers)
- [Utility](#utility)
- [License](#license)

---

## Bands

### `get_band_info(p_band_id INT) RETURNS RECORD`
Fetches detailed info about a single band.

### `update_band_profile(...) RETURNS VOID`
Updates a band's profile (name, bio, contact, location).

### `update_band_profile_and_tags(...) RETURNS VOID`
Simultaneously updates the profile and tag list.

---

## Tags

### `add_band_tag(p_band_id INT, p_tag TEXT) RETURNS VOID`
Sanitizes and links a tag to a band. Creates the tag if missing.

### `remove_band_tag(p_band_id INT, p_tag TEXT) RETURNS VOID`
Unlinks a tag from a band.

---

## Suggestions (Swipes & Matches)

### `get_best_band_match(p_band_id INT, min_shared_tags INT) RETURNS RECORD`
Finds the next best match, considering shared tags and swipe history.

### `accept_suggestion(p_suggestion_id INT, p_band_id INT) RETURNS VOID`
Records a band’s "accept" swipe (sets accepted1 or accepted2 to `2`).

### `reject_suggestion(p_suggestion_id INT, p_band_id INT) RETURNS VOID`
Records a "reject" swipe (sets accepted1 or accepted2 to `1`).

### `resetSwipes(p_band_id INT) RETURNS VOID`
Resets a band's swipes by setting accepted1/2 back to 0.

### `get_confirmed_matches(p_band_id INT) RETURNS SETOF INT`
Returns band IDs of all mutual matches (both sides accepted).

### `getBandIdFromSuggestions(suggestion_id INT, bandid INT) RETURNS INT`
Gets the other band’s ID from a suggestion.

---

## Images

### `get_band_images(p_band_id INT) RETURNS SETOF RECORD`
Gets up to 6 band profile images (with slot numbers).

### `save_band_image(p_band_id INT, p_data BYTEA, p_slot INT) RETURNS VOID`
Saves or replaces a profile image in the given slot.

### `delete_band_image(p_band_id INT, p_slot INT) RETURNS VOID`
Deletes a band's image from the specified slot.

---

## Places (Kraji)

### `getKraji() RETURNS SETOF RECORD`
Returns all locations available for band profiles.

---

## Login & Registration

### `login_band(p_email TEXT, p_pass TEXT) RETURNS INT`
Logs in a band based on email/password. Returns band ID or -1.

### `register_band(p_name TEXT, p_email TEXT, p_pass TEXT) RETURNS INT`
Registers a new band. Returns band ID, or -1 if email already used.

---

## Triggers

> These are PostgreSQL triggers set up to enforce integrity and audit history automatically.

### `trg_profile_update`
Logs profile changes into the `log` table when a band is updated.

### `trg_image_upload`
Logs image uploads (insert into `images`) with timestamp.

### `trg_suggestion_created`
Logs creation of a suggestion (new swipe attempt).

### `trg_suggestion_accepted`
Logs any change in `accepted1` or `accepted2` indicating a swipe.

### `trg_tag_count_update`
Updates the `tags.num` counter when tags are added/removed.

### `trg_image_count_update`
Maintains a count of images per band (`bands.imgcount`).

---

## Utility

### Manual Count Recalculation
If the database already contains data prior to triggers, use this SQL to refresh the counts:

```sql
-- Recalculate tag counts
UPDATE tags SET num = (
    SELECT COUNT(*) FROM bands_tags WHERE tags_id = tags.id
);

-- Recalculate image counts
ALTER TABLE bands ADD COLUMN IF NOT EXISTS imgcount INT DEFAULT 0;
UPDATE bands SET imgcount = (
    SELECT COUNT(*) FROM bands_images WHERE band_id = bands.id
);
```

---

## License

This project is licensed under the **MIT License**.  
Feel free to use, fork, and adapt as needed.
