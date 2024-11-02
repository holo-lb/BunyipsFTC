package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.DualServos;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.Switch;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.SequentialTaskGroup;

/**
 * Sends the vertical arm straight down, moves the claw rotator to the basket, and drops the sample in.
 *
 * @author Lachlan Paul, 2024
 */
public class SampleToBasket extends SequentialTaskGroup {
    /**
     * Sequence of events:<br>
     * Makes sure the claw rotator is out of the way, if not, move it<br>
     * Sends the arm straight down<br>
     * Makes sure the claw rotator and arm are in the correct position for sample dropping, if not, move it<br>
     * Drop the sample<br>
     * Move the claw rotator back to the front<br>
     * @param verticalArm the vertical arm
     * @param horizontalArm the horizontal arm
     * @param clawRotator the claw rotator
     * @param claws       the claws
     */
    public SampleToBasket(HoldableActuator verticalArm, HoldableActuator horizontalArm, Switch clawRotator, DualServos claws) {
        super(
                // TODO: measure correct distance that the claw arm will have to be at for proper placement
                clawRotator.tasks.close(),
                verticalArm.tasks.home(),
                clawRotator.tasks.open(),
                claws.tasks.openBoth()
        );
    }
}
