package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.teleop

import au.edu.sa.mbhs.studentrobotics.bunyipslib.CommandBasedBunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Scheduler.Companion.rising
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.HolonomicDriveTask
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.HolonomicVectorDriveTask
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.bases.Task.Companion.default
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto

/**
 * Primary TeleOp for Proto.
 *
 * @author Lucas Bubner, 2024
 */
class MainTeleOp : CommandBasedBunyipsOpMode() {
    private val robot = Proto()

    override fun onInitialise() {
        robot.init()
    }

    override fun assignCommands() {
        robot.drive default HolonomicVectorDriveTask(gamepad1, robot.drive)
        robot.clawLift default robot.clawLift.tasks.control { -gamepad1.lsy.toDouble() }
        robot.ascent default robot.ascent.tasks.control { -gamepad1.rsy.toDouble() }
        robot.clawRotator default robot.clawRotator.tasks.controlPosition { gamepad1.rt.toDouble() }

        operator() whenPressed Controls.A run robot.claws.tasks.toggleBoth()
        driver() whenPressed Controls.BACK run HolonomicDriveTask(gamepad1, robot.drive) finishIf { gamepad1 rising Controls.BACK }
    }
}