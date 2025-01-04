package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.debug

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.hardware.Motor
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Test Vertical Lift", group = "a")
@Disabled
class VerticalLiftTest : BunyipsOpMode() {
    override fun activeLoop() {
        Proto.clawLift.setPower(-gamepad1.lsy.toDouble())
        Proto.clawLift.update()
        Motor.debug(Proto.hw.clawLift!!, "Claw Lift", t)
    }
}