package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.debug

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Unit.Companion.of
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueLeft
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

/**
 * Null OpMode that reports the current robot position and can be pushed around the field.
 *
 * @author Lucas Bubner, 2024
 */
@TeleOp(name = "Pusher", group = "a")
@Disabled
class Pusher : BunyipsOpMode() {
    private val robot = Proto()

    override fun onInit() {
        robot.init()
        robot.drive.pose = blueLeft().tile(2.0).backward(1 of Inches).rotate(90 of Degrees).build().toFieldPose()
        robot.hw.bl?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        robot.hw.br?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        robot.hw.fl?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        robot.hw.fr?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
    }

    override fun activeLoop() {
        robot.drive.update()
    }
}