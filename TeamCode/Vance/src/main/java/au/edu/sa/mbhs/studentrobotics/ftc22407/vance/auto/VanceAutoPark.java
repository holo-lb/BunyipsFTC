package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.auto;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;

import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * VanceAuto but also parks in parking position
 * Vance actually can't touch the metal thingo to score so we need to park elsewhere
 *
 * @author Lachlan Paul, 2024
 */
@Autonomous
public class VanceAutoPark extends VanceAuto {
    @Override
    protected void park() {
//        robot.drive.makeTrajectory(new Vector2d(61.28, 51.09), Inches, 270.00, Degrees)
        defer (() -> robot.drive.makeTrajectory()
                .splineTo(new Vector2d(55.27, 42.21), Inches, 203.57, Degrees)
                .splineTo(new Vector2d(24.44, 36.72), Inches, 180.00, Degrees)
                .splineTo(new Vector2d(-28.62, 41.68), Inches, 178.46, Degrees)
                .strafeToLinearHeading(new Vector2d(-58.41, 60.02), Inches, 90.00, Degrees)
                .build());
    }
}
