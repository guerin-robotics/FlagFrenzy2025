package frc.robot;

import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.ArcadeDriveCommand;
import frc.robot.commands.DriveForwardCommand;
import frc.robot.commands.FeederSetCommand;
import frc.robot.commands.FeederRunCommand;
import frc.robot.commands.TurnCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.FeederSubsystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared.
 * Since Command-based is a "declarative" paradigm, very little robot logic
 * should actually be in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    private final DriveSubsystem driveSubsystem = new DriveSubsystem();
    private final FeederSubsystem feederSubsystem = new FeederSubsystem();
    private final ShooterSubsystem shooterSubsystem = new ShooterSubsystem();
    // Joystick port: USB port number on the roboRIO/roboRIO 2.0
    // Port 0 = First USB port, Port 1 = Second USB port, etc.
    // Configure this in Driver Station under "USB Devices" or check the USB port number
    private final Joystick joystick1 = new Joystick(OIConstants.kDriverJoystickPort);

    public RobotContainer() {
        // Validate joystick connection
        if (joystick1.getButtonCount() == 0) {
            System.err.println("WARNING: Joystick not detected on port " + OIConstants.kDriverJoystickPort);
        }
        
        configureButtonBindings();

        driveSubsystem.setDefaultCommand(new ArcadeDriveCommand(driveSubsystem, //
                () -> -joystick1.getRawAxis(OIConstants.kArcadeDriveSpeedAxis),
                () -> joystick1.getRawAxis(OIConstants.kArcadeDriveTurnAxis))//
        );
        // Feeder has default idle command to keep motor stopped when not in use
    }

    private void configureButtonBindings() {
        // Button 6: Run feeder motor while held - runs at configured percent output
        new JoystickButton(joystick1, OIConstants.kIntakeCloseButtonIdx)
                .whileTrue(new FeederSetCommand(feederSubsystem));
        
        // Button 5: Toggle shooter on/off - runs at target velocity using PID control
        new JoystickButton(joystick1, OIConstants.kShooterButtonIdx)
                .toggleOnTrue(shooterSubsystem.shootCommand());
    }

    /**
     * Returns the drive subsystem. Used for applying braking when disabled.
     * 
     * @return The drive subsystem
     */
    public DriveSubsystem getDriveSubsystem() {
        return driveSubsystem;
    }

    /**
     * Returns the feeder subsystem. Used for applying braking when disabled.
     * 
     * @return The feeder subsystem
     */
    public FeederSubsystem getFeederSubsystem() {
        return feederSubsystem;
    }

    /**
     * Returns the shooter subsystem. Used for applying braking when disabled.
     * 
     * @return The shooter subsystem
     */
    public ShooterSubsystem getShooterSubsystem() {
        return shooterSubsystem;
    }

    /**
     * Helper method to create a drive forward command with custom distance.
     * 
     * @param distanceMeters Distance to drive forward in meters
     * @return DriveForwardCommand with specified distance
     */
    public Command driveForward(double distanceMeters) {
        return new DriveForwardCommand(driveSubsystem, distanceMeters);
    }

    /**
     * Helper method to create a turn command with custom angle.
     * 
     * @param degrees Angle to turn in degrees (positive = right/clockwise, negative = left/counter-clockwise)
     * @return TurnCommand with specified angle
     */
    public Command turn(double degrees) {
        return new TurnCommand(driveSubsystem, degrees);
    }

    /**
     * Helper method to create a feeder run command with custom rotations.
     * 
     * @param rotations Number of rotations to run the feeder
     * @return FeederRunCommand with specified rotations
     */
    public Command runFeeder(double rotations) {
        return new FeederRunCommand(feederSubsystem, rotations);
    }

    /**
     * Helper method to start the shooter (runs continuously until interrupted).
     * 
     * @return Shooter shoot command
     */
    public Command startShooter() {
        return shooterSubsystem.shootCommand();
    }

    /**
     * Default autonomous command.
     * You can easily modify this to create different autonomous routines by:
     * - Changing distances: driveForward(0.5) → driveForward(1.0)
     * - Changing turn angles: turn(90) → turn(-45)
     * - Adding feeder: runFeeder(2.0) for 2 rotations
     * - Reordering commands in any sequence
     */
    public Command getAutonomousCommand() {
        // Example: Start shooter, wait for spin-up, drive forward, turn, run feeder
        return new ParallelCommandGroup(
            // Shooter runs continuously (never finishes)
            startShooter(),
            // Sequential group: wait for spin-up, then drive, then turn, then feed
            new SequentialCommandGroup(
                // Wait 2 seconds for shooter to spin up
                new WaitCommand(1.0),
                // Drive forward 0.5m (you can change this to any distance)
                driveForward(0.5),
                // Turn 90 degrees (you can change this to any angle)
                turn(90.0),
                // Run feeder for 2 rotations (you can change this to any number)
                runFeeder(2.0)
            )
        );
    }

}

