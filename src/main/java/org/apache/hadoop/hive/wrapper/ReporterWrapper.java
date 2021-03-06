/*
 * Copyright 2016 Axibase Corporation or its affiliates. All Rights Reserved.
 * Copyright 2013-2015 Qubole
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.wrapper;

import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.StatusReporter;

public class ReporterWrapper extends StatusReporter implements Reporter {
    private Reporter wrappedReporter;

    public ReporterWrapper(Reporter reporter) {
        wrappedReporter = reporter;
    }

    @Override
    public Counters.Counter getCounter(Enum<?> anEnum) {
        return wrappedReporter.getCounter(anEnum);
    }

    @Override
    public Counters.Counter getCounter(String s, String s1) {
        return wrappedReporter.getCounter(s, s1);
    }

    @Override
    public void incrCounter(Enum<?> anEnum, long l) {
        wrappedReporter.incrCounter(anEnum, l);
    }

    @Override
    public void incrCounter(String s, String s1, long l) {
        wrappedReporter.incrCounter(s, s1, l);
    }

    @Override
    public InputSplit getInputSplit() throws UnsupportedOperationException {
        return wrappedReporter.getInputSplit();
    }

    @Override
    public void progress() {
        wrappedReporter.progress();
    }

    // @Override
    public float getProgress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatus(String s) {
        wrappedReporter.setStatus(s);
    }
}
