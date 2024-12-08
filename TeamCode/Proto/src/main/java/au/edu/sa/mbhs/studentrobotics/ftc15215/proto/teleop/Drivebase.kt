package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.teleop

import au.edu.sa.mbhs.studentrobotics.bunyipslib.CommandBasedBunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.HolonomicDriveTask
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

/**
 * Drivebase and localizer only TeleOp with auto-lock.
 */
@TeleOp(name = "Drivebase Control")
@Disabled
class Drivebase : CommandBasedBunyipsOpMode() {
    private val robot = Proto()

    override fun onInitialise() {
        robot.init()
    }

    override fun assignCommands() {
        robot.drive.setDefaultTask(HolonomicDriveTask(gamepad1, robot.drive))
    }
}
