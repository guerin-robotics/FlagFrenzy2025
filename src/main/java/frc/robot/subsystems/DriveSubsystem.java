package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;
import com.ctre.phoenix6.hardware.TalonFX;

public class DriveSubsystem extends SubsystemBase {
    private final TalonFX m_leftDrive = new TalonFX(DriveConstants.kLeftMotorPort);
    private final TalonFX m_rightDrive = new TalonFX(DriveConstants.kRightMotorPort);

    public DriveSubsystem() {
        // Reset encoders to zero on initialization
        resetEncoders();
    }

    /**
     * Gets the average distance traveled by both drive motors in meters.
     * Uses the internal encoders on the TalonFX (Kraken) motors.
     * 
     * @return Average distance in meters
     */
    public double getEncoderMeters() {
        // Get position in rotations from TalonFX internal encoders
        // getPosition() returns SignalValue, getValueAsDouble() returns the numeric value as a double in rotations
        double leftRotations = m_leftDrive.getPosition().getValueAsDouble();
        double rightRotations = m_rightDrive.getPosition().getValueAsDouble();
        
        // Convert rotations to meters and average
        double leftMeters = leftRotations * DriveConstants.kTalonFXRotationsToMeters;
        double rightMeters = rightRotations * DriveConstants.kTalonFXRotationsToMeters;
        
        return (leftMeters + rightMeters) / 2.0;
    }

    /**
     * Gets the distance traveled by the left drive motor in meters.
     * 
     * @return Left distance in meters
     */
    public double getLeftEncoderMeters() {
        double leftRotations = m_leftDrive.getPosition().getValueAsDouble();
        return leftRotations * DriveConstants.kTalonFXRotationsToMeters;
    }

    /**
     * Gets the distance traveled by the right drive motor in meters.
     * 
     * @return Right distance in meters
     */
    public double getRightEncoderMeters() {
        double rightRotations = m_rightDrive.getPosition().getValueAsDouble();
        return rightRotations * DriveConstants.kTalonFXRotationsToMeters;
    }

    /**
     * Resets the encoder positions to zero.
     */
    public void resetEncoders() {
        m_leftDrive.setPosition(0);
        m_rightDrive.setPosition(0);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Drive encoder value (m)", getEncoderMeters());
        SmartDashboard.putNumber("Left motor position (rot)", m_leftDrive.getPosition().getValueAsDouble());
        SmartDashboard.putNumber("Right motor position (rot)", m_rightDrive.getPosition().getValueAsDouble());
    }

    /**
     * Sets the motor speeds for the drivetrain.
     * Note: Right motor is inverted to match physical wiring/orientation.
     *
     * @param leftSpeed Left motor speed (-1.0 to 1.0)
     * @param rightSpeed Right motor speed (-1.0 to 1.0)
     */
    public void setMotors(double leftSpeed, double rightSpeed) {
        // Validate inputs to prevent NaN/Infinity from damaging motors
        if (!Double.isFinite(leftSpeed)) {
            leftSpeed = 0;
        }
        if (!Double.isFinite(rightSpeed)) {
            rightSpeed = 0;
        }
        
        // Clamp speeds to safe range [-1.0, 1.0] to prevent motor damage
        leftSpeed = MathUtil.clamp(leftSpeed, -1.0, 1.0);
        rightSpeed = MathUtil.clamp(rightSpeed, -1.0, 1.0);
        
        m_leftDrive.set(leftSpeed);
        m_rightDrive.set(-rightSpeed); // Inverted to match physical orientation
    }

    /**
     * Arcade drive method that applies turn sensitivity multiplier.
     * This makes turning less sensitive for easier control.
     *
     * @param speed Forward/backward speed (-1.0 to 1.0)
     * @param turn Turn speed (-1.0 to 1.0), will be reduced by turn sensitivity multiplier
     */
    public void arcadeDrive(double speed, double turn) {
        // Validate inputs
        if (!Double.isFinite(speed)) {
            speed = 0;
        }
        if (!Double.isFinite(turn)) {
            turn = 0;
        }
        
        // Apply turn sensitivity multiplier to reduce turning effect
        turn *= DriveConstants.kTurnSensitivityMultiplier;
        
        // Calculate left and right speeds for arcade drive
        double leftSpeed = speed + turn;
        double rightSpeed = speed - turn;
        
        setMotors(leftSpeed, rightSpeed);
    }

    /**
     * Applies brake power to both drive motors to help slow down the robot.
     * Used when the robot is disabled.
     *
     * @param brakePower Brake power to apply (0.0 to 1.0, typically negative for reverse/braking)
     */
    public void brake(double brakePower) {
        // Validate input
        if (!Double.isFinite(brakePower)) {
            brakePower = 0;
        }
        
        m_leftDrive.set(brakePower);
        m_rightDrive.set(-brakePower); // Inverted to match physical orientation
    }
}

