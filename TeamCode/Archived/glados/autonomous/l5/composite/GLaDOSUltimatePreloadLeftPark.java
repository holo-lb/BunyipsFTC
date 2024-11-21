package org.murraybridgebunyips.glados.autonomous.l5.composite;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Centimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Degrees;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTile;
import static org.murraybridgebunyips.glados.autonomous.l4.GLaDOSSpikeMarkPlacerAutonomous.ANGLED_INITIAL_FORWARD_DIST_FT;
import static org.murraybridgebunyips.glados.autonomous.l4.GLaDOSSpikeMarkPlacerAutonomous.M_FORWARD_DIST_CM;
import static org.murraybridgebunyips.glados.autonomous.l4.GLaDOSSpikeMarkPlacerAutonomous.M_FORWARD_INITIAL_FORWARD_DIST_FT;
import static org.murraybridgebunyips.glados.autonomous.l4.GLaDOSSpikeMarkPlacerAutonomous.M_LEFT_TURN_DEG;
import static org.murraybridgebunyips.glados.autonomous.l4.GLaDOSSpikeMarkPlacerAutonomous.M_RIGHT_TURN_DEG;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.murraybridgebunyips.bunyipslib.Direction;
import org.murraybridgebunyips.bunyipslib.tasks.RoadRunnerTask;
import org.murraybridgebunyips.bunyipslib.tasks.bases.Task;
import org.murraybridgebunyips.bunyipslib.tasks.groups.ParallelTaskGroup;
import org.murraybridgebunyips.glados.autonomous.l3.GLaDOSBackdropPlacerATLeftPark;

/**
 * Composite OpMode of l4, l3, and l1 OpModes.
 *
 * @author Lucas Bubner, 2024
 */
@Autonomous(name = "Ultimate Preload (Purple on Left, Yellow on Right, Left Park)", group = "L5")
@Disabled
public class GLaDOSUltimatePreloadLeftPark extends GLaDOSBackdropPlacerATLeftPark {
    /**
     * arm to ground from stow
     */
    public static int ARM_DELTA_GROUND = 2000;

    @Override
    protected void onStart() {
        super.onStart();

        RoadRunnerTask taskOneDrive = makeTrajectory(startingPosition.getPose())
                .forward(spikeMark == Direction.FORWARD ? M_FORWARD_INITIAL_FORWARD_DIST_FT : ANGLED_INITIAL_FORWARD_DIST_FT, FieldTile)
                .withName("Move Forward to Spike Marks")
                .buildTask();

        Task taskOne = new ParallelTaskGroup(
                arm.tasks.goTo(ARM_DELTA_GROUND).withName("Extend Arm"),
                taskOneDrive
        ).withName("Move to Spike Marks");

        RoadRunnerTask taskTwo;
        switch (spikeMark) {
            case FORWARD:
                taskTwo = makeTrajectory(taskOneDrive.getEndPose())
                        .forward(M_FORWARD_DIST_CM, Centimeters)
                        .withName("Align to Center Mark")
                        .buildTask();
                break;
            case LEFT:
                taskTwo = makeTrajectory(taskOneDrive.getEndPose())
                        .turn(M_LEFT_TURN_DEG, Degrees)
                        .withName("Rotate to Left Mark")
                        .buildTask();
                break;
            case RIGHT:
                taskTwo = makeTrajectory(taskOneDrive.getEndPose())
                        .turn(M_RIGHT_TURN_DEG, Degrees)
                        .withName("Rotate to Right Mark")
                        .buildTask();
                break;
            default:
                throw new IllegalStateException("should never happen");
        }

        Task taskThree = claws.tasks.openLeft().withName("Open Left Claw");
        Task taskFour = arm.tasks.delta(-ARM_DELTA_GROUND).withName("Retract Arm");

        // Add backwards to queue
        addTaskFirst(taskFour);
        addTaskFirst(taskThree);
        addTaskFirst(taskTwo);
        addTaskFirst(taskOne);
    }
}
