package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.autonomous

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Unit.Companion.of
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.ParallelTaskGroup
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueRight
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlin.math.PI

@Autonomous(name = "?+0 Specimen Placer (Center, Right)")
class SpecimenPlacer : AutonomousBunyipsOpMode() {
    private val robot = Proto()

    override fun onInitialise() {
        robot.init()
        robot.clawLift.withTolerance(20, true)
    }

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        robot.drive.pose = blueRight().tile(3.5).backward(5 of Inches).build().toFieldPose()
        add(robot.claws.tasks.closeBoth())
        add(ParallelTaskGroup(
            robot.drive.makeTrajectory()
                .setVelConstraints { _, _, s ->
                    if (s > 20)
                        1.0
                    else
                        40.0
                }
                .lineToY(38.0)
                .build(),
            robot.clawLift.tasks.goTo(2100) // TODO: lower slightly to avoid going over
        ))
        add(robot.clawRotator.tasks.setTo(0.05).forAtLeast(Seconds.of(0.5)))
        add(robot.clawLift.tasks.goTo(1050).withTimeout(Seconds.of(2.0))
            .with(robot.claws.tasks.openBoth().after(0.3 of Seconds).with(robot.clawRotator.tasks.open())))

        add(robot.drive.makeTrajectory(Pose2d(0.0, 38.0, 3 * PI / 2))
            .setReversed(true)
            .splineToConstantHeading(Vector2d(-36.7, 26.8), tangent = 3 * PI / 2)
            .splineToConstantHeading(Vector2d(-47.0, 12.0), tangent = PI)
            .setTangent(3 * PI / 2)
            .setReversed(true)
            .lineToY(56.0)
            .setReversed(false)
            .splineToConstantHeading(Vector2d(-56.0, 12.0), tangent = PI)
            .setReversed(true)
            .lineToY(56.0)
            .setReversed(false)
            .splineToConstantHeading(Vector2d(-62.0, 12.0), tangent = PI)
            .setReversed(true)
            .lineToY(56.0)
            .build()
            .with(robot.clawLift.tasks.home().after(0.5 of Seconds)))

        robot.drive.makeTrajectory(Pose2d(-62.0, 56.0, 3 * PI / 2))
            .strafeToLinearHeading(Vector2d(-47.0, 48.0), heading = PI / 2)
            .addTask()
        add(robot.claws.tasks.openBoth())
        add(robot.clawRotator.tasks.setTo(0.1).forAtLeast(0.4, Seconds)) // TODO: find proper specimen angle
        add(robot.claws.tasks.closeBoth().forAtLeast(0.2, Seconds))
        add(robot.clawRotator.tasks.open())
    }
}