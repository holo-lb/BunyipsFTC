package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.auto;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Milliseconds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.IdentityPoseMap;
import com.acmerobotics.roadrunner.PoseMap;
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
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks.PickUpSample;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks.TransferSample;

/**
 * Auto that scores a pre load and three extra scoring elements
 * Name shamelessly stolen from the Bunyips
 *
 * @author Lachlan Paul, 2024
 */

@Autonomous
public class VanceQuadBasketPlacer extends AutonomousBunyipsOpMode {
    protected final Vance robot = new Vance();
    private MessageTask waitMessage;
    private Vector2d basketPlacerPos;
    private PoseMap currentPoseMap;
    final int rightSampleXPos = -48;

    private void placeInScoringBasket() {
        add(new BasketPlacer(robot.verticalLift, robot.basketRotator, robot.drive));
        wait(300, Milliseconds);
    }

    private void placeInRobotBasket() {
        add(new PickUpSample(robot.horizontalLift, robot.claws, 100));
        wait(300, Milliseconds);
        add(new TransferSample(robot.verticalLift, robot.horizontalLift, robot.clawRotator, robot.basketRotator, robot.claws));
    }

    private void acquireSampleAndPlace() {
        // might combine later idk still deciding
        placeInRobotBasket();
        robot.drive.makeTrajectory()
                .strafeToSplineHeading(basketPlacerPos, Inches, 230.00, Degrees)
                .addTask();
        placeInScoringBasket();
    }

    @Override
    protected void onInitialise() {
        robot.init();
        robot.verticalLift.withTolerance(25, true);

        setOpModes(
                StartingConfiguration.redLeft().tile(2),
                StartingConfiguration.blueLeft().tile(2)
        );

        basketPlacerPos = new Vector2d(-57.13, -56.35);
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @NonNull Controls selectedButton) {
        if (selectedOpMode == null) return;
        StartingConfiguration.Position startingPosition = (StartingConfiguration.Position) selectedOpMode.require();
        currentPoseMap = startingPosition.isBlue() ? new SymmetricPoseMap() : new IdentityPoseMap();

        robot.drive.setPose(startingPosition.toFieldPose());

        robot.drive.makeTrajectory(new Vector2d(-36.26, -69.39), Inches, 90.00, Degrees, currentPoseMap)
                .splineTo(basketPlacerPos, Inches, 230.00, Degrees)
                .addTask();

        placeInScoringBasket();

        robot.drive.makeTrajectory(basketPlacerPos, Inches, 230.00, Degrees, currentPoseMap)
                .strafeToLinearHeading(new Vector2d(rightSampleXPos, -38.87), Inches, 90.00, Degrees)
                .strafeToSplineHeading(basketPlacerPos, Inches, 230.00, Degrees)
                .addTask();

//        placeInBasket();

        robot.drive.makeTrajectory(basketPlacerPos, Inches, 230.00, Degrees, currentPoseMap)
                .strafeToLinearHeading(new Vector2d(rightSampleXPos - 10, -38.87), Inches, 90.00, Degrees)
                .strafeToSplineHeading(basketPlacerPos, Inches, 230.00, Degrees)
                .addTask();

//        placeInBasket();

        robot.drive.makeTrajectory(basketPlacerPos, Inches, 230.00, Degrees, currentPoseMap)
                // We need to grab the last sample at an angle so we need to use different positions
                // We could just grab all of them at an angle like the Bunyips but uuhhhhhhhhh i dont wanna rewrite this
                .strafeToSplineHeading(new Vector2d(-55, -25.7), Inches, 180, Degrees)
                .strafeToSplineHeading(basketPlacerPos, Inches, 230.00, Degrees)
                .addTask();

//        placeInBasket();
        // todo: figure out if we should park, sit in place, or both
    }
}
