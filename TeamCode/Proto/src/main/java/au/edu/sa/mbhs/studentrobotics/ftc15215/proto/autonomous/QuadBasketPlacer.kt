package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.autonomous

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.Mathf.degToRad
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Unit.Companion.of
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.SymmetricPoseMap
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.ParallelTaskGroup
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueLeft
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redLeft
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Constants
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.acmerobotics.roadrunner.IdentityPoseMap
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.PoseMap
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlin.math.PI

/**
 * 0+4 Autonomous for Neutral Samples and Preload.
 *
 * @author Lucas Bubner, 2024
 */
@Autonomous(name = "0+4 Quad Basket Placer (Left, L1 Asc.)")
open class QuadBasketPlacer : AutonomousBunyipsOpMode() {
    protected val robot = Proto()
    protected lateinit var map: PoseMap

    override fun onInitialise() {
        robot.init()

        setOpModes(
            blueLeft().tile(2.0).backward(1 of Inches).rotate(90 of Degrees),
            redLeft().tile(2.0).backward(1 of Inches).rotate(90 of Degrees)
        )

        robot.clawLift.withTolerance(20)

//        setInitTask(SwitchableLocalizer(robot.drive.localizer, robot.drive.localizer).tasks.manualTestMainLocalizer())
    }

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        if (selectedOpMode == null) return
        val startLocation = selectedOpMode.require() as StartingConfiguration.Position
        robot.drive.pose = startLocation.toFieldPose()

        // there aren't actually any changes
        map = if (startLocation.isRed) SymmetricPoseMap() else IdentityPoseMap()
        val basket = Pose2d(54.6, 53.6, PI / 4)
        val basketTarget = Constants.cl_MAX.toInt() - 600
        val waypoints = listOf(
            Pose2d(37.87, 36.19, (-54.9).degToRad()),
            Pose2d(46.9, 35.43, (-49.6).degToRad()),
            Pose2d(51.4, 23.88, 0.0),
        )

        // Navigate to the basket and lift up the vertical lift at the same time
        add(
            ParallelTaskGroup(
                robot.drive.makeTrajectory(map)
                    .strafeToLinearHeading(basket.position, heading = basket.heading)
                    .build(),
                robot.clawLift.tasks.goTo(basketTarget) timeout (3 of Seconds)
            )
        )
        // Angle claw rotator down and drop sample
        add(robot.clawRotator.tasks.setTo(0.2).forAtLeast(0.5, Seconds))
        add(robot.claws.tasks.openBoth().forAtLeast(0.1, Seconds))
        add(robot.clawRotator.tasks.close().forAtLeast(0.5, Seconds))

        for (i in waypoints.indices) {
            // Begin moving to the sample
            add(
                ParallelTaskGroup(
                    robot.drive.makeTrajectory(basket, map)
                        .strafeToLinearHeading(waypoints[i].position, heading = waypoints[i].heading)
                        .build(),
                    robot.clawRotator.tasks.setTo(0.8).after(0.5, Seconds),
                    robot.clawLift.tasks.home(),
                )
            )
            // Yoink
            add(robot.clawRotator.tasks.open().forAtLeast(0.3, Seconds))
            add(robot.claws.tasks.closeBoth().forAtLeast(0.2, Seconds))
            add(robot.clawRotator.tasks.close())
            // Go back and place
            add(
                ParallelTaskGroup(
                    robot.drive.makeTrajectory(waypoints[i], map)
                        .strafeToLinearHeading(basket.position, heading = basket.heading)
                        .build(),
                    robot.clawLift.tasks.goTo(basketTarget) timeout (3 of Seconds)
                )
            )
            add(robot.clawRotator.tasks.setTo(0.2).forAtLeast(0.5, Seconds))
            add(robot.claws.tasks.openBoth().forAtLeast(0.1, Seconds))
            add(robot.clawRotator.tasks.close().forAtLeast(0.5, Seconds))
        }

        add(
            robot.drive.makeTrajectory(Pose2d(54.6, 53.6, Math.PI / 4), map)
                .setReversed(true)
                .splineTo(Vector2d(48.13, 11.09), tangent = 0.0)
                .lineToX(20.0)
                .build()
                .with(
                    robot.clawLift.tasks.goTo(1600) timeout (3 of Seconds),
                    robot.clawRotator.tasks.setTo(0.2)
                )
        )
    }
}
