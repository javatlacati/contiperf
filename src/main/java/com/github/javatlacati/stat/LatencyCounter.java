/*
 * (c) Copyright 2006-2012 by Volker Bergmann. All rights reserved.
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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.github.javatlacati.contiperf.PerfTestExecutionError;
import com.github.javatlacati.contiperf.clock.SystemClock;

/**
 * Counts latencies and calculates performance-related statistics.<br>
 * <br>
 * Created: Created: 14.12.2006 18:11:58
 * 
 * @since 1.0
 * @author Volker Bergmann
 */
public final class LatencyCounter {

    private String name;
    private String clockName;

    private int minLatency;
    private int maxLatency;
    private long latencyCounts[];

    private boolean running;
    private long startTime;
    private long endTime;
    private long sampleCount;
    private long totalLatency;

    private List<PerfTestExecutionError> assertionErrors = new ArrayList<>(
			0);

    public LatencyCounter(String name) {
	this(name, SystemClock.NAME, 1000);
    }

    public LatencyCounter(String name, String clockName, int expectedMaxLatency) {
	this.name = name;
	this.clockName = clockName;
	this.latencyCounts = new long[1 + expectedMaxLatency];
	this.sampleCount = 0;
	this.totalLatency = 0;
	this.minLatency = -1;
	this.maxLatency = -1;
	this.startTime = -1;
	this.endTime = -1;
    }

    public String getName() {
	return name;
    }

    public String getClockName() {
	return clockName;
    }

    // interface
    // -------------------------------------------------------------------------------------------------------

    public void start() {
	if (running) {
	    throw new IllegalStateException(this + " has already been started");
	}
	this.startTime = System.currentTimeMillis();
	this.running = true;
    }

    public synchronized void addSample(int latency,
	    PerfTestExecutionError assertionError) {
	if (null != assertionError) {
	    this.assertionErrors.add(assertionError);
	} else {
	    if (latency >= latencyCounts.length) {
		resize(latency);
	    }
	    latencyCounts[latency]++;
	    sampleCount++;
	    totalLatency += latency;
	    if (minLatency == -1 || latency < minLatency) {
		minLatency = latency;
	    }
	    if (latency > maxLatency) {
		maxLatency = latency;
	    }
	}
    }

    public void stop() {
	if (!running) {
	    throw new IllegalStateException("Stopping " + this
		    + " which is not running");
	}
	this.running = false;
	this.endTime = System.currentTimeMillis();
    }

    public boolean isRunning() {
	return running;
    }

    public long getStartTime() {
	return startTime;
    }

    public long getLatencyCount(long latency) {
	if (latency < latencyCounts.length) {
	    return latencyCounts[(int) latency];
	} else {
	    return 0;
	}
    }

    public List<PerfTestExecutionError> getAssertionErrors() {
	return assertionErrors;
    }

    public long totalLatency() {
	return totalLatency;
    }

    public double averageLatency() {
	return (double) totalLatency / sampleCount;
    }

    public long minLatency() {
	return Math.max(minLatency, 0);
    }

    public long maxLatency() {
	return Math.max(maxLatency, 0);
    }

    public long sampleCount() {
	return sampleCount;
    }

    public long totalInvocations() {

	return sampleCount + assertionErrors.size();
    }

    public long percentileLatency(int percentile) {
	long targetCount = percentile * sampleCount / 100;
	long count = 0;
	for (long value = minLatency(); value <= maxLatency; value++) {
	    count += getLatencyCount(value);
	    if (count >= targetCount) {
		return value;
	    }
	}
	return maxLatency;
    }

    public double percentileAboveLatency(int latency) {
	long count = 0;
	for (long value = latency + 1; value <= maxLatency; value++) {
	    count += getLatencyCount(value);
	}
	return (count * 100.) / sampleCount;
    }

    public double throughput() {
	if (startTime == -1 || endTime == -1) {
	    throw new IllegalArgumentException(
		    "Invalid setup: Use start() and stop() to indicate test start and end!");
	}
	return 1000. * sampleCount / duration();
    }

    public long duration() {
	return endTime - startTime;
    }

    public double errorsRate() {

	return ((double) assertionErrors.size()) / totalInvocations();
    }

    // private helpers
    // -------------------------------------------------------------------------------------------------

    private void resize(int requestedIndex) {
	int sizingFactor = (requestedIndex + latencyCounts.length)
		/ latencyCounts.length;
	int newLength = sizingFactor * latencyCounts.length;
	long[] newLatencyCounts = new long[newLength];
	System.arraycopy(latencyCounts, 0, newLatencyCounts, 0,
		latencyCounts.length);
	latencyCounts = newLatencyCounts;
    }

    public void printSummary(PrintWriter out, int... percentiles) {
	out.println("samples: " + sampleCount);
	out.println("max:     " + maxLatency());
	out.println("average: " + averageLatency());
	out.println("median:  " + percentileLatency(50));
	for (int percentile : percentiles) {
	    out.println(percentile + "%:     " + percentileLatency(percentile));
	}
	if (assertionErrors.size() > 0) {
	    out.println("errors:  " + assertionErrors.size() + " ("
		    + (errorsRate() * 100.) + "%)");
	}
	out.flush();
    }

    // java.lang.Object overrides
    // --------------------------------------------------------------------------------------

    @Override
    public String toString() {
	return getClass().getSimpleName();
    }

}
