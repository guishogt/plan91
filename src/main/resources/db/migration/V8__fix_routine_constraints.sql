-- Fix constraints to support configurable target days and new frequency types

-- 1. Drop the old 91-day constraint and create a new one that uses target_days
ALTER TABLE routines
DROP CONSTRAINT chk_91_day_cycle;

ALTER TABLE routines
ADD CONSTRAINT chk_target_day_cycle
    CHECK (DATEDIFF(expected_end_date, start_date) = target_days - 1);

-- 2. Update recurrence_type constraint to include new TIMES_PER_WEEK types
ALTER TABLE routines
DROP CONSTRAINT chk_recurrence_type;

ALTER TABLE routines
ADD CONSTRAINT chk_recurrence_type
    CHECK (recurrence_type IN (
        'DAILY', 'WEEKDAYS', 'WEEKENDS', 'SPECIFIC_DAYS', 'NTH_DAY_OF_MONTH',
        'TIMES_PER_WEEK_1', 'TIMES_PER_WEEK_3', 'TIMES_PER_WEEK_4',
        'TIMES_PER_WEEK_5', 'TIMES_PER_WEEK_6'
    ));
