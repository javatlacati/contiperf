/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package com.github.javatlacati.stat;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/**
 * Tests the {@link LatencyCounter}.<br>
 * <br>
 * Created: 26.02.2012 18:31:16
 *
 * @author Volker Bergmann
 * @since 2.1.0
 */
@Execution(ExecutionMode.CONCURRENT)
public class LatencyCounterTest {

    @Test
    public void testStartTwice() {

        Assertions.assertThrows(IllegalStateException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				LatencyCounter counter = new LatencyCounter("test");
				counter.start();
				counter.start();
			}
		});
    }

    @Test
    public void testStopTwice() {
		Assertions.assertThrows(IllegalStateException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				LatencyCounter counter = new LatencyCounter("test");
				counter.start();
				counter.stop();
				counter.stop();
			}
		});

    }

    @Test
    public void testPercentileAboveLatency() {
        LatencyCounter counter = new LatencyCounter("test");
        counter.start();
        for (int i = 25; i <= 125; i += 25) {
            counter.addSample(i, null);
        }
        counter.stop();
        assertThat(counter.percentileAboveLatency(0), is(100.));
        assertThat(counter.percentileAboveLatency(25), is(80.));
        assertThat(counter.percentileAboveLatency(99), is(40.));
        assertThat(counter.percentileAboveLatency(100), is(20.));
        assertThat(counter.percentileAboveLatency(124), is(20.));
        assertThat(counter.percentileAboveLatency(125), is(0.));
        assertThat(counter.percentileAboveLatency(126), is(0.));
    }

}
