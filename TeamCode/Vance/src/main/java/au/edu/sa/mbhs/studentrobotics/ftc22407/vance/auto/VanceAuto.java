package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.auto;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.FieldTiles;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.MessageTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks.BasketPlacer;

/**
 * Simple Auto to score pre-loaded elements
 *
 * @author Lachlan Paul, 2024
 */
@Autonomous
public class VanceAuto extends AutonomousBunyipsOpMode {
    private final Vance robot = new Vance();
    private MessageTask waitMessage;

    @Override
    protected void onInitialise() {
        robot.init();

        setOpModes(
                StartingConfiguration.redLeft().tile(2),
                StartingConfiguration.redRight().tile(2),
                StartingConfiguration.blueLeft().tile(2),
                StartingConfiguration.blueRight().tile(2)
        );

        waitMessage = new MessageTask(Seconds.of(10), "<style=\"color:red;\">DO NOT PANIC because the robot isn't moving, it is waiting for others to move</>");
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @NonNull Controls selectedButton) {
        if (selectedOpMode == null) return;
        StartingConfiguration.Position startingPosition = (StartingConfiguration.Position) selectedOpMode.require();

        robot.drive.setPose(startingPosition.toFieldPose());

        // We need to wait for other robots when we're far away from the basket, so we use a message task to delay our cross-country road trip
        if (startingPosition.isRight()) {
            add(waitMessage);
        }

        // Vance is a goofy mf(marquee function) so we need to manually set it's pose.
        // Just steal it from rrpathgen and it will be fine.
        // TODO: Add code for putting things in the baskets, and then parking.
        //  The blue pathing will most likely be almost identical so we won't worry about that till the whole thing is written.
        if (startingPosition.isRed()) {
            if (startingPosition.isLeft()) {
                robot.drive.setPose(new Vector2d(-36.24, -62.76), Inches, 270.00, Degrees);
            } else {
                robot.drive.makeTrajectory(new Vector2d(-35.76, -61.30), Inches, 270.00, Degrees)
                        .splineTo(new Vector2d(-8.51, -41.35), Inches, -46.42, Degrees)
                        .splineTo(new Vector2d(26.51, -37.95), Inches, 1.62, Degrees)
                        .addTask();
            }
            robot.drive.makeTrajectory()
                    .splineTo(new Vector2d(1.47, 2.14), FieldTiles, 99.06, Degrees)
                    .splineTo(new Vector2d(-0.95, 1.56), FieldTiles, 168.05, Degrees)
                    .splineTo(new Vector2d(-2.45, 2.46), FieldTiles, 230.00, Degrees)
                    .addTask();

        } else {  // always blue, da ba dee da ba di
            if (startingPosition.isLeft()) {
                robot.drive.setPose(new Vector2d(36.24, 62.76), Inches, 270.00, Degrees);
            } else { // this is always right, isn't that right, Mr Wright?
                // Get a bit closer to the basket so our robot doesn't just beeline towards it, likely ramming into someone else's robot
                robot.drive.makeTrajectory(new Vector2d(-35.76, 61.30), Inches, 270.00, Degrees)
                        .splineTo(new Vector2d(-8.51, 41.35), Inches, -46.42, Degrees)
                        .splineTo(new Vector2d(26.51, 37.95), Inches, 1.62, Degrees)
                        .addTask();
            }

            robot.drive.makeTrajectory()
                    .splineTo(new Vector2d(53.03, 54.00), Inches, 40.00, Degrees)
                    .addTask();

            // todo: who knows if this will work lmaooooooooooooooooooooooooooooooo
            add(new BasketPlacer(robot.verticalLift, robot.basketRotator, robot.drive));

            robot.drive.makeTrajectory()
                    .strafeToLinearHeading(new Vector2d(26.51, 24.81), Inches, 270.00, Degrees)
                    .addTask();
        }
    }
}
