/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics;

import io.opentelemetry.api.common.Labels;
import io.opentelemetry.api.metrics.LongValueRecorder;
import io.opentelemetry.sdk.metrics.LongValueRecorderSdk.BoundInstrument;
import io.opentelemetry.sdk.metrics.aggregator.Aggregator;
import io.opentelemetry.sdk.metrics.common.InstrumentType;
import io.opentelemetry.sdk.metrics.common.InstrumentValueType;

final class LongValueRecorderSdk extends AbstractSynchronousInstrument<BoundInstrument>
    implements LongValueRecorder {

  private LongValueRecorderSdk(
      InstrumentDescriptor descriptor, InstrumentProcessor instrumentProcessor) {
    super(descriptor, instrumentProcessor, BoundInstrument::new);
  }

  @Override
  public void record(long value, Labels labels) {
    BoundInstrument boundInstrument = bind(labels);
    boundInstrument.record(value);
    boundInstrument.unbind();
  }

  @Override
  public void record(long value) {
    record(value, Labels.empty());
  }

  static final class BoundInstrument extends AbstractBoundInstrument
      implements BoundLongValueRecorder {

    BoundInstrument(Aggregator aggregator) {
      super(aggregator);
    }

    @Override
    public void record(long value) {
      recordLong(value);
    }
  }

  static final class Builder extends AbstractInstrument.Builder<LongValueRecorderSdk.Builder>
      implements LongValueRecorder.Builder {

    Builder(
        String name,
        MeterProviderSharedState meterProviderSharedState,
        MeterSharedState meterSharedState) {
      super(name, meterProviderSharedState, meterSharedState);
    }

    @Override
    Builder getThis() {
      return this;
    }

    @Override
    public LongValueRecorderSdk build() {
      InstrumentDescriptor descriptor =
          getInstrumentDescriptor(InstrumentType.VALUE_RECORDER, InstrumentValueType.LONG);
      return register(new LongValueRecorderSdk(descriptor, getBatcher(descriptor)));
    }
  }
}
