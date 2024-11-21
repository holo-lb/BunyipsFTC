package org.murraybridgebunyips.pbody.autonomous;


import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTiles;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.murraybridgebunyips.bunyipslib.tasks.RoadRunnerTask;

/**
 * Primary Autonomous OpMode (Right Park)
 */
@Autonomous(name = "Backdrop Placer (Right Park)")
public class PbodyBackdropPlacerRightPark extends PbodyBackdropPlacerLeftPark {
    @Override
    protected RoadRunnerTask afterPixelDropDriveAction(RoadRunnerTrajectoryTaskBuilder builder) {
        return builder
                .strafeRight(0.95 * FIELD_TILE_SCALE, FieldTiles)
                .buildTask();
    }
}
