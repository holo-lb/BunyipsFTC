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
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.acmerobotics.roadrunner.IdentityPoseMap
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlin.math.PI

/**
 * 0+4 Autonomous for Neutral Samples and Preload.
 *
 * @author Lucas Bubner, 2024
 */
@Autonomous(name = "0+4 Quad Basket Placer, Left")
class QuadBasketPlacer : AutonomousBunyipsOpMode() {
    private val robot = Proto()
    private val maxTicks = au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Constants.cl_MAX.toInt()

    override fun onInitialise() {
        robot.init()

        setOpModes(
            blueLeft().tile(2.0).backward(5 of Inches).rotate(90 of Degrees),
            redLeft().tile(2.0).backward(5 of Inches).rotate(90 of Degrees)
        )

        robot.clawLift.withTolerance(20, true)

//        setInitTask(SwitchableLocalizer(robot.drive.localizer, robot.drive.localizer).tasks.manualTestMainLocalizer())
    }

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        if (selectedOpMode == null) return
        val startLocation = selectedOpMode.require() as StartingConfiguration.Position
        robot.drive.pose = startLocation.toFieldPose()

        val waypoints = listOf(
            Pose2d(38.33, 40.02, 310.0.degToRad()),
            Pose2d(53.64, 42.77, (-70.0).degToRad()),
            Pose2d(58.69, 40.78, (-55.0).degToRad()),
        )

        // Reset position of claw rotator to be upright, as it might be slanted backwards for preloading
        add(robot.clawRotator.tasks.open())
        // Navigate to the basket and lift up the vertical lift at the same time
        add(
            ParallelTaskGroup(
                robot.drive.makeTrajectory(if (startLocation.isRed) SymmetricPoseMap() else IdentityPoseMap())
                    .strafeToLinearHeading(Vector2d(56.0, 56.0), heading = PI / 4)
                    .build(),
                robot.clawLift.tasks.goTo(maxTicks) timeout (3 of Seconds)
            )
        )
        // Angle claw rotator down and drop sample
        add(robot.clawRotator.tasks.setTo(0.8).forAtLeast(0.5, Seconds))
        add(robot.claws.tasks.openBoth().forAtLeast(0.1, Seconds))
        add(robot.clawRotator.tasks.open().forAtLeast(0.5, Seconds))

        for (i in waypoints.indices) {
            // Begin moving to the sample
            add(
                ParallelTaskGroup(
                    robot.drive.makeTrajectory(
                        Pose2d(55.0, 55.0, PI / 4),
                        if (startLocation.isRed) SymmetricPoseMap() else IdentityPoseMap()
                    )
                        .strafeToLinearHeading(waypoints[i].position, heading = waypoints[i].heading)
                        .build(),
                    robot.clawRotator.tasks.setTo(0.3).after(0.5, Seconds),
                    robot.clawLift.tasks.home(),
                )
            )
            // Yoink
            add(robot.clawRotator.tasks.close().forAtLeast(0.4, Seconds))
            add(robot.claws.tasks.closeBoth().forAtLeast(0.2, Seconds))
            add(robot.clawRotator.tasks.open())
            // Go back and place
            add(
                ParallelTaskGroup(
                    let {
                        val t = robot.drive.makeTrajectory(
                            waypoints[i],
                            if (startLocation.isRed) SymmetricPoseMap() else IdentityPoseMap()
                        )
                        if (i <= 1) {
                            t.strafeToLinearHeading(Vector2d(55.0, 55.0), heading = PI / 4)
                        } else {
                            // For the last trajectory only, back up while en route to the basket
                            t.setReversed(true)
                                .strafeToLinearHeading(Vector2d(50.0, 48.0), heading = 320.0.degToRad())
                                .splineToLinearHeading(Vector2d(55.0, 55.0), heading = PI / 4, tangent = PI / 4)
                        }
                        t.build()
                    },
                    robot.clawLift.tasks.goTo(maxTicks) timeout (3 of Seconds)
                )
            )
            add(robot.clawRotator.tasks.setTo(0.8).forAtLeast(0.5, Seconds))
            add(robot.claws.tasks.openBoth().forAtLeast(0.1, Seconds))
            add(robot.clawRotator.tasks.open().forAtLeast(0.5, Seconds))
        }

//        add(
//            robot.drive.makeTrajectory(Pose2d(55.0, 55.0, PI / 4))
//            .turnTo(225.0, Degrees)
//            .splineTo(Vector2d(24.26, 11.25), Inches, 180.00, Degrees)
//            .build()
//            .with(
//                robot.clawLift.tasks.goTo(250) timeout (3 of Seconds),
//                robot.clawRotator.tasks.setTo(0.1)
//            )
//        )
    }
}
