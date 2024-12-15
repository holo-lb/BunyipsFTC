package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.teleop

import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.NullLocalizer
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.HolonomicDriveTask
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.bases.Task.Companion.default
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

/**
 * Variant of [MainTeleOp] that does not use localization for improved raw control.
 *
 * @author Lucas Bubner, 2024
 */
@TeleOp(name = "TeleOp (No Localization)")
class MainTeleOpNoLocalizer : MainTeleOp() {
    override fun onInitialise() {
        super.onInitialise()
        robot.drive.localizer = NullLocalizer()
    }

    override fun assignCommands() {
        super.assignCommands()
        unbind(0)
        robot.drive default HolonomicDriveTask(gamepad1, robot.drive)
    }
}