package com.fishstory.oopsday.interfaces.tipping;

import com.fishstory.oopsday.AbstractJunitStory;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InstanceStepsFactory;

import java.util.List;

/**
 * User: Julius.Yu
 * Date: 11/30/11
 */
public class UserPostATip extends AbstractJunitStory {
    @Override
    public List<CandidateSteps> candidateSteps() {
        return new InstanceStepsFactory(configuration(), new user_post_a_tip_steps()).createCandidateSteps();
    }

}
