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

import com.github.javatlacati.contiperf.ExecutionConfig;
import com.github.javatlacati.contiperf.ExecutionLogger;
import com.github.javatlacati.contiperf.PerformanceRequirement;
import com.github.javatlacati.stat.LatencyCounter;

/**
 * Replaces the {@link ExecutionLogger} interface of
 * ContiPerf 1 and adds context access and inter-module referencing features.<br>
 * <br>
 * Created: 16.01.2011 07:44:56
 * 
 * @since 2.0.0
 * @author Volker Bergmann
 */
public interface ReportModule {
    void setContext(ReportContext context);

    String getReportReferenceLabel(String serviceId);

    String getReportReference(String serviceId);

    void starting(String serviceId);

    void invoked(String serviceId, int latency, long startTime);

    void completed(String serviceId, LatencyCounter[] counters,
                   ExecutionConfig executionConfig, PerformanceRequirement requirement);

    void error(String serviceId);
}
