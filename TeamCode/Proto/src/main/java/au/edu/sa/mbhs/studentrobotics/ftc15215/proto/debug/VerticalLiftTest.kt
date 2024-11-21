package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.debug

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.hardware.Motor
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Test Vertical Lift", group = "a")
@Disabled
class VerticalLiftTest : BunyipsOpMode() {
    private val robot = Proto()

    override fun onInit() {
        robot.init()
    }

    override fun activeLoop() {
        robot.clawLift.setPower(-gamepad1.lsy.toDouble())
        robot.clawLift.update()
        Motor.debug(robot.hw.clawLift!!, "Claw Lift", t)
    }
}