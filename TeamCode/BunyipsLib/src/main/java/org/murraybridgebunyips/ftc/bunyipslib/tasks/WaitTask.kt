package org.firstinspires.ftc.teamcode.common.tasks

import org.murraybridgebunyips.ftc.bunyipslib.BunyipsOpMode
import org.murraybridgebunyips.ftc.bunyipslib.tasks.AutoTask

/**
 * Task to wait for a specific amount of time.
 */
class WaitTask(opMode: BunyipsOpMode, time: Double) : Task(opMode, time), AutoTask {
    override fun init() {
        return
    }

    override fun run() {
        opMode.addTelemetry("Waiting % seconds...", time)
        opMode.idle()
    }

    override fun isTaskFinished(): Boolean {
        return false
    }

    override fun onFinish() {
        return
    }
}