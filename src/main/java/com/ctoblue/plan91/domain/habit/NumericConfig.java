package com.ctoblue.plan91.domain.habit;

/**
 * Value object representing numeric tracking configuration for a habit.
 *
 * <p>Used when a habit has trackingType = NUMERIC to define:
 * <ul>
 *   <li>Unit of measurement (e.g., "meters", "pages", "minutes")</li>
 *   <li>Optional minimum value</li>
 *   <li>Optional maximum value</li>
 *   <li>Optional target value</li>
 * </ul>
 *
 * <p>Examples:
 * <pre>
 * NumericConfig swimming = new NumericConfig("meters", null, null, 1500);
 * NumericConfig reading = new NumericConfig("pages", 1, 100, 10);
 * </pre>
 */
public record NumericConfig(
        String unit,
        Integer min,
        Integer max,
        Integer target
) {

    /**
     * Compact constructor with validation.
     */
    public NumericConfig {
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Unit cannot be null or blank");
        }
        unit = unit.trim();

        if (min != null && min < 0) {
            throw new IllegalArgumentException("Min cannot be negative, got: " + min);
        }
        if (max != null && max < 0) {
            throw new IllegalArgumentException("Max cannot be negative, got: " + max);
        }
        if (target != null && target < 0) {
            throw new IllegalArgumentException("Target cannot be negative, got: " + target);
        }
        if (min != null && max != null && min > max) {
            throw new IllegalArgumentException("Min (" + min + ") cannot be greater than max (" + max + ")");
        }
    }

    /**
     * Checks if a given value is valid according to min/max constraints.
     *
     * @param value the value to check
     * @return true if value is within min/max bounds (if specified)
     */
    public boolean isValueValid(int value) {
        if (min != null && value < min) {
            return false;
        }
        if (max != null && value > max) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a value meets the target (if specified).
     *
     * @param value the value to check
     * @return true if no target specified, or value >= target
     */
    public boolean meetsTarget(int value) {
        if (target == null) {
            return true;
        }
        return value >= target;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NumericConfig{unit='").append(unit).append("'");
        if (min != null) sb.append(", min=").append(min);
        if (max != null) sb.append(", max=").append(max);
        if (target != null) sb.append(", target=").append(target);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing NumericConfig...\n");

        // Test 1: Valid config with all fields
        NumericConfig config1 = new NumericConfig("pages", 1, 100, 10);
        assert config1.unit().equals("pages");
        assert config1.min() == 1;
        assert config1.max() == 100;
        assert config1.target() == 10;
        System.out.println("✓ Test 1: Valid config with all fields: " + config1);

        // Test 2: Valid config with only unit
        NumericConfig config2 = new NumericConfig("meters", null, null, null);
        assert config2.unit().equals("meters");
        assert config2.min() == null;
        assert config2.max() == null;
        assert config2.target() == null;
        System.out.println("✓ Test 2: Valid config with only unit: " + config2);

        // Test 3: Valid config with unit and target
        NumericConfig config3 = new NumericConfig("minutes", null, null, 30);
        assert config3.unit().equals("minutes");
        assert config3.target() == 30;
        System.out.println("✓ Test 3: Valid config with unit and target: " + config3);

        // Test 4: Whitespace trimming in unit
        NumericConfig config4 = new NumericConfig("  kilometers  ", null, null, null);
        assert config4.unit().equals("kilometers");
        System.out.println("✓ Test 4: Whitespace trimming works");

        // Test 5: isValueValid with no constraints
        assert config2.isValueValid(0);
        assert config2.isValueValid(100);
        assert config2.isValueValid(1000000);
        System.out.println("✓ Test 5: isValueValid with no constraints");

        // Test 6: isValueValid with min constraint
        NumericConfig config6 = new NumericConfig("pages", 5, null, null);
        assert !config6.isValueValid(4);
        assert config6.isValueValid(5);
        assert config6.isValueValid(6);
        System.out.println("✓ Test 6: isValueValid with min constraint");

        // Test 7: isValueValid with max constraint
        NumericConfig config7 = new NumericConfig("pages", null, 50, null);
        assert config7.isValueValid(49);
        assert config7.isValueValid(50);
        assert !config7.isValueValid(51);
        System.out.println("✓ Test 7: isValueValid with max constraint");

        // Test 8: isValueValid with min and max
        assert !config1.isValueValid(0);
        assert config1.isValueValid(1);
        assert config1.isValueValid(50);
        assert config1.isValueValid(100);
        assert !config1.isValueValid(101);
        System.out.println("✓ Test 8: isValueValid with min and max");

        // Test 9: meetsTarget with no target
        assert config2.meetsTarget(0);
        assert config2.meetsTarget(100);
        System.out.println("✓ Test 9: meetsTarget with no target");

        // Test 10: meetsTarget with target
        assert !config1.meetsTarget(9);
        assert config1.meetsTarget(10);
        assert config1.meetsTarget(11);
        System.out.println("✓ Test 10: meetsTarget with target");

        // Test 11: Null unit validation
        try {
            new NumericConfig(null, null, null, null);
            assert false : "Should throw for null unit";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 11: Null unit validation works: " + e.getMessage());
        }

        // Test 12: Blank unit validation
        try {
            new NumericConfig("   ", null, null, null);
            assert false : "Should throw for blank unit";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 12: Blank unit validation works: " + e.getMessage());
        }

        // Test 13: Negative min validation
        try {
            new NumericConfig("pages", -1, null, null);
            assert false : "Should throw for negative min";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 13: Negative min validation works: " + e.getMessage());
        }

        // Test 14: Negative max validation
        try {
            new NumericConfig("pages", null, -10, null);
            assert false : "Should throw for negative max";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 14: Negative max validation works: " + e.getMessage());
        }

        // Test 15: Negative target validation
        try {
            new NumericConfig("pages", null, null, -5);
            assert false : "Should throw for negative target";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 15: Negative target validation works: " + e.getMessage());
        }

        // Test 16: Min > max validation
        try {
            new NumericConfig("pages", 100, 50, null);
            assert false : "Should throw for min > max";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 16: Min > max validation works: " + e.getMessage());
        }

        // Test 17: Equality
        NumericConfig c1 = new NumericConfig("pages", 1, 10, 5);
        NumericConfig c2 = new NumericConfig("pages", 1, 10, 5);
        assert c1.equals(c2);
        System.out.println("✓ Test 17: Equality works");

        // Test 18: Inequality - different unit
        NumericConfig c3 = new NumericConfig("meters", 1, 10, 5);
        assert !c1.equals(c3);
        System.out.println("✓ Test 18: Inequality (different unit) works");

        // Test 19: toString()
        String str = config1.toString();
        assert str.contains("pages");
        assert str.contains("min=1");
        assert str.contains("max=100");
        assert str.contains("target=10");
        System.out.println("✓ Test 19: toString() works: " + str);

        System.out.println("\n✅ All NumericConfig tests passed!");
    }
}
