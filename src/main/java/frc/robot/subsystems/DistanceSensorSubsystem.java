package frc.robot.subsystems;

import au.grapplerobotics.LaserCan;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DistanceSensorConstants;

/**
 * Subsystem for managing left and right Grapple Robotics LaserCAN distance sensors.
 * Reads distance values directly from CAN-based sensors and converts to meters.
 */
public class DistanceSensorSubsystem extends SubsystemBase {
    private final LaserCan m_leftSensor;
    private final LaserCan m_rightSensor;

    public DistanceSensorSubsystem() {
        m_leftSensor = new LaserCan(DistanceSensorConstants.kLeftDistanceSensorCANId);
        m_rightSensor = new LaserCan(DistanceSensorConstants.kRightDistanceSensorCANId);
    }

    /**
     * Gets the distance reading from the left sensor in meters.
     * LaserCAN returns distance in millimeters, which is converted to meters.
     * 
     * @return Distance in meters, or -1.0 if reading is invalid
     */
    public double getLeftDistanceMeters() {
        try {
            LaserCan.Measurement measurement = m_leftSensor.getMeasurement();
            if (measurement != null && measurement.status == LaserCan.LASERCAN_STATUS_VALID_MEASUREMENT) {
                // Convert from millimeters to meters (divide by 1000)
                return measurement.distance_mm / 1000.0;
            }
        } catch (Exception e) {
            // Sensor not responding or invalid reading - return invalid marker
            // Don't log every time to avoid console spam
        }
        return -1.0;
    }

    /**
     * Gets the distance reading from the right sensor in meters.
     * LaserCAN returns distance in millimeters, which is converted to meters.
     * 
     * @return Distance in meters, or -1.0 if reading is invalid
     */
    public double getRightDistanceMeters() {
        try {
            LaserCan.Measurement measurement = m_rightSensor.getMeasurement();
            if (measurement != null && measurement.status == LaserCan.LASERCAN_STATUS_VALID_MEASUREMENT) {
                // Convert from millimeters to meters (divide by 1000)
                return measurement.distance_mm / 1000.0;
            }
        } catch (Exception e) {
            // Sensor not responding or invalid reading - return invalid marker
            // Don't log every time to avoid console spam
        }
        return -1.0;
    }

    /**
     * Gets the average distance from both sensors.
     * 
     * @return Average distance in meters, or -1.0 if either sensor is invalid
     */
    public double getAverageDistanceMeters() {
        double left = getLeftDistanceMeters();
        double right = getRightDistanceMeters();
        
        // If either sensor is invalid, return -1
        if (left < 0 || right < 0) {
            return -1.0;
        }
        
        return (left + right) / 2.0;
    }

    /**
     * Checks if both sensors are at the target distance (within tolerance).
     * 
     * @param targetDistance Target distance in meters
     * @param tolerance Tolerance in meters
     * @return True if both sensors are within tolerance of target distance
     */
    public boolean bothSensorsAtTarget(double targetDistance, double tolerance) {
        double left = getLeftDistanceMeters();
        double right = getRightDistanceMeters();
        
        // Check if sensors are valid
        if (left < 0 || right < 0) {
            return false;
        }
        
        // Check if both are within tolerance
        boolean leftAtTarget = Math.abs(left - targetDistance) <= tolerance;
        boolean rightAtTarget = Math.abs(right - targetDistance) <= tolerance;
        
        return leftAtTarget && rightAtTarget;
    }

    /**
     * Checks if both sensors are reading valid values.
     * 
     * @return True if both sensors are providing valid readings
     */
    public boolean bothSensorsValid() {
        double left = getLeftDistanceMeters();
        double right = getRightDistanceMeters();
        return left >= 0 && right >= 0;
    }

    @Override
    public void periodic() {
        // Update SmartDashboard with sensor readings
        SmartDashboard.putNumber("Left Distance (m)", getLeftDistanceMeters());
        SmartDashboard.putNumber("Right Distance (m)", getRightDistanceMeters());
        SmartDashboard.putNumber("Average Distance (m)", getAverageDistanceMeters());
        SmartDashboard.putBoolean("Sensors At Target", 
            bothSensorsAtTarget(
                DistanceSensorConstants.kTargetDistanceMeters,
                DistanceSensorConstants.kDistanceToleranceMeters
            ));
        SmartDashboard.putBoolean("Sensors Valid", bothSensorsValid());
    }
}

