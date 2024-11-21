package org.murraybridgebunyips.jerry.tasks

import org.murraybridgebunyips.bunyipslib.external.units.Measure
import org.murraybridgebunyips.bunyipslib.external.units.Time
import org.murraybridgebunyips.bunyipslib.tasks.bases.Task
import org.murraybridgebunyips.jerry.components.JerryLift

/**
 * Autonomous operation lift control task for Jerry robot.
 * Takes in a desired percentage of arm extension.
 */
class JerryLiftTask(
    time: Measure<Time>,
    private val lift: JerryLift?,
    private val percent: Int,
    private val power: Double? = null,
) : Task(time) {

    override fun init() {
        if (power != null) {
            lift?.power = power
        }
        lift?.set(percent)
    }

    override fun isTaskFinished(): Boolean {
        return lift?.isBusy() == false
    }

    override fun periodic() {
        lift?.update()
    }
}