package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.debug

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.qualcomm.robotcore.eventloop.opmode.Disabled

/**
 * No-op to init hardware and print timer status.
 */
@TeleOp(name = "No-op", group = "a")
@Disabled
class Noop : BunyipsOpMode() {
    private val robot = Proto()

    override fun onInit() {
        robot.init()
    }

    override fun activeLoop() {
        telemetry.add(timer)
    }
}
