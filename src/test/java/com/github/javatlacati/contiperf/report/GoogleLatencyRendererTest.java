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

package com.github.javatlacati.contiperf.report;

import com.github.javatlacati.stat.LatencyCounter;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

/**
 * Tests the {@link GoogleLatencyRenderer}.<br>
 * <br>
 * Created: 14.01.2011 12:57:27
 *
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class GoogleLatencyRendererTest {

    private static final int RANDOM_BOUND = 20;
    /**
     * Communication protocol part of the generated URL.
     */
    private static final String PROTOCOL = "https://";
    /**
     * Maximum width of the url to display
     */
    private static final int MAX_WIDTH = 1400;
    /**
     * Maximum height of the url to display
     */
    private static final int MAX_HEIGHT = 1300;

    @Test
    public void testDataset() {
        LatencyDataSet dataset = new LatencyDataSet(15);
        dataset.addPoint(4, 0);
        dataset.addPoint(5, 1);
        dataset.addPoint(6, 10);
        dataset.addPoint(7, 134);
        dataset.addPoint(8, 156);
        dataset.addPoint(9, 142);
        dataset.addPoint(10, 126);
        dataset.addPoint(11, 60);
        dataset.addPoint(12, 40);
        dataset.addPoint(13, 30);
        dataset.addPoint(14, 10);
        dataset.addPoint(15, 1);
        dataset.addPoint(16, 0);
        dataset.addLabel("med", 10);
        dataset.addLabel("avg", 11);
        dataset.addLabel("90%", 13);
        int width = random.nextInt(MAX_WIDTH);
        int height = random.nextInt(MAX_HEIGHT);
        String url = new GoogleLatencyRenderer().renderDataset(dataset,
                getClass().getSimpleName(), width, height);
        assertThat("Generated link should start with protocol", url, startsWith(PROTOCOL));
        assertThat("Was not rendered with the specified height and width", url, containsString(new StringBuilder("chs=").append(width).append("x").append(height).toString()));
        System.out.println(url);
    }

    Random random = new Random();

    @Test
    public void testCounter() {
        LatencyCounter counter = new LatencyCounter("test");
        for (int i = 0; i < 50000; i++) {
            counter.addSample(rand() + rand() + rand() + rand(), null);
        }
        int width = random.nextInt(MAX_WIDTH);
        int height = random.nextInt(MAX_HEIGHT);
        String url = new GoogleLatencyRenderer().render(counter,
                getClass().getSimpleName(), width, height);
        assertThat("Generated link should start with protocol", url, startsWith(PROTOCOL));
        assertThat("Was not rendered with the specified height and width", url, containsString(new StringBuilder("chs=").append(width).append("x").append(height).toString()));
        System.out.println(url);
    }

    private int rand() {
        return random.nextInt(RANDOM_BOUND) * random.nextInt(RANDOM_BOUND);
    }

}
