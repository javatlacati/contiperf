/*
 * (c) Copyright 2012 by Volker Bergmann. All rights reserved.
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

package com.github.javatlacati.contiperf.timer;

import com.github.javatlacati.contiperf.WaitTimer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link CumulatedTimer}.<br>
 * <br>
 * Created: 06.04.2012 18:19:40
 *
 * @author Volker Bergmann
 * @since 2.1.0
 */
@Execution(ExecutionMode.CONCURRENT)
public class CumulatedTimerTest {

    private Random random = new Random();

    @Test
    public void testEmptyInitialization() throws Exception {
        WaitTimer timer = CumulatedTimer.class.getDeclaredConstructor().newInstance();
        timer.init(new double[0]);
        final int defaultMinExpected = 500;
        for (int i = 0; i < 1000; i++) {
            assertRange(defaultMinExpected, defaultMinExpected + 1000, timer.getWaitTime());
        }
    }

    @Test
    public void testUnderInitialization() throws Exception {
        WaitTimer timer = CumulatedTimer.class.getDeclaredConstructor().newInstance();
        int minimumWaitTime = random.nextInt(2000);
        timer.init(new double[]{minimumWaitTime});
        for (int i = 0; i < 1000; i++) {
            assertRange(minimumWaitTime, minimumWaitTime + 1000, timer.getWaitTime());
        }
    }

    @Test
    public void testNormalInitialization() throws Exception {
        WaitTimer timer = CumulatedTimer.class.getDeclaredConstructor().newInstance();
        int minimumWaitTime = random.nextInt(2000);
        int maximumWaitTime = minimumWaitTime + random.nextInt(2500);
        timer.init(new double[]{minimumWaitTime, maximumWaitTime});
        for (int i = 0; i < 1000; i++) {
            assertRange(minimumWaitTime, maximumWaitTime, timer.getWaitTime());
        }
    }

    //TODO add a test for testing Illegal argument exception to be thrown for non positive waiting times

    @Test
    public void testOverInitialization() throws Exception {
        WaitTimer timer = CumulatedTimer.class.getDeclaredConstructor().newInstance();
        int minimumWaitTime = random.nextInt(2000);
        int maximumWaitTime = minimumWaitTime + random.nextInt(2500);
        timer.init(new double[]{minimumWaitTime, maximumWaitTime, 3000});
        for (int i = 0; i < 1000; i++) {
            assertRange(minimumWaitTime, maximumWaitTime, timer.getWaitTime());
        }
    }

    private void assertRange(int minExpected, int maxExpected, int waitTime) {
        assertTrue(minExpected <= waitTime && waitTime <= maxExpected);
    }

}
