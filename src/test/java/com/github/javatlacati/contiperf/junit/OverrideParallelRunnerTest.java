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

package com.github.javatlacati.contiperf.junit;

import org.junit.AfterClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Tests the {@link ParallelRunner}.<br>
 * <br>
 * Created: 23.01.2014 12:20:35
 *
 * @author Lucas Pouzac
 * @since 2.4.0
 */
@Parallel(count = OverrideParallelRunnerTest.OVERRIDE_CONCURRENT_COUNT)
public class OverrideParallelRunnerTest extends AbstractParallelRunnerTest {

    protected final static int OVERRIDE_CONCURRENT_COUNT = 5;

    @AfterClass
    public static void assertTestThreadsSpawned() {
        assertThat(threads.size(), is(OVERRIDE_CONCURRENT_COUNT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getConcurrentCount() {
        return OVERRIDE_CONCURRENT_COUNT;
    }

}