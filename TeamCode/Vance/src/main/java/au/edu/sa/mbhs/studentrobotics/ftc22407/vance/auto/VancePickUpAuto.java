package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.auto;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;

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

/**
 * Auto that picks up extra scoring elements
 *
 * @author Lachlan Paul, 2024
 */
@Autonomous
public class VancePickUpAuto extends AutonomousBunyipsOpMode {
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

//        waitMessage = new MessageTask(Seconds.of(10), "<style=\"color:red;\">DO NOT PANIC because the robot isn't moving, it is waiting for others to move</>");
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

            } else { // always right

            }

        } else {  // always blue
            if (startingPosition.isLeft()) {

            } else { // this is always right, isn't that right, Mr Wright?
                robot.drive.makeTrajectory(new Vector2d(34.91, 61.78), Inches, 270.00, Degrees)
                        .splineTo(new Vector2d(55.24, 55.48), Inches, 40.00, Degrees)
                        .strafeToLinearHeading(new Vector2d(48.16, 39.13), Inches, 270.00, Degrees)
                        .strafeToLinearHeading(new Vector2d(56.44, 50.26), Inches, 58.39, Degrees)
                        .strafeToLinearHeading(new Vector2d(57.62, 37.33), Inches, 270.00, Degrees)
                        .strafeToLinearHeading(new Vector2d(58.01, 53.24), Inches, 65.0, Degrees)
//                .strafeToLinearHeading(new Vector2d(58.80, 54.27), Inches, 90.00, Degrees)
//                .strafeToLinearHeading(new Vector2d(58.01, 27.87), Inches, 0.00, Degrees)
                        .strafeToLinearHeading(new Vector2d(27.68, 10.74), Inches, 235.43, Degrees)
                        .addTask();
            }
        }
    }
}
