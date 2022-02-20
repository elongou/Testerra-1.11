/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.internal.Counters;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepController;
import org.testng.ITestResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds the information of a test method.
 *
 * @author mibu
 */
public class MethodContext extends AbstractContext {

    public enum Type {
        TEST_METHOD,
        CONFIGURATION_METHOD
    }

    private ITestResult testResult;
    private Status status = Status.NO_RUN;
    private final Type methodType;
    private List<Object> parameterValues;
    private int retryNumber = 0;
    private final int methodRunIndex;
    private final String threadName;
    private TestStep lastFailedStep;
    private Class failureCorridorClass = FailureCorridor.High.class;

    /**
     * @deprecated
     */
    public final List<String> infos = new LinkedList<>();
    private final List<SessionContext> sessionContexts = new LinkedList<>();
    private String priorityMessage = null;
    private final TestStepController testStepController = new TestStepController();
    private final List<MethodContext> relatedMethodContexts = new LinkedList<>();
    private final List<MethodContext> dependsOnMethodContexts = new LinkedList<>();
    private List<CustomContext> customContexts;
    private List<Annotation> customAnnotations;

    /**
     * Public constructor. Creates a new <code>MethodContext</code> object.
     *
     * @param name The test method name.
     * @param methodType method type.
     * @param classContext .
     */
    public MethodContext(
            final String name,
            final Type methodType,
            final ClassContext classContext
    ) {
        this.setName(name);
        this.setParentContext(classContext);
        this.methodRunIndex = Counters.increaseMethodExecutionCounter();
        this.methodType = methodType;
        final Thread currentThread = Thread.currentThread();
        this.threadName = currentThread.getName() + "#" + currentThread.getId();
    }

    public void setRetryCounter(int retryCounter) {
        this.retryNumber = retryCounter;
    }

    public int getRetryCounter() {
        return this.retryNumber;
    }

    public void setFailureCorridorClass(Class failureCorridorClass) {
        this.failureCorridorClass = failureCorridorClass;
    }

    public Class getFailureCorridorClass() {
        return this.failureCorridorClass;
    }

    private List<CustomContext> getCustomContexts() {
        if (this.customContexts == null) {
            this.customContexts = new LinkedList<>();
        }
        return customContexts;
    }

    public void addCustomContext(CustomContext customContext) {
        this.getCustomContexts().add(customContext);
    }

    public Stream<CustomContext> readCustomContexts() {
        if (this.customContexts == null) {
            return Stream.empty();
        } else {
            return this.customContexts.stream();
        }
    }

    public Type getMethodType() {
        return this.methodType;
    }

    public Stream<SessionContext> readSessionContexts() {
        return sessionContexts.stream();
    }

