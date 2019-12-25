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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests the {@link ConstantTimer}.<br>
 * <br>
 * Created: 06.04.2012 18:10:41
 *
 * @author Volker Bergmann
 * @since 2.1.0
 */
@Execution(ExecutionMode.CONCURRENT)
public class ConstantTimerTest {

    private static final int DEFAULT_WAIT_TIME = 1000;

    @Test
    public void testEmptyInitialization() throws Exception {
        WaitTimer timer = ConstantTimer.class.getDeclaredConstructor().newInstance();
        timer.init(new double[0]);
        for (int i = 0; i < 1000; i++) {
            assertThat(timer.getWaitTime(), is(DEFAULT_WAIT_TIME));
        }
    }

    @Test
    public void testNormalInitialization() throws Exception {
        WaitTimer timer = ConstantTimer.class.getDeclaredConstructor().newInstance();
        int waitTime = 123;
        timer.init(new double[]{waitTime});
        for (int i = 0; i < 1000; i++) {
            assertThat(timer.getWaitTime(), is(waitTime));
        }
    }

    @Test
    public void testTooManyParams() throws Exception {
        WaitTimer timer = ConstantTimer.class.getDeclaredConstructor().newInstance();
        int waitTime = 234;
        timer.init(new double[]{waitTime, 456});
        for (int i = 0; i < 1000; i++) {
            assertThat(timer.getWaitTime(), is(waitTime));
        }
    }

}
