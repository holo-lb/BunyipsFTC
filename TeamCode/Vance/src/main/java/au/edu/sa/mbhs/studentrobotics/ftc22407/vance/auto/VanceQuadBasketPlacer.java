package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.auto;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Milliseconds;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.IdentityPoseMap;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseMap;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.SymmetricPoseMap;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks.BasketPlacer;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks.PickUpSample;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks.TransferSample;

/**
 * Auto that scores a pre load and two extra scoring elements
 * Name shamlessly stolen from Bunyips.
 * <p></p>
 * WIP, if not finished use Tri version
 * @see VanceTriBasketPlacer
 *
 * @author Lachlan Paul, 2024
 */

@Autonomous
public class VanceQuadBasketPlacer extends AutonomousBunyipsOpMode {
    protected final Vance robot = new Vance();
    private Vector2d basketPlacerPos;
    private PoseMap currentPoseMap;
    final int rightSampleXPos = -50;
    final int rightSampleYPos = -45;
    final int pickUpSamplePos = 215;  // The amount of ticks the horizontal arm needs to go to pick up a sample
    final int preClawOut = pickUpSamplePos - 80;  // how far to go out while rotating the name sucks shut up
    final int armTimeout = 1200;

    private void acquireSampleAndPlace() {
        // Place in robot basket
        add(new PickUpSample(robot.horizontalLift, robot.claws, pickUpSamplePos));
        wait(250, Milliseconds);
        add(new TransferSample(robot.verticalLift, robot.horizontalLift, robot.clawRotator, robot.basketRotator, robot.claws, false));

        add(robot.drive.makeTrajectory(currentPoseMap) // FIXME: these trajectories are impicit meaning they are always built from the robot starting position...
                .strafeToSplineHeading(basketPlacerPos, Inches, 230.00, Degrees)
                .build().with(robot.verticalLift.tasks.goTo(830)));

        add(new BasketPlacer(robot.verticalLift, robot.basketRotator, robot.drive));
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
                .build().with(robot.verticalLift.tasks.goTo(830)));

        // Since we already have a preload, we only need to place the sample
        add(new BasketPlacer(robot.verticalLift, robot.basketRotator, robot.drive));

        add(robot.drive.makeTrajectory(basketPlacerPos, Inches, 230.00, Degrees, currentPoseMap)
                .strafeToSplineHeading(new Vector2d(rightSampleXPos + 1, rightSampleYPos), Inches, 90.00, Degrees)
                .build().with(robot.verticalLift.tasks.home(), robot.horizontalLift.tasks.goTo(preClawOut).after(Milliseconds.of(500)).timeout(Milliseconds.of(armTimeout))));

        acquireSampleAndPlace();

        add(robot.drive.makeTrajectory(basketPlacerPos, Inches, 230.00, Degrees, currentPoseMap)
                .strafeToLinearHeading(new Vector2d(rightSampleXPos - 10, rightSampleYPos), Inches, 85.00, Degrees) // FIXME: heading set to 85 as the robot seems to be out of tune as drifting 5 degrees, this needs a fix
                .build().with(robot.verticalLift.tasks.home(), robot.horizontalLift.tasks.goTo(preClawOut).after(Milliseconds.of(500)).timeout(Milliseconds.of(armTimeout))));

        acquireSampleAndPlace();

        add(robot.drive.makeTrajectory(basketPlacerPos, Inches, 230.00, Degrees, currentPoseMap)
                // We need to grab the last sample at an angle so we need to use different positions
                // We could just grab all of them at an angle like the Bunyips but uuhhhhhhhhh i dont wanna rewrite this
                //   from the bunyips: we rewrote it
                .strafeToSplineHeading(new Vector2d(-63.0, -40.0), Inches, 115, Degrees) //position altered to meet new claw pos
                .build().with(robot.verticalLift.tasks.home(), robot.horizontalLift.tasks.goTo(preClawOut).after(Milliseconds.of(500)).timeout(Milliseconds.of(armTimeout))));

        add(new PickUpSample(robot.horizontalLift, robot.claws, pickUpSamplePos));

        add(robot.drive.makeTrajectory(new Pose2d(-62.12, -40.0, Math.toRadians(115)), currentPoseMap)
                .setReversed(true)
                .splineTo(new Vector2d(-51.8, -44.3), Inches, 135.00, Degrees) // FIXME: weird oscillations for heading - retune from ManualFeedbackTuner?
                .setReversed(false)
                .strafeToSplineHeading(basketPlacerPos, Inches, 230.00, Degrees)
                .build()
                .with(new TransferSample(robot.verticalLift, robot.horizontalLift, robot.clawRotator, robot.basketRotator, robot.claws, false)
                        .after(1.0, Seconds)
                        .then(robot.verticalLift.tasks.goTo(830))));

        add(new BasketPlacer(robot.verticalLift, robot.basketRotator, robot.drive));
    }
}
