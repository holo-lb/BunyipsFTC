package org.murraybridgebunyips.glados.autonomous.l3;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTile;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.murraybridgebunyips.bunyipslib.tasks.RoadRunnerTask;

/**
 * Backdrop Placer Autonomous for Right Parking with AprilTag detection
 *
 * @author Lucas Bubner, 2024
 */
@Autonomous(name = "Backdrop Placer (Right Park, Vision)", group = "L3")
@Disabled
public class GLaDOSBackdropPlacerATRightPark extends GLaDOSBackdropPlacerATLeftPark {
    @Override
    protected RoadRunnerTask afterPixelDropDriveAction(RoadRunnerTrajectoryTaskBuilder builder) {
        return builder
                .strafeRight(0.95 * FIELD_TILE_SCALE, FieldTile)
                .buildTask();
    }
}
