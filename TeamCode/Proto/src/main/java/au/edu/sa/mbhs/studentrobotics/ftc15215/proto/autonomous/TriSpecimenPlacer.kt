package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.autonomous

// giulio wass here and you dont know
import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.Mathf.degToRad
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Unit.Companion.of
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Milliseconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.SymmetricPoseMap
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.ParallelTaskGroup
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.SequentialTaskGroup
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueRight
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redRight
import au.edu.sa.mbhs.studentrobotics.bunyipslib.util.Ref.ref
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.acmerobotics.roadrunner.IdentityPoseMap
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.frozenmilk.util.cell.RefCell
import kotlin.math.PI

@Autonomous(name = "3+0 Specimen Placer (Right, Ob. Park)")
class TriSpecimenPlacer : AutonomousBunyipsOpMode() {
    override fun onInitialise() {
        Proto.clawLift.withTolerance(25)
        setOpModes(
            blueRight().tile(2.5).backward(1 of Inches),
            redRight().tile(2.5).backward(1 of Inches)
        )
    }

    override fun onReady(selectedOpMode: RefCell<*>?) {
        if (selectedOpMode == null) return
        val startLocation = selectedOpMode.get() as StartingConfiguration.Position

        val map = if (startLocation.isRed) SymmetricPoseMap() else IdentityPoseMap()
        val last = startLocation.toFieldPose().ref()
        Proto.drive.pose = last.get()

        val highChamberTicks = 1700
        val homeDelay = 0.5 of Seconds
        val wallPickup = Pose2d(-47.14, 56.5, PI / 2)
        var i = 0
        val wallPickupRoutine = {
            SequentialTaskGroup(
                Proto.claws.tasks.openBoth() named "Prepare Claws",
                Proto.clawRotator.tasks.setTo(0.45).forAtLeast(0.4, Seconds) named "Reach Specimen",
                Proto.claws.tasks.closeBoth().forAtLeast(0.2, Seconds) named "Grip Specimen",
                Proto.clawLift.tasks.goTo(550) named "Lift Specimen" timeout (2 of Seconds) forAtLeast (0.5 of Seconds),
            )
        }
        val hookSpecimenRoutine = {
            ParallelTaskGroup(
                Proto.clawLift.tasks.goTo(750).timeout(Seconds.of(2.0)) named "Hook Specimen",
                Proto.claws.tasks.openBoth().after(0.4 of Seconds) named "Release Specimen"
            )
        }
        val wallToSubmersibleRoutine = {
            val tb = if (i == 0) Proto.drive.makeTrajectory(map) else Proto.drive.makeTrajectory(last.get(), map)
            tb.setReversed(i >= 1)
                .splineToLinearHeading(Vector2d(-2.0 - (++i * 3), 31.0), heading = 3 * PI / 2 + 1.0e-6, tangent = 3 * PI / 2)
                .withName("Go to Submersible Zone")
                .build(last)
                .with(
                    Proto.clawLift.tasks.goTo(highChamberTicks) named "Lift to High Chamber" timeout (3.0 of Seconds),
                    Proto.clawRotator.tasks.close().after(300.0, Milliseconds)
                )
        }

        add(wallToSubmersibleRoutine.invoke().after(Proto.clawLift.tasks.home()))
        add(hookSpecimenRoutine.invoke())

        val returning = Pose2d(-37.87, 52.02, 3 * PI / 4)
        Proto.drive.makeTrajectory(last.get(), map)
            .afterTime(homeDelay to Seconds, a = Proto.clawLift.tasks.home() named "Home Lift")
            .splineToLinearHeading(Pose2d(-5.0, 32.0, 3 * PI / 2), tangent = 3 * PI / 2)
            .afterTime(1.0, a = Proto.clawRotator.tasks.setTo(0.85).after(0.2, Seconds).forAtLeast(1.3, Seconds)
                .then(Proto.claws.tasks.closeBoth().forAtLeast(0.7, Seconds).then(Proto.clawRotator.tasks.setTo(0.5))))
            .setReversed(true)
            .splineToLinearHeading(Pose2d(-45.55, 42.16, 250.degToRad()), tangent = PI)
            .waitSeconds(0.5)
            .setReversed(false)
            .afterTime(0.6, a = Proto.claws.tasks.openBoth())
            .strafeToLinearHeading(returning.position, heading = returning.heading)
            .afterTime(1.0, a = Proto.clawRotator.tasks.setTo(0.85).forAtLeast(0.35, Seconds)
                .then(Proto.claws.tasks.closeBoth().forAtLeast(0.7, Seconds)).then(Proto.clawRotator.tasks.close().after(0.4, Seconds)))
            .strafeToLinearHeading(Vector2d(-49.5, 39.86), heading = 5 * PI / 4)
            .waitSeconds(0.5) //giulio is here
            .strafeToLinearHeading(returning.position, heading = returning.heading)
            .afterTime(0.1, a = Proto.clawRotator.tasks.close().then(Proto.claws.tasks.openBoth()))
            .strafeToLinearHeading(wallPickup.position - Vector2d(0.0, 1.4), heading = wallPickup.heading)
            .withName("Move Two Samples")
            .addTask(last)

        add(wallPickupRoutine.invoke())
        add(wallToSubmersibleRoutine.invoke())
        add(hookSpecimenRoutine.invoke())
// giuilo was here
        add(Proto.drive.makeTrajectory(last.get(), map)
            .strafeToLinearHeading(wallPickup.position, heading = wallPickup.heading)
            .withName("Return to Observation Zone")
            .build(last)
            .with(Proto.clawLift.tasks.home().after(homeDelay) named "Home Lift"))

        add(wallPickupRoutine.invoke())
        add(wallToSubmersibleRoutine.invoke())
        add(hookSpecimenRoutine.invoke())

        add(Proto.drive.makeTrajectory(last.get(), map)
            .strafeTo(Vector2d(-39.0, 60.0))
            .withName("Park in Observation Zone")
            .build(last)
            .with(Proto.clawLift.tasks.home().after(homeDelay) named "Home Lift"))
        //Hello Bubner
        // You are a robot
        //Why is code so strange
        //I am a robot
        //I am a robot
        //Hmmmmm, what are you doing mister?
        //I am a robot
        //I am a robot
    }

