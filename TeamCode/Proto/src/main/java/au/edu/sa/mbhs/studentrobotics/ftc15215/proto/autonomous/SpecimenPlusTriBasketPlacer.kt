package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.autonomous

import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.Mathf.degToRad
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Unit.Companion.of
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.bases.Task.Companion.task
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueLeft
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redLeft
import au.edu.sa.mbhs.studentrobotics.bunyipslib.util.Geometry
import com.acmerobotics.roadrunner.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlin.math.PI

/**
 * Variant of QuadBasketPlacer that uses a TriBasket instead of a QuadBasket, allowing time to place
 * a preloaded SPECIMEN on the left side of the rung.
 *
 * @author Lucas Bubner, 2024
 */
@Autonomous(name = "1+3 Preload Specimen and Triple Basket (Left, L1 Asc.)")
class SpecimenPlusTriBasketPlacer : QuadBasketPlacer() {
    override fun onInitialise() {
        robot.init()
        robot.clawLift.withTolerance(25)
        setOpModes(
            blueLeft().tile(2.5).backward(1 of Inches),
            redLeft().tile(2.5).backward(1 of Inches)
        )
    }

    private val offset = 1.2
    override val waypoints = listOf(
        Pose2d(37.87 - offset, 36.19 + offset, (-54.9).degToRad()),
        Pose2d(47.67 - offset, 35.12 + offset, (-49.6).degToRad()), // may need adjustments
        Pose2d(64.5 - offset, 39.4 + offset, (-75.0).degToRad()),
    )
    override val basket = Pose2d(54.1, 53.1, PI / 4)

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        super.onReady(selectedOpMode, selectedButton)

        // Clear initial tasks to go to basket
        repeat(4) { removeFirst() }

        val placing = Pose2d(7.42, 32.0, 3 * PI / 2)
        // Must populate tasks backwards
        addFirst(
            task {
                named("Move Backwards")
                timeout(0.4 of Seconds)
                init { robot.drive.setPower(Geometry.vel(-1.0, 0.0, 0.0)) }
                onFinish { robot.drive.setPower(Geometry.zeroVel()) }
            }
        )
        addFirst(robot.clawLift.tasks.goTo(750).timeout(2 of Seconds)
            .with(robot.claws.tasks.openBoth().after(0.4 of Seconds)))
        addFirst(robot.drive.makeTrajectory(map)
                .splineToConstantHeading(placing.position, tangent = placing.heading)
                .build()
                .with(robot.clawLift.tasks.goTo(1700).timeout(2 of Seconds)))
    }
}