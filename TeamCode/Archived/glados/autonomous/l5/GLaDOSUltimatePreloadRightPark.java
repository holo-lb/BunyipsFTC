package org.murraybridgebunyips.glados.autonomous.l5;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.murraybridgebunyips.bunyipslib.Direction;

/**
 * @see GLaDOSUltimatePreloadLeftPark
 */
@Autonomous(name = "Ultimate Preload (Purple on Left, Yellow on Right, Right Park)", group = "L5")
public class GLaDOSUltimatePreloadRightPark extends GLaDOSUltimatePreloadLeftPark {
    @Override
    protected Direction getParkingDirection() {
        return Direction.RIGHT;
    }
}
