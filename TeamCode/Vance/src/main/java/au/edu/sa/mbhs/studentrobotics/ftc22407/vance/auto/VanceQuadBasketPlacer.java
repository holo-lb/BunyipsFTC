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
    final int rightSampleXPos = -46;
    final int rightSampleYPos = -48;
    final int pickUpSamplePos = 247;

    private void placeInScoringBasket() {
        add(new BasketPlacer(robot.verticalLift, robot.basketRotator, robot.drive));
    }

    private void acquireSampleAndPlace() {
        // Place in robot basket
        add(new PickUpSample(robot.horizontalLift, robot.claws, pickUpSamplePos));
        wait(300, Milliseconds);
        add(new TransferSample(robot.verticalLift, robot.horizontalLift, robot.clawRotator, robot.basketRotator, robot.claws, false));

        add(robot.drive.makeTrajectory(currentPoseMap)
                .strafeToSplineHeading(basketPlacerPos, Inches, 230.00, Degrees)
                .build().with(robot.verticalLift.tasks.goTo(800)));

        placeInScoringBasket();
    }

    @Override
    protected void onInitialise() {
        robot.init();
        robot.verticalLift.withTolerance(35);

        setOpModes(
                StartingConfiguration.redLeft().tile(2).backward(Inches.of(5)).rotate(Degrees.of(90)),
                StartingConfiguration.blueLeft().tile(2).backward(Inches.of(5)).rotate(Degrees.of(90))
        );

        basketPlacerPos = new Vector2d(-60, -60);
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @NonNull Controls selectedButton) {
        if (selectedOpMode == null) return;
        StartingConfiguration.Position startingPosition = (StartingConfiguration.Position) selectedOpMode.require();
        currentPoseMap = startingPosition.isBlue() ? new SymmetricPoseMap() : new IdentityPoseMap();

        robot.drive.setPose(startingPosition.toFieldPose());
        add(robot.claws.tasks.openBoth());
        add(robot.verticalLift.tasks.home());
        add(robot.horizontalLift.tasks.home());

        add(robot.drive.makeTrajectory(new Vector2d(-38.12, -62.74), Inches, 180.00, Degrees, currentPoseMap)
                .strafeToLinearHeading(basketPlacerPos, Inches, 230.00, Degrees)
                .build().with(robot.verticalLift.tasks.goTo(800)));

        placeInScoringBasket();

        add(robot.drive.makeTrajectory(basketPlacerPos, Inches, 230.00, Degrees, currentPoseMap)
                .strafeToSplineHeading(new Vector2d(rightSampleXPos, rightSampleYPos), Inches, 90.00, Degrees)
                .build().with(robot.verticalLift.tasks.home()));

        acquireSampleAndPlace();

        add(robot.drive.makeTrajectory(basketPlacerPos, Inches, 230.00, Degrees, currentPoseMap)
                .strafeToLinearHeading(new Vector2d(rightSampleXPos - 10, rightSampleYPos), Inches, 90.00, Degrees)
                .build().with(robot.verticalLift.tasks.home()));

        acquireSampleAndPlace();

        robot.drive.makeTrajectory(basketPlacerPos, Inches, 230.00, Degrees, currentPoseMap)
                // We need to grab the last sample at an angle so we need to use different positions
                // We could just grab all of them at an angle like the Bunyips but uuhhhhhhhhh i dont wanna rewrite this
                .strafeToSplineHeading(new Vector2d(-55, -25.7), Inches, 180, Degrees)
                .addTask();

        acquireSampleAndPlace();
        // todo: figure out if we should park, sit in place, or both
    }
}
