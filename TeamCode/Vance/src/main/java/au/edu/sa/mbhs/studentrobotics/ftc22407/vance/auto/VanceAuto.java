package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.auto;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.IdentityPoseMap;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.SymmetricPoseMap;
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
    protected final Vance robot = new Vance();
    private MessageTask waitMessage;
    private SymmetricPoseMap symmetricPoseMap;
    private boolean isRed;

    protected void park() {
        // for now we've decided to set up shop in front of the basket until auto is open
//        robot.drive.makeTrajectory(isRed ? symmetricPoseMap : new IdentityPoseMap())
//                .strafeToLinearHeading(new Vector2d(34.5, 10.0), Inches, 270.00, Degrees)
//                .strafeToLinearHeading(new Vector2d(27.0, 10.0), Inches, 270.00, Degrees)
//                .addTask();
    }

    // An autonomous period has started in FTC INTO THE DEEP
    // Start the initialisation!
    @Override
    protected void onInitialise() {
        robot.init();
        robot.verticalLift.withTolerance(25, true);

        setOpModes(
                StartingConfiguration.redLeft().tile(2),
                StartingConfiguration.redRight().tile(2),
                StartingConfiguration.blueLeft().tile(2),
                StartingConfiguration.blueRight().tile(2)
        );

        waitMessage = new MessageTask(Seconds.of(10), "<style=\"color:red;\">DO NOT PANIC because the robot isn't moving, it is waiting for others to move</>");
        symmetricPoseMap = new SymmetricPoseMap();
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @NonNull Controls selectedButton) {
        if (selectedOpMode == null) return;
        StartingConfiguration.Position startingPosition = (StartingConfiguration.Position) selectedOpMode.require();
        isRed = startingPosition.isRed();

        robot.drive.setPose(startingPosition.toFieldPose());

        if (startingPosition.isRight()) {
            // We need to wait for other robots when we're far away from the basket,
            // so we use a message task to delay our cross-country road trip
            add(waitMessage);

            // Get a bit closer to the basket so our robot doesn't just beeline towards it, likely ramming into someone else's robot
            robot.drive.makeTrajectory(/*new Vector2d(-35.76, 61.30), Inches, 270.00, Degrees,*/ isRed ? symmetricPoseMap : new IdentityPoseMap())
                    .splineTo(new Vector2d(-8.51, 41.35), Inches, -46.42, Degrees)
                    .splineTo(new Vector2d(26.51, 37.95), Inches, 1.62, Degrees)
                    .addTask();
        }

        // HEY!
        // Build the trajectory!
        robot.drive.makeTrajectory(isRed ? symmetricPoseMap : new IdentityPoseMap())
                .splineTo(new Vector2d(60.03, 59.00), Inches, 40.00, Degrees)
                .addTask();
        // And off to the scoring basket!

        // Prepare the vertical arm!
        // Score the pre-load!
        add(new BasketPlacer(robot.verticalLift, robot.basketRotator, robot.drive));

        // And park in the dedicated zone! (actually false here but shhhh i wanna keep the funny comments)
        park();

        // The new autonomous collection from MBHS Mulyawonks
    }
}