    public void addSessionContext(SessionContext sessionContext) {
        if (!this.sessionContexts.contains(sessionContext)) {
            this.sessionContexts.add(sessionContext);
            sessionContext.addMethodContext(this);
            sessionContext.setParentContext(this);
            TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(this));
        }
    }

    public ClassContext getClassContext() {
        return (ClassContext) this.getParentContext();
    }

    public Stream<MethodContext> readRelatedMethodContexts() {
        return this.relatedMethodContexts.stream();
    }

    public Stream<MethodContext> readDependsOnMethodContexts() {
        return this.dependsOnMethodContexts.stream();
    }

    public void addRelatedMethodContext(MethodContext relatedMethodContext) {
        this.relatedMethodContexts.add(relatedMethodContext);
    }

    public void addDependsOnMethod(MethodContext methodContext) {
        if (!this.dependsOnMethodContexts.contains(methodContext)) {
            this.dependsOnMethodContexts.add(methodContext);
        }
    }

    private Stream<TestStepAction> readTestStepActions() {
        return this.readTestSteps().flatMap(testStep -> testStep.getTestStepActions().stream());
    }

    public TestStepAction addLogMessage(LogMessage logMessage) {
        return testStepController.addLogMessage(logMessage);
    }

    public TestStep getTestStep(String name) {
        return this.testStepController.getTestStep(name);
    }

    public Stream<TestStep> readTestSteps() {
        return this.testStepController.getTestSteps().stream();
    }

    public TestStep getCurrentTestStep() {
        return this.testStepController.getCurrentTestStep();
    }

    public int getLastFailedTestStepIndex() {
        return this.testStepController.getTestSteps().indexOf(this.lastFailedStep);
    }

    public void setFailedStep(TestStep step) {
        this.lastFailedStep = step;
    }

    public Stream<ErrorContext> readErrors() {
        return readTestStepActions().flatMap(TestStepAction::readErrors);
    }

    @Override
    public boolean equals(final Object obj) {

        if (!(obj instanceof MethodContext)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        return obj.toString().equals(toString());
    }

    public void addOptionalAssertion(Throwable throwable) {
        getCurrentTestStep().getCurrentTestStepAction().addAssertion(new ErrorContext(throwable, true));
    }

    public void addError(Throwable throwable) {
        addError(new ErrorContext(throwable, false));
    }

    public void addError(ErrorContext errorContext) {
        getCurrentTestStep().getCurrentTestStepAction().addAssertion(errorContext);
    }

    @Deprecated
    public void addPriorityMessage(String msg) {

        if (priorityMessage == null) {
            priorityMessage = "";
        }

        if (!priorityMessage.contains(msg)) {
            priorityMessage += msg;
        }
    }

    public boolean isConfigMethod() {
        return methodType == Type.CONFIGURATION_METHOD;
    }

    public boolean isTestMethod() {
        return !isConfigMethod();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isStatusOneOf(Status ...statuses) {
        return Arrays.stream(statuses).anyMatch(givenStatus -> givenStatus == this.status);
    }

    @Override
    public String toString() {
        return "MethodContext{" +
                "methodRunIndex=" + methodRunIndex +
                ", name='" + getName() + '\'' +
                '}';
    }

    /**
     * Publish the screenshots to the report into the current errorContext.
     */
    public void addScreenshots(Stream<Screenshot> screenshots) {
        TestStepAction currentTestStepAction = this.testStepController.getCurrentTestStep().getCurrentTestStepAction();
        screenshots.forEach(currentTestStepAction::addScreenshot);
    }

    /**
     * Proper parameter names are available by setting {https://stackoverflow.com/questions/6759880/getting-the-name-of-a-method-parameter}
     */
    public Parameter[] getParameters() {
        return getTestNgResult().map(testResult -> testResult.getMethod().getConstructorOrMethod().getMethod().getParameters()).orElse(new Parameter[]{});
    }

    public MethodContext setParameterValues(Object[] parameters) {
        // TestNG method parameters can be NULL
        this.parameterValues = Arrays.stream(parameters).filter(Objects::nonNull).collect(Collectors.toList());
        return this;
    }

    public List<Object> getParameterValues() {
        if (this.parameterValues == null) {
            return Collections.emptyList();
        } else {
            return this.parameterValues;
        }
    }

    public Optional<ITestResult> getTestNgResult() {
        return Optional.ofNullable(this.testResult);
    }

    public MethodContext setTestNgResult(ITestResult testResult) {
        this.testResult = testResult;
        return this;
    }

    public Stream<Annotation> readAnnotations() {
        return Stream.concat(
                (this.customAnnotations!=null)?this.customAnnotations.stream():Stream.empty(),
                getTestNgResult()
                        .map(testResult -> Stream.of(testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotations()))
                        .orElse(Stream.empty())
        );
    }

    public Optional<Fails> getFailsAnnotation() {
        return getAnnotation(Fails.class);
    }

    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationClass) {
        return readAnnotations().filter(annotationClass::isInstance).map(annotation -> (T)annotation).findFirst();
    }

    /**
     * Required by cucumber-connector
     * @param annotation
     */
    public void addAnnotation(Annotation annotation) {
        if (this.customAnnotations == null) {
            this.customAnnotations = new LinkedList<>();
        }
        this.customAnnotations.add(annotation);
    }

    public int getMethodRunIndex() {
        return methodRunIndex;
    }

    public String getThreadName() {
        return threadName;
    }

    public Stream<String> readInfos() {
        return infos.stream();
    }

    public void addInfo(String info) {
        this.infos.add(info);
    }

    public Optional<String> getPriorityMessage() {
        return Optional.ofNullable(priorityMessage);
    }
}
