package com.fishstory.oopsday;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryPathResolver;
import org.jbehave.core.io.UnderscoredCamelCaseResolver;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.ParameterConverters.ParameterConverter;
import org.jbehave.core.steps.ParameterConverters.StringListConverter;
import org.jbehave.core.steps.SilentStepMonitor;

/**
 * @author Julius.Yu
 * @date   7:06:59 AM
 */
public abstract class AbstractJunitStory extends JUnitStory{

    public AbstractJunitStory(){
        StoryPathResolver storyPathResolver = new UnderscoredCamelCaseResolver(".story");
        Class<?> storyClass = this.getClass();
        Properties viewProperties = new Properties();
        viewProperties.put("decorateNonHtml", "true");
        URL codeLocation = CodeLocations.codeLocationFromClass(storyClass);
        Configuration configuration = new MostUsefulConfiguration()
                .useStoryControls(new StoryControls().doDryRun(false).doSkipScenariosAfterFailure(false))
                .useStoryLoader(new LoadFromClasspath(storyClass.getClassLoader()))
                .useStoryReporterBuilder(new StoryReporterBuilder()
                    .withCodeLocation(codeLocation)
                    .withDefaultFormats()
                    .withViewResources(viewProperties)
                    .withFormats(Format.CONSOLE, Format.HTML)
                    .withFailureTrace(true)
                    .withFailureTraceCompression(false))
                .useStoryPathResolver(storyPathResolver)
                .useStepMonitor(new SilentStepMonitor())
                .useParameterConverters(new ParameterConverters().addConverters(customConverters()));

        useConfiguration(configuration);
        addSteps(candidateSteps());

        configuredEmbedder().embedderControls().doGenerateViewAfterStories(true).doIgnoreFailureInStories(true);
    }
    private ParameterConverter[] customConverters() {
        List<ParameterConverter> converters = new ArrayList<ParameterConverter>();
        converters.add(new StringListConverter());
        return converters.toArray(new ParameterConverter[converters.size()]);
    }

}