package org.murraybridgebunyips.glados.autonomous.l5.composite;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTile;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.murraybridgebunyips.bunyipslib.tasks.RoadRunnerTask;

/**
 * Composite OpMode of l4, l3, and l1 OpModes.
 * Park right instead of left.
 *
 * @author Lucas Bubner, 2024
 */
@Autonomous(name = "Ultimate Preload (Purple on Left, Yellow on Right, Right Park)", group = "L5")
@Disabled
public class GLaDOSUltimatePreloadRightPark extends GLaDOSUltimatePreloadLeftPark {
    @Override
    protected RoadRunnerTask afterPixelDropDriveAction(RoadRunnerTrajectoryTaskBuilder builder) {
        return builder
                .strafeRight(0.95 * FIELD_TILE_SCALE, FieldTile)
                .buildTask();
    }
}
