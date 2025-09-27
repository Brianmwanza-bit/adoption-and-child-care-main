-- Migration 1 -> 2
-- Add new columns to support profile photos, adoption status and audit call_card
ALTER TABLE users ADD COLUMN profile_photo_uri TEXT;
ALTER TABLE case_reports ADD COLUMN adoption_status TEXT;
ALTER TABLE families ADD COLUMN adoption_eligibility TEXT;
ALTER TABLE audit_logs ADD COLUMN call_card TEXT;

-- Create icons table
CREATE TABLE IF NOT EXISTS icons (
  icon_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  `key` TEXT,
  url TEXT,
  provider TEXT,
  created_at TEXT
);
