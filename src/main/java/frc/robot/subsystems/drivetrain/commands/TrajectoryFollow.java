package frc.robot.subsystems.drivetrain.commands;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.drivetrain.DrivetrainSubsystem;

public class TrajectoryFollow extends CommandBase {

    private String m_pathName;
    private PathPlannerTrajectory m_trajectory = null;

    /**
     * Executes a trajectory that makes it remain still
     */
    public TrajectoryFollow() {
        m_pathName = "Stay Still";
    }

    public TrajectoryFollow(String pathName) {
        m_pathName = pathName;
    }

    public TrajectoryFollow(Trajectory traj) {
        m_trajectory = (PathPlannerTrajectory) traj;
    }

    @Override
    public void initialize() {
        System.out.println("Trajectory begun");


        if (m_trajectory == null) {
            try {
                m_trajectory = PathPlanner.loadPath(m_pathName, 4.9, 4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ProfiledPIDController thetaController = new ProfiledPIDController(1.5, 0, 0,
                new TrapezoidProfile.Constraints(DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND,
                        Math.pow(DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND, 2)));
        thetaController.enableContinuousInput(-Math.PI, Math.PI);

        new PPSwerveControllerCommand(m_trajectory,
                DrivetrainSubsystem.getInstance()::getCurrentPose,
                DrivetrainSubsystem.getInstance().getKinematics(),
                new PIDController(SmartDashboard.getNumber("x_P", 1), SmartDashboard.getNumber("x_I", 0), SmartDashboard.getNumber("x_D", 0)),
                new PIDController(3, 0, 0),
                thetaController,
                DrivetrainSubsystem.getInstance()::actuateModulesAuto,
                DrivetrainSubsystem.getInstance())
                        .andThen(() -> DrivetrainSubsystem.getInstance().drive(new ChassisSpeeds(0.0, 0.0, 0.0)))
                        .schedule();
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
    }

}

/*
 * OLD PATHFOLLOWING COMMAND
 * PathPlannerState finalAngle = ((PathPlannerState) trajectory.getEndState());
 * 
 * // new SwerveControllerCommand(trajectory,
 * // DrivetrainSubsystem.getInstance()::getCurrentPose,
 * // DrivetrainSubsystem.getInstance().getKinematics(),
 * // new PIDController(3, 0, 0),
 * // new PIDController(3, 0, 0),
 * // thetaController,
 * // () -> finalAngle.holonomicRotation,
 * // DrivetrainSubsystem.getInstance()::actuateModulesAuto,
 * // DrivetrainSubsystem.getInstance()).andThen(() ->
 * DrivetrainSubsystem.getInstance().drive(new ChassisSpeeds(0.0, 0.0,
 * 0.0))).schedule();
 * 
 */