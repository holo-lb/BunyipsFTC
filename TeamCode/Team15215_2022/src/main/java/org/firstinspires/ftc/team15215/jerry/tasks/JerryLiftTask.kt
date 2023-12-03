package org.firstinspires.ftc.team15215.jerry.tasks

import org.firstinspires.ftc.team15215.jerry.components.JerryLift
import org.firstinspires.ftc.teamcode.common.tasks.Task
import org.murraybridgebunyips.ftc.bunyipslib.BunyipsOpMode
import org.murraybridgebunyips.ftc.bunyipslib.tasks.AutoTask

/**
 * Autonomous operation lift control task for Jerry robot.
 * Takes in a desired percentage of arm extension.
 */
class JerryLiftTask(
    opMode: BunyipsOpMode,
    time: Double,
    private val lift: JerryLift?,
    private val percent: Int,
    private val power: Double? = null,
) : Task(opMode, time), AutoTask {

    override fun init() {
        if (power != null) {
            lift?.power = power
        }
        lift?.set(percent)
    }

    override fun isTaskFinished(): Boolean {
        return lift?.isBusy() == false
    }

    override fun run() {
        lift?.update()
    }

    override fun onFinish() {
        return
    }
}