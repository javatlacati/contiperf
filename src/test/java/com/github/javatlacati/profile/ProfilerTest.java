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

package com.github.javatlacati.profile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Tests the {@link Profiler}.<br>
 * <br>
 * Created: 19.05.2011 09:43:47
 *
 * @author Volker Bergmann
 * @since 2.0.0
 */
@Execution(ExecutionMode.CONCURRENT)
public class ProfilerTest {

    @Test
    public void test() {

        // given
        Profiler profiler = new Profiler("test", 100, "ds");

        // when
        List<String> path = new ArrayList<>();
        profiler.addSample(path, 1000);
        path.add("sub1");
        profiler.addSample(path, 200);
        path.remove(path.size() - 1);
        path.add("sub2");
        profiler.addSample(path, 300);
        profiler.addSample(path, 400);

        // then
        Profile rootProfile = profiler.getRootProfile();
        assertEquals(1, rootProfile.getInvocationCount());
        assertEquals(10., rootProfile.getAverageLatency(), 0.01);
        assertEquals(10, rootProfile.getTotalLatency());

        Profile sub1Profile = rootProfile.getOrCreateSubProfile("sub1");
        assertEquals(1, sub1Profile.getInvocationCount());
        assertEquals(2., sub1Profile.getAverageLatency(), 0.01);
        assertEquals(2, sub1Profile.getTotalLatency());

        Profile sub2Profile = rootProfile.getOrCreateSubProfile("sub2");
        assertEquals(2, sub2Profile.getInvocationCount());
        assertEquals(3.5, sub2Profile.getAverageLatency(), 0.01);
        assertEquals(7, sub2Profile.getTotalLatency());

        profiler.printSummary();
    }

}
