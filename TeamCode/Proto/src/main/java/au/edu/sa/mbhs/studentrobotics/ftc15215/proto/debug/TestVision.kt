package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.debug

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.pid.PController
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.MoveToContourTask
import au.edu.sa.mbhs.studentrobotics.bunyipslib.util.Tasks
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.intothedeep.BlueSample
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
@Disabled
class TestVision : BunyipsOpMode() {
    private val robot = Proto()
    private val bs = BlueSample()

    override fun onInit() {
        robot.init()
        robot.camera.init(bs).start(bs).flip().startPreview()
        MoveToContourTask.DEFAULT_X_CONTROLLER = PController(0.05)
        MoveToContourTask.DEFAULT_R_CONTROLLER = PController(0.1)
        Tasks.register(MoveToContourTask(robot.drive) { bs.data }.withForwardErrorSupplier {
            t.add(it.areaPercent)
            8.0 - it.areaPercent
        })
    }

    override fun activeLoop() {
        Tasks.runRepeatedly(0)
        robot.drive.update()
    }
}