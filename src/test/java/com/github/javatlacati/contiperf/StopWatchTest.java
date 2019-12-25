/*
 * (c) Copyright 2011-2012 by Volker Bergmann. All rights reserved.
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

package com.github.javatlacati.contiperf;

import com.github.javatlacati.stat.CounterRepository;
import com.github.javatlacati.stat.LatencyCounter;
import org.hamcrest.number.OrderingComparison;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests the {@link StopWatch}.<br>
 * <br>
 * Created: 14.01.2011 11:33:37
 *
 * @author Volker Bergmann
 * @since 1.08
 */
public class StopWatchTest {

    private static final String NAME = "StopWatchTest";
    /**
     * Delay in milli seconds
     */
    private static final int STANDARD_DELAY = 50;

    @AfterEach
    public void tearDown() {
        CounterRepository.getInstance().clear();
    }

    @Test
    public void testSingleCall() throws InterruptedException {
        sleepTimed(STANDARD_DELAY);
        assertThat(getCounter().sampleCount(), is(1L));
    }

    @Test
    public void testSubsequentCalls() throws InterruptedException {
        sleepTimed(STANDARD_DELAY);
        sleepTimed(STANDARD_DELAY);
        sleepTimed(STANDARD_DELAY);
        LatencyCounter counter = getCounter();
        assertThat(counter.sampleCount(), is(3L));
        assertThat(counter.minLatency(), OrderingComparison.greaterThanOrEqualTo(39L));
        assertThat(counter.minLatency(), OrderingComparison.lessThan(100L));
        assertThat(counter.averageLatency(), OrderingComparison.greaterThanOrEqualTo(39.));
        assertThat(counter.averageLatency(), OrderingComparison.lessThan(100.));
    }

    @Test
    public void testParallelCalls() throws InterruptedException {
        Thread[] threads = new Thread[20];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 20; i++) {
                            sleepTimed(STANDARD_DELAY);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        LatencyCounter counter = getCounter();
        assertThat(counter.sampleCount(), is(400L));
        assertThat(counter.minLatency(), OrderingComparison.greaterThanOrEqualTo(39L));
        assertThat(counter.minLatency(), OrderingComparison.lessThan(100L));
        assertThat(counter.averageLatency(), OrderingComparison.greaterThanOrEqualTo(39.));
        assertThat(counter.averageLatency(), OrderingComparison.lessThan(100.));
    }

    @Test
    public void testMultiStop() {
        Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                StopWatch watch = new StopWatch(NAME);
                watch.stop();
                watch.stop();
            }
        });

    }

    private void sleepTimed(int delay) throws InterruptedException {
        StopWatch watch = new StopWatch(NAME);
        Thread.sleep(delay);
        watch.stop();
    }

    private LatencyCounter getCounter() {
        return CounterRepository.getInstance().getCounter(NAME);
    }

}
