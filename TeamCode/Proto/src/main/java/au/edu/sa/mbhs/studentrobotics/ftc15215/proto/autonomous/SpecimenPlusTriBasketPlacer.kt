package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.autonomous

import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Unit.Companion.of
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueLeft
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redLeft
import com.acmerobotics.roadrunner.Vector2d
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

        setOpModes(
            blueLeft().tile(2.5).backward(1 of Inches),
            redLeft().tile(2.5).backward(1 of Inches)
        )

        robot.clawLift.withTolerance(25)
    }

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        super.onReady(selectedOpMode, selectedButton)

        // Clear initial tasks to go to basket
        repeat(4) { removeFirst() }

        // Must populate backwards
        addFirst(robot.clawLift.tasks.goTo(850).withTimeout(2 of Seconds)
            .with(robot.claws.tasks.openBoth().after(0.4 of Seconds)))
        addFirst(robot.drive.makeTrajectory(map)
                .splineToConstantHeading(Vector2d(7.42, 33.13), tangent = 3 * PI / 2)
                .build()
                .with(robot.clawLift.tasks.goTo(1700)))

        // TODO: consider starting location and how the inter-splice is handled
    }
}