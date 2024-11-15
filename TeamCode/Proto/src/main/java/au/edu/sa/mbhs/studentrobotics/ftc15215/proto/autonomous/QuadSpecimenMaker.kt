package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.autonomous

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Unit.Companion.of
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.SymmetricPoseMap
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueRight
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redRight
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.acmerobotics.roadrunner.IdentityPoseMap
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous

/**
 * Pushes 4 ALLIANCE-SPECIFIC SPECIMENS to the OBSERVATION ZONE for the HUMAN PLAYER.
 * Hopefully someone will be able to use these SPECIMENS...
 *
 * @author Lucas Bubner, 2024
 */
@Autonomous(name = "0+0 Quad Specimen Maker, Right")
class QuadSpecimenMaker : AutonomousBunyipsOpMode() {
    private val robot = Proto()

    override fun onInitialise() {
        robot.init()

        setOpModes(
            blueRight().tile(2.5).backward(5 of Inches).rotate(-90 of Degrees),
            redRight().tile(2.5).backward(5 of Inches).rotate(-90 of Degrees),
        )
    }

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        if (selectedOpMode == null) return
        val startLocation = selectedOpMode.require() as StartingConfiguration.Position
        robot.drive.pose = startLocation.toFieldPose()

        robot.drive.makeTrajectory(
            Pose2d(-28.0, 60.0, 0.0),
            if (startLocation.isRed) SymmetricPoseMap() else IdentityPoseMap()
        )
            .lineToX(-38.0)
            .setTangent(3 * Math.PI / 2)
            .splineToLinearHeading(Pose2d(-35.0, 31.0, 0.0), tangent = 3 * Math.PI / 2)
            .splineToLinearHeading(Pose2d(-44.0, 13.0, 3 * Math.PI / 2), tangent = Math.PI)
            .setReversed(true)
            .lineToY(56.0)
            .setReversed(false)
            .splineToConstantHeading(Vector2d(-56.0, 12.0), tangent = Math.PI)
            .setReversed(true)
            .lineToY(56.0)
            .setReversed(false)
            .splineToConstantHeading(Vector2d(-62.0, 12.0), tangent = Math.PI)
            .setReversed(true)
            .lineToY(56.0)
            .addTask()
    }
}