/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU Lesser General Public License (LGPL), Eclipse Public License (EPL)
 * and the BSD License.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.javatlacati.contiperf.junit;

import java.lang.reflect.Field;

import com.github.javatlacati.contiperf.log.ConsoleExecutionLogger;
import junit.framework.AssertionFailedError;

import com.github.javatlacati.contiperf.Config;
import com.github.javatlacati.contiperf.ExecutionConfig;
import com.github.javatlacati.contiperf.ExecutionLogger;
import com.github.javatlacati.contiperf.PerfTest;
import com.github.javatlacati.contiperf.PerformanceRequirement;
import com.github.javatlacati.contiperf.Required;
import com.github.javatlacati.contiperf.report.HtmlReportModule;
import com.github.javatlacati.contiperf.report.LoggerModuleAdapter;
import com.github.javatlacati.contiperf.report.ReportContext;
import com.github.javatlacati.contiperf.report.ReportModule;
import com.github.javatlacati.contiperf.util.ContiPerfUtil;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Implements the JUnit {@link MethodRule} interface for adding performance test
 * features to test calls.
 * 
 * for activating it, add an attribute of this class to your test class, e.g.:
 * 
 * <pre>
 * public class SimpleTest {
 *     &#064;Rule
 *     public ContiPerfRule i = new ContiPerfRule();
 * 
 *     &#064;Test
 *     public void sleepAWhile() throws Exception {
 * 	Thread.sleep(100);
 *     }
 * }
 * </pre>
 * 
 * ContiPerf will then intercept each test method call and optionally cause
 * multiple invocations and check total execution time against a time limit.
 *
 * invocation counts and time limits can be configured by Java annotations, e.g.
 * 
 * <pre>
 * &#064;Test(timeout = 300)
 * &#064;PerfTest(invocations = 5, timeLimit = 500)
 * public void sleepALittleLonger() throws Exception {
 *     System.out.print('x');
 *     Thread.sleep(200);
 * }
 * </pre>
 *
 * For enabling different test settings, the invocation count values can be
 * configured in a properties file <code>contiperf.properties</code> which
 * assigns the invocation count to the fully qualified method name, e.g.
 * 
 * <pre>
 * org.databene.contiperf.junit.SimpleTest.sleepAWhile=3
 * org.databene.contiperf.junit.SimpleTest.sleepALittleLonger=2
 * </pre>
 * 
 * If the properties file exists, it overrides the annotation values.
 *
 * By default, the execution times are written to the CSV file
 * <code>target/contiperf/contiperf.log</code>. They have four columns, listing
 * the
 * <ol>
 * <li>fully qualified method name</li>
 * <li>total execution time</li>
 * <li>invocation count</li>
 * <li>start time in milliseconds since 1970-01-01</li>
 * </ol>
 *
 * For reusing integration tests as performance tests, you can suppress
 * ContiPerf execution by setting the System property
 * <code>contiperf.active=false</code>.
 * <br>
 * <br>
 * Created: 12.10.2009 07:36:02
 * 
 * @since 1.0
 * @author Volker Bergmann
 */
@SuppressWarnings("deprecation")
public class ContiPerfRule implements MethodRule {

    private ExecutionConfig defaultExecutionConfig;
    ReportContext context;
    private PerformanceRequirement defaultRequirements;

    // constructors
    // ----------------------------------------------------------------------------------------------------

    public ContiPerfRule() {
	this((ReportContext) null);
    }

    public ContiPerfRule(ExecutionLogger logger) {
	this(createReportContext(logger), null);
    }

    public ContiPerfRule(ReportModule... modules) {
	this(createReportContext(modules), null);
    }

    protected ContiPerfRule(ReportContext context) {
	this(context, null);
    }

    protected ContiPerfRule(ReportContext context, Object suite) {
	if (context == null) {
	    this.context = JUnitReportContext.createInstance(suite);
	} else {
	    this.context = context;
	}
	if (suite != null) {
	    Class<? extends Object> suiteClass = suite.getClass();
	    this.defaultExecutionConfig = configurePerfTest(
		    ContiPerfUtil.annotationOfClass(suiteClass, PerfTest.class),
		    suiteClass.getName());
	    this.defaultRequirements = ContiPerfUtil.mapRequired(ContiPerfUtil
		    .annotationOfClass(suiteClass, Required.class));
	}
    }

    // static factory methods
    // ------------------------------------------------------------------------------------------

    public static ContiPerfRule createDefaultRule() {
	ReportContext context = new JUnitReportContext();
	context.addReportModule(new HtmlReportModule());
	return new ContiPerfRule(context);
    }

    public static ContiPerfRule createVerboseRule() {
	ReportContext context = new JUnitReportContext();
	context.addReportModule(new HtmlReportModule());
	context.addReportModule(new LoggerModuleAdapter(
		new ConsoleExecutionLogger()));
	return new ContiPerfRule(context);
    }