    /*
    // MeepMeep
    final boolean isRed = false;
    double submersiblePlacingY = 36.0;
    double samplePushY = 12.0;
    double observationPushY = 50.0;
    Measure<Velocity<Distance>> slowSpeed = FieldTilesPerSecond.of(0.1);
    PoseMap map = isRed ? new SymmetricPoseMap() : new IdentityPoseMap();
    Reference<Pose2d> last = Reference.of(blueRight().tile(3.5).backward(Inches.of(1)).build().toFieldPose());

    drive.makeTrajectory(last.get(), map)
            .setVelConstraints((u0, u1, s) -> s > submersiblePlacingY * 0.6 ? slowSpeed.in(InchesPerSecond) : 40)
            .lineToY(submersiblePlacingY)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .setReversed(true)
            .splineToConstantHeading(new Vector2d(-36.7, 26.8), 3 * PI / 2)
            .splineToConstantHeading(new Vector2d(-47.0, samplePushY), PI)
            .setTangent(3 * PI / 2)
            .setReversed(true)
            .lineToY(observationPushY)
            .setReversed(false)
            .splineToConstantHeading(new Vector2d(-56.0, samplePushY), PI)
            .setReversed(true)
            .lineToY(observationPushY)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .strafeTo(new Vector2d(-47.0, 44.0))
            .turn(180.0, Degrees)
            .setVelConstraints(Vel.ofMax(0.1, FieldTilesPerSecond))
            .lineToY(47.0)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .splineTo(new Vector2d(-5.0, 38.0), 3 * PI / 2)
            .setVelConstraints(Vel.ofMax(0.1, FieldTilesPerSecond))
            .lineToY(submersiblePlacingY)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .strafeToLinearHeading(new Vector2d(-35.6, 50.0), 3 * PI / 4)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .strafeToLinearHeading(new Vector2d(-8.0, 38.0), 3 * PI / 2)
            .setTangent(PI / 2)
            .setVelConstraints(Vel.ofMax(0.1, FieldTilesPerSecond))
            .lineToY(submersiblePlacingY)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .strafeTo(new Vector2d(-39.0, 60.0))
            .addTask(last);
     */
}
//still here