package org.firstinspires.ftc.teamcode.common.tasks

import org.firstinspires.ftc.teamcode.common.BunyipsOpMode

/**
 * Task to wait for a specific amount of time.
 */
class WaitTask(opMode: BunyipsOpMode, time: Double) : Task(opMode, time), AutoTask {
    override fun run() {
        opMode.addTelemetry("Waiting % seconds...", time)
        opMode.idle()
    }

    override fun onFinish() {
        return
    }
}