    // MethodRule interface implementation
    // -----------------------------------------------------------------------------

    public Statement apply(Statement base, FrameworkMethod method, Object target) {
	// check if ContiPerf is active, if not then ignore the annotation
	Config config = Config.instance();
	if (!config.active()) {
	    return base;
	}
	// remove RunBefores and RunAfters from the statement cascade
	RunBefores runBefores = null;
	RunAfters runAfters = null;
	while (base instanceof RunAfters || base instanceof RunBefores) {
	    try {
		if (base instanceof RunAfters) {
		    runAfters = (RunAfters) base;
		    Field fNext = getFieldNextWithJunit412Fallback(base);
		    fNext.setAccessible(true);
		    base = (Statement) fNext.get(base);
		} else if (base instanceof RunBefores) {
		    runBefores = (RunBefores) base;
		    Field fNext = getFieldNextWithJunit412Fallback(base);
		    fNext.setAccessible(true);
		    base = (Statement) fNext.get(base);
		}
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
	}
	// set up report context
	if (context.getReportModules().size() == 0) {
	    context = JUnitReportContext.createInstance(target);
	    if (context.getReportModules().size() == 0) {
		context = Config.instance().createDefaultReportContext(
			AssertionFailedError.class);
	    }
	}
	// create perf test statement
	String testId = methodName(method, target);
	base = new PerfTestStatement(base, testId, executionConfig(method,
		testId), requirements(method, testId), context);
	try {
	    // if runBefores has been removed, reapply it
	    if (runBefores != null) {
		Field fNext = getFieldNextWithJunit412Fallback(runBefores);
		fNext.setAccessible(true);
		fNext.set(runBefores, base);
		base = runBefores;
	    }
	    // if runAfters has been removed, reapply it
	    if (runAfters != null) {
		Field fNext = getFieldNextWithJunit412Fallback(runAfters);
		fNext.setAccessible(true);
		fNext.set(runAfters, base);
		base = runAfters;
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	return base;
    }

    /**
     * Helper that uses reflection to get a field, but applies a fallback as field name changed in JUnit 4.12
     * from legacy named "fNext" to "next".
     *
     * @see <a href="https://github.com/junit-team/junit/commit/df00d5eced3a7737b88de0f6f9e3673f0cf88f88">https://github.com/junit-team/junit/commit/df00d5eced3a7737b88de0f6f9e3673f0cf88f88</a>
     */
    private Field getFieldNextWithJunit412Fallback(final Statement base) throws NoSuchFieldException{
      try {
	  return base.getClass().getDeclaredField("fNext");
      } catch (NoSuchFieldException e) {
	  return base.getClass().getDeclaredField("next");
      }
    }

    public ReportContext getContext() {
	return context;
    }

    void setContext(ReportContext context) {
	this.context = context;
    }

    // helpers
    // ---------------------------------------------------------------------------------------------------------

    private static ReportContext createReportContext(ExecutionLogger logger) {
	ReportContext context = new JUnitReportContext();
	context.addReportModule(new LoggerModuleAdapter(logger));
	return context;
    }

    private static ReportContext createReportContext(ReportModule... modules) {
	ReportContext context = new JUnitReportContext();
	for (ReportModule module : modules) {
	    context.addReportModule(module);
	}
	return context;
    }

    private ExecutionConfig executionConfig(FrameworkMethod method,
	    String methodName) {
	PerfTest annotation = ContiPerfUtil.annotationOfMethodOrClass(method,
		PerfTest.class);
	if (annotation != null) {
	    return configurePerfTest(annotation, methodName);
	}
	if (defaultExecutionConfig != null) {
	    return defaultExecutionConfig;
	}
	return new ExecutionConfig(1);
    }

    private PerformanceRequirement requirements(FrameworkMethod method,
	    String testId) {
	Required annotation = ContiPerfUtil.annotationOfMethodOrClass(method,
		Required.class);
	if (annotation != null) {
	    return ContiPerfUtil.mapRequired(annotation);
	}
	if (defaultRequirements != null) {
	    return defaultRequirements;
	}
	return null;
    }

    public static ExecutionConfig configurePerfTest(PerfTest annotation,
	    String testId) {
	ExecutionConfig config = ContiPerfUtil
		.mapPerfTestAnnotation(annotation);
	if (annotation == null) {
	    config = new ExecutionConfig(1);
	}
	int count = Config.instance().getInvocationCount(testId);
	if (count >= 0) {
	    config.setInvocations(count);
	}
	return config;
    }

    private static String methodName(FrameworkMethod method, Object target) {
	return target.getClass().getName() + '.' + method.getName();
	// no need to check signature: JUnit test methods do not have parameters
    }

}
