package org.murraybridgebunyips.jerry.tasks

import org.murraybridgebunyips.bunyipslib.drive.CartesianMecanumDrive
import org.murraybridgebunyips.bunyipslib.IMUOp
import org.murraybridgebunyips.bunyipslib.tasks.bases.RobotTask
import org.murraybridgebunyips.bunyipslib.tasks.bases.Task
import kotlin.math.abs

/**
 * Full-featured task for driving to a specific distance, with fail safes in case configuration is not available.
 * This supports movement throughout the 2D plane, and can be used to move in any one direction
 * 13/11/23: removed precision from precision drive as odometry is removed from Jerry now
 *
 * ** THIS SYSTEM IS DEPRECATED IN FAVOUR OF ROADRUNNER **
 * Autonomous movement should be done using RoadRunner, and future robots should try to be built
 * with RoadRunner as the path generation toolset. This task is only here for legacy purposes.
 * RoadRunner offers a much more robust and reliable path generation system, and is much more
 * accurate than the systems you're able to build by hand.
 *
 * @author Lucas Bubner, 2023
 */
class JerryPrecisionDriveTask(
    time: Double,
    private val drive: CartesianMecanumDrive?,
    private val imu: IMUOp?,
    // Odometry moved to other robots, removed from Jerry
//    private val x: Odometer?,
//    private val y: Odometer?,
//    private val distanceMM: Double,
    private val direction: Directions,
    private var power: Double,
    private val tolerance: Double = 3.0 // Optional tolerance can be specified if 3 degrees is inadequate
) : Task(time), RobotTask {

    init {
        try {
            assert(drive != null)
        } catch (e: AssertionError) {
            opMode.addTelemetry(
                "Failed to initialise a drive task as the drive system is unavailable.",
                true
            )
        }
        // Use absolute values of power to ensure that the robot moves correctly and is not fed with negative values
        // This is because the task will handle the power management and determine whether the value
        // to the motor should be negative or not
        this.power = abs(power)
    }

    enum class Directions {
        // Use RelativePose2d instead
        LEFT, RIGHT, FORWARD, BACKWARD
    }

    override fun isTaskFinished(): Boolean {
        // Check if the task is done by checking if it has timed out in the super call or if the target has been reached
        // by the respective deadwheel. If the deadwheel is not available, then we cannot check if the target has been
        // reached, so we will just rely on the timeout.
//        val evaluating = if (direction == Directions.LEFT || direction == Directions.RIGHT) {
//            x?.travelledMM()
//        } else {
//            y?.travelledMM()
//        }
//        return evaluating != null && evaluating >= distanceMM
        // Can only rely on timeout now
        return false
    }

    override fun init() {
        // Capture vectors and start tracking
        imu?.startCapture()
        // Deadwheels removed
//        x?.track()
//        y?.track()
    }

    override fun periodic() {
        drive?.setSpeedXYR(
            if (direction == Directions.LEFT) -power else if (direction == Directions.RIGHT) power else 0.0,
            if (direction == Directions.FORWARD) power else if (direction == Directions.BACKWARD) power else 0.0,
            imu?.getRPrecisionSpeed(0.0, tolerance) ?: 0.0
        )

        drive?.update()
        imu?.update()

        // Add telemetry of current operation
//        opMode.addTelemetry(
//            "Distance progress: ${
//                if (direction == Directions.LEFT || direction == Directions.RIGHT) {
//                    String.format("%.2f", x?.travelledMM())
//                } else {
//                    String.format("%.2f", y?.travelledMM())
//                }
//            }/$distanceMM"
//        )

        opMode.addTelemetry(
            "Axis correction: ${
                String.format("%.2f", imu?.capture?.minus(tolerance))
            } <= ${
                String.format("%.2f", imu?.heading)
            } <= ${
                String.format("%.2f", imu?.capture?.plus(tolerance))
            }"
        )

    }

    override fun onFinish() {
        drive?.stop()
    }
}