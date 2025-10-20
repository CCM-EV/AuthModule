-- ==============================================================================
-- FIX ROLE CONSTRAINT - Migrate from old roles to new Carbon Credit roles
-- ==============================================================================
-- This script updates the users table to support the Carbon Credit Marketplace roles:
-- - EV_OWNER (Electric Vehicle Owner)
-- - CC_BUYER (Carbon Credit Buyer)
-- - CVA (Carbon Verification & Audit)
-- - ADMIN (System Administrator)
-- 
-- Run this if you see error: "users_role_check constraint violation"
-- ==============================================================================

-- Step 1: Update existing users to use new role names
UPDATE users SET role = 'EV_OWNER' WHERE role = 'USER';
UPDATE users SET role = 'ADMIN' WHERE role = 'ADMIN';

-- Step 2: Drop old role constraint
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- Step 3: Add new role constraint with correct Carbon Credit roles
ALTER TABLE users ADD CONSTRAINT users_role_check 
CHECK (role IN ('EV_OWNER', 'CC_BUYER', 'CVA', 'ADMIN'));

-- Verify the change
\d users

