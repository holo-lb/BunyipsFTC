package org.firstinspires.ftc.teamcode.jerry.tasks

import org.firstinspires.ftc.teamcode.common.BunyipsOpMode
import org.firstinspires.ftc.teamcode.common.IMUOp
import org.firstinspires.ftc.teamcode.common.tasks.Task
import org.firstinspires.ftc.teamcode.common.tasks.TaskImpl
import org.firstinspires.ftc.teamcode.jerry.components.JerryDrive

class JerryIMURotationTask(
    opMode: BunyipsOpMode,
    time: Double,
    private val imu: IMUOp,
    private val drive: JerryDrive,
    private val angle: Double,
    private val speed: Double
) : Task(opMode, time), TaskImpl {
    // Enum to find out which way we need to be turning
    var direction: Direction? = null

    enum class Direction {
        LEFT, RIGHT
    }

    override fun init() {
        super.init()
        imu.tick()

        val currentAngle = imu.heading
        // If we can't get angle info, then terminate task as we can't do anything
        if (currentAngle == null) {
            taskFinished = true
            return
        }

        // Find out which way we need to turn based on the information provided
        direction = if (currentAngle < angle && angle <= 180) {
            // Faster to turn right to get to the target. If the desired angle is 180 degrees,
            // will also turn right (as it is equal, just mere preference)
            Direction.RIGHT
        } else {
            // Faster to turn left to get to the target
            Direction.LEFT
        }
    }

    override fun run() {
        when (direction) {
            Direction.LEFT -> drive.setSpeedXYR(0.0, 0.0, -speed)
            Direction.RIGHT -> drive.setSpeedXYR(0.0, 0.0, speed)
            else -> {}
        }
        imu.tick()
        drive.update()
    }
}