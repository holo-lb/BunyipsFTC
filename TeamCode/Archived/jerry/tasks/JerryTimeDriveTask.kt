package org.murraybridgebunyips.jerry.tasks

import org.murraybridgebunyips.bunyipslib.drive.CartesianMecanumDrive
import org.murraybridgebunyips.bunyipslib.external.units.Measure
import org.murraybridgebunyips.bunyipslib.external.units.Time
import org.murraybridgebunyips.bunyipslib.tasks.bases.Task

/**
 * Base drive task which will run Cartesian XYR speed for a given time
 * Only used for tests and as a failsafe, do not use in actual OpMode as field positioning data is lost
 * Use JerryPrecisionDriveTask instead!
 * @see JerryPrecisionDriveTask
 */
class JerryTimeDriveTask(
    time: Measure<Time>,
    private val drive: CartesianMecanumDrive?,
    private val x: Double,
    private val y: Double,
    private val r: Double
) : Task(time) {
    override fun periodic() {
        drive?.setSpeedXYR(x, y, r)
        drive?.update()
    }

    override fun isTaskFinished(): Boolean {
        return false
    }

    override fun onFinish() {
        drive?.stop()
    }
}