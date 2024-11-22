package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.debug

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.pid.PController
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.MoveToContourTask
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.intothedeep.BlueSample
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
class TestVision : AutonomousBunyipsOpMode() {
    private val robot = Proto()
    private val bs = BlueSample()

    override fun onInitialise() {
        robot.init()
        robot.camera.init(bs).start(bs).flip().startPreview()
        MoveToContourTask.DEFAULT_X_CONTROLLER = PController(0.25)
        MoveToContourTask.DEFAULT_R_CONTROLLER = PController(0.25)
    }

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        add(MoveToContourTask(robot.drive) { bs.data })
    }
}