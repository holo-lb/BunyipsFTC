package org.murraybridgebunyips.wheatley.autonomous.arm;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Centimeters;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.murraybridgebunyips.bunyipslib.tasks.RoadRunnerTask;

/**
 * Places two pixels on the backboard and parks on the right side of the backboard.
 * Extends off of LeftClaw to keep the code dry.
 *
 * @author Lachlan Paul, 2024
 */
@Autonomous(name = "Right Claw Auto")
public class WheatleyRightClawAuto extends WheatleyLeftClawAuto {
    protected int STRAFE_TO_BACKBOARD = -34;

    /**
     * Parks to the right of the backboard.
     *
     * @param builder builds the path
     * @return the path as a task
     */
    @Override
    public RoadRunnerTask park(RoadRunnerTrajectoryTaskBuilder builder) {
        return builder
                .strafeRight(72, Centimeters)
                .forward(50, Centimeters)
                .buildTask();
    }
}
