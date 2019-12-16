/*
 * (c) Copyright 2014 by Volker Bergmann. All rights reserved.
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

import com.github.javatlacati.contiperf.PerfTest;
import com.github.javatlacati.contiperf.timer.ConstantTimer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link ParallelRunner}.<br>
 * <br>
 * Created: 23.01.2014 12:10:45
 *
 * @author Lucas Pouzac
 * @since 2.4.0
 */
@RunWith(ParallelRunner.class)
@Parallel(count = AbstractParallelRunnerTest.CONCURRENT_COUNT)
public abstract class AbstractParallelRunnerTest {

    protected final static int CONCURRENT_COUNT = 3;

    protected static final Set<String> threads = Collections
            .synchronizedSet(new HashSet<String>(CONCURRENT_COUNT));

    @Test
    public void shouldRunInParallel1() throws TimeoutException,
            InterruptedException {
        logCurrentThread();
    }

    @Test
    public void shouldRunInParallel2() throws TimeoutException,
            InterruptedException {
        logCurrentThread();
    }

    @Test
    public void shouldRunInParallel3() throws TimeoutException,
            InterruptedException {
        logCurrentThread();
    }

    @Test
    public void shouldRunInParallel4() throws TimeoutException,
            InterruptedException {
        logCurrentThread();
    }

    @Test
    public void shouldRunInParallel5() throws TimeoutException,
            InterruptedException {
        logCurrentThread();
    }

    void logCurrentThread() throws TimeoutException,
            InterruptedException {
        Thread.sleep(200);
        threads.add(Thread.currentThread().getName());
        waitToForceCachedThreadPoolToCreateNewThread();
    }

    private void waitToForceCachedThreadPoolToCreateNewThread()
            throws InterruptedException, TimeoutException {
        success(new Condition() {
            public boolean isSatisfied() {
                return threads.size() == getConcurrentCount();
            }
        });
    }

    @Test(expected = AssertionError.class)
    public void concurrentFailuresFailInTheMainTestThread()
            throws InterruptedException {
        Assert.fail();
    }

    protected abstract int getConcurrentCount();

    private static boolean success(final Condition condition)
            throws InterruptedException {

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        final AtomicBoolean stopCommand = new AtomicBoolean(false);
        final AtomicBoolean result = new AtomicBoolean(false);
        final Runnable command = new Runnable() {
            @Override
            public void run() {
                if (condition.isSatisfied()) {
                    result.set(true);
                    stopCommand.set(true);
                }
            }
        };
        executorService.scheduleAtFixedRate(
                command, 0, 50, TimeUnit.MILLISECONDS
        );

        executorService.shutdown();

        executorService.awaitTermination(2, TimeUnit.SECONDS);

        stopCommand.set(true);

        return result.get();
    }

    private interface Condition {
        boolean isSatisfied();
    }

    @Rule
    public ContiPerfRule rule = new ContiPerfRule();

    private static volatile long test1First = -1;
    private static volatile long test1Last = -1;

    private static volatile long test2First = -1;
    private static volatile long test2Last = -1;

    @Test
    @PerfTest(duration = 2000, threads = 3, timer = ConstantTimer.class, timerParams = {1200})
    public void test1() throws Exception {
        long currentTime = System.currentTimeMillis();
        if (test1First == -1) {
            test1First = currentTime;
        }
        test1Last = currentTime;
        System.out.println("test1 - " + Thread.currentThread() + " - "
                + (currentTime - Math.min(test1First, test2First)));
    }

    @Test
    @PerfTest(duration = 3000, threads = 2, timer = ConstantTimer.class, timerParams = {700})
    public void test2() throws Exception {
        long currentTime = System.currentTimeMillis();
        if (test2First == -1) {
            test2First = currentTime;
        }
        test2Last = currentTime;
        System.out.println("test2 - " + Thread.currentThread() + " - "
                + (currentTime - Math.min(test1First, test2First)));
    }

    @Test
    public void test3() throws Exception {
    }

    @AfterClass
    public static void verifyParallelExecution() {
        assertTrue((test1First <= test2First && test2First <= test1Last)
                || (test2First <= test1First && test1First <= test2Last));
    }
}
