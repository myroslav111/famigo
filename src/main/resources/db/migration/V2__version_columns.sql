-- Optimistic Locking für Sterne-Buchungen und Statusübergänge von Aufgaben
ALTER TABLE users ADD COLUMN version bigint NOT NULL DEFAULT 0;
ALTER TABLE tasks ADD COLUMN version bigint NOT NULL DEFAULT 0;
