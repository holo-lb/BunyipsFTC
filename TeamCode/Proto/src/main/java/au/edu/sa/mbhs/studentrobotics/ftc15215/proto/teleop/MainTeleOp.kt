package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.teleop

import au.edu.sa.mbhs.studentrobotics.bunyipslib.CommandBasedBunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Sound
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.HolonomicDriveTask
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.HolonomicVectorDriveTask
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.bases.Task.Companion.default
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls.Companion.rising
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.R
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

/**
 * Primary TeleOp for Proto.
 *
 * @author Lucas Bubner, 2024
 */
@TeleOp(name = "TeleOp")
open class MainTeleOp : CommandBasedBunyipsOpMode() {
    protected val robot = Proto()
    private val boom = Sound(R.raw.vineboom)

    override fun onInitialise() {
        robot.init()
    }

    override fun assignCommands() {
        robot.drive default HolonomicVectorDriveTask(gamepad1, robot.drive)
        driver() whenPressed Controls.BACK run HolonomicDriveTask(gamepad1, robot.drive) finishIf { gamepad1 rising Controls.BACK }

        robot.clawLift default robot.clawLift.tasks.control { -gamepad2.lsy.toDouble() }
        robot.clawRotator default robot.clawRotator.tasks.controlDelta { gamepad2.rsy.toDouble() * 0.75f * (timer.deltaTime() to Seconds) }
        operator() whenPressed Controls.A run robot.claws.tasks.toggleBoth()
        operator() whenRising (Controls.Analog.RIGHT_TRIGGER to { v -> v == 1.0f }) run robot.clawLift.tasks.home() finishIf { gamepad2.lsy != 0.0f }
    }

    override fun periodic() {
        if (gamepad2.getDebounced(Controls.A))
            boom.play()
    }
}