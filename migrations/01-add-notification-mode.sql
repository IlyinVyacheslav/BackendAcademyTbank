DO
'
BEGIN
    CREATE TYPE notification_mode AS ENUM (''IMMEDIATE'', ''DIGEST'');
EXCEPTION
    WHEN duplicate_object THEN null;
END;
' LANGUAGE PLPGSQL;

ALTER TABLE chats ADD COLUMN notification_mode notification_mode NOT NULL DEFAULT 'IMMEDIATE';